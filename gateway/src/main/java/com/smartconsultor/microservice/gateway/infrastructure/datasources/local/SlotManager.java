package com.smartconsultor.microservice.gateway.infrastructure.datasources.local;

import io.vertx.core.Future;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SlotManager extends AutoCloseable {

    /**
     * Gán một topic cho userId. Trả về Future chứa topic được gán hoặc null nếu tất cả đều đầy.
     */
    Future<String> assignTopicToUser(String userId);

    /**
     * Xóa user khỏi topic.
     */
    Future<Void> removeUser(String userId);

    /**
     * Lấy topic mà user đã được gán.
     */
    Future<String> getTopicOfUser(String userId);

    /**
     * Trả về số lượng user trong mỗi topic.
     */
    Future<Map<String, Integer>> getTopicLoad();

    /**
     * Trả về tất cả userId được quản lý.
     */
    Future<Set<String>> getAllUsers();

    /**
     * Danh sách topic mà pod đang quản lý.
     * Có thể trả về trực tiếp vì là dữ liệu tĩnh, không bất đồng bộ.
     */
    List<String> getManagedTopics();

    /**
     * ID của pod hiện tại.
     * Có thể trả về trực tiếp.
     */
    String getPodId();
    
    @Override
    public void close();

}
