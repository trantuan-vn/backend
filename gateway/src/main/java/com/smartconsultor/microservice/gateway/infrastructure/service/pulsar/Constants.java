package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

public class Constants {
    public static final long INITIAL_RECONNECTION_DELAY_MS = 1000;
    public static final long MAX_RECONNECTION_DELAY_MS = 30000;
    public static final int MAX_RECONNECT_ATTEMPTS = 5;
    public static final int DEFAULT_RATE_LIMIT = 1000;
    public static final int DEFAULT_BACKPRESSURE_THRESHOLD = 1000;
    public static final int MAX_MESSAGE_SIZE_BYTES = 1024 * 1024; // 1MB

    public static long calculateExponentialBackoff(int attempt) {
        long delay = (long) (INITIAL_RECONNECTION_DELAY_MS * Math.pow(2, attempt));
        return Math.min(delay, MAX_RECONNECTION_DELAY_MS);
    }
}