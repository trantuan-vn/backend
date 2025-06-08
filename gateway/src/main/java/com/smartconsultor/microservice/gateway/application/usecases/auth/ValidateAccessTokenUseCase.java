package com.smartconsultor.microservice.gateway.application.usecases.auth;

import javax.inject.Inject;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;

import io.vertx.core.Future;

public class ValidateAccessTokenUseCase {

    private final AuthRepository repository;

    @Inject
    public ValidateAccessTokenUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public Future<Result<Boolean>> validate(String accessToken) {
        //return repository.validateAccessToken(accessToken);
        return repository.validateAccessTokenWithJwt(accessToken);
    }
}
