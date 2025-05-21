package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class WebSocketManagerImpl implements WebSocketManager {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketManagerImpl.class);
    private static final int MAX_MESSAGE_SIZE = 65536; // 64KB
    private static final int MAX_BROADCAST_CHUNK_SIZE = 1000;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 100L;
    private static final short NORMAL_CLOSE_CODE = 1000;

    private final Map<String, ServerWebSocket> sessions;
    private final Vertx vertx;
    private final WorkerExecutor broadcastExecutor;
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    @Inject
    public WebSocketManagerImpl(Vertx vertx) {
        this.vertx = vertx;
        this.broadcastExecutor = vertx.createSharedWorkerExecutor(
            "broadcast-worker-pool", 
            10,
            2, TimeUnit.MINUTES // Timeout để ngăn worker bị treo
        );
        this.sessions = new ConcurrentHashMap<>();
        logger.info("WebSocketManagerImpl initialized.");
    }

    @Override
    public Future<Void> registerSession(ServerWebSocket webSocket) {
        if (isShuttingDown.get()) {
            logger.warn("Rejecting new WebSocket registration during shutdown");
            return Future.failedFuture("Server is shutting down");
        }

        if (webSocket == null || webSocket.isClosed()) {
            logger.warn("Attempted to register a null or closed WebSocket.");
            return Future.failedFuture("Invalid WebSocket");
        }

        String socketId = webSocket.textHandlerID();
        ServerWebSocket previous = sessions.put(socketId, webSocket);

        if (previous != null && previous != webSocket && !previous.isClosed()) {
            logger.warn("Replaced existing WebSocket session for ID: {}", socketId);
            closeQuietly(previous, "Replaced by new session");
        }

        logger.info("Registered WebSocket session: {}", socketId);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> removeSession(ServerWebSocket webSocket) {
        if (webSocket == null) {
            return Future.succeededFuture();
        }

        String socketId = webSocket.textHandlerID();
        ServerWebSocket removed = sessions.remove(socketId);

        if (removed != null && !removed.isClosed()) {
            logger.info("Closing WebSocket session [{}] due to remove", socketId);
            closeQuietly(removed, "Session removed");
        } else if (removed == null) {
            logger.debug("WebSocket session [{}] not found in active sessions", socketId);
        }

        return Future.succeededFuture();
    }

    @Override
    public Future<Void> sendMessage(String socketId, String message) {
        if (message == null || message.isEmpty()) {
            return Future.failedFuture("Message cannot be null or empty");
        }

        if (message.length() > MAX_MESSAGE_SIZE) {
            return Future.failedFuture("Message size exceeds maximum limit");
        }

        ServerWebSocket socket = sessions.get(socketId);
    
        if (socket == null || socket.isClosed()) {
            logger.warn("Attempted to send message to invalid or closed socket: {}", socketId);
            sessions.remove(socketId);
            return Future.failedFuture("Socket is closed or not found");
        }
    
        return socket.writeTextMessage(message)
            .onFailure(err -> {
                logger.warn("Failed to send message to socket [{}]: {}", socketId, err.getMessage());
                sessions.remove(socketId);
            });
    }

    @Override
    public Future<Void> broadcast(String message) {
        if (isShuttingDown.get()) {
            return Future.failedFuture("Server is shutting down");
        }

        if (message == null || message.isEmpty()) {
            return Future.failedFuture("Message cannot be null or empty");
        }

        if (message.length() > MAX_MESSAGE_SIZE) {
            return Future.failedFuture("Message size exceeds maximum limit");
        }

        if (sessions.isEmpty()) {
            return Future.succeededFuture();
        }

        byte[] rawBytes = message.getBytes(StandardCharsets.UTF_8);
        Buffer sharedBuffer = Buffer.buffer(rawBytes);
        List<ServerWebSocket> sockets = new ArrayList<>(sessions.values());

        // Chunk the sockets for parallel processing
        List<List<ServerWebSocket>> chunks = chunkList(sockets, MAX_BROADCAST_CHUNK_SIZE);
        List<Future> chunkFutures = new ArrayList<>(chunks.size());

        for (List<ServerWebSocket> chunk : chunks) {
            Promise<Void> chunkPromise = Promise.promise();
            chunkFutures.add(chunkPromise.future());

            broadcastExecutor.executeBlocking(p -> {
                try {
                    for (ServerWebSocket socket : chunk) {
                        sendWithRetry(socket, sharedBuffer.copy(), MAX_RETRIES);
                    }
                    p.complete();
                } catch (Exception e) {
                    p.fail(e);
                }
            }, false, res -> {
                if (res.succeeded()) {
                    chunkPromise.complete();
                } else {
                    chunkPromise.fail(res.cause());
                }
            });
        }

        return CompositeFuture.all(chunkFutures)
            .recover(err -> {
                logger.error("Broadcast partially failed: {}", err.getMessage());
                return Future.failedFuture(err);
            })
            .mapEmpty();
    }

    private List<List<ServerWebSocket>> chunkList(List<ServerWebSocket> list, int chunkSize) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<ServerWebSocket>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(new ArrayList<>(list.subList(i, Math.min(i + chunkSize, list.size()))));
        }
        return chunks;
    }

    private void sendWithRetry(ServerWebSocket socket, Buffer data, int retriesLeft) {
        if (socket.isClosed()) {
            sessions.remove(socket.textHandlerID());
            return;
        }
    
        if (socket.writeQueueFull()) {
            logger.debug("Write queue full, skipping socket: {}", socket.textHandlerID());
            return;
        }
    
        socket.writeBinaryMessage(data, ar -> {
            if (ar.failed()) {
                if (retriesLeft > 0) {
                    logger.warn("Send failed, retrying ({} left): {}", retriesLeft, socket.textHandlerID());
                    vertx.setTimer(RETRY_DELAY_MS, id -> sendWithRetry(socket, data, retriesLeft - 1));
                } else {
                    logger.error("Final send attempt failed, removing socket: {}", socket.textHandlerID());
                    sessions.remove(socket.textHandlerID());
                    closeQuietly(socket, "Send failed after retries");
                }
            }
        });
    }        

    @Override
    public Future<Void> shutdown() {
        if (!isShuttingDown.compareAndSet(false, true)) {
            return Future.succeededFuture(); // Already shutting down
        }

        logger.info("Shutting down all WebSocket sessions...");
        sessions.forEach((id, socket) -> closeQuietly(socket, "Server shutdown"));
        sessions.clear();
        
        return broadcastExecutor.close()
            .onComplete(res -> {
                if (res.succeeded()) {
                    logger.info("All WebSocket sessions and executor closed.");
                } else {
                    logger.error("Error closing broadcast executor: {}", res.cause().getMessage());
                }
            });
    }

    @Override
    public int getSessionCount() {
        return sessions.size();
    }

    @Override
    public boolean hasSession(String socketId) {
        return sessions.containsKey(socketId);
    }

    private void closeQuietly(ServerWebSocket socket, String reason) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close(NORMAL_CLOSE_CODE, reason);
            }
        } catch (Exception ex) {
            logger.warn("Error closing socket [{}]: {}", socket.textHandlerID(), ex.getMessage());
        }
    }
}