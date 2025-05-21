package com.smartconsultor.microservice.gateway.adapter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRequest {

    private static final Logger logger = LoggerFactory.getLogger(MessageRequest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("tableName")
    private String tableName;

    @JsonProperty("action")
    private String action;

    @JsonProperty("fields")
    private Map<String, Object> fields = new HashMap<>();

    public MessageRequest() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void setField(String key, Object value) {
        this.fields.put(key, value);
    }

    public Object getField(String key) {
        return this.fields.get(key);
    }

    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            logger.error("Failed to serialize Message", e);
            return "{\"status\":\"error\",\"message\":\"Unable to serialize message\"}";
        }
    }

    public static MessageRequest fromJson(String json) {
        try {
            return mapper.readValue(json, MessageRequest.class);
        } catch (Exception e) {
            // Ghi log nếu cần hoặc throw exception custom nếu ứng dụng yêu cầu
            return null;
        }
    }    
}
