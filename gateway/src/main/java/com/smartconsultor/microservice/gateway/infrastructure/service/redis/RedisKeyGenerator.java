package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

public class RedisKeyGenerator {
    public String socketKey(String socketId) {
        return "socket:" + socketId;
    }

    public String sessionKey(String userId) {
        return "session:" + userId;
    }
}