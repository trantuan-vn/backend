package com.smartconsultor.microservice.gateway.verticle.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    private final int replicasCount;
    private final int podId;

    public EnvConfig() {
        this.replicasCount = parseIntEnv("REPLICAS_COUNT", 1); // fallback = 1
        this.podId = extractPodId();
    }

    public int getReplicasCount() {
        return replicasCount;
    }

    public int getPodId() {
        return podId;
    }

    private int parseIntEnv(String name, int defaultValue) {
        String val = System.getenv(name);
        if (val == null || val.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            logger.warn("Invalid int for env {}: '{}'. Using default={}", name, val, defaultValue);
            return defaultValue;
        }
    }

    private int extractPodId() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null || !hostname.contains("-")) {
            logger.warn("HOSTNAME not set or invalid format. Defaulting podId to 0.");
            return 0;
        }
        try {
            String[] parts = hostname.split("-");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            logger.warn("Failed to extract podId from HOSTNAME '{}'. Defaulting to 0.", hostname, e);
            return 0;
        }
    }
}
