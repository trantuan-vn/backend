package com.smartconsultor.microservice.business.application.config;

import com.typesafe.config.Config;

public class PostgresConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public PostgresConfig(Config config) {
        this.host = config.getString("host");
        this.port = config.getInt("port");
        this.database = config.getString("database");
        this.user = config.getString("user");
        this.password = config.getString("password");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }


    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
