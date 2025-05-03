package com.smartconsultor.microservice.gateway.adapter.web.handler;

import javax.inject.Inject;
import com.smartconsultor.microservice.gateway.adapter.web.dto.AuthCodeRequest;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject; 

// Lớp AuthHandler sẽ xử lý các yêu cầu liên quan đến xác thực (login, refresh token, logout).
public class AuthHandler {
    
    private final ExchangeCodeUseCase exchangeCodeUseCase;
    private final RefreshUseCase refreshUseCase;
    private final LogoutUseCase logoutUseCase;
    
    @Inject
    public AuthHandler(ExchangeCodeUseCase exchangeCodeUseCase, RefreshUseCase refreshUseCase, LogoutUseCase logoutUseCase) {
        this.exchangeCodeUseCase = exchangeCodeUseCase;
        this.refreshUseCase = refreshUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    // 1. Trao đổi token dựa trên code
    public void exchangeCode(RoutingContext ctx) {
        AuthCodeRequest request = ctx.body()
            .asJsonObject()
            .mapTo(AuthCodeRequest.class);

        exchangeCodeUseCase.exchangeCode(request.code, request.redirectUri)
            .onSuccess(result -> handleExchangeResult(ctx, result, request))
            .onFailure(err -> AuthUtils.handleError(ctx, err, "exchange code", request.code, request.redirectUri));
    }

    private void handleExchangeResult(RoutingContext ctx, Result<AuthTokens> result, AuthCodeRequest request) {
        Result.foldVoid(result,
            tokens -> {
                AuthUtils.setAuthCookies(ctx, tokens.getAccessToken(), tokens.getExpiresIn(), tokens.getRefreshToken(), tokens.getRefreshExpiresIn());
                ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(tokens).encode());
            },
            failure -> AuthUtils.handleFailure(ctx, failure)
        );
    }

    public void refresh(RoutingContext ctx) {
        String refreshToken = AuthUtils.getRefreshTokenFromContext(ctx);

        if (refreshToken == null || refreshToken.isBlank()) {
            AuthUtils.sendErrorResponse(ctx, 400, "Missing refresh token");
            return;
        }

        refreshUseCase.refresh(refreshToken)
            .onSuccess(result -> handleRefreshResult(ctx, result))
            .onFailure(err -> AuthUtils.handleError(ctx, err, "refresh token", refreshToken, ""));
    }

    private void handleRefreshResult(RoutingContext ctx, Result<AuthTokens> result) {
        Result.foldVoid(result,
            tokens -> {
                AuthUtils.setAuthCookies(ctx, tokens.getAccessToken(), tokens.getExpiresIn(), tokens.getRefreshToken(), tokens.getRefreshExpiresIn());
                ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(tokens).encode());
            },
            failure -> AuthUtils.handleFailure(ctx, failure)
        );
    }

    public void logout(RoutingContext ctx) {
        String refreshToken = AuthUtils.getRefreshTokenFromContext(ctx);

        if (refreshToken == null || refreshToken.isBlank()) {
            AuthUtils.sendErrorResponse(ctx, 400, "Missing refresh token");
            return;
        }

        logoutUseCase.logout(refreshToken)
            .onSuccess(result -> handleLogoutResult(ctx, result))
            .onFailure(err -> AuthUtils.handleError(ctx, err, "logout", refreshToken, ""));
    }

    private void handleLogoutResult(RoutingContext ctx, Result<Boolean> result) {
        Result.foldVoid(result,
            success -> {
                AuthUtils.setAuthCookies(ctx, "", 0, "", 0);
                ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("success", success).encode());
            },
            failure -> AuthUtils.handleFailure(ctx, failure)
        );
    }
}