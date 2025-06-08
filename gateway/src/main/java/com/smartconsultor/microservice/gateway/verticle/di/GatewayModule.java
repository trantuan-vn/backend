package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.GeoIPService;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.pulsar.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import dagger.Module;
import dagger.Provides;
import io.vertx.ext.web.client.WebClient;

import javax.inject.Singleton;

@Module
public class GatewayModule {
    @Provides
    @Singleton
    public GatewayVerticle provideGatewayVerticle(
        AuthRouter authRouter,
        WebSocketHandler webSocketHandler,
        AppConfig appConfig,
        WebClient webClient,
        PulsarService pulsarService,
        WebSocketManager webSocketManager,
        SlotManager slotManager,
        SessionStore sessionStore,
        GeoIPService geoIPService) {
        return new GatewayVerticle(
            authRouter,
            webSocketHandler,
            appConfig,
            webClient,
            pulsarService,
            webSocketManager,
            slotManager,
            sessionStore,
            geoIPService
        );
    }
}