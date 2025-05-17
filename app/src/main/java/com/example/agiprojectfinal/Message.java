package com.example.agiprojectfinal;

public class Message {
    private String messageId;
    private String senderId;
    private String content;
    private long timestamp;

    public Message() {
        // Required empty constructor for Firestore
    }

    public Message(String messageId, String senderId, String content, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 