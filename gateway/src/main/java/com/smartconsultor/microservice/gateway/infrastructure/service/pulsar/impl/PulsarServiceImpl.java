package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.impl;


import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageResponse;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.Constants;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.TopicUtils;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.apache.pulsar.client.api.PulsarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PulsarServiceImpl implements PulsarService {
    private static final Logger logger = LoggerFactory.getLogger(PulsarServiceImpl.class);

    private final Vertx vertx;
    private final AppConfig appConfig;
    private final SlotManager slotManager;
    private final PulsarClientFactory clientFactory;
    private final PulsarProducerManager producerManager;
    private final PulsarConsumerManager consumerManager;
    private final BacklogManager backlogManager;
    private volatile PulsarClient pulsarClient;
    private volatile boolean shuttingDown = false;
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

    @Inject
    public PulsarServiceImpl(Vertx vertx, AppConfig appConfig,
                            SlotManager slotManager, PulsarClientFactory clientFactory,
                            PulsarProducerManager producerManager, PulsarConsumerManager consumerManager,
                            BacklogManager backlogManager) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
        this.appConfig = Objects.requireNonNull(appConfig, "AppConfig cannot be null");
        this.slotManager = Objects.requireNonNull(slotManager, "SlotManager cannot be null");
        this.clientFactory = Objects.requireNonNull(clientFactory, "PulsarClientFactory cannot be null");
        this.producerManager = Objects.requireNonNull(producerManager, "PulsarProducerManager cannot be null");
        this.consumerManager = Objects.requireNonNull(consumerManager, "PulsarConsumerManager cannot be null");
        this.backlogManager = Objects.requireNonNull(backlogManager, "BacklogManager cannot be null");

        initializeWithRetry();
    }

    private void initializeWithRetry() {
        if (shuttingDown) return;

        long delay = Constants.calculateExponentialBackoff(reconnectAttempts.get());

        init()
            .onFailure(err -> {
                if (reconnectAttempts.incrementAndGet() < Constants.MAX_RECONNECT_ATTEMPTS) {
                    logger.warn("Initialization failed (attempt {}/{}), retrying in {}ms...",
                        reconnectAttempts.get(), Constants.MAX_RECONNECT_ATTEMPTS, delay);
                    vertx.setTimer(delay, id -> initializeWithRetry());
                } else {
                    logger.error("Failed to initialize after {} attempts", Constants.MAX_RECONNECT_ATTEMPTS, err);
                    reconnectAttempts.set(0);
                }
            })
            .onSuccess(v -> {
                reconnectAttempts.set(0);
                logger.info("Pulsar service initialized successfully");
            });
    }

    private Future<Void> init() {
        if (shuttingDown) {
            return Future.failedFuture("Service is shutting down");
        }

        Promise<Void> promise = Promise.promise();
        
        // Clean up any existing resources first
        shutdownInternalResources()
            .compose(v -> clientFactory.providePulsarClient(appConfig))
            .compose(client -> {
                this.pulsarClient = client;
                return Future.all(
                    producerManager.initializeProducers(client, slotManager),
                    consumerManager.initializeConsumers(client, slotManager)
                );
            })
            .onSuccess(v -> promise.complete())
            .onFailure(err -> {
                logger.error("Pulsar initialization failed", err);
                shutdownInternalResources()
                    .onComplete(r -> promise.fail(err));
            });

        return promise.future();        
    }


    private Future<Void> shutdownInternalResources() {
        return Future.all(
            producerManager.shutdown(),
            consumerManager.shutdown(),
            clientFactory.closePulsarClient(pulsarClient)
        ).mapEmpty();
    }

    @Override
    public Future<MessageResponse> sendToTopic(String userId, String socketId, MessageRequest message) {
        if (!TopicUtils.isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (message == null) {
            return Future.failedFuture("Message cannot be null");
        }

        String topic = slotManager.getTopicOfUser(userId).result();
        if (topic == null) {
            return Future.failedFuture("No topic assigned for socket ID");
        }

        return producerManager.sendToTopic(topic, socketId, message);
    }

    @Override
    public Future<Void> addConsumerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.assignTopicToUser(userId).result();
        if (topic == null) {
            return Future.failedFuture("No available topic for user");
        }

        return consumerManager.addConsumer(pulsarClient, appConfig, topic);
    }

    @Override
    public Future<Void> addProducerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.assignTopicToUser(userId).result();
        if (topic == null) {
            return Future.failedFuture("No available topic for user");
        }

        return producerManager.addProducer(pulsarClient, topic);
    }

    @Override
    public Future<Void> removeConsumerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.getTopicOfUser(userId).result();
        if (topic == null) {
            return Future.succeededFuture();
        }

        slotManager.removeUser(userId);
        return consumerManager.removeConsumer(topic);
    }

    @Override
    public Future<Void> removeProducerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.getTopicOfUser(userId).result();
        if (topic == null) {
            return Future.succeededFuture();
        }

        slotManager.removeUser(userId);
        return producerManager.removeProducer(topic);
    }

    @Override
    public Future<Void> shutdown() {
        if (shuttingDown) {
            return Future.failedFuture("Already shutting down");
        }
        shuttingDown = true;
        backlogManager.shutdown();
        return shutdownInternalResources()
            .onComplete(ar -> shuttingDown = false);
    }
}