package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisExpiredMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(RedisExpiredMessageHandler.class);
    private final RedisClientManager clientManager;
    private final RedisInputValidator validator;
    private final RedisKeyGenerator keyGenerator;

    public RedisExpiredMessageHandler(RedisClientManager clientManager) {
        this.clientManager = clientManager;
        this.validator = new RedisInputValidator();
        this.keyGenerator = new RedisKeyGenerator();
    }

    public void handleExpiredMessage(Response message) {
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

        if (!validator.isValidUserId(userId) || !validator.isValidSocketId(socketId)) {
            logger.warn("Invalid user ID or socket ID in expired message: userId={}, socketId={}", userId, socketId);
            return;
        }

        RedisAPI redisAPI = clientManager.getRedisAPI();
        if (redisAPI == null) {
            logger.warn("RedisAPI unavailable when handling expired message");
            return;
        }

        redisAPI.srem(Arrays.asList(keyGenerator.sessionKey(userId), socketId))
            .onSuccess(_ -> logger.info("Expired socket {} removed from session:{}", socketId, userId))
            .onFailure(err -> logger.warn("Failed to remove expired socket {}", socketId, err));
    }
}