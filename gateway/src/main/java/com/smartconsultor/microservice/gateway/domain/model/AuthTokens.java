package com.smartconsultor.microservice.gateway.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTokens {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("refresh_expires_in")
    private long refreshExpiresIn;

    public AuthTokens() {
        // Default constructor cần cho Jackson
    }

    // Getter và Setter (hoặc Lombok @Data cũng được)
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }
}
