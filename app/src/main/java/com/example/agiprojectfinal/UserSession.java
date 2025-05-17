package com.example.agiprojectfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserSession {
    private static final String TAG = "UserSession";
    private static UserSession instance;
    private String userRole;
    private String userId;
    private String userEmail;
    private boolean isAdmin;
    private String userName;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "user_session_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_ADMIN = "is_admin";

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
            loadFromPrefs();
        }
    }

    private void loadFromPrefs() {
        if (prefs != null) {
            userId = prefs.getString(KEY_USER_ID, null);
            userRole = prefs.getString(KEY_USER_ROLE, null);
            userName = prefs.getString(KEY_USER_NAME, null);
            isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
            Log.d(TAG, "Loaded user data from prefs - UserID: " + userId + ", Role: " + userRole);
        }
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
        if (prefs != null) {
            prefs.edit().putString(KEY_USER_ROLE, userRole).apply();
        }
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
        if (prefs != null) {
            prefs.edit().putBoolean(KEY_IS_ADMIN, isAdmin).apply();
        }
    }

    public void clearSession() {
        userRole = null;
        userId = null;
        userEmail = null;
        isAdmin = false;
        userName = null;
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
    }

    public void setUsername(String userName) {
        this.userName = userName;
        if (prefs != null) {
            prefs.edit().putString(KEY_USER_NAME, userName).apply();
        }
    }

    public String getUsername() {
        if (userName == null && prefs != null) {
            userName = prefs.getString(KEY_USER_NAME, null);
        }
        return userName;
    }

    public boolean isLoggedIn() {
        return userId != null && !userId.isEmpty();
    }
} 