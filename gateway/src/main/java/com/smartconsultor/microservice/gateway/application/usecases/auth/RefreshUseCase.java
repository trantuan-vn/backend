package com.smartconsultor.microservice.gateway.application.usecases.auth;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;

import io.vertx.core.Future;

public class RefreshUseCase {
    private final AuthRepository repository;
    @Inject
    public RefreshUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public Future<Result<AuthTokens>> refresh(String refreshToken) {
        return repository.refresh(refreshToken);
    }
}
