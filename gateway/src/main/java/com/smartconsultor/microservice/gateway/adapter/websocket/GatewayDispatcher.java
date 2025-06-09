package com.smartconsultor.microservice.gateway.adapter.websocket;

import java.util.EnumMap;
import java.util.Map;

import com.smartconsultor.microservice.gateway.adapter.dto.common.MessageType;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.BusinessMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.HeartbeatMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.ResumeMessage;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.GatewayUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.impl.BusinessUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.impl.HeartbeatUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.impl.ResumeUseCase;
import com.smartconsultor.microservice.gateway.common.error.ErrorCodes;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;
import io.vertx.core.http.ServerWebSocket;

public class GatewayDispatcher {

    private final Map<MessageType, GatewayUseCase<?>> handlers = new EnumMap<>(MessageType.class);

    public static GatewayDispatcher create() {
        return new GatewayDispatcher()
            .register(MessageType.RESUME, new ResumeUseCase())
            .register(MessageType.BUSINESS, new BusinessUseCase())
            .register(MessageType.HEARTBEAT, new HeartbeatUseCase());
    }

    public GatewayDispatcher register(MessageType type, GatewayUseCase<?> useCase) {
        handlers.put(type, useCase);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void dispatch(GatewayMessage message, ServerWebSocket ws) {
        MessageType type = message.getType();
        GatewayUseCase<?> useCase = handlers.get(type);

        if (useCase == null) {
            AuthUtils.sendErrorAndClose(ws, ErrorCodes.UNSUPPORTED_MESSAGE,  "Unsupported message type: " + type);
            return;
        }

        try {
            switch (type) {
                case RESUME:
                    ((GatewayUseCase<ResumeMessage>) useCase).handle(message.getResume(), ws, message);
                    break;
                case BUSINESS:
                    ((GatewayUseCase<BusinessMessage>) useCase).handle(message.getBusiness(), ws, message);
                    break;
                case HEARTBEAT:
                    ((GatewayUseCase<HeartbeatMessage>) useCase).handle(message.getHeartbeat(), ws, message);
                    break;
                default:
                    AuthUtils.sendErrorAndClose(ws, ErrorCodes.UNSUPPORTED_MESSAGE , "Unknown message type: " + type);
            }
        } catch (Exception e) {
            AuthUtils.sendErrorAndClose(ws, ErrorCodes.UNSUPPORTED_MESSAGE , "Error handling message: " + e.getMessage());
        }
    }
}

