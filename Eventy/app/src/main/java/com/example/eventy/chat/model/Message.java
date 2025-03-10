package com.example.eventy.chat.model;

import java.time.LocalDateTime;

public class Message {
    private long senderId;
    private String message;
    private LocalDateTime timestamp;

    public Message() {}

    public Message(long senderId, String message, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
