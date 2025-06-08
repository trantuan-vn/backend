// RedisServiceImpl.java (Optimized & Safe)
package com.smartconsultor.microservice.gateway.infrastructure.service.redis.impl;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;

import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisClientManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisExpiredMessageHandler;
import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisInputValidator;
import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisKeyGenerator;
import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisService;
import com.smartconsultor.microservice.gateway.infrastructure.service.redis.RedisTimeoutHandler;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RedisServiceImpl implements RedisService {
    private final RedisClientManager clientManager;
    private final RedisKeyGenerator keyGenerator;
    private final RedisInputValidator validator;
    private final RedisTimeoutHandler timeoutHandler;

    @Inject
    public RedisServiceImpl(Vertx vertx, AppConfig appConfig) {
        this.clientManager = new RedisClientManager(vertx, appConfig);
        this.keyGenerator = new RedisKeyGenerator();
        this.validator = new RedisInputValidator();
        this.timeoutHandler = new RedisTimeoutHandler(vertx);
    }

    public Future<Void> initialize() {
        return timeoutHandler.withTimeout(clientManager.createRedisClient())
            .compose(_ -> clientManager.createRedisSubscriberClient(new RedisExpiredMessageHandler(clientManager)));
    }

    @Override
    public Future<Void> registerWebsocket(String userId, String deviceId, String socketId, String podId, long socketTTL) {
        if (!validator.isValidInput(userId, deviceId, socketId, podId) ||
            !validator.isValidUserId(userId) ||
            !validator.isValidDeviceId(deviceId) ||
            !validator.isValidSocketId(socketId) ||
            !validator.isValidPodId(podId)) {
            return Future.failedFuture("Invalid input parameters");
        }
        if (socketTTL <= 0) {
            return Future.failedFuture("TTL must be positive");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        String skey = keyGenerator.socketKey(socketId);
        String sessionSetKey = keyGenerator.sessionKey(userId);

        return timeoutHandler.withTimeout(redisAPI.multi()
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
            }));
    }

    @Override
    public Future<Void> refreshWebsocketTTL(String socketId, long socketTTL) {
        if (!validator.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (socketTTL <= 0) {
            return Future.failedFuture("TTL must be positive");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.expire(Arrays.asList(keyGenerator.socketKey(socketId), String.valueOf(socketTTL)))
            .mapEmpty());
    }

    @Override
    public Future<Void> removeWebsocket(String socketId) {
        if (!validator.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.keys(keyGenerator.socketKey(socketId)).compose(keys -> {
            if (keys == null || keys.size() == 0) return Future.succeededFuture();
            String key = keys.get(0).toString();
            return redisAPI.hget(key, "userId")
                .compose(resp -> {
                    if (resp == null) return Future.succeededFuture();
                    String userId = resp.toString();
                    if (!validator.isValidUserId(userId)) {
                        return Future.succeededFuture();
                    }
                    String sessionSetKey = keyGenerator.sessionKey(userId);
                    return redisAPI.multi()
                        .compose(_ -> redisAPI.del(Collections.singletonList(key)))
                        .compose(_ -> redisAPI.srem(Arrays.asList(sessionSetKey, socketId)))
                        .compose(_ -> redisAPI.exec())
                        .recover(err -> redisAPI.discard().compose(_ -> Future.failedFuture(err)))
                        .mapEmpty();
                });
        }));
    }

    @Override
    public Future<Void> saveLastProcessedSeqId(String socketId, String seqId) {
        if (!validator.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (seqId == null || seqId.isEmpty()) {
            return Future.failedFuture("Sequence ID cannot be null or empty");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.hset(Arrays.asList(keyGenerator.socketKey(socketId), "lastSeq", seqId)).mapEmpty());
    }

    @Override
    public Future<Optional<String>> getLastProcessedMessageId(String socketId) {
        if (!validator.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.hget(keyGenerator.socketKey(socketId), "lastSeq")
            .map(resp -> resp != null ? Optional.of(resp.toString()) : Optional.empty()));
    }

    @Override
    public Future<List<String>> getWebsocketsByUser(String userId) {
        if (!validator.isValidUserId(userId)) {
            return Future.failedFuture("Invalid user ID");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.smembers(keyGenerator.sessionKey(userId)).map(response -> 
            response.stream()
                .map(Response::toString)
                .filter(validator::isValidSocketId)
                .collect(Collectors.toList())
        ));
    }

    @Override
    public Future<String> getUserIdByWebsocket(String socketId) {
        if (!validator.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) return Future.failedFuture("RedisAPI unavailable");

        return timeoutHandler.withTimeout(redisAPI.hget(keyGenerator.socketKey(socketId), "userId")
            .map(resp -> {
                if (resp == null) return null;
                String userId = resp.toString();
                return validator.isValidUserId(userId) ? userId : null;
            }));
    }

    @Override
    public boolean isHealthy() {
        return clientManager.isHealthy();
    }

    @Override
    public Future<Void> shutdown() {
        return clientManager.shutdown();
    }
}