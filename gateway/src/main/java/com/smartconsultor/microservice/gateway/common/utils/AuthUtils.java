package com.smartconsultor.microservice.gateway.common.utils;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartconsultor.microservice.gateway.common.error.Failure;
import com.smartconsultor.microservice.gateway.common.error.HttpError;

public class AuthUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    // 1. Lấy refresh token từ request body hoặc cookie
    public static String getRefreshTokenFromContext(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String refreshToken = body.getString("refresh_token");

        if (refreshToken == null || refreshToken.isBlank()) {
            Cookie refreshCookie = ctx.request().getCookie("refresh_token");
            if (refreshCookie != null) {
                refreshToken = refreshCookie.getValue();
            }
        }
        return refreshToken;
    }

    // 2. Lấy access token từ request body hoặc cookie
    public static String getAccessTokenFromContext(RoutingContext ctx) {        
        String accessToken = ctx.request().getHeader("Authorization");
        if (accessToken == null) {
            accessToken = ctx.request().getParam("access_token");
        }

        if (accessToken == null || accessToken.isBlank()) {
            Cookie refreshCookie = ctx.request().getCookie("access_token");
            if (refreshCookie != null) {
                accessToken = refreshCookie.getValue();
            }
        }
        return accessToken;
    }

    // 3. Xử lý lỗi chung trong quá trình thực hiện một hành động
    public static void handleError(RoutingContext ctx, Throwable err, String action, String... params) {
        logger.error("Unexpected failure to {} with params [{}]: {}", action, String.join(", ", params), err.getMessage(), err);
        ctx.response()
            .setStatusCode(500)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("error", "Internal server error").encode());
    }

    // 4. Xử lý lỗi cụ thể khi có Failure
    public static void handleFailure(RoutingContext ctx, Failure failure) {
        HttpError httpError = HttpError.fromFailure(failure);
        ctx.response()
            .setStatusCode(httpError.statusCode())
            .putHeader("Content-Type", "application/json")
            .end(httpError.body().encode());
    }

    // 5. Gửi lỗi với mã status code và thông báo cụ thể
    public static void sendErrorResponse(RoutingContext ctx, int statusCode, String message) {
        ctx.response()
            .setStatusCode(statusCode)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("error", message).encode());
    }

    // 6. Thiết lập cookies cho access token và refresh token
    public static void setAuthCookies(RoutingContext ctx, String accessToken, long accessTokenExpiresIn, String refreshToken, long refreshTokenExpiresIn) {
        Cookie accessCookie = Cookie.cookie("access_token", accessToken)
            .setHttpOnly(true)  // Đảm bảo cookie không thể bị truy cập qua JavaScript
            .setSecure(true)    // Chỉ gửi cookie qua kết nối HTTPS
            .setPath("/")       // Áp dụng cho tất cả các URL trong domain
            .setMaxAge(accessTokenExpiresIn); // Thời gian sống của cookie

        Cookie refreshCookie = Cookie.cookie("refresh_token", refreshToken)
            .setHttpOnly(true)
            .setSecure(true)
            .setPath("/")
            .setMaxAge(refreshTokenExpiresIn);

        ctx.response().addCookie(accessCookie);
        ctx.response().addCookie(refreshCookie);
    }
    /**
     * Hàm reject handshake với status code + JSON body
     */
    public static void rejectHandshakeWithJson(ServerWebSocket handshake, int statusCode, String error, String message) {
        if (!handshake.isClosed()) {
            handshake.reject(statusCode);
            try {
                handshake.writeFinalTextFrame("{\"error\": \"" + error + "\", \"message\": \"" + message + "\"}");
            } catch (Exception e) {
                logger.warn("Failed to send JSON error response after rejecting WebSocket handshake", e);
            }
        } else {
            logger.warn("Attempted to reject a closed WebSocket handshake");
        }
    }       
}
