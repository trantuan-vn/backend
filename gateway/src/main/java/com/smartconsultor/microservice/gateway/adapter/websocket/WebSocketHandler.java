package com.smartconsultor.microservice.gateway.adapter.websocket;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler; 
import io.vertx.core.http.ServerWebSocket; 
import io.vertx.core.json.JsonObject;  

public class WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final ValidateAccessTokenUseCase validateAccessTokenUseCase;
    private final WebSocketManager webSocketManager;
    private final PulsarService pulsarService;


    @Inject
    public WebSocketHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase,
                            PulsarService pulsarService,
                            WebSocketManager webSocketManager) {
        this.validateAccessTokenUseCase = validateAccessTokenUseCase;
        this.pulsarService = pulsarService;
        this.webSocketManager = webSocketManager;
    }

    public void handleWebSocket(ServerWebSocket socket) {
        authenticate(socket, ar -> {
            if (ar.succeeded()) {
                if (ar.result()) {
                    socket.accept();
                    //
                    final boolean[] initialized = {false};
                    String socketId = socket.textHandlerID();
                    // handleIncomingMessage
                    socket.handler(buffer -> {
                        JsonObject msg = buffer.toJsonObject();

                        if (!initialized[0]) {
                            // registerSession
                            webSocketManager.registerSession(socket);                                
                            if ("init".equals(msg.getString("type")) && msg.containsKey("seqId")) {
                                long seqId = msg.getLong("seqId");
                                // Trả lời ACK init
                                webSocketManager.sendMessage(socketId, new JsonObject()
                                    .put("type", "init_ack")
                                    .put("seqId", seqId)
                                    .encode());
        
                                initialized[0] = true;
                            } else {
                                // Reject nếu message đầu tiên không phải init
                                webSocketManager.sendMessage(socketId, new JsonObject()
                                    .put("type", "error")
                                    .put("reason", "init_required")
                                    .put("details", "First message must be of type 'init' with seqId")
                                    .encode());
                                socket.close();
                                webSocketManager.removeSession(socket);
                                
                            }   
                            return;                         
                        }                                    
                        
                        MessageRequest message = MessageRequest.fromJson(buffer.toString());                        
                        
                        pulsarService.sendToTopic(socketId, message)
                        .onSuccess(pulsarSuccess -> {
                            // Gửi ACK khi thành công
                            webSocketManager.sendMessage(socketId, new JsonObject()
                                .put("type", "ack")
                                .put("message", pulsarSuccess.toJson())
                                .encode());
                        })
                        .onFailure(pulsarError -> {
                            // Nếu gửi Pulsar không thành công, gửi lỗi
                            webSocketManager.sendMessage(socketId, new JsonObject()
                                .put("type", "error")
                                .put("reason", "pulsar_error")
                                .put("details", pulsarError.getMessage())
                                .encode());
                        });
                    });
                    // closeHandler
                    socket.closeHandler(v -> {
                        logger.warn("WebSocket {} closed, removing from WebSocketManager", socket.textHandlerID());
                        webSocketManager.removeSession(socket);
                    });                    
                } else {
                    socket.reject(401);
                }
            } else {
                socket.reject(500);
            }
        });
    }

    // Hàm handshake authentication
    private void authenticate(ServerWebSocket handshake, Handler<AsyncResult<Boolean>> resultHandler) {
        String authHeader = handshake.headers().get("Authorization");
    
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
    
            validateAccessTokenUseCase.validate(accessToken)
                .onSuccess(result -> {
                    Result.foldVoid(result,
                        isValid -> {
                            if (isValid) {
                                resultHandler.handle(Future.succeededFuture(true));
                            } else {
                                AuthUtils.rejectHandshakeWithJson(handshake, 401, "Unauthorized", "Invalid or expired access token");
                                resultHandler.handle(Future.succeededFuture(false));
                            }
                        },
                        failure -> {
                            AuthUtils.rejectHandshakeWithJson(handshake, failure.statusCode(), "Unauthorized", failure.message());
                            resultHandler.handle(Future.succeededFuture(false));
                        }
                    );
                })
                .onFailure(err -> {
                    logger.error("Unexpected failure to validate access token with params [{}]: {}", accessToken, err.getMessage(), err);
                    AuthUtils.rejectHandshakeWithJson(handshake, 500, "Server Error", "Internal validation error");
                    resultHandler.handle(Future.succeededFuture(false));
                });
        } else {
            // Authorization header thiếu
            AuthUtils.rejectHandshakeWithJson(handshake, 401, "Unauthorized", "Missing Authorization header");
            resultHandler.handle(Future.succeededFuture(false));
        }
    }
  
}
