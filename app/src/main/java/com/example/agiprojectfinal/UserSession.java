package com.example.agiprojectfinal;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static UserSession instance;
    private String userRole;
    private String userId;
    private String userEmail;
    private boolean isAdmin;
    private String userName;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "user_session_prefs";
    private static final String KEY_USER_ID = "user_id";

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserId() {
        if (userId == null && prefs != null) {
            userId = prefs.getString(KEY_USER_ID, null);
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if (prefs != null) {
            prefs.edit().putString(KEY_USER_ID, userId).apply();
        }
    }

    public void loadUserIdFromPrefs() {
        if (prefs != null) {
            userId = prefs.getString(KEY_USER_ID, null);
        }
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void clearSession() {
        userRole = null;
        userId = null;
        userEmail = null;
        isAdmin = false;
    }
    public void setUsername(String userName)
    {
        this.userName= userName;
    }

    public String getUsername() {
        return userName;
    }
} 