package com.smartconsultor.microservice.gateway.domain.repository;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;

import io.vertx.core.Future;

public interface AuthRepository {
    Future<Result<AuthTokens>> exchangeCode(String code, String redirectUri);
    Future<Result<AuthTokens>> refresh(String refreshToken);
    Future<Result<Boolean>> logout(String refreshToken);
    Future<Result<Boolean>> validateAccessToken(String accessToken);
    Future<Result<Boolean>> validateAccessTokenWithJwt(String accessToken);
}
