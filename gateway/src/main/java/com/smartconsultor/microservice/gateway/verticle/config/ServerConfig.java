package com.smartconsultor.microservice.gateway.verticle.config;

public class ServerConfig {
    private int port;
    private String host;
    private int max_payload_size;

    public int getPort() { return port; }
    public String getHost() { return host; }
    public int getMaxPayloadSize() { return max_payload_size; }

    public ServerConfig() {}
}
