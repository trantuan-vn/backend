package com.smartconsultor.microservice.business.application.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {

    private final Config root;

    private final PulsarConfig pulsarConfig;
    private final PostgresConfig postgresConfig;

    public AppConfig() {
        this.root = ConfigFactory.load(); // Load từ application.conf
        this.pulsarConfig = new PulsarConfig(root.getConfig("pulsar"));
        this.postgresConfig = new PostgresConfig(root.getConfig("postgres"));
    }

    public PulsarConfig getPulsarConfig() {
        return pulsarConfig;
    }

    public PostgresConfig getPostgresConfig() {
        return postgresConfig;
    }
}
