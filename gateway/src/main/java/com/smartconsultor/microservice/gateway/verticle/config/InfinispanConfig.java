package com.smartconsultor.microservice.gateway.verticle.config;

public class InfinispanConfig {
    private String host;
    private int port;
    private String username;
    private String password;

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public InfinispanConfig() {}
}
