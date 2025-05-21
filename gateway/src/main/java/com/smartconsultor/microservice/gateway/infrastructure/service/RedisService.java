package com.smartconsultor.microservice.gateway.infrastructure.service;

import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface RedisService {
    // Session tracking
    public Future<Void> registerWebsocket(String userId, String deviceId, String socketId, String podId, long socketTTL);
    public Future<Void> refreshWebsocketTTL(String socketId,long socketTTL);
    public Future<Void> removeWebsocket(String socketId);
    public Future<List<String>> getWebsocketsByUser(String userId);
    public Future<String> getUserIdByWebsocket(String socketId);

    // State coordination
    public Future<Void> saveLastProcessedSeqId(String socketId, String seqId);
    public Future<Optional<String>> getLastProcessedMessageId(String socketId);
}
