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
    private RedisConfig redis; 
    private EnvConfig env;
    private GeoIPConfig geoip; // <-- thêm đây
    private RocksDBConfig rocksdb; // <-- thêm mới
    @Inject
    public AppConfig() {}

    public ServerConfig getServer() { return server; }
    public PulsarConfig getPulsar() { return pulsar; }
    public WebsocketConfig getWebsocket() { return websocket; }
    public InfinispanConfig getInfinispan() { return infinispan; }
    public KeycloakConfig getKeycloak() { return keycloak; }
    public RedisConfig getRedis() { return redis; } 
    public EnvConfig getEnv() { return env; }
    public GeoIPConfig getGeoip() { return geoip; }  // getter mới
    public RocksDBConfig getRocksdb() { return rocksdb; } // getter

    public static AppConfig load(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(path);
            if (input == null) {
                throw new RuntimeException("Config not found: " + path);
            }
    
            AppConfig config = mapper.readValue(input, AppConfig.class);
                    
            // Inject environment config
            config.env = new EnvConfig();
    
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Error loading config from file: " + path, e);
        }
    }
}