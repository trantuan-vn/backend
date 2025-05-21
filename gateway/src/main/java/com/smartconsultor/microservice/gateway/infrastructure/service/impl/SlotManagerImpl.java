package com.smartconsultor.microservice.gateway.infrastructure.service.impl;

import com.smartconsultor.microservice.gateway.infrastructure.service.SlotManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

public class SlotManagerImpl implements SlotManager {

    private static final int MAX_USERS_PER_TOPIC = 1000;

    private final AppConfig appConfig;
    private final List<String> managedTopics;

    private final Map<String, String> userToTopicMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> topicToUserSet = new ConcurrentHashMap<>();
    
    @Inject
    public SlotManagerImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.managedTopics = generateTopicsForPod(String.valueOf(appConfig.getEnv().getPodId()));
        for (String topic : managedTopics) {
            topicToUserSet.put(topic, ConcurrentHashMap.newKeySet());
        }
    }

    private List<String> generateTopicsForPod(String podId) {
        List<String> topics = new ArrayList<>();
        int base = Integer.parseInt(podId) * 10;
        for (int i = 0; i < 10; i++) {
            topics.add("group-u" + (base + i));
        }
        return topics;
    }

    @Override
    public synchronized String assignTopicToUser(String userId) {
        if (userToTopicMap.containsKey(userId)) {
            return userToTopicMap.get(userId);
        }

        for (String topic : managedTopics) {
            Set<String> users = topicToUserSet.get(topic);
            if (users.size() < MAX_USERS_PER_TOPIC) {
                users.add(userId);
                userToTopicMap.put(userId, topic);
                return topic;
            }
        }

        return null; // all full
    }

    @Override
    public synchronized void removeUser(String userId) {
        String topic = userToTopicMap.remove(userId);
        if (topic != null) {
            Set<String> users = topicToUserSet.get(topic);
            if (users != null) {
                users.remove(userId);
            }
        }
    }

    @Override
    public String getTopicOfUser(String userId) {
        return userToTopicMap.get(userId);
    }

    @Override
    public Map<String, Integer> getTopicLoad() {
        Map<String, Integer> load = new HashMap<>();
        for (String topic : managedTopics) {
            load.put(topic, topicToUserSet.get(topic).size());
        }
        return load;
    }

    @Override
    public Set<String> getAllUsers() {
        return new HashSet<>(userToTopicMap.keySet());
    }

    @Override
    public List<String> getManagedTopics() {
        return managedTopics;
    }

    @Override
    public String getPodId() {
        return String.valueOf(appConfig.getEnv().getPodId());
    }
}
