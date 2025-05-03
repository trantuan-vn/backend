package com.smartconsultor.microservice.gateway.verticle.config;

public class WebsocketConfig {
    private int max_connections_per_ip;
    private int max_payload_size;
    private int max_sockets;
    private int expireAfterAccess;

    public int getMaxConnectionsPerIp() { return max_connections_per_ip; }
    public int getMaxPayloadSize() { return max_payload_size; }
    public int getMaxSockets() { return max_sockets; }
    public int getExpireAfterAccess() { return expireAfterAccess; }

    public WebsocketConfig() {}
}
