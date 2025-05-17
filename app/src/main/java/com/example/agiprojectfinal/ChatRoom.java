package com.example.agiprojectfinal;

import java.util.List;

public class ChatRoom {
    private String chatRoomId;
    private List<String> participants;
    private long lastMessageTimestamp;

    public ChatRoom() {
        // Required empty constructor for Firestore
    }

    public ChatRoom(String chatRoomId, List<String> participants, long lastMessageTimestamp) {
        this.chatRoomId = chatRoomId;
        this.participants = participants;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
} 