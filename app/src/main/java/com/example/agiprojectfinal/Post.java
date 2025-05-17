package com.example.agiprojectfinal;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String userId;
    private String username;
    private String content;
    private long timestamp;
    private int likes;
    private int comments;
    private Map<String, Boolean> userLikes; // Map to track which users have liked the post

    // Empty constructor needed for Firestore
    public Post() {
        this.userLikes = new HashMap<>();
    }

    public Post(String postId, String userId, String username, String content, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = 0;
        this.comments = 0;
        this.userLikes = new HashMap<>();
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    // New methods for handling user likes
    public Map<String, Boolean> getUserLikes() {
        if (userLikes == null) {
            userLikes = new HashMap<>();
        }
        return userLikes;
    }

    public void setUserLikes(Map<String, Boolean> userLikes) {
        this.userLikes = userLikes != null ? userLikes : new HashMap<>();
    }

    public boolean hasUserLiked(String userId) {
        if (userLikes == null) {
            return false;
        }
        Boolean hasLiked = userLikes.get(userId);
        return hasLiked != null && hasLiked;
    }
} 