package com.smartconsultor.microservice.gateway.verticle.config;

public class ServerConfig {
    private int port;
    private String host;
    private int max_payload_size;

    private int maxUsersPerTopic = 1000;
    private int numTopics = 10;

    public ServerConfig() {}

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getMaxPayloadSize() {
        return max_payload_size;
    }

    public int getMaxUsersPerTopic() {
        return maxUsersPerTopic;
    }

    public void setMaxUsersPerTopic(int maxUsersPerTopic) {
        this.maxUsersPerTopic = maxUsersPerTopic;
    }

    public int getNumTopics() {
        return numTopics;
    }
}
