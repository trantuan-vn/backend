package com.smartconsultor.microservice.gateway.common.error;

public final class NetworkFailure extends Failure {
    public NetworkFailure(int statusCode, String message, Throwable exception) {
        super(statusCode, message, exception);
    }   
}