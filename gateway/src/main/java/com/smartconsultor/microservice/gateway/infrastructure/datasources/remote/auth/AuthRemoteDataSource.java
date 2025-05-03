package com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth;

import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;

import io.vertx.core.Future;

public interface AuthRemoteDataSource {
    Future<AuthTokens> exchangeCode(String code, String redirectUri);
    Future<AuthTokens> refreshToken(String refreshToken);
    Future<Boolean> logout(String refreshToken);
    Future<Boolean> validateAccessToken(String accessToken);
}