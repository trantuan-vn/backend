package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

public class RedisTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(RedisTimeoutHandler.class);
    private static final long REDIS_COMMAND_TIMEOUT_MS = 5000;
    private final Vertx vertx;

    public RedisTimeoutHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public <T> Future<T> withTimeout(Future<T> future) {
        Promise<T> promise = Promise.promise();
        long timerId = vertx.setTimer(REDIS_COMMAND_TIMEOUT_MS, tid -> {
            if (!promise.tryFail(new TimeoutException("Operation timed out after " + REDIS_COMMAND_TIMEOUT_MS + "ms"))) {
                logger.debug("Timeout occurred but future was already completed");
            }
        });
        future.onComplete(ar -> {
            vertx.cancelTimer(timerId);
            promise.handle(ar);
        });
        return promise.future();
    }
}