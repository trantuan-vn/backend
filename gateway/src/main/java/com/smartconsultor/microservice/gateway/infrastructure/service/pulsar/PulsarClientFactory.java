package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.apache.pulsar.client.api.PulsarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PulsarClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(PulsarClientFactory.class);
    private final Vertx vertx;

    @Inject
    public PulsarClientFactory(Vertx vertx) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
    }

    public Future<PulsarClient> providePulsarClient(AppConfig appConfig) {
        Promise<PulsarClient> promise = Promise.promise();
        vertx.executeBlocking(blockingPromise -> {
            try {
                String serviceUrl = TopicUtils.validateAndSanitizeServiceUrl(appConfig.getPulsar().getUrl());

                PulsarClient client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .connectionsPerBroker(Math.max(1, appConfig.getPulsar().getConnectionsPerBroker()))
                    .ioThreads(Math.max(1, appConfig.getPulsar().getNumIoThreads()))
                    .listenerThreads(Math.max(1, appConfig.getPulsar().getNumListenerThreads()))
                    .enableTcpNoDelay(true)
                    .operationTimeout(Math.max(1, appConfig.getPulsar().getOperationTimeout()), TimeUnit.SECONDS)
                    .keepAliveInterval(Math.max(1, appConfig.getPulsar().getKeepAliveInterval()), TimeUnit.SECONDS)
                    .build();

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

    public Future<Void> closePulsarClient(PulsarClient client) {
        Promise<Void> promise = Promise.promise();
        if (client == null) {
            return Future.succeededFuture();
        }
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
}