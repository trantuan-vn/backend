package com.smartconsultor.microservice.gateway.verticle.config;

public class RedisConfig {
    private String host;
    private int port;
    private String password;
    private int database;
    private int maxPoolSize;
    private int maxWaitingHandlers;
    private int poolRecycleTimeout;
    private int poolCleanerInterval;

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getPassword() { return password; }
    public int getDatabase() { return database; }
    public int getMaxPoolSize() { return maxPoolSize; }
    public int getMaxWaitingHandlers() { return maxWaitingHandlers; }
    public int getPoolRecycleTimeout() { return poolRecycleTimeout; }
    public int getPoolCleanerInterval() { return poolCleanerInterval; }

    public RedisConfig() {}
}
