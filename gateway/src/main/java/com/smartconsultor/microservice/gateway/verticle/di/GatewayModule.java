package com.smartconsultor.microservice.gateway.verticle.di;

import javax.inject.Singleton;

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
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSourceImpl;
import com.smartconsultor.microservice.gateway.infrastructure.repositories.AuthRepositoryImpl;
import com.smartconsultor.microservice.gateway.infrastructure.service.PulsarService;
import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.infrastructure.service.impl.PulsarServiceImpl;
import com.smartconsultor.microservice.gateway.infrastructure.service.impl.SlotManagerImpl;
import com.smartconsultor.microservice.gateway.infrastructure.service.impl.WebSocketManagerImpl;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import dagger.Module;

@Module
public class GatewayModule {
    
    @Provides
    @Singleton
    public Vertx provideVertx() {
        VertxOptions options = new VertxOptions()
            .setWorkerPoolSize(20)  // Cấu hình số lượng worker threads
            .setEventLoopPoolSize(4)  // Cấu hình số lượng event loop threads        
            .setPreferNativeTransport(true);
        return Vertx.vertx(options);
    }

    @Provides
    @Singleton
    public AppConfig provideAppConfig() {
        return AppConfig.load("config/app-config.json");
    }

    @Provides
    @Singleton
    public WebSocketManager provideWebSocketManager(Vertx vertx) {
        return new WebSocketManagerImpl(vertx);
    }    

    @Provides
    @Singleton
    public SlotManager provideSlotManager(AppConfig appConfig) {
        return new SlotManagerImpl(appConfig);
    }  

    @Provides
    @Singleton
    public PulsarService providePulsarService(Vertx vertx, AppConfig appConfig, WebSocketManager webSocketManager, SlotManager slotManager) {
        return new PulsarServiceImpl(vertx, appConfig, webSocketManager, slotManager);
    }    

    @Provides
    @Singleton
    public WebClient provideWebClient(Vertx vertx) {
        return WebClient.create(vertx, new WebClientOptions()
            .setUserAgent("SmartConsultor/1.0") 
            .setKeepAlive(true)
            .setConnectTimeout(3000)
            .setMaxPoolSize(100));
    }

    @Provides
    @Singleton
    public WebSocketHandler provideWebSocketHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase,
                            PulsarService pulsarService,
                            WebSocketManager webSocketManager) {
        return new WebSocketHandler(validateAccessTokenUseCase,pulsarService,webSocketManager);
    }    

    @Singleton
    @Provides
    public AuthRemoteDataSource provideAuthRemoteDataSource(WebClient webClient, AppConfig appConfig) {
        return new AuthRemoteDataSourceImpl(webClient, appConfig); 
    }

    @Singleton
    @Provides
    public AuthRepository provideAuthRepository(AuthRemoteDataSource authRemoteDataSource) {
        return new AuthRepositoryImpl(authRemoteDataSource); 
    }

    @Singleton
    @Provides
    public ExchangeCodeUseCase provideExchangeCodeUseCase(AuthRepository authRepository) {
        return new ExchangeCodeUseCase(authRepository);
    }
    
    @Singleton
    @Provides
    public RefreshUseCase provideRefreshUseCase(AuthRepository authRepository) {
        return new RefreshUseCase(authRepository);
    }    

    @Singleton
    @Provides
    public LogoutUseCase provideLogoutUseCase(AuthRepository authRepository) {
        return new LogoutUseCase(authRepository);
    }

    @Singleton
    @Provides
    public ValidateAccessTokenUseCase provideValidateAccessTokenUseCase(AuthRepository authRepository) {
        return new ValidateAccessTokenUseCase(authRepository);
    }


    @Provides
    @Singleton
    public AuthHandler provideAuthHandler(ExchangeCodeUseCase exchangeCodeUseCase, RefreshUseCase refreshUseCase, LogoutUseCase logoutUseCase) {
        return new AuthHandler(exchangeCodeUseCase,refreshUseCase,logoutUseCase);
    }

    @Provides
    @Singleton
    public ValidateAccessTokenHandler provideValidateAccessTokenHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase) {
        return new ValidateAccessTokenHandler(validateAccessTokenUseCase);
    }    

    @Provides
    @Singleton
    public AuthRouter provideAuthRouter(Vertx vertx, AuthHandler authHandler) {
        return new AuthRouter(vertx, authHandler);
    }        
}
