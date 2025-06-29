package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.adapter.dto.gateway.GatewayMessage;

import io.vertx.core.Future;

public interface PulsarService {
    public Future<GatewayMessage> sendToTopic(GatewayMessage message);
    public Future<Void> shutdown();
    public Future<Void> addConsumerForUser(String userId);
    public Future<Void> removeConsumerForUser(String userId);
    public Future<Void> addProducerForUser(String userId);
    public Future<Void> removeProducerForUser(String userId);
}
