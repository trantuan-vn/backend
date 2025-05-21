package com.smartconsultor.microservice.gateway.infrastructure.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SlotManager {

    /**
     * Gán một topic cho userId. Trả về topic hoặc null nếu tất cả đều đầy.
     */
    String assignTopicToUser(String userId);

    /**
     * Xóa user khỏi topic.
     */
    void removeUser(String userId);

    /**
     * Lấy topic mà user đã được gán.
     */
    String getTopicOfUser(String userId);

    /**
     * Trả về số lượng user trong mỗi topic.
     */
    Map<String, Integer> getTopicLoad();

    /**
     * Trả về tất cả userId được quản lý.
     */
    Set<String> getAllUsers();

    /**
     * Danh sách topic mà pod đang quản lý.
     */
    List<String> getManagedTopics();

    /**
     * ID của pod hiện tại.
     */
    String getPodId();
}
