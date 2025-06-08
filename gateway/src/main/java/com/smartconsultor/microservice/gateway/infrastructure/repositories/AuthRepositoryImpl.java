package com.smartconsultor.microservice.gateway.infrastructure.repositories;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.common.error.Failure;
import com.smartconsultor.microservice.gateway.common.error.Result;
import com.smartconsultor.microservice.gateway.common.error.UnexpectedFailure;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;

import io.vertx.core.Future;

public class AuthRepositoryImpl implements AuthRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuthRepositoryImpl.class);

    private final AuthRemoteDataSource remoteDataSource;

    @Inject
    public AuthRepositoryImpl(AuthRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public Future<Result<AuthTokens>> exchangeCode(String code, String redirectUri) {
        return remoteDataSource.exchangeCode(code, redirectUri)
            .map(tokens -> Result.success(tokens))
            .recover(err -> {
                logger.error("Failed to exchange code [{}] with redirectUri [{}]: {}", code, redirectUri, err.getMessage(), err);
                Failure failure;
                if (err instanceof Failure f) {
                    failure = f; // nếu đã là Failure thì dùng luôn
                } else {
                    failure = new UnexpectedFailure(500, "Unexpected error when exchanging code", err);
                }
                return Future.succeededFuture(Result.failure(failure));
            });
    }


    @Override
    public Future<Result<AuthTokens>> refresh(String refreshToken) {
        return remoteDataSource.refreshToken(refreshToken)
            .map(tokens -> Result.success(tokens))
            .recover(err -> {
                logger.error("Failed to refresh token [{}]: {}", refreshToken, err.getMessage(), err);
                Failure failure;
                if (err instanceof Failure f) {
                    failure = f; // nếu đã là Failure thì dùng luôn
                } else {
                    failure = new UnexpectedFailure(500, "Unexpected error when exchanging code", err);
                }
                return Future.succeededFuture(Result.failure(failure));
            });
    }

    @Override
    public Future<Result<Boolean>> logout(String refreshToken) {
        return remoteDataSource.logout(refreshToken)
            .map(success -> Result.success(success))
            .recover(err -> {
                logger.error("Failed to logout with refresh token [{}]: {}", refreshToken, err.getMessage(), err);
                Failure failure;
                if (err instanceof Failure f) {
                    failure = f; // nếu đã là Failure thì dùng luôn
                } else {
                    failure = new UnexpectedFailure(500, "Unexpected error when exchanging code", err);
                }
                return Future.succeededFuture(Result.failure(failure));
            });
    }
    @Override
    public Future<Result<Boolean>> validateAccessToken(String accessToken) {
        return remoteDataSource.validateAccessToken(accessToken)
            .map(isValid -> Result.success(isValid)) 
            .recover(err -> {
                logger.error("Failed to validate access token [{}]: {}", accessToken, err.getMessage(), err);
                Failure failure;
                if (err instanceof Failure f) {
                    failure = f; // nếu đã là Failure thì dùng luôn
                } else {
                    failure = new UnexpectedFailure(500, "Unexpected error when validating access token", err);
                }
                return Future.succeededFuture(Result.failure(failure));
            });
    }

    @Override
    public Future<Result<Boolean>> validateAccessTokenWithJwt(String accessToken) {
        return remoteDataSource.validateAccessTokenWithJwt(accessToken)
            .map(isValid -> Result.success(isValid)) 
            .recover(err -> {
                logger.error("Failed to validate access token [{}]: {}", accessToken, err.getMessage(), err);
                Failure failure;
                if (err instanceof Failure f) {
                    failure = f; // nếu đã là Failure thì dùng luôn
                } else {
                    failure = new UnexpectedFailure(500, "Unexpected error when validating access token", err);
                }
                return Future.succeededFuture(Result.failure(failure));
            });
    }    
}
