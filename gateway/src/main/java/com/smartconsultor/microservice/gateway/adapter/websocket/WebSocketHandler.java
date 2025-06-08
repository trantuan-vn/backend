package com.smartconsultor.microservice.gateway.adapter.websocket;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.adapter.dto.common.MessageType;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.ErrorMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.HandshakeMessage;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.common.error.ErrorCodes;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket; 

public class WebSocketHandler implements Handler<ServerWebSocket> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final ValidateAccessTokenUseCase validateAccessTokenUseCase;
    private final WebSocketManager webSocketManager;
    private final GatewayDispatcher dispatcher;


    @Inject
    public WebSocketHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase,
                            WebSocketManager webSocketManager,
                            GatewayDispatcher dispatcher) {
        this.validateAccessTokenUseCase = validateAccessTokenUseCase;
        this.webSocketManager = webSocketManager;
        this.dispatcher = dispatcher;
    }
    
    @Override
    public void handle(ServerWebSocket socket) {
        authenticate(socket).onComplete(authResult -> {
            // Luôn accept để trả lỗi bằng Protobuf
            socket.accept(); 

            if (authResult.succeeded()) {
                if (authResult.result()) {
                    // Success
                    webSocketManager.registerSession(socket);

                    // Gửi HANDSHAKE
                    GatewayMessage handshakeMsg = GatewayMessage.newBuilder()
                            .setType(MessageType.HANDSHAKE)
                            .setHandshake(HandshakeMessage.newBuilder()
                                        .setTimestamp(System.currentTimeMillis())
                                        .build())   
                            .build();
                    socket.writeBinaryMessage(Buffer.buffer(handshakeMsg.toByteArray()));

                    // handle incoming
                    socket.binaryMessageHandler(buffer -> {
                        try {
                            GatewayMessage msg = GatewayMessage.parseFrom(buffer.getBytes());
                            dispatcher.dispatch(msg, socket);
                            
                        } catch (Exception e) {
                            logger.error("WebSocket {} received invalid message: {}", socket.textHandlerID(), e.getMessage());
                            GatewayMessage errorMsg = GatewayMessage.newBuilder()
                                    .setType(MessageType.ERROR)
                                    .setError(ErrorMessage.newBuilder()
                                            .setCode(400)
                                            .setReason("Invalid message")
                                            .build())
                                    .build();
                            socket.writeBinaryMessage(Buffer.buffer(errorMsg.toByteArray()));
                        }
                    });

                    socket.closeHandler(v -> {
                        logger.warn("WebSocket {} closed, removing from WebSocketManager", socket.textHandlerID());
                        webSocketManager.removeSession(socket);
                    });

                } else {
                    // Trả lỗi Unauthorized (401)
                    AuthUtils.sendErrorAndClose(socket, ErrorCodes.INVALID_TOKEN, "Unauthorized");
                }
            } else {
                // Internal Error
                AuthUtils.sendErrorAndClose(socket, ErrorCodes.INTERNAL_ERROR, authResult.cause().getMessage());
            }
        });
    }

    // Hàm handshake authentication
    private Future<Boolean> authenticate(ServerWebSocket socket) {
        String authHeader = socket.headers().get("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());

            Promise<Boolean> promise = Promise.promise();

            validateAccessTokenUseCase.validate(token)
                .onSuccess(result -> {
                    Result.foldVoid(result,
                        isValid -> {
                            if (isValid) {
                                promise.complete(true);
                            } else {
                                promise.complete(false); // Sai token
                            }
                        },
                        failure -> {
                            promise.fail(new RuntimeException(failure.message())); // Dữ liệu lỗi từ use-case
                        }
                    );
                })
                .onFailure(err -> {
                    logger.error("Token validation failed: {}", err.getMessage(), err);
                    promise.fail(new RuntimeException("Internal validation error"));
                });

            return promise.future();
        } else {
            return Future.succeededFuture(false); // Thiếu Authorization header
        }
    }

 
}
