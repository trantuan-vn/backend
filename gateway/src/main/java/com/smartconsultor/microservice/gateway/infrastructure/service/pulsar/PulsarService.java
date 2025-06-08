package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageResponse;
import io.vertx.core.Future;

public interface PulsarService {
    public Future<MessageResponse> sendToTopic(String userId, String socketId, MessageRequest message);
    public Future<Void> shutdown();
    public Future<Void> addConsumerForUser(String userId);
    public Future<Void> removeConsumerForUser(String userId);
    public Future<Void> addProducerForUser(String userId);
    public Future<Void> removeProducerForUser(String userId);
}
