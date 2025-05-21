package com.smartconsultor.microservice.gateway.adapter.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartconsultor.microservice.gateway.domain.model.MessagePointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageResponse {
    private static final Logger logger = LoggerFactory.getLogger(MessageResponse.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private String status;
    private MessagePointer messagePointer;

    public MessageResponse() {}

    public MessageResponse(String status, MessagePointer messagePointer) {
        this.status = status;
        this.messagePointer = messagePointer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MessagePointer getMessagePointer() {
        return messagePointer;
    }

    public void setMessagePointer(MessagePointer messagePointer) {
        this.messagePointer = messagePointer;
    }

    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            logger.error("Failed to serialize MessageStatusResponse", e);
            return "{\"status\":\"error\",\"message\":\"Unable to serialize response\"}";
        }
    }
}
