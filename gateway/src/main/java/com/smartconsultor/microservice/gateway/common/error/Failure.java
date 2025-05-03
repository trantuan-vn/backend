package com.smartconsultor.microservice.gateway.common.error;

import java.util.Objects;

// Failure giờ extends Throwable
public sealed abstract class Failure extends Throwable
        permits NetworkFailure, AuthFailure, UnexpectedFailure, MessageFailure, ProducerFailure {

    protected final int statusCode;
    protected final String message;
    protected final Throwable exception;

    public Failure(int statusCode, String message, Throwable exception) {
        super(message, exception); // <-- gọi constructor Throwable
        this.statusCode = statusCode;
        this.message = message;
        this.exception = exception;
    }

    public int statusCode() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    public Throwable exception() {
        return exception;
    }

    @Override
    public String toString() {
        String detail = exception != null ? " - " + exception : "";
        return getClass().getSimpleName() + " [statusCode=" + statusCode + "]: " + message + detail;
    }

    public String fullDebugString() {
        StringBuilder sb = new StringBuilder(toString());
        if (exception != null) {
            sb.append("\nStackTrace:\n");
            for (StackTraceElement el : exception.getStackTrace()) {
                sb.append("  at ").append(el).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Failure other &&
                this.statusCode == other.statusCode &&
                Objects.equals(this.message, other.message) &&
                Objects.equals(this.exception, other.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, exception);
    }
}
