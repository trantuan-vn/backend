package com.smartconsultor.microservice.gateway.infrastructure.service.redis;

import java.util.regex.Pattern;

public class RedisInputValidator {
    private static final Pattern SOCKET_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{20,50}$");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{10,36}$");
    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{10,50}$");
    private static final Pattern POD_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{5,50}$");

    public boolean isValidInput(String... args) {
        for (String arg : args) {
            if (arg == null || arg.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidSocketId(String socketId) {
        return socketId != null && SOCKET_ID_PATTERN.matcher(socketId).matches();
    }

    public boolean isValidUserId(String userId) {
        return userId != null && USER_ID_PATTERN.matcher(userId).matches();
    }

    public boolean isValidDeviceId(String deviceId) {
        return deviceId != null && DEVICE_ID_PATTERN.matcher(deviceId).matches();
    }

    public boolean isValidPodId(String podId) {
        return podId != null && POD_ID_PATTERN.matcher(podId).matches();
    }
}