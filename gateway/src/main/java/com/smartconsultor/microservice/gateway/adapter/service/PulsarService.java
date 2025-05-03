package com.smartconsultor.microservice.gateway.adapter.service;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pulsar.client.admin.PulsarAdmin;

import com.smartconsultor.microservice.gateway.common.error.MessageFailure;
import com.smartconsultor.microservice.gateway.common.error.ProducerFailure;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class PulsarService {
    private final Logger logger = LoggerFactory.getLogger(PulsarService.class);
    private PulsarClient pulsarClient;
    private Producer<byte[]> producer;
    private Consumer<byte[]> consumer;
    private final WebSocketManager webSocketManager;

    @Inject
    public PulsarService(AppConfig appConfig, WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;

        // Khởi tạo PulsarClient, Producer, và Consumer trong phương thức init()
        init(appConfig).onFailure(err -> {
            logger.error("Failed to initialize PulsarService: {}", err.getMessage());
        });
    }

    // Hàm khởi tạo bất đồng bộ
    public Future<Void> init(AppConfig appConfig) {
        Promise<Void> promise = Promise.promise();
        providePulsarClient(appConfig).onSuccess(client -> {
            this.pulsarClient = client;
            provideProducer(pulsarClient, appConfig).onSuccess(prod -> {
                this.producer = prod;
                provideConsumer(pulsarClient, appConfig).onSuccess(cons -> {
                    this.consumer = cons;
                    promise.complete();  // Hoàn thành khởi tạo
                }).onFailure(promise::fail);
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);

        return promise.future();
    }

    // Cung cấp PulsarClient
    private Future<PulsarClient> providePulsarClient(AppConfig appConfig) {
        Promise<PulsarClient> promise = Promise.promise();
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(appConfig.getPulsar().getUrl())
                    .connectionsPerBroker(appConfig.getPulsar().getConnectionsPerBroker())
                    .ioThreads(appConfig.getPulsar().getNumIoThreads())
                    .listenerThreads(appConfig.getPulsar().getNumListenerThreads())
                    .enableTcpNoDelay(true)
                    .operationTimeout(appConfig.getPulsar().getOperationTimeout(), TimeUnit.SECONDS)
                    .keepAliveInterval(appConfig.getPulsar().getKeepAliveInterval(), TimeUnit.SECONDS)
                    .build();
            promise.complete(client);
        } catch (PulsarClientException e) {
            promise.fail(new RuntimeException("Failed to initialize PulsarClient", e));
        }
        return promise.future();
    }

    // Cung cấp Producer
    private Future<Producer<byte[]>> provideProducer(PulsarClient pulsarClient, AppConfig appConfig) {
        Promise<Producer<byte[]>> promise = Promise.promise();
        try {
            String podName = System.getenv("HOSTNAME");
            if (podName == null || podName.isEmpty()) {
                promise.fail(new RuntimeException("Pod name is not available in the environment."));
                return promise.future();
            }

            String topic = "gateway-requests-" + podName;
            PulsarAdmin pulsarAdmin = PulsarAdmin.builder()
                    .serviceHttpUrl(appConfig.getPulsar().getAdminUrl())
                    .build();
            if (!pulsarAdmin.topics().getList("public/default").contains(topic)) {
                pulsarAdmin.topics().createNonPartitionedTopic(topic);
            }

            Producer<byte[]> newProducer = pulsarClient.newProducer()
                    .topic(topic)
                    .sendTimeout(0, TimeUnit.SECONDS)
                    .compressionType(CompressionType.LZ4)
                    .batchingMaxMessages(appConfig.getPulsar().getBatchingMaxMessagesPerBatch())
                    .batchingMaxPublishDelay(appConfig.getPulsar().getBatchingMaxPublishDelay(), TimeUnit.MILLISECONDS)
                    .create();

            promise.complete(newProducer);
        } catch (Exception e) {
            promise.fail(new RuntimeException("Failed to initialize Pulsar Producer", e));
        }
        return promise.future();
    }

    // Cung cấp Consumer với DeadLetterPolicy
    private Future<Consumer<byte[]>> provideConsumer(PulsarClient pulsarClient, AppConfig appConfig) {
        Promise<Consumer<byte[]>> promise = Promise.promise();
        try {
            String podName = System.getenv("HOSTNAME");
            if (podName == null || podName.isEmpty()) {
                promise.fail(new RuntimeException("Pod name is not available in the environment."));
                return promise.future();
            }

            String topic = "gateway-responses-" + podName;
            String deadLetterTopic = "dead-message-topic-" + podName;

            DeadLetterPolicy dlPolicy = DeadLetterPolicy.builder()
                    .maxRedeliverCount(3)
                    .deadLetterTopic(deadLetterTopic)
                    .build();

            Consumer<byte[]> newConsumer = pulsarClient.newConsumer()
                    .topic(topic)
                    .subscriptionName("ws-sub")
                    .subscriptionType(SubscriptionType.Shared)
                    .receiverQueueSize(appConfig.getPulsar().getReceiverQueueSize())
                    .messageListener(this::handleMessage)
                    .enableRetry(true)
                    .deadLetterPolicy(dlPolicy)
                    .subscribe();

            promise.complete(newConsumer);
        } catch (Exception e) {
            promise.fail(new RuntimeException("Failed to initialize Pulsar Consumer", e));
        }
        return promise.future();
    }

    // Hàm xử lý tin nhắn (messageListener)
    private void handleMessage(Consumer<byte[]> consumer, Message<byte[]> message) {
        try {
            String raw = new String(message.getData(), StandardCharsets.UTF_8);
            JsonObject json = new JsonObject(raw);

            String socketId = json.getString("socketid");
            JsonObject payload = json.getJsonObject("message");
            webSocketManager.sendMessage(socketId, payload.encode());
            consumer.acknowledge(message);
        } catch (Exception e) {
            consumer.negativeAcknowledge(message);
            logger.error("Failed to process message: {}" , e.getMessage());
        }
    }

    // Hàm gửi message vào topic
    public Future<Boolean> sendToTopic(String socketid, JsonObject message) {
        Promise<Boolean> promise = Promise.promise();
        byte[] payload;
        try {
            String topicName = producer.getTopic();  // Lấy tên topic từ producer
    
            JsonObject payloadJson = new JsonObject()
                .put("socketid", socketid)
                .put("topic", topicName)
                .put("message", message);
    
            payload = payloadJson.encode().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to encode message to JSON for topic [gateway_requests]: {}", e.getMessage(), e);
            return Future.failedFuture(new MessageFailure("Failed to encode message to JSON", e));
        }

        producer.newMessage()
            .value(payload)
            .sendAsync()
            .thenAccept(msgId -> {
                logger.info("Message successfully sent to Pulsar topic [gateway_requests]: {}", message.encode());
                promise.complete(true);
            }).exceptionally(ex -> {
                logger.error("Failed to send message to Pulsar topic [gateway_requests]: {}", ex.getMessage(), ex);
                promise.fail(new ProducerFailure("Failed to send message to Pulsar", ex));
                return null;
            });

        return promise.future();    
    }
    // Phương thức shutdown bất đồng bộ
    public Future<Void> shutdown() {
        Promise<Void> promise = Promise.promise();

        try {
            logger.info("Shutting down Pulsar resources...");
            
            // Đóng consumer
            if (consumer != null) {
                consumer.closeAsync().thenAccept(v -> {
                    logger.info("Consumer closed.");
                }).exceptionally(ex -> {
                    logger.error("Error closing consumer: {}", ex.getMessage(), ex);
                    return null;
                });
            }
            
            // Đóng producer
            if (producer != null) {
                producer.closeAsync().thenAccept(v -> {
                    logger.info("Producer closed.");
                }).exceptionally(ex -> {
                    logger.error("Error closing producer: {}", ex.getMessage(), ex);
                    return null;
                });
            }

            // Đóng pulsarClient
            if (pulsarClient != null) {
                pulsarClient.closeAsync().thenAccept(v -> {
                    logger.info("PulsarClient closed.");
                    promise.complete(); // Hoàn tất shutdown khi tất cả đã được đóng
                }).exceptionally(ex -> {
                    logger.error("Error closing PulsarClient: {}", ex.getMessage(), ex);
                    promise.fail(ex); // Nếu có lỗi trong quá trình đóng, fail promise
                    return null;
                });
            } else {
                // Nếu pulsarClient không được khởi tạo, hoàn tất shutdown ngay lập tức
                promise.complete();
            }
        } catch (Exception ex) {
            logger.error("Error during shutdown: {}", ex.getMessage(), ex);
            promise.fail(ex); // Fail promise nếu có lỗi ngoại lệ xảy ra trong quá trình shutdown
        }
        return promise.future();
    }
}
