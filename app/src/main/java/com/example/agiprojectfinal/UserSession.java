package com.example.agiprojectfinal;

public class UserSession {
    private static UserSession instance;
    private String userRole;
    private String userId;
    private String userEmail;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isAdmin() {
        return "Admin".equals(userRole);
    }

    public void clearSession() {
        userRole = null;
        userId = null;
        userEmail = null;
    }
} 