package com.smartconsultor.microservice.gateway.verticle.di;

import dagger.Component;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import javax.inject.Singleton;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

@Singleton
@Component(modules = GatewayModule.class)
public interface GatewayComponent {
    // Vertx
    void inject(Vertx vertx);
    // Vertical
    void inject(GatewayVerticle gatewayVerticle);
    // Appconfig
    void inject(AppConfig appConfig);
    // Webclient
    void inject(WebClient webClient);
    // WebSocketManager
    void inject(WebSocketManager webSocketManager);
    // WebSocketHandler
    void inject(WebSocketHandler webSocketHandler);
    // infra for all websocket function
    void inject(PulsarService pulsarService);
    
    // AuthRouter
    void inject(AuthRouter authRouter);
    // AuthHandler
    void inject(AuthHandler authHandler);    
    // Usecases for basic Auth
    void inject(ExchangeCodeUseCase exchangeCodeUseCase);
    void inject(RefreshUseCase refreshUseCase);
    void inject(LogoutUseCase logoutUseCase);
    // middle handler for validate access token
    void inject(ValidateAccessTokenHandler validateAccessTokenHandler);
    // middle usecase for validate access token
    void inject(ValidateAccessTokenUseCase validateAccessTokenUseCase);    
    // infra for all auth function
    void inject(AuthRemoteDataSource authRemoteDataSource);    
    void inject(AuthRepository authRepository); 

    // --- Getter Methods (constructor or method access) ---
    Vertx vertx();
    WebClient webClient();
    PulsarService pulsarService();
    WebSocketManager webSocketManager();
    AuthRouter authRouter();
    AppConfig appConfig();
    WebSocketHandler webSocketHandler();
    AuthHandler authHandler();
    ExchangeCodeUseCase exchangeCodeUseCase();
    RefreshUseCase refreshUseCase();
    LogoutUseCase logoutUseCase();
    ValidateAccessTokenHandler validateAccessTokenHandler();
    ValidateAccessTokenUseCase validateAccessTokenUseCase();
    AuthRemoteDataSource authRemoteDataSource();
    AuthRepository authRepository();
}    
