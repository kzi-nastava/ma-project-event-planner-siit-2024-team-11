package com.example.eventy.chat.model;

import java.time.LocalDateTime;

public class Chat {
    private long chatId;
    private long otherId;
    private String otherName;
    private String otherPicture;
    private LocalDateTime lastMessageTime;

    public Chat() {

    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getOtherId() {
        return otherId;
    }

    public void setOtherId(long otherId) {
        this.otherId = otherId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getOtherPicture() {
        return otherPicture;
    }

    public void setOtherPicture(String otherPicture) {
        this.otherPicture = otherPicture;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
