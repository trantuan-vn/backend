package com.smartconsultor.microservice.gateway.infrastructure.datasources.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class GeoIPService {

  private final Vertx vertx;
  private final DatabaseReader[] readerPool;
  private final int poolSize;
  private final AtomicInteger roundRobin = new AtomicInteger(0);
  private final Cache<String, CityResponse> ipCache;

  @Inject
  public GeoIPService(Vertx vertx, AppConfig appConfig) {
    this.vertx = vertx;
    this.poolSize = Runtime.getRuntime().availableProcessors();

    File dbFile = new File(appConfig.getGeoip().getDbPath());
    if (!dbFile.exists()) {
      throw new RuntimeException("GeoIP DB not found at: " + dbFile.getAbsolutePath());
    }

    this.readerPool = new DatabaseReader[poolSize];
    try {
      for (int i = 0; i < poolSize; i++) {
        this.readerPool[i] = new DatabaseReader.Builder(dbFile).build();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load GeoIP database", e);
    }
    this.ipCache = Caffeine.newBuilder()
        .maximumSize(appConfig.getGeoip().getMaximumSize())
        .expireAfterWrite(Duration.ofMinutes(appConfig.getGeoip().getExpireAfterWrite()))
        .build();    
  }

  public Future<CityResponse> lookup(String ip) {
    Promise<CityResponse> promise = Promise.promise();

    CityResponse cached = ipCache.getIfPresent(ip);
    if (cached != null) {
      promise.complete(cached);
      return promise.future();
    }

    vertx.executeBlocking(future -> {
      try {
        InetAddress ipAddress = InetAddress.getByName(ip);
        DatabaseReader reader = getReader();
        CityResponse response = reader.city(ipAddress);

        ipCache.put(ip, response);
        future.complete(response);
      } catch (IOException | GeoIp2Exception e) {
        future.fail(e);
      }
    }, false, promise);

    return promise.future();
  }

  private DatabaseReader getReader() {
    int index = Math.abs(roundRobin.getAndIncrement() % poolSize);
    return readerPool[index];
  }
}
