package com.smartconsultor.microservice.gateway.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartconsultor.microservice.gateway.adapter.dto.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(ClientCommand.class); // Khai báo logger

    @JsonProperty("socketId")
    private String socketId;

    @JsonProperty("message")
    private MessageRequest message;

    // Khởi tạo ObjectMapper
    private static final ObjectMapper mapper = new ObjectMapper();

    public ClientCommand() {
    }

    public ClientCommand(String socketId, MessageRequest message) {
        this.socketId = socketId;
        this.message = message;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public MessageRequest getMessage() {
        return message;
    }

    public void setMessage(MessageRequest message) {
        this.message = message;
    }

    // Phương thức chuyển đối tượng thành JSON String
    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            // Log lỗi khi xảy ra ngoại lệ
            logger.error("Failed to serialize ClientCommand to JSON", e);
            return "{\"error\":\"Serialization failed: " + e.getMessage() + "\"}";
        }
    }

    // Phương thức chuyển JSON String thành đối tượng ClientCommand
    public static ClientCommand fromJson(String json) {
        try {
            return mapper.readValue(json, ClientCommand.class);
        } catch (Exception e) {
            // Log lỗi khi xảy ra ngoại lệ
            logger.error("Failed to deserialize JSON to ClientCommand", e);
            return null;
        }
    }
}
