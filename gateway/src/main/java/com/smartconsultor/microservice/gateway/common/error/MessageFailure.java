package com.smartconsultor.microservice.gateway.common.error;

/**
 * Lỗi khi message không hợp lệ hoặc không thể xử lý.
 */
public final class MessageFailure extends Failure {

    public MessageFailure(String message) {
        super(400, message, null); // 400 - Bad Request
    }

    public MessageFailure(String message, Throwable exception) {
        super(400, message, exception);
    }
}