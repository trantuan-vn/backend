package com.smartconsultor.microservice.gateway.application.usecases.websocket;

import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;

import io.vertx.core.http.ServerWebSocket;

public interface GatewayUseCase {
    void handle( ServerWebSocket ws, GatewayMessage rawMessage);
}
