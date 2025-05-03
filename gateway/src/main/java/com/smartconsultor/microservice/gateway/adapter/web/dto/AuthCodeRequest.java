package com.smartconsultor.microservice.gateway.adapter.web.dto;

public class AuthCodeRequest {
    public String code;
    public String redirectUri;
    public AuthCodeRequest() {} // cần constructor rỗng cho JSON decode
    public AuthCodeRequest(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }        
}
