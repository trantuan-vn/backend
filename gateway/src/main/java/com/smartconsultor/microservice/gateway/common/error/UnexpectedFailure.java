package com.smartconsultor.microservice.gateway.common.error;

public final class UnexpectedFailure extends Failure {
    public UnexpectedFailure(int statusCode, String message, Throwable exception) {
        super(statusCode, message, exception);
    }
}