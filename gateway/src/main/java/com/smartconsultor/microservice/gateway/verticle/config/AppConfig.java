package com.smartconsultor.microservice.gateway.verticle.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.InputStream;

public class AppConfig {

    private ServerConfig server;
    private PulsarConfig pulsar;
    private WebsocketConfig websocket;
    private InfinispanConfig infinispan;
    private KeycloakConfig keycloak;

    @Inject
    public AppConfig() {}

    public ServerConfig getServer() { return server; }
    public PulsarConfig getPulsar() { return pulsar; }
    public WebsocketConfig getWebsocket() { return websocket; }
    public InfinispanConfig getInfinispan() { return infinispan; }
    public KeycloakConfig getKeycloak() { return keycloak; }

    public static AppConfig load(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(path);
            if (input == null) {
                throw new RuntimeException("Config not found: " + path);
            }
            return mapper.readValue(input, AppConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config from file: " + path, e);
        }
    }
}
