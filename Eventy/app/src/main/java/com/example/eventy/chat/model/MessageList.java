package com.example.eventy.chat.model;

import java.util.List;

public class MessageList {
    private List<Message> allMessages;

    public MessageList() {

    }

    public List<Message> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(List<Message> allMessages) {
        this.allMessages = allMessages;
    }
}
