package com.android.reseller.models;

public class ChatUser {
    public String uid;
    public String email;
    public String firebaseToken;

    public ChatUser() {

    }

    public ChatUser(String uid, String email, String firebaseToken){

        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
}