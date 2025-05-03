package com.smartconsultor.microservice.gateway.application.usecases.auth;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;

import io.vertx.core.Future;

public class LogoutUseCase {
    private final AuthRepository repository;
    @Inject
    public LogoutUseCase(AuthRepository repository) {
        this.repository = repository;
    }
    
    public Future<Result<Boolean>> logout(String refreshToken) {
        return repository.logout(refreshToken);
    }    
}
