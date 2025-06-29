package com.smartconsultor.microservice.gateway.application.usecases.websocket.impl;

import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.GatewayUseCase;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import io.vertx.core.http.ServerWebSocket;

public class BusinessUseCase implements GatewayUseCase {

    private final PulsarService pulsarService;

    public BusinessUseCase(PulsarService pulsarService) {
        this.pulsarService = pulsarService;
    }

    @Override
    public void handle(ServerWebSocket ws, GatewayMessage rawMessage) {

        pulsarService.sendToTopic(rawMessage).onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("Sent to Pulsar successfully: " + ar.result());
            } else {
                System.err.println("Failed to send to Pulsar: " + ar.cause());
            }
        });
    }
}
