package com.android.reseller.models;

/**
 * Created by User on 2/28/2018.
 */

public class Users {
    private String emailId;
    private String lastMessage;
    private int notifCount;

    public String getEmailId(){
        return emailId;
    }

    public void setEmailId(){
        this.emailId = emailId;
    }

    public String getLastMessage(){
        return lastMessage;
    }

    public void setLastMessage() {
        this.lastMessage = lastMessage;
    }

    public void setNotifCount(int notifCount){
        this.notifCount = notifCount;
    }

    public int getNotifCount(){
        return notifCount;
    }
}
