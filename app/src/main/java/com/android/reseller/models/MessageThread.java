package com.android.reseller.models;

import java.util.ArrayList;
import java.util.List;

public class MessageThread {

    private String sellerId;
    private String buyerId;
    private String postId;
    private List<Message> messages;

    public MessageThread() {
        sellerId = "";
        buyerId = "";
        postId = "";
        messages = new ArrayList<>();
    }

    public MessageThread(String sellerId, String buyerId, String postId, List<Message> messages) {
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.postId = postId;
        this.messages = messages;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // to add a message to the MessageThread
    public void addMessage(Message message) {
        messages.add(message);
    }
}
