package com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class GeoIPServiceImpl implements GeoIPService {
    private static final Logger logger = LoggerFactory.getLogger(GeoIPServiceImpl.class);
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100;

    private final Vertx vertx;
    private final DatabaseReader[] readerPool;
    private final int poolSize;
    private final AtomicInteger roundRobin = new AtomicInteger(0);
    private final Cache<String, CityResponse> ipCache;
    private volatile boolean isClosed = false;

    @Inject
    public GeoIPServiceImpl(Vertx vertx, AppConfig appConfig) {
        this.vertx = vertx;
        validateConfig(appConfig);
        
        this.poolSize = Runtime.getRuntime().availableProcessors();

        File dbFile = new File(appConfig.getGeoip().getDbPath());
        if (!dbFile.exists() || !dbFile.canRead()) {
            throw new IllegalStateException("GeoIP DB not accessible at: " + dbFile.getAbsolutePath());
        }

        this.readerPool = new DatabaseReader[poolSize];
        try {
            for (int i = 0; i < poolSize; i++) {
                this.readerPool[i] = new DatabaseReader.Builder(dbFile)
                        .withCache(new CHMCache()) 
                        .build();
            }
        } catch (IOException e) {
            logger.error("Failed to initialize GeoIP database readers", e);
            throw new IllegalStateException("Failed to load GeoIP database", e);
        }

        this.ipCache = Caffeine.newBuilder()
                .maximumSize(appConfig.getGeoip().getMaximumSize())
                .expireAfterWrite(Duration.ofMinutes(appConfig.getGeoip().getExpireAfterWrite()))
                .recordStats()
                .build();
    }

    private void validateConfig(AppConfig appConfig) {
        if (appConfig.getGeoip() == null) {
            throw new IllegalArgumentException("GeoIP configuration is null");
        }
        if (appConfig.getGeoip().getDbPath() == null || appConfig.getGeoip().getDbPath().isEmpty()) {
            throw new IllegalArgumentException("GeoIP database path is not configured");
        }
        if (appConfig.getGeoip().getMaximumSize() <= 0) {
            throw new IllegalArgumentException("Invalid cache maximum size");
        }
        if (appConfig.getGeoip().getExpireAfterWrite() <= 0) {
            throw new IllegalArgumentException("Invalid cache expiration time");
        }
    }

    @Override
    public Future<CityResponse> lookup(String ip) {
        if (isClosed) {
            return Future.failedFuture("GeoIPService is closed");
        }

        if (!isValidIp(ip)) {
            logger.warn("Invalid IP address format: {}", ip);
            return Future.failedFuture("Invalid IP address format: " + ip);
        }

        Promise<CityResponse> promise = Promise.promise();
        CityResponse cached = ipCache.getIfPresent(ip);
        if (cached != null) {
            logger.debug("Cache hit for IP: {}", ip);
            promise.complete(cached);
            return promise.future();
        }

        executeWithRetry(ip, 0, promise);
        return promise.future();
    }

    private void executeWithRetry(String ip, int attempt, Promise<CityResponse> promise) {
        vertx.executeBlocking(future -> {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                DatabaseReader reader = getReader();
                CityResponse response = reader.city(ipAddress);

                ipCache.put(ip, response);
                logger.info("GeoIP lookup successful for IP: {}", ip);
                future.complete(response);
            } catch (IOException | GeoIp2Exception e) {
                if (attempt < MAX_RETRIES - 1) {
                    logger.warn("Retrying GeoIP lookup for IP: {}, attempt: {}", ip, attempt + 1);
                    vertx.setTimer(RETRY_DELAY_MS, id -> executeWithRetry(ip, attempt + 1, promise));
                } else {
                    logger.error("GeoIP lookup failed for IP: {} after {} attempts", ip, MAX_RETRIES, e);
                    future.fail(e);
                }
            }
        }, false, promise);
    }

    private boolean isValidIp(String ip) {
        return ip != null && IP_PATTERN.matcher(ip).matches();
    }

    private DatabaseReader getReader() {
        if (isClosed) {
            throw new IllegalStateException("GeoIPService is closed");
        }
        int index = Math.abs(roundRobin.getAndUpdate(i -> (i + 1) % poolSize));
        return readerPool[index];
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;

        for (DatabaseReader reader : readerPool) {
            if (reader != null) {
                try {
                    reader.close();
                    logger.info("GeoIP DatabaseReader closed successfully");
                } catch (IOException e) {
                    logger.error("Failed to close GeoIP DatabaseReader", e);
                }
            }
        }

        // Clean up cache
        ipCache.cleanUp();
        logger.info("GeoIPService closed, cache stats: {}", ipCache.stats());
    }
}