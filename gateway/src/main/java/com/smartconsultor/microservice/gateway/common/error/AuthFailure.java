package com.smartconsultor.microservice.gateway.common.error;

public final class AuthFailure extends Failure {
    public AuthFailure(int statusCode, String message, Throwable exception) {
        super(statusCode, message, exception);
    }
}