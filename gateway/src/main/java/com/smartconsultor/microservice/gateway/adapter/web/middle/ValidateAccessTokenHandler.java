package com.smartconsultor.microservice.gateway.adapter.web.middle;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.Handler;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.common.utils.AuthUtils;

public class ValidateAccessTokenHandler implements Handler<RoutingContext> {
    
    private final ValidateAccessTokenUseCase validateAccessTokenUseCase;
    
    @Inject
    public ValidateAccessTokenHandler(ValidateAccessTokenUseCase validateAccessTokenUseCase) {
        this.validateAccessTokenUseCase = validateAccessTokenUseCase;
    }

    @Override
    public void handle(RoutingContext ctx) {
        String accessToken = AuthUtils.getAccessTokenFromContext(ctx);
        
        if (accessToken == null || accessToken.isBlank()) {
            AuthUtils.sendErrorResponse(ctx, 400, "Missing access token");
            return;  
        }

        validateAccessTokenUseCase.validate(accessToken)
            .onSuccess(result -> handleValidateResult(ctx, result))
            .onFailure(err -> AuthUtils.handleError(ctx, err, "validate access token", accessToken, ""));
    }

    private void handleValidateResult(RoutingContext ctx, Result<Boolean> result) {
        Result.foldVoid(result,
            isValid -> {
                if (isValid) {
                    ctx.put("isValidToken", true);
                    ctx.next();
                } else {
                    AuthUtils.sendErrorResponse(ctx, 400, "Access token is invalid");
                }
            },
            failure -> AuthUtils.handleFailure(ctx, failure)
        );
    }
}