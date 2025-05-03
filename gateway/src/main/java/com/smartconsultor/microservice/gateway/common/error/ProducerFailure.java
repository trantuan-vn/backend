package com.smartconsultor.microservice.gateway.common.error;

/**
 * Lỗi khi gửi message tới Pulsar producer.
 */
public final class ProducerFailure extends Failure {

    public ProducerFailure(String message) {
        super(502, message, null); // 502 - Bad Gateway (gửi tới backend lỗi)
    }

    public ProducerFailure(String message, Throwable exception) {
        super(502, message, exception);
    }
}
