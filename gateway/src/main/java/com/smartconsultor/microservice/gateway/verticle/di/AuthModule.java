package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.impl.AuthRemoteDataSourceImpl;
import com.smartconsultor.microservice.gateway.infrastructure.repositories.AuthRepositoryImpl;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import javax.inject.Singleton;

@Module
public class AuthModule {

    @Provides
    @Singleton
    public AuthRemoteDataSource provideAuthRemoteDataSource(Vertx vertx, WebClient webClient, AppConfig appConfig) {
        return new AuthRemoteDataSourceImpl(vertx, webClient, appConfig);
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(AuthRemoteDataSource authRemoteDataSource) {
        return new AuthRepositoryImpl(authRemoteDataSource);
    }

    @Provides
    @Singleton
    public ExchangeCodeUseCase provideExchangeCodeUseCase(AuthRepository authRepository) {
        return new ExchangeCodeUseCase(authRepository);
    }

    @Provides
    @Singleton
    public RefreshUseCase provideRefreshUseCase(AuthRepository authRepository) {
        return new RefreshUseCase(authRepository);
    }

    @Provides
    @Singleton
    public LogoutUseCase provideLogoutUseCase(AuthRepository authRepository) {
        return new LogoutUseCase(authRepository);
    }

    @Provides
    @Singleton
    public ValidateAccessTokenUseCase provideValidateAccessTokenUseCase(AuthRepository authRepository) {
        return new ValidateAccessTokenUseCase(authRepository);
    }

    @Provides
    @Singleton
    public AuthHandler provideAuthHandler(ExchangeCodeUseCase exchangeCodeUseCase, RefreshUseCase refreshUseCase, LogoutUseCase logoutUseCase) {
        return new AuthHandler(exchangeCodeUseCase, refreshUseCase, logoutUseCase);
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