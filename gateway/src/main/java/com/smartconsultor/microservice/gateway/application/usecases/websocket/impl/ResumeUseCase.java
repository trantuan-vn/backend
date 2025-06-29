package com.smartconsultor.microservice.gateway.application.usecases.websocket.impl;

import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.GatewayUseCase;
import io.vertx.core.http.ServerWebSocket;

public class ResumeUseCase implements GatewayUseCase {
    @Override
    public void handle(ServerWebSocket ws, GatewayMessage rawMessage) {
        // TODO: Tìm session trước đó của user, khôi phục trạng thái
        System.out.println("Handling RESUME for user: ");
    }
}

