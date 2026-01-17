package com.example.model;

/**
 * Model cho tin nhắn chat
 */
public class ChatMessage {
    private String sender;
    private String content;
    private String timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT,    // Tin nhắn thông thường
        JOIN,    // User tham gia
        LEAVE    // User rời khỏi
    }

    // Constructors
    public ChatMessage() {
    }

    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}

