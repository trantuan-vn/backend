package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.redis.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.config.RedisConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RedisClientManager {
    private static final Logger logger = LoggerFactory.getLogger(RedisClientManager.class);
    private static final int MAX_RECONNECT_RETRIES = 5;
    private static final long MAX_BACKOFF_MS = 10_000;

    private final Vertx vertx;
    private final RedisOptions options;
    private final AtomicBoolean CONNECTING = new AtomicBoolean();
    private final AtomicReference<RedisAPI> redisAPIRef = new AtomicReference<>();
    private Redis redis;
    private Redis subscriberRedis;
    private RedisConnection subscriberConn;
    private final RedisConfig config;

    public RedisClientManager(Vertx vertx, AppConfig appConfig) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx instance cannot be null");
        this.config = Objects.requireNonNull(appConfig, "AppConfig cannot be null").getRedis();
        this.options = new RedisOptions()
            .addConnectionString("redis://" + sanitizeRedisHost(config.getHost()) + ":" + config.getPort() + "/" + config.getDatabase())
            .setPassword(config.getPassword())
            .setMaxPoolSize(config.getMaxPoolSize())
            .setMaxWaitingHandlers(config.getMaxWaitingHandlers())
            .setPoolRecycleTimeout(config.getPoolRecycleTimeout())
            .setPoolCleanerInterval(config.getPoolCleanerInterval());
    }

    public Future<RedisConnection> createRedisClient() {
        if (redisAPIRef.get() != null) return Future.succeededFuture();

        Promise<RedisConnection> promise = Promise.promise();
        if (redis != null) {
            redis.close();
        }

        if (CONNECTING.compareAndSet(false, true)) {
            redis = Redis.createClient(vertx, options);
            redis.connect().onSuccess(conn -> {
                redisAPIRef.set(RedisAPI.api(conn));
                conn.exceptionHandler(e -> {
                    logger.warn("Redis connection exception", e);
                    attemptReconnect(1);
                });
                conn.endHandler(v -> {
                    logger.info("Redis connection ended");
                    attemptReconnect(1);
                });
                CONNECTING.set(false);
                promise.complete(conn);
            }).onFailure(err -> {
                CONNECTING.set(false);
                logger.error("Redis connection failed", err);
                attemptReconnect(1);
                promise.fail(err);
            });
        } else {
            promise.fail("Already connecting");
        }
        return promise.future();
    }

    public Future<Void> createRedisSubscriberClient(RedisExpiredMessageHandler handler) {
        Promise<Void> promise = Promise.promise();
        if (subscriberConn != null) {
            subscriberConn.close();
        }
        if (subscriberRedis != null) {
            subscriberRedis.close();
        }

        subscriberRedis = Redis.createClient(vertx, options);
        subscriberRedis.connect().onSuccess(conn -> {
            this.subscriberConn = conn;
            RedisAPI subApi = RedisAPI.api(conn);
            String expiredChannel = "__keyevent@" + config.getDatabase() + "__:expired";
            subApi.subscribe(Collections.singletonList(expiredChannel)).onSuccess(v -> {
                logger.info("Subscribed to expired: {}", expiredChannel);
                promise.complete();
            }).onFailure(promise::fail);

            conn.handler(handler::handleExpiredMessage);
            conn.exceptionHandler(err -> {
                logger.warn("Subscriber connection exception", err);
                reconnectSubscriber(1);
            });
            conn.endHandler(v -> {
                logger.info("Subscriber connection ended");
                reconnectSubscriber(1);
            });
        }).onFailure(err -> {
            logger.error("Subscriber connection failed", err);
            reconnectSubscriber(1);
            promise.fail(err);
        });
        return promise.future();
    }

    public Future<Void> shutdown() {
        List<Future> closures = new ArrayList<>();
        RedisAPI api = redisAPIRef.get();
        if (api != null) {
            closures.add(Future.future(promise -> {
                api.close();
                promise.complete();
            }));
        }
        if (redis != null) {
            closures.add(Future.future(promise -> {
                redis.close();
                promise.complete();
            }));
        }
        if (subscriberConn != null) {
            RedisAPI subApi = RedisAPI.api(subscriberConn);
            String expiredChannel = "__keyevent@" + config.getDatabase() + "__:expired";
            closures.add(subApi.unsubscribe(Collections.singletonList(expiredChannel)));
            closures.add(Future.future(promise -> {
                subscriberConn.close();
                promise.complete();
            }));
        }
        if (subscriberRedis != null) {
            closures.add(Future.future(promise -> {
                subscriberRedis.close();
                promise.complete();
            }));
        }
        return CompositeFuture.all(closures).mapEmpty();
    }

    public RedisAPI getRedisAPI() {
        return redisAPIRef.get();
    }

    public boolean isHealthy() {
        return redisAPIRef.get() != null;
    }

    private void attemptReconnect(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            logger.error("Redis reconnect failed after {} attempts", retry);
            return;
        }
        long backoff = Math.min((long) Math.pow(2, retry) * 100, MAX_BACKOFF_MS);
        vertx.setTimer(backoff, t -> createRedisClient()
            .onFailure(err -> attemptReconnect(retry + 1)));
    }

    private void reconnectSubscriber(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            logger.error("Subscriber reconnect failed after {} attempts", retry);
            return;
        }
        long backoff = Math.min((long) Math.pow(2, retry) * 100, MAX_BACKOFF_MS);
        vertx.setTimer(backoff, t -> createRedisSubscriberClient(new RedisExpiredMessageHandler(this))
            .onFailure(err -> reconnectSubscriber(retry + 1)));
    }

    private String sanitizeRedisHost(String host) {
        return host.replaceAll("[^a-zA-Z0-9.-]", "");
    }
}