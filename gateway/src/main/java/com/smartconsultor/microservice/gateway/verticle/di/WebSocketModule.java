package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.websocket.GatewayDispatcher;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.WebSocketManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.websocket.impl.WebSocketManagerImpl;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import javax.inject.Singleton;

@Module
public class WebSocketModule {

    @Provides
    @Singleton
    public WebSocketManager provideWebSocketManager(Vertx vertx) {
        return new WebSocketManagerImpl(vertx);
    }

    @Provides
    @Singleton
    public GatewayDispatcher provideGatewayDispatcher() {
        return GatewayDispatcher.create();
    }

    @Provides
    @Singleton
    public WebSocketHandler provideWebSocketHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase,
                                                   WebSocketManager webSocketManager,
                                                   GatewayDispatcher gatewayDispatcher) {
        return new WebSocketHandler(validateAccessTokenUseCase, webSocketManager, gatewayDispatcher);
    }
}