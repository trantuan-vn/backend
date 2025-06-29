package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;


import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageResponse;
import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.gateway.common.error.ProducerFailure;
import com.smartconsultor.microservice.gateway.domain.model.ClientCommand;
import com.smartconsultor.microservice.gateway.domain.model.MessagePointer;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
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

public class PulsarProducerManager {
    private static final Logger logger = LoggerFactory.getLogger(PulsarProducerManager.class);
    private final Map<String, Producer<byte[]>> producers = new ConcurrentHashMap<>();
    private final Map<String, BacklogManager> backlogManagers = new ConcurrentHashMap<>();
    private final Vertx vertx;
    private final AppConfig appConfig;

    @Inject
    public PulsarProducerManager(Vertx vertx, AppConfig appConfig) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
        this.appConfig = appConfig;
    }

    public Future<Void> initializeProducers(PulsarClient client, SlotManager slotManager) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        Promise<Void> promise = Promise.promise();
        List<Future> producerFutures = new ArrayList<>();

        for (String topic : slotManager.getManagedTopics()) {
            if (!TopicUtils.isValidTopicName(topic)) {
                logger.warn("Invalid topic name: {}", topic);
                continue;
            }

            producerFutures.add(
                provideProducer(client, topic)
                    .onSuccess(producer -> {
                        producers.put(topic, producer);
                        backlogManagers.put(topic, new BacklogManager(vertx, appConfig)); // <== Thêm
                    })
                    .onFailure(err -> logger.error("Failed to create producer for topic {}", topic, err))
            );
        }

        if (producerFutures.isEmpty()) {
            return Future.succeededFuture();
        }

        CompositeFuture.all(producerFutures)
            .onSuccess(v -> promise.complete())
            .onFailure(promise::fail);

        return promise.future();
    }

    private Future<Producer<byte[]>> provideProducer(PulsarClient client, String topic) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        String fullTopicName = TopicUtils.buildTopicPath("ws-pub-" + topic);

        Promise<Producer<byte[]>> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                Producer<byte[]> newProducer = client.newProducer()
                    .topic(fullTopicName)
                    .sendTimeout(0, TimeUnit.SECONDS)
                    .compressionType(CompressionType.LZ4)
                    .batchingMaxMessages(Math.max(1, appConfig.getPulsar().getBatchingMaxMessagesPerBatch()))
                    .batchingMaxPublishDelay(Math.max(1, appConfig.getPulsar().getBatchingMaxPublishDelay()), TimeUnit.MILLISECONDS)
                    .enableBatching(true)
                    .enableChunking(true)
                    .blockIfQueueFull(true)
                    .maxPendingMessages(appConfig.getPulsar().getBackpressureThreshold())
                    .createAsync()
                    .thenApply(prod -> {
                        logger.info("Producer created successfully for topic {}", topic);
                        return prod;
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException("Failed to create producer for topic " + topic, ex);
                    })
                    .get();

                blockingPromise.complete(newProducer);
            } catch (Exception e) {
                blockingPromise.fail(new RuntimeException("Failed to initialize Pulsar Producer for topic " + topic, e));
            }
        }, false, promise);

        return promise.future();
    }

    public Future<GatewayMessage> sendToTopic(String topic, GatewayMessage message) {
        Producer<byte[]> producer = producers.get(topic);
        BacklogManager backlogManager = backlogManagers.get(topic);

        if (producer == null || !producer.isConnected()) {
            return Future.failedFuture("Producer for topic " + topic + " is not initialized or disconnected");
        }

        if (backlogManager == null || !backlogManager.getRateLimiter().tryAcquire()) {
            logger.warn("Rate limit exceeded or missing backlogManager for topic {}", topic);
            return Future.failedFuture("Rate limit exceeded");
        }

        Promise<GatewayMessage> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                byte[] payload = message.toByteArray();

                producer.newMessage()
                    .value(payload)
                    .eventTime(System.currentTimeMillis())
                    .sendAsync()
                    .thenAccept(msgId -> {
                        MessagePointer pointer = new MessagePointer(producer.getTopic(), msgId);
                        MessageResponse response = new MessageResponse("processing", pointer);
                        logger.debug("Message successfully sent to Pulsar topic {} with reqid: {}", topic, message.toString());
                        blockingPromise.complete(response);
                        backlogManager.getRateLimiter().release();
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to send message to Pulsar topic {}: {}", topic, ex.getMessage());
                        blockingPromise.fail(new ProducerFailure("Failed to send message to Pulsar", ex));
                        backlogManager.getRateLimiter().release();
                        return null;
                    });
            } catch (Exception e) {
                logger.error("Error preparing message for Pulsar: {}", e.getMessage());
                blockingPromise.fail(new ProducerFailure("Failed to prepare message", e));
                backlogManager.getRateLimiter().release();
            }
        }, false, promise);

        return promise.future();
    }

    public Future<Void> addProducer(PulsarClient client, String topic) {
        String fullTopicName = TopicUtils.buildTopicPath(topic);
        if (producers.containsKey(topic)) {
            return Future.succeededFuture();
        }

        Promise<Void> promise = Promise.promise();

        provideProducer(client, fullTopicName)
            .onSuccess(producer -> {
                producers.put(topic, producer);
                backlogManagers.put(topic, new BacklogManager(vertx, appConfig)); // <== Thêm
                logger.info("Producer added for topic {}", topic);
                promise.complete();
            })
            .onFailure(err -> {
                logger.error("Failed to add producer for topic {}", topic, err);
                promise.fail(err);
            });

        return promise.future();
    }

    public Future<Void> removeProducer(String topic) {
        Producer<byte[]> producer = producers.remove(topic);
        backlogManagers.remove(topic); // <== Thêm
        if (producer != null) {
            return closeProducer(producer);
        }
        return Future.succeededFuture();
    }

    private Future<Void> closeProducer(Producer<byte[]> producer) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            producer.closeAsync()
                .thenRun(() -> {
                    logger.info("Producer for topic {} closed successfully", producer.getTopic());
                    blockingPromise.complete();
                })
                .exceptionally(ex -> {
                    logger.error("Failed to close producer for topic {}: {}", producer.getTopic(), ex.getMessage());
                    blockingPromise.fail(ex);
                    return null;
                });
        }, false, promise);

        return promise.future();
    }

    public Future<Void> shutdown() {
        List<Future> closeFutures = new ArrayList<>();
        producers.values().forEach(producer -> closeFutures.add(closeProducer(producer)));
        producers.clear();
        backlogManagers.clear(); // <== Thêm
        return CompositeFuture.all(closeFutures).mapEmpty();
    }
}