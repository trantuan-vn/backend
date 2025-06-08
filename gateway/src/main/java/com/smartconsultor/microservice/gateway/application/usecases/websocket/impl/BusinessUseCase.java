package com.smartconsultor.microservice.gateway.application.usecases.websocket.impl;

import com.smartconsultor.microservice.gateway.adapter.dto.gateway.BusinessMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.GatewayUseCase;
import io.vertx.core.http.ServerWebSocket;

public class BusinessUseCase implements GatewayUseCase<BusinessMessage> {
    @Override
    public void handle(BusinessMessage payload, ServerWebSocket ws, GatewayMessage rawMessage) {
        // TODO: Chuyển message vào Pulsar hoặc xử lý business logic
        System.out.println("Handling BUSINESS payload: " + payload.getEventTypeValue());
    }
}


