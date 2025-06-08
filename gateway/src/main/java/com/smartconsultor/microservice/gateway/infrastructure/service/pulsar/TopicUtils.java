package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import java.util.regex.Pattern;

public class TopicUtils {
    private static final Pattern SOCKET_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{20,50}$");
    private static final Pattern TOPIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    public static String buildTopicPath(String topicName) {
        return "persistent://public/default/" + sanitizeTopicName(topicName);
    }

    public static String sanitizeTopicName(String topicName) {
        return topicName.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public static boolean isValidTopicName(String topic) {
        return topic != null && !topic.isEmpty() && TOPIC_PATTERN.matcher(topic).matches();
    }

    public static boolean isValidSocketId(String socketId) {
        return socketId != null && SOCKET_ID_PATTERN.matcher(socketId).matches();
    }

    public static String validateAndSanitizeServiceUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Pulsar service URL cannot be null or empty");
        }

        String sanitized = url.trim()
            .replaceAll("[\\r\\n]", "")
            .replaceAll("\\s+", "");

        if (!sanitized.matches("^pulsar://[a-zA-Z0-9.-]+:[0-9]+$")) {
            throw new IllegalArgumentException("Invalid Pulsar service URL format");
        }

        return sanitized;
    }
}