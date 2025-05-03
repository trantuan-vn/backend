package com.smartconsultor.microservice.gateway.application.usecases.auth;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;

import io.vertx.core.Future;

public class ExchangeCodeUseCase {
    private final AuthRepository repository;
    @Inject
    public ExchangeCodeUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public Future<Result<AuthTokens>> exchangeCode(String code, String redirectUri) {
        return repository.exchangeCode(code, redirectUri);
    }
}
