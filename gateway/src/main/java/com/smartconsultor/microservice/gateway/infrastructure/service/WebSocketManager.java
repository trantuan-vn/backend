package com.smartconsultor.microservice.gateway.infrastructure.service;

import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;

public interface WebSocketManager {
    Future<Void> registerSession(ServerWebSocket webSocket);
    Future<Void> removeSession(ServerWebSocket webSocket);
    Future<Void> sendMessage(String socketId, String message);
    Future<Void> broadcast(String message);
    Future<Void> shutdown();
    int getSessionCount();
    boolean hasSession(String socketId);
}
