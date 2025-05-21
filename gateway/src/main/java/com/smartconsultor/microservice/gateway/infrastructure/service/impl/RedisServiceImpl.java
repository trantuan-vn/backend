// RedisServiceImpl.java (Optimized & Safe)
package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import io.vertx.core.*;
import io.vertx.redis.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.infrastructure.service.RedisService;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.config.RedisConfig;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class RedisServiceImpl implements RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
    private static final int MAX_RECONNECT_RETRIES = 5;
    private static final long MAX_BACKOFF_MS = 10_000;
    private static final long REDIS_COMMAND_TIMEOUT_MS = 5000;
    private static final Pattern SOCKET_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{20,50}$");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{10,36}$");
    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{10,50}$");
    private static final Pattern POD_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{5,50}$");

    private final Vertx vertx;
    private final RedisOptions options;
    private final AtomicBoolean CONNECTING = new AtomicBoolean();
    private final AtomicReference<RedisAPI> redisAPIRef = new AtomicReference<>();

    private Redis redis;
    private Redis subscriberRedis;
    private RedisConnection subscriberConn;
    private final RedisConfig config;

    @Inject
    public RedisServiceImpl(Vertx vertx, AppConfig appConfig) {
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

    public Future<Void> initialize() {
        return withTimeout(createRedisClient(), REDIS_COMMAND_TIMEOUT_MS)
            .compose(_ -> createRedisSubscriberClient())
            .mapEmpty();
    }

    @Override
    public Future<Void> registerWebsocket(String userId, String deviceId, String socketId, String podId, long socketTTL) {
        if (!isValidInput(userId, deviceId, socketId, podId)) {
            return Future.failedFuture("Invalid input parameters");
        }
        if (socketTTL <= 0) {
            return Future.failedFuture("TTL must be positive");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        String skey = socketKey(socketId);
        String sessionSetKey = sessionKey(userId);

        return redisAPI.multi()
            .compose(_ -> redisAPI.hset(Arrays.asList(skey, "userId", userId, "deviceId", deviceId, "podId", podId)))
            .compose(_ -> redisAPI.expire(Arrays.asList(skey, String.valueOf(socketTTL))))
            .compose(_ -> redisAPI.sadd(Arrays.asList(sessionSetKey, socketId)))
            .compose(_ -> redisAPI.exec())
            .recover(err -> redisAPI.discard().compose(__ -> Future.failedFuture(err)))
            .compose(execResp -> {
                if (execResp == null || execResp.size() == 0) {
                    return Future.failedFuture("Redis exec failed or was aborted");
                }
                return Future.succeededFuture();
            });
    }

    @Override
    public Future<Void> refreshWebsocketTTL(String socketId, long socketTTL) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (socketTTL <= 0) {
            return Future.failedFuture("TTL must be positive");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");
        
        return redisAPI.expire(Arrays.asList(socketKey(socketId), String.valueOf(socketTTL)))
            .recover(err -> Future.failedFuture(err))
            .mapEmpty();
    }

    @Override
    public Future<Void> removeWebsocket(String socketId) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return redisAPI.keys(socketKey(socketId)).compose(keys -> {
            if (keys == null || keys.size() == 0) return Future.succeededFuture();
            String key = keys.get(0).toString();
            return redisAPI.hget(key, "userId") 
                .compose(resp -> {
                    if (resp == null) return Future.succeededFuture();
                    String userId = resp.toString();
                    if (!isValidUserId(userId)) {
                        logger.warn("Invalid user ID found in Redis: {}", userId);
                        return Future.succeededFuture();
                    }
                    String sessionSetKey = sessionKey(userId);
                    return redisAPI.multi()
                        .compose(_ -> redisAPI.del(Collections.singletonList(key)))
                        .compose(_ -> redisAPI.srem(Arrays.asList(sessionSetKey, socketId)))
                        .compose(_ -> redisAPI.exec())
                        .recover(err -> redisAPI.discard().compose(_ -> Future.failedFuture(err)))
                        .mapEmpty();
                });
        });
    }

    @Override
    public Future<Void> saveLastProcessedSeqId(String socketId, String seqId) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (seqId == null || seqId.isEmpty()) {
            return Future.failedFuture("Sequence ID cannot be null or empty");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");
        return redisAPI.hset(Arrays.asList(socketKey(socketId), "lastSeq", seqId)).mapEmpty();
    }

    @Override
    public Future<Optional<String>> getLastProcessedMessageId(String socketId) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");
        return redisAPI.hget(socketKey(socketId), "lastSeq")
            .map(resp -> resp != null ? Optional.of(resp.toString()) : Optional.empty());
    }

    @Override
    public Future<List<String>> getWebsocketsByUser(String userId) {
        if (!isValidUserId(userId)) {
            return Future.failedFuture("Invalid user ID");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");
        return redisAPI.smembers(sessionKey(userId)).map(response -> {
            List<String> result = new ArrayList<>();
            for (Response r : response) {
                String socketId = r.toString();
                if (isValidSocketId(socketId)) {
                    result.add(socketId);
                } else {
                    logger.warn("Invalid socket ID found in session set: {}", socketId);
                }
            }
            return result;
        });
    }

    @Override
    public Future<String> getUserIdByWebsocket(String socketId) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");
        return redisAPI.hget(socketKey(socketId), "userId")
            .map(resp -> {
                if (resp == null) return null;
                String userId = resp.toString();
                return isValidUserId(userId) ? userId : null;
            });
    }

    public boolean isHealthy() {
        return redisAPIRef.get() != null;
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

    private Future<RedisConnection> createRedisClient() {
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

    private void attemptReconnect(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            logger.error("Redis reconnect failed after {} attempts", retry);
            return;
        }
        long backoff = Math.min((long) Math.pow(2, retry) * 100, MAX_BACKOFF_MS);
        vertx.setTimer(backoff, t -> createRedisClient()
            .onFailure(err -> attemptReconnect(retry + 1)));
    }

    private Future<Void> createRedisSubscriberClient() {
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

            conn.handler(this::handleExpiredMessage);
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

    private void reconnectSubscriber(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            logger.error("Subscriber reconnect failed after {} attempts", retry);
            return;
        }
        long backoff = Math.min((long) Math.pow(2, retry) * 100, MAX_BACKOFF_MS);
        vertx.setTimer(backoff, t -> createRedisSubscriberClient()
            .onFailure(err -> reconnectSubscriber(retry + 1)));
    }

    private void handleExpiredMessage(Response message) {
        if (message.size() < 3) {
            logger.debug("Invalid expired message format");
            return;
        }
        String expiredKey = message.get(2).toString();
        if (!expiredKey.startsWith("socket:")) {
            return;
        }

        String[] parts = expiredKey.split(":");
        if (parts.length < 3) {
            logger.warn("Invalid socket key format in expired message: {}", expiredKey);
            return;
        }
        String userId = parts[1];
        String socketId = parts[2];

        if (!isValidUserId(userId) || !isValidSocketId(socketId)) {
            logger.warn("Invalid user ID or socket ID in expired message: userId={}, socketId={}", userId, socketId);
            return;
        }

        RedisAPI redisAPI = redisAPIRef.get();
        if (redisAPI == null) {
            logger.warn("RedisAPI unavailable when handling expired message");
            return;
        }

        redisAPI.srem(Arrays.asList(sessionKey(userId), socketId))
            .onSuccess(_ -> logger.info("Expired socket {} removed from session:{}", socketId, userId))
            .onFailure(err -> logger.warn("Failed to remove expired socket {}", socketId, err));
    }

    private String socketKey(String socketId) {
        return "socket:" + socketId;
    }

    private String sessionKey(String userId) {
        return "session:" + userId;
    }

    private boolean isValidInput(String... args) {
        for (String arg : args) {
            if (arg == null || arg.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidSocketId(String socketId) {
        return socketId != null && SOCKET_ID_PATTERN.matcher(socketId).matches();
    }

    private boolean isValidUserId(String userId) {
        return userId != null && USER_ID_PATTERN.matcher(userId).matches();
    }

    private boolean isValidDeviceId(String deviceId) {
        return deviceId != null && DEVICE_ID_PATTERN.matcher(deviceId).matches();
    }

    private boolean isValidPodId(String podId) {
        return podId != null && POD_ID_PATTERN.matcher(podId).matches();
    }

    private String sanitizeRedisHost(String host) {
        // Basic sanitization to prevent injection in connection string
        return host.replaceAll("[^a-zA-Z0-9.-]", "");
    }

    private <T> Future<T> withTimeout(Future<T> future, long ms) {
        Promise<T> promise = Promise.promise();
        long timerId = vertx.setTimer(ms, tid -> {
            if (!promise.tryFail(new TimeoutException("Operation timed out after " + ms + "ms"))) {
                logger.debug("Timeout occurred but future was already completed");
            }
        });
        future.onComplete(ar -> {
            vertx.cancelTimer(timerId);
            promise.handle(ar);
        });
        return promise.future();
    }
}