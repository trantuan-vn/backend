package com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.common.error.AuthFailure;
import com.smartconsultor.microservice.gateway.common.error.NetworkFailure;
import com.smartconsultor.microservice.gateway.domain.model.AuthTokens;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {

    private final WebClient webClient;
    private final AppConfig appConfig;

    @Inject
    public AuthRemoteDataSourceImpl(WebClient webClient, AppConfig appConfig) {
        this.webClient = webClient;
        this.appConfig=appConfig;
    }

    @Override
    public Future<AuthTokens> exchangeCode(String code, String redirectUri) {
        JsonObject form = new JsonObject()
            .put("grant_type", "authorization_code")
            .put("client_id", appConfig.getKeycloak().getResource())
            .put("client_secret", appConfig.getKeycloak().getCredentials().getSecret())
            .put("code", code)
            .put("redirect_uri", redirectUri);

        return sendTokenRequest(form);
    }

    @Override
    public Future<AuthTokens> refreshToken(String refreshToken) {
        JsonObject form = new JsonObject()
            .put("grant_type", "refresh_token")
            .put("client_id", appConfig.getKeycloak().getResource())
            .put("client_secret", appConfig.getKeycloak().getCredentials().getSecret())
            .put("refresh_token", refreshToken);

        return sendTokenRequest(form);
    }

    @Override 
    public Future<Boolean> logout(String refreshToken) {
        JsonObject form = new JsonObject()
            .put("client_id", appConfig.getKeycloak().getResource())
            .put("client_secret", appConfig.getKeycloak().getCredentials().getSecret())
            .put("refresh_token", refreshToken);

        return webClient.post(appConfig.getKeycloak().getSiteUrl() + "/protocol/openid-connect/logout")
            .sendJsonObject(form)
            .map(response -> response.statusCode() == 204)
            .recover(err -> {
                if (err instanceof java.io.IOException) {
                    return Future.failedFuture(new NetworkFailure(503, "Network failure or connection issues", null));
                } else if (err instanceof java.util.concurrent.TimeoutException) {
                    return Future.failedFuture(new NetworkFailure(408, "Request Timeout",null));
                } else {
                    return Future.failedFuture(err);  // Các lỗi khác sẽ được xử lý tại đây
                }                                
            });
    }

    private Future<AuthTokens> sendTokenRequest(JsonObject form) {
        Promise<AuthTokens> promise = Promise.promise();

        webClient.post(appConfig.getKeycloak().getSiteUrl() + "/protocol/openid-connect/token")
            .sendJsonObject(form)
            .onSuccess(response -> {
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    promise.complete(response.bodyAsJson(AuthTokens.class));
                } else {
                    promise.fail(new AuthFailure(response.statusCode(), response.bodyAsString(), null));
                }
            })
            .onFailure(err -> {
                // Kiểm tra lỗi kết nối mạng
                if (err instanceof java.io.IOException) {
                    promise.fail(new NetworkFailure(503, "Network failure or connection issues", null));
                } else if (err instanceof java.util.concurrent.TimeoutException) {
                    promise.fail(new NetworkFailure(408, "Request Timeout",null));
                } else {
                    promise.fail(err);  // Các lỗi khác sẽ được xử lý tại đây
                }
            });

        return promise.future();
    }
    
    @Override
    public Future<Boolean> validateAccessToken(String accessToken) {
        JsonObject form = new JsonObject()
            .put("client_id", appConfig.getKeycloak().getResource())
            .put("client_secret", appConfig.getKeycloak().getCredentials().getSecret())
            .put("token", accessToken);
    
        return webClient.post(appConfig.getKeycloak().getSiteUrl() + "/protocol/openid-connect/token/introspect")
            .sendJsonObject(form)
            .map(response -> {
                if (response.statusCode() == 200) {
                    JsonObject body = response.bodyAsJsonObject();
                    return body.getBoolean("active", false); // Kiểm tra xem token có hợp lệ không
                } else {
                    return false;
                }
            })
            .recover(err -> {
                // Xử lý các lỗi mạng như trong các phương thức trước
                if (err instanceof java.io.IOException) {
                    return Future.failedFuture(new NetworkFailure(503, "Network failure or connection issues", null));
                } else if (err instanceof java.util.concurrent.TimeoutException) {
                    return Future.failedFuture(new NetworkFailure(408, "Request Timeout", null));
                } else {
                    return Future.failedFuture(err); // Các lỗi khác
                }
            });
    }    
}
