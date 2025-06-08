package com.smartconsultor.microservice.gateway.application.usecases.websocket.impl;

import com.smartconsultor.microservice.gateway.adapter.dto.common.MessageType;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.HeartbeatMessage;
import com.smartconsultor.microservice.gateway.application.usecases.websocket.GatewayUseCase;
import com.smartconsultor.microservice.gateway.common.error.ErrorCodes;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

public class HeartbeatUseCase implements GatewayUseCase<HeartbeatMessage> {

    // private final ConnectionService connectionService;

    public HeartbeatUseCase() {
        // this.connectionService = connectionService;
    }

    @Override
    public void handle(HeartbeatMessage message, ServerWebSocket ws, GatewayMessage gatewayMessage) {
        if (message == null || message.getStatus().isEmpty() || message.getTimestamp().isEmpty()) {
            AuthUtils.sendErrorAndClose(ws, ErrorCodes.UNSUPPORTED_MESSAGE, "Invalid heartbeat message: missing status or timestamp");
            return;
        }

        // Business logic: Handle ping/pong based on status
        try {
            String status = message.getStatus();
            if (!status.equals("ping") && !status.equals("pong")) {
                AuthUtils.sendErrorAndClose(ws, ErrorCodes.UNSUPPORTED_MESSAGE, "Invalid heartbeat status: " + status);
                return;
            }

            // Update connection state (e.g., last heartbeat timestamp)
            // Example: connectionService.updateLastHeartbeat(ws.textHandlerID(), message.getTimestamp());

            if (status.equals("ping")) {
                // Client sent ping, respond with pong
                GatewayMessage response = GatewayMessage.newBuilder()
                                    .setType(MessageType.HANDSHAKE)
                                    .setHeartbeat(HeartbeatMessage.newBuilder()
                                            .setStatus("pong")
                                            .setTimestamp(message.getTimestamp())
                                            .build())
                                    .build();
                ws.writeBinaryMessage(Buffer.buffer(response.toByteArray()));
            } else {
                // Client sent pong, acknowledge silently (no response)
                // Example: connectionService.acknowledgeClientPong(ws.textHandlerID(), message.getTimestamp());
            }
        } catch (Exception e) {
            AuthUtils.sendErrorAndClose(ws, ErrorCodes.INTERNAL_ERROR, "Heartbeat processing failed: " + e.getMessage());
        }
    }
}