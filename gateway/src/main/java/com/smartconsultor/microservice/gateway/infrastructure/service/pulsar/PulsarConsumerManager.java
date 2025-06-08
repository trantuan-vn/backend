package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PulsarConsumerManager {
    private static final Logger logger = LoggerFactory.getLogger(PulsarConsumerManager.class);
    private final Map<String, Consumer<byte[]>> consumers = new ConcurrentHashMap<>();
    private final Vertx vertx;
    private final WebSocketManager webSocketManager;
    private final AppConfig appConfig;

    @Inject
    public PulsarConsumerManager(Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
        this.appConfig = Objects.requireNonNull(appConfig, "AppConfig cannot be null");
        this.webSocketManager = Objects.requireNonNull(webSocketManager, "WebSocketManager cannot be null");
    }

    public Future<Void> initializeConsumers(PulsarClient client, SlotManager slotManager) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        Promise<Void> promise = Promise.promise();
        List<Future> consumerFutures = new ArrayList<>();

        for (String topic : slotManager.getManagedTopics()) {
            if (!TopicUtils.isValidTopicName(topic)) {
                logger.warn("Invalid topic name: {}", topic);
                continue;
            }

            consumerFutures.add(
                provideConsumer(client, topic)
                    .onSuccess(consumer -> consumers.put(topic, consumer))
                    .onFailure(err -> logger.error("Failed to create consumer for topic {}", topic, err))
            );
        }

        if (consumerFutures.isEmpty()) {
            return Future.succeededFuture();
        }

        CompositeFuture.all(consumerFutures)
            .onSuccess(v -> promise.complete())
            .onFailure(err -> promise.fail(err));

        return promise.future();
    }

    private Future<Consumer<byte[]>> provideConsumer(PulsarClient client, String topic) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        String fullTopicName = TopicUtils.buildTopicPath("ws-sub-" + topic);
        String deadTopicName = TopicUtils.buildTopicPath("ws-sub-dead-" + topic);        
        String retryTopicName = TopicUtils.buildTopicPath("ws-sub-retry-" + topic);        
        

        Promise<Consumer<byte[]>> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                DeadLetterPolicy dlPolicy = DeadLetterPolicy.builder()
                    .maxRedeliverCount(Math.max(1, appConfig.getEnv().getReplicasCount()))
                    .deadLetterTopic(deadTopicName)
                    .retryLetterTopic(retryTopicName)
                    .build();

                Consumer<byte[]> newConsumer = client.newConsumer()
                    .topic(fullTopicName)
                    .subscriptionName("subscription-" + TopicUtils.sanitizeTopicName(topic))
                    .subscriptionType(SubscriptionType.Shared)
                    .receiverQueueSize(Math.max(1, appConfig.getPulsar().getReceiverQueueSize()))
                    .messageListener((consumer, msg) -> vertx.runOnContext(v -> handleMessage(topic, consumer, msg)))
                    .enableRetry(true)
                    .deadLetterPolicy(dlPolicy)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .negativeAckRedeliveryDelay(10, TimeUnit.SECONDS)
                    .ackTimeout(30, TimeUnit.SECONDS)
                    .maxPendingChunkedMessage(10)
                    .autoAckOldestChunkedMessageOnQueueFull(true)
                    .subscribeAsync()
                    .thenApply(cons -> {
                        logger.info("Consumer created successfully for topic {}", topic);
                        return cons;
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException("Failed to create consumer for topic " + topic, ex);
                    })
                    .get();

                blockingPromise.complete(newConsumer);
            } catch (Exception e) {
                blockingPromise.fail(new RuntimeException("Failed to initialize Pulsar Consumer for topic " + topic, e));
            }
        }, false, promise);

        return promise.future();
    }

    private void handleMessage(String topic, Consumer<byte[]> consumer, Message<byte[]> message) {
        if (message == null || message.getData() == null) {
            logger.warn("Received null message or message data");
            return;
        }

        vertx.executeBlocking(promise -> {
            try {
                if (message.getData().length > Constants.MAX_MESSAGE_SIZE_BYTES) {
                    logger.warn("Message size {} exceeds limit, rejecting", message.getData().length);
                    negativeAcknowledgeSafely(consumer, message);
                    return;
                }

                String raw = new String(message.getData(), StandardCharsets.UTF_8);
                JsonObject json;

                try {
                    json = new JsonObject(raw);
                } catch (Exception e) {
                    logger.warn("Invalid JSON message format: {}", raw);
                    negativeAcknowledgeSafely(consumer, message);
                    return;
                }

                String socketId = json.getString("socketid");
                if (!TopicUtils.isValidSocketId(socketId)) {
                    logger.warn("Invalid socket ID in message: {}", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    return;
                }

                if (!webSocketManager.hasSession(socketId)) {
                    logger.debug("No active session for socket {}, negative ack", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    return;
                }

                JsonObject payload = json.getJsonObject("message");
                if (payload == null) {
                    logger.warn("Message payload is null for socket {}", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    return;
                }

                webSocketManager.sendMessage(socketId, payload.encode())
                    .onSuccess(v -> acknowledgeSafely(consumer, message))
                    .onFailure(err -> {
                        logger.warn("Failed to send message to socket {}: {}", socketId, err.getMessage());
                        negativeAcknowledgeSafely(consumer, message);
                    });
            } catch (Exception e) {
                logger.error("Failed to process message from topic {}: {}", topic, e.getMessage());
                negativeAcknowledgeSafely(consumer, message);
                promise.fail(e);
            }
        }, false, ar -> {
            if (ar.failed()) {
                logger.error("Error processing message", ar.cause());
            }
        });
    }

    private void acknowledgeSafely(Consumer<byte[]> consumer, Message<byte[]> message) {
        if (consumer == null || message == null) return;

        vertx.executeBlocking(promise -> {
            try {
                consumer.acknowledge(message);
                promise.complete();
            } catch (PulsarClientException e) {
                logger.error("Failed to acknowledge message: {}", e.getMessage());
                promise.fail(e);
            }
        }, false, ar -> {});
    }

    private void negativeAcknowledgeSafely(Consumer<byte[]> consumer, Message<byte[]> message) {
        if (consumer == null || message == null) return;

        vertx.executeBlocking(promise -> {
            try {
                consumer.negativeAcknowledge(message);
                promise.complete();
            } catch (Exception e) {
                logger.error("Failed to negative acknowledge message: {}", e.getMessage());
                promise.fail(e);
            }
        }, false, ar -> {});
    }

    public Future<Void> addConsumer(PulsarClient client, AppConfig appConfig, String topic) {
        String fullTopicName = TopicUtils.buildTopicPath(topic);
        if (consumers.containsKey(topic)) {
            return Future.succeededFuture();
        }

        Promise<Void> promise = Promise.promise();

        provideConsumer(client, fullTopicName)
            .onSuccess(consumer -> {
                consumers.put(topic, consumer);
                promise.complete();
            })
            .onFailure(err -> {
                logger.error("Failed to add consumer for topic {}", topic, err);
                promise.fail(err);
            });

        return promise.future();
    }

    public Future<Void> removeConsumer(String topic) {
        Consumer<byte[]> consumer = consumers.remove(topic);
        if (consumer != null) {
            return closeConsumer(consumer);
        }
        return Future.succeededFuture();
    }

    private Future<Void> closeConsumer(Consumer<byte[]> consumer) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            consumer.closeAsync()
                .thenRun(() -> {
                    logger.info("Consumer for topic {} closed successfully", consumer.getTopic());
                    blockingPromise.complete();
                })
                .exceptionally(ex -> {
                    logger.error("Failed to close consumer for topic {}: {}", consumer.getTopic(), ex.getMessage());
                    blockingPromise.fail(ex);
                    return null;
                });
        }, false, promise);

        return promise.future();
    }

    public Future<Void> shutdown() {
        List<Future> closeFutures = new ArrayList<>();
        consumers.values().forEach(consumer -> closeFutures.add(closeConsumer(consumer)));
        consumers.clear();
        return CompositeFuture.all(closeFutures).mapEmpty();
    }
}