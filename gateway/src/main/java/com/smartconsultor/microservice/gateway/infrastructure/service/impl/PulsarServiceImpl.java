package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageResponse;
import com.smartconsultor.microservice.gateway.common.error.ProducerFailure;
import com.smartconsultor.microservice.gateway.domain.model.ClientCommand;
import com.smartconsultor.microservice.gateway.domain.model.MessagePointer;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.*;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class PulsarServiceImpl implements PulsarService {
    private static final Logger logger = LoggerFactory.getLogger(PulsarServiceImpl.class);
    private static final Pattern SOCKET_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{20,50}$");
    private static final Pattern TOPIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final long INITIAL_RECONNECTION_DELAY_MS = 1000;
    private static final long MAX_RECONNECTION_DELAY_MS = 30000;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int DEFAULT_RATE_LIMIT = 1000;
    private static final int DEFAULT_BACKPRESSURE_THRESHOLD = 1000;
    private static final int MAX_MESSAGE_SIZE_BYTES = 1024 * 1024; // 1MB

    private volatile PulsarClient pulsarClient;
    private volatile Producer<byte[]> producer;
    private final Map<String, Consumer<byte[]>> consumers = new ConcurrentHashMap<>();
    private final WebSocketManager webSocketManager;
    private final AppConfig appConfig;
    private final SlotManager slotManager;
    private final Vertx vertx;
    private volatile boolean shuttingDown = false;
    private volatile int reconnectAttempts = 0;
    
    // Backlog management components
    private final Semaphore rateLimiter;
    private final AtomicInteger pendingMessages = new AtomicInteger(0);
    private final AtomicLong lastRateLimitWindow = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger currentRate = new AtomicInteger(0);
    private final int rateLimit;
    private final int backpressureThreshold;
    private volatile boolean backpressureActive = false;
    private long backlogMonitorTimerId = -1;
    private final AtomicLong totalMessagesProcessed = new AtomicLong(0);
    private final AtomicLong totalMessagesFailed = new AtomicLong(0);

    @Inject
    public PulsarServiceImpl(Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager, SlotManager slotManager) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
        this.webSocketManager = Objects.requireNonNull(webSocketManager, "WebSocketManager cannot be null");
        this.appConfig = Objects.requireNonNull(appConfig, "AppConfig cannot be null");
        this.slotManager = Objects.requireNonNull(slotManager, "SlotManager cannot be null");
        
        // Validate configuration values
        this.rateLimit = Math.max(1, appConfig.getPulsar().getRateLimit() > 0 ? 
            appConfig.getPulsar().getRateLimit() : DEFAULT_RATE_LIMIT);
        this.backpressureThreshold = Math.max(1, appConfig.getPulsar().getBackpressureThreshold() > 0 ?
            appConfig.getPulsar().getBackpressureThreshold() : DEFAULT_BACKPRESSURE_THRESHOLD);
        this.rateLimiter = new Semaphore(rateLimit, true);
        
        initializeWithRetry();
        startBacklogMonitor();
        startMetricsCollector();
    }

    private void startMetricsCollector() {
        vertx.setPeriodic(60000, id -> {
            logger.info("Metrics - Processed: {}, Failed: {}, Pending: {}, Backpressure: {}",
                totalMessagesProcessed.get(),
                totalMessagesFailed.get(),
                pendingMessages.get(),
                backpressureActive ? "Active" : "Inactive");
        });
    }

    private void startBacklogMonitor() {
        backlogMonitorTimerId = vertx.setPeriodic(5000, id -> {
            if (!shuttingDown) {
                vertx.executeBlocking(promise -> {
                    try {
                        monitorAndAdjust();
                        promise.complete();
                    } catch (Exception e) {
                        logger.error("Backlog monitoring error", e);
                        promise.fail(e);
                    }
                }, false, ar -> {
                    if (ar.failed()) {
                        logger.error("Failed to monitor backlog", ar.cause());
                    }
                });
            }
        });
    }

    private void monitorAndAdjust() {
        int pending = pendingMessages.get();
        double utilization = (double) pending / backpressureThreshold;
        
        if (utilization > 0.8 && !backpressureActive) {
            logger.warn("High system load detected ({}% utilization), enabling backpressure", (int)(utilization * 100));
            backpressureActive = true;
        } else if (utilization < 0.5 && backpressureActive) {
            logger.info("System load normalized ({}% utilization), disabling backpressure", (int)(utilization * 100));
            backpressureActive = false;
        }
        
        long now = System.currentTimeMillis();
        if (now - lastRateLimitWindow.get() > 1000) {
            lastRateLimitWindow.set(now);
            currentRate.set(0);
        }
    }

    private void initializeWithRetry() {
        if (shuttingDown) return;
        
        long delay = calculateExponentialBackoff(reconnectAttempts);
        
        init()
            .onFailure(err -> {
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts++;
                    logger.warn("Initialization failed (attempt {}/{}), retrying in {}ms...", 
                        reconnectAttempts, MAX_RECONNECT_ATTEMPTS, delay);
                    
                    vertx.setTimer(delay, id -> initializeWithRetry());
                } else {
                    logger.error("Failed to initialize after {} attempts", MAX_RECONNECT_ATTEMPTS, err);
                    // Reset attempts after max reached to allow future retries
                    reconnectAttempts = 0;
                }
            })
            .onSuccess(v -> {
                reconnectAttempts = 0;
                logger.info("Pulsar service initialized successfully");
            });
    }

    private long calculateExponentialBackoff(int attempt) {
        long delay = (long) (INITIAL_RECONNECTION_DELAY_MS * Math.pow(2, attempt));
        return Math.min(delay, MAX_RECONNECTION_DELAY_MS);
    }

    private synchronized Future<Void> init() {
        if (shuttingDown) {
            return Future.failedFuture("Service is shutting down");
        }

        Promise<Void> promise = Promise.promise();
        
        // Clean up any existing resources first
        shutdownInternalResources()
            .compose(v -> providePulsarClient(appConfig))
            .compose(client -> {
                this.pulsarClient = client;
                return Future.all(
                    provideProducer(client, appConfig)
                        .onSuccess(prod -> this.producer = prod)
                        .onFailure(err -> logger.error("Failed to create producer", err)),
                    initializeConsumers(client, appConfig)
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
        List<Future> closeFutures = new ArrayList<>();
        
        if (producer != null) {
            closeFutures.add(closeProducer(producer));
            producer = null;
        }
        
        if (!consumers.isEmpty()) {
            consumers.values().forEach(consumer -> closeFutures.add(closeConsumer(consumer)));
            consumers.clear();
        }
        
        if (pulsarClient != null) {
            closeFutures.add(closePulsarClient(pulsarClient));
            pulsarClient = null;
        }
        
        return CompositeFuture.all(closeFutures).mapEmpty();
    }

    private Future<Void> initializeConsumers(PulsarClient client, AppConfig appConfig) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        Promise<Void> promise = Promise.promise();
        List<Future> consumerFutures = new ArrayList<>();

        for (String topic : slotManager.getManagedTopics()) {
            if (!isValidTopicName(topic)) {
                logger.warn("Invalid topic name: {}", topic);
                continue;
            }
            
            String fullTopicName = buildTopicPath(topic);
            consumerFutures.add(
                provideConsumer(client, appConfig, fullTopicName)
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

    private String buildTopicPath(String topicName) {
        return "persistent://public/default/" + sanitizeTopicName(topicName);
    }

    private String sanitizeTopicName(String topicName) {
        // Remove any invalid characters from topic name
        return topicName.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    private boolean isValidTopicName(String topic) {
        return topic != null && !topic.isEmpty() && TOPIC_PATTERN.matcher(topic).matches();
    }

    private Future<PulsarClient> providePulsarClient(AppConfig appConfig) {
        Promise<PulsarClient> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                String serviceUrl = validateAndSanitizeServiceUrl(appConfig.getPulsar().getUrl());
                
                PulsarClient client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .connectionsPerBroker(Math.max(1, appConfig.getPulsar().getConnectionsPerBroker()))
                    .ioThreads(Math.max(1, appConfig.getPulsar().getNumIoThreads()))
                    .listenerThreads(Math.max(1, appConfig.getPulsar().getNumListenerThreads()))
                    .enableTcpNoDelay(true)
                    .operationTimeout(Math.max(1, appConfig.getPulsar().getOperationTimeout()), TimeUnit.SECONDS)
                    .keepAliveInterval(Math.max(1, appConfig.getPulsar().getKeepAliveInterval()), TimeUnit.SECONDS)
                    .build();
                
                // Verify connection
                client.getPartitionsForTopic("persistent://public/default/dummy-topic")
                    .exceptionally(ex -> {
                        throw new RuntimeException("Failed to verify Pulsar connection", ex);
                    })
                    .thenAccept(partitions -> blockingPromise.complete(client));
            } catch (Exception e) {
                blockingPromise.fail(new RuntimeException("Failed to initialize PulsarClient", e));
            }
        }, false, promise);
        
        return promise.future();
    }

    private String validateAndSanitizeServiceUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Pulsar service URL cannot be null or empty");
        }
        
        // Basic URL sanitization
        String sanitized = url.trim()
            .replaceAll("[\\r\\n]", "") // Remove newlines to prevent CRLF injection
            .replaceAll("\\s+", ""); // Remove whitespace
            
        if (!sanitized.matches("^pulsar://[a-zA-Z0-9.-]+:[0-9]+$")) {
            throw new IllegalArgumentException("Invalid Pulsar service URL format");
        }
        
        return sanitized;
    }

    private Future<Producer<byte[]>> provideProducer(PulsarClient client, AppConfig appConfig) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        Promise<Producer<byte[]>> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                Producer<byte[]> newProducer = client.newProducer()
                    .topic("persistent://public/default/gateway-requests")
                    .sendTimeout(0, TimeUnit.SECONDS)
                    .compressionType(CompressionType.LZ4)
                    .batchingMaxMessages(Math.max(1, appConfig.getPulsar().getBatchingMaxMessagesPerBatch()))
                    .batchingMaxPublishDelay(Math.max(1, appConfig.getPulsar().getBatchingMaxPublishDelay()), TimeUnit.MILLISECONDS)
                    .enableBatching(true)
                    .enableChunking(true)
                    .blockIfQueueFull(true)
                    .maxPendingMessages(backpressureThreshold)
                    .createAsync()
                    .thenApply(prod -> {
                        logger.info("Producer created successfully for topic gateway-requests");
                        return prod;
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException("Failed to create producer", ex);
                    })
                    .get();

                blockingPromise.complete(newProducer);
            } catch (Exception e) {
                blockingPromise.fail(new RuntimeException("Failed to initialize Pulsar Producer", e));
            }
        }, false, promise);
        
        return promise.future();
    }

    private Future<Consumer<byte[]>> provideConsumer(PulsarClient client, AppConfig appConfig, String topic) {
        if (client == null) {
            return Future.failedFuture("Pulsar client is not initialized");
        }

        if (topic == null || topic.isEmpty()) {
            return Future.failedFuture("Topic cannot be null or empty");
        }

        Promise<Consumer<byte[]>> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                DeadLetterPolicy dlPolicy = DeadLetterPolicy.builder()
                    .maxRedeliverCount(Math.max(1, appConfig.getEnv().getReplicasCount()))
                    .deadLetterTopic("persistent://public/default/dead-message-topic")
                    .build();

                Consumer<byte[]> newConsumer = client.newConsumer()
                    .topic(topic)
                    .subscriptionName("ws-sub-" + sanitizeTopicName(topic) + "-" + slotManager.getPodId())
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

        pendingMessages.incrementAndGet();
        vertx.executeBlocking(promise -> {
            try {
                // Check message size limit
                if (message.getData().length > MAX_MESSAGE_SIZE_BYTES) {
                    logger.warn("Message size {} exceeds limit, rejecting", message.getData().length);
                    negativeAcknowledgeSafely(consumer, message);
                    totalMessagesFailed.incrementAndGet();
                    return;
                }

                if (currentRate.incrementAndGet() > rateLimit) {
                    logger.warn("Rate limit exceeded, delaying message processing");
                    Thread.sleep(100);
                }

                String raw = new String(message.getData(), StandardCharsets.UTF_8);
                JsonObject json;
                
                try {
                    json = new JsonObject(raw);
                } catch (Exception e) {
                    logger.warn("Invalid JSON message format: {}", raw);
                    negativeAcknowledgeSafely(consumer, message);
                    totalMessagesFailed.incrementAndGet();
                    return;
                }
                
                String socketId = json.getString("socketid");
                if (!isValidSocketId(socketId)) {
                    logger.warn("Invalid socket ID in message: {}", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    totalMessagesFailed.incrementAndGet();
                    return;
                }

                if (!webSocketManager.hasSession(socketId)) {
                    logger.debug("No active session for socket {}, negative ack", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    totalMessagesFailed.incrementAndGet();
                    return;
                }

                JsonObject payload = json.getJsonObject("message");
                if (payload == null) {
                    logger.warn("Message payload is null for socket {}", socketId);
                    negativeAcknowledgeSafely(consumer, message);
                    totalMessagesFailed.incrementAndGet();
                    return;
                }

                if (backpressureActive) {
                    logger.debug("Backpressure active, delaying message processing");
                    Thread.sleep(50);
                }

                webSocketManager.sendMessage(socketId, payload.encode())
                    .onSuccess(v -> {
                        acknowledgeSafely(consumer, message);
                        totalMessagesProcessed.incrementAndGet();
                    })
                    .onFailure(err -> {
                        logger.warn("Failed to send message to socket {}: {}", socketId, err.getMessage());
                        negativeAcknowledgeSafely(consumer, message);
                        totalMessagesFailed.incrementAndGet();
                        handleError();
                    });
            } catch (Exception e) {
                logger.error("Failed to process message from topic {}: {}", topic, e.getMessage());
                negativeAcknowledgeSafely(consumer, message);
                totalMessagesFailed.incrementAndGet();
                handleError();
                promise.fail(e);
            } finally {
                pendingMessages.decrementAndGet();
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

    @Override
    public Future<MessageResponse> sendToTopic(String socketId, MessageRequest message) {
        if (!isValidSocketId(socketId)) {
            return Future.failedFuture("Invalid socket ID");
        }
        if (message == null) {
            return Future.failedFuture("Message cannot be null");
        }

        if (producer == null || !producer.isConnected()) {
            return Future.failedFuture("Producer is not initialized or disconnected");
        }

        if (!rateLimiter.tryAcquire()) {
            logger.warn("Rate limit exceeded for producer, rejecting message");
            return Future.failedFuture("Rate limit exceeded");
        }

        Promise<MessageResponse> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                ClientCommand clientCommand = new ClientCommand(socketId, message);
                String json = clientCommand.toJson();
                
                // Validate JSON size
                if (json.getBytes(StandardCharsets.UTF_8).length > MAX_MESSAGE_SIZE_BYTES) {
                    rateLimiter.release();
                    blockingPromise.fail("Message size exceeds maximum allowed limit");
                    return;
                }
                
                byte[] payload = json.getBytes(StandardCharsets.UTF_8);

                producer.newMessage()
                    .value(payload)
                    .eventTime(System.currentTimeMillis())
                    .sendAsync()
                    .thenAccept(msgId -> {
                        MessagePointer pointer = new MessagePointer(producer.getTopic(), msgId);
                        MessageResponse response = new MessageResponse("processing", pointer);
                        logger.debug("Message successfully sent to Pulsar topic [gateway-requests] with reqid: {}", message.toString());
                        blockingPromise.complete(response);
                        rateLimiter.release();
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to send message to Pulsar topic [gateway-requests]: {}", ex.getMessage());
                        handleError();
                        blockingPromise.fail(new ProducerFailure("Failed to send message to Pulsar", ex));
                        rateLimiter.release();
                        return null;
                    });
            } catch (Exception e) {
                logger.error("Error preparing message for Pulsar: {}", e.getMessage());
                handleError();
                blockingPromise.fail(new ProducerFailure("Failed to prepare message", e));
                rateLimiter.release();
            }
        }, false, promise);

        return promise.future();
    }

    @Override
    public Future<Void> addConsumerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.assignTopicToUser(userId);
        if (topic == null) {
            return Future.failedFuture("No available topic for user");
        }

        String fullTopicName = buildTopicPath(topic);
        if (consumers.containsKey(topic)) {
            return Future.succeededFuture();
        }

        Promise<Void> promise = Promise.promise();

        provideConsumer(pulsarClient, appConfig, fullTopicName)
            .onSuccess(consumer -> {
                consumers.put(topic, consumer);
                promise.complete();
            })
            .onFailure(err -> {
                logger.error("Failed to add consumer for user {}", userId, err);
                promise.fail(err);
            });

        return promise.future();
    }

    @Override
    public Future<Void> removeConsumerForUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Future.failedFuture("User ID cannot be null or empty");
        }

        String topic = slotManager.getTopicOfUser(userId);
        if (topic == null) {
            return Future.succeededFuture();
        }

        slotManager.removeUser(userId);
        
        if (slotManager.getTopicLoad().getOrDefault(topic, 0) == 0) {
            Consumer<byte[]> consumer = consumers.remove(topic);
            if (consumer != null) {
                return closeConsumer(consumer);
            }
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

    private Future<Void> closeProducer(Producer<byte[]> producer) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            producer.closeAsync()
                .thenRun(() -> {
                    logger.info("Producer closed successfully");
                    blockingPromise.complete();
                })
                .exceptionally(ex -> {
                    logger.error("Failed to close producer: {}", ex.getMessage());
                    blockingPromise.fail(ex);
                    return null;
                });
        }, false, promise);
        
        return promise.future();
    }

    private Future<Void> closePulsarClient(PulsarClient client) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            client.closeAsync()
                .thenRun(() -> {
                    logger.info("Pulsar client closed successfully");
                    blockingPromise.complete();
                })
                .exceptionally(ex -> {
                    logger.error("Failed to close Pulsar client: {}", ex.getMessage());
                    blockingPromise.fail(ex);
                    return null;
                });
        }, false, promise);
        
        return promise.future();
    }

    private synchronized void handleError() {
        if (shuttingDown) return;

        logger.warn("Handling Pulsar connection error...");
        shutdownInternalResources()
            .onSuccess(v -> initializeWithRetry())
            .onFailure(err -> logger.error("Error during recovery process: {}", err.getMessage()));
    }

    @Override
    public synchronized Future<Void> shutdown() {
        if (shuttingDown) {
            return Future.failedFuture("Already shutting down");
        }
        shuttingDown = true;

        if (backlogMonitorTimerId != -1) {
            vertx.cancelTimer(backlogMonitorTimerId);
            backlogMonitorTimerId = -1;
        }

        return shutdownInternalResources()
            .onComplete(ar -> shuttingDown = false);
    }

    private boolean isValidSocketId(String socketId) {
        return socketId != null && SOCKET_ID_PATTERN.matcher(socketId).matches();
    }
}