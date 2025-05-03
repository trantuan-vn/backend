package com.smartconsultor.microservice.gateway.adapter.service;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketManager {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    private final ConcurrentHashMap<String, ServerWebSocket> sessions;

    public WebSocketManager() {
        this.sessions = new ConcurrentHashMap<>();
        logger.info("WebSocketManager initialized.");
    }

    /** Đăng ký một WebSocket mới nếu hợp lệ */
    public Future<Void> registerSession(ServerWebSocket webSocket) {
        Promise<Void> promise = Promise.promise();

        if (webSocket == null || webSocket.isClosed()) {
            logger.warn("Attempted to register a null or closed WebSocket.");
            return Future.succeededFuture(); // Không throw lỗi
        }

        String socketId = webSocket.textHandlerID();
        ServerWebSocket previous = sessions.put(socketId, webSocket);

        if (previous != null && previous != webSocket) {
            logger.warn("Replaced existing WebSocket session for ID: {}", socketId);
            closeQuietly(previous, "Replaced by new session");
        }

        logger.info("Registered WebSocket session: {}", socketId);
        return Future.succeededFuture();
    }

    /** Hủy session và đảm bảo đóng WebSocket nếu còn mở */
    public Future<Void> removeSession(ServerWebSocket webSocket) {
        if (webSocket == null) return Future.succeededFuture();

        String socketId = webSocket.textHandlerID();
        ServerWebSocket removed = sessions.remove(socketId);

        if (removed != null && !removed.isClosed()) {
            logger.info("Closing WebSocket session [{}] due to remove", socketId);
            try {
                removed.close((short) 1000, "Session removed");
            } catch (Exception ex) {
                logger.warn("Error while closing socket [{}]: {}", socketId, ex.getMessage());
            }
        } else {
            logger.info("Removed WebSocket session [{}] without active socket", socketId);
        }

        return Future.succeededFuture();
    }

    /** Gửi message an toàn theo socketId */
    public Future<Void> sendMessage(String socketId, String message) {
        Promise<Void> promise = Promise.promise();
        ServerWebSocket socket = sessions.get(socketId);

        if (socket != null && !socket.isClosed()) {
            socket.writeTextMessage(message, ar -> {
                if (ar.succeeded()) {
                    promise.complete();
                } else {
                    logger.warn("Failed to send message to WebSocket {}: {}", socketId, ar.cause().getMessage());
                    removeSession(socket); // không chờ future này
                    promise.fail(ar.cause());
                }
            });
        } else {
            logger.warn("Attempted to send message to invalid or closed socket: {}", socketId);
            sessions.remove(socketId);
            promise.fail("Socket is closed or not found");
        }

        return promise.future();
    }

    /** Đóng toàn bộ socket và dọn map */
    public Future<Void> shutdown() {
        Promise<Void> promise = Promise.promise();

        logger.info("Shutting down all WebSocket sessions...");
        sessions.forEach((id, socket) -> closeQuietly(socket, "Server shutdown"));
        sessions.clear();

        logger.info("All WebSocket sessions closed.");
        promise.complete();
        return promise.future();
    }

    /** Optional: cho metrics hoặc debug */
    public int getSessionCount() {
        return sessions.size();
    }

    /** Private helper: đóng socket an toàn */
    private void closeQuietly(ServerWebSocket socket, String reason) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close((short) 1000, reason);
            }
        } catch (Exception ex) {
            logger.warn("Error closing socket: {}", ex.getMessage());
        }
    }
}
