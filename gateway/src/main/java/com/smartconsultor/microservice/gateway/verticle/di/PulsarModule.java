package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.BacklogManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarClientFactory;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarConsumerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarProducerManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.impl.PulsarServiceImpl;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import javax.inject.Singleton;

@Module
public class PulsarModule {

    @Provides
    @Singleton
    public PulsarClientFactory providePulsarClientFactory(Vertx vertx) {
        return new PulsarClientFactory(vertx);
    }

    @Provides
    @Singleton
    public PulsarProducerManager providePulsarProducerManager(Vertx vertx, AppConfig appConfig) {
        return new PulsarProducerManager(vertx, appConfig);
    }

    @Provides
    @Singleton
    public PulsarConsumerManager providePulsarConsumerManager(Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager) {
        return new PulsarConsumerManager(vertx, appConfig, webSocketManager);
    }

    @Provides
    @Singleton
    public BacklogManager provideBacklogManager(Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager) {
        return new BacklogManager(vertx, appConfig);
    }

    @Provides
    @Singleton
    public PulsarService providePulsarService(Vertx vertx, AppConfig appConfig, SlotManager slotManager,
                                             PulsarClientFactory pulsarClientFactory, PulsarProducerManager pulsarProducerManager,
                                             PulsarConsumerManager pulsarConsumerManager, BacklogManager backlogManager) {
        return new PulsarServiceImpl(vertx, appConfig, slotManager, pulsarClientFactory, pulsarProducerManager,
                                     pulsarConsumerManager, backlogManager);
    }
}