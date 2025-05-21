package com.smartconsultor.microservice.gateway.domain.model;

import org.apache.pulsar.client.api.MessageId;

import java.util.Objects;

public class MessagePointer {
    private final String topic;
    private final MessageId messageId;

    public MessagePointer(String topic, MessageId messageId) {
        this.topic = topic;
        this.messageId = messageId;
    }

    public String getTopic() {
        return topic;
    }

    public MessageId getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "MessagePointer{" +
                "topic='" + topic + '\'' +
                ", messageId=" + messageId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagePointer)) return false;
        MessagePointer that = (MessagePointer) o;
        return Objects.equals(topic, that.topic) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, messageId);
    }
}
