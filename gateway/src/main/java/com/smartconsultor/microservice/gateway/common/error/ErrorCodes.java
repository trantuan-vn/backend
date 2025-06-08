package com.smartconsultor.microservice.gateway.common.error;

public class ErrorCodes {
    public static final int INVALID_TOKEN = 1001;
    public static final int INVALID_HANDSHAKE = 1002;
    public static final int UNSUPPORTED_MESSAGE = 2001;
    public static final int INTERNAL_ERROR = 3001;
    public static final int HANDLER_NOT_FOUND = 3002;
    public static final int RATE_LIMITED = 4001;
    public static final int RESUME_FAILED = 5001;
}
