package com.example.agiprojectfinal;

import com.google.firebase.Timestamp;

public class UserModel {
    private String name;
    private String email;
    //private String role;
    private Timestamp timestamp;
    private String userID;

    public UserModel() {

    }

    public UserModel(String name, String email, Timestamp timestamp, String userID){
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   /* public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }*/

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
