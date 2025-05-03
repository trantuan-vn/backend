package com.smartconsultor.microservice.gateway.common.error;

import io.vertx.core.json.JsonObject;

public class HttpError {

    private final int statusCode;
    private final JsonObject body;

    public HttpError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.body = new JsonObject().put("error", message);
    }

    public int statusCode() {
        return statusCode;
    }

    public JsonObject body() {
        return body;
    }

    public static HttpError fromFailure(Failure failure) {
        return new HttpError(failure.statusCode(), failure.message());
    }
}
