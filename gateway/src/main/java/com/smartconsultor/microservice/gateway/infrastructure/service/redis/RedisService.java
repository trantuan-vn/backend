package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface RedisService {
    Future<Void> registerWebsocket(String userId, String deviceId, String socketId, String podId, long socketTTL);
    Future<Void> refreshWebsocketTTL(String socketId, long socketTTL);
    Future<Void> removeWebsocket(String socketId);
    Future<Void> saveLastProcessedSeqId(String socketId, String seqId);
    Future<Optional<String>> getLastProcessedMessageId(String socketId);
    Future<List<String>> getWebsocketsByUser(String userId);
    Future<String> getUserIdByWebsocket(String socketId);
    boolean isHealthy();
    Future<Void> shutdown();
}