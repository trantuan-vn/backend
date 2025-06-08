package com.smartconsultor.microservice.business.application.config;

import com.typesafe.config.Config;

public class PulsarConfig {
    private final String serviceUrl;
    private final String adminUrl;
    private final String topicIn;
    private final String topicOut;

    public PulsarConfig(Config config) {
        this.serviceUrl = config.getString("serviceUrl");
        this.adminUrl = config.getString("adminUrl");       // thêm trường adminUrl
        this.topicIn = config.getString("topic.in");
        this.topicOut = config.getString("topic.out");
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public String getTopicIn() {
        return topicIn;
    }

    public String getTopicOut() {
        return topicOut;
    }
}
