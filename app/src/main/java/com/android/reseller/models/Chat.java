package com.android.reseller.models;

/**
 * Created by Chairmo on 2/28/2018.
 */

public class Chat  {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timeStamp;

    public Chat() {

    }

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timeStamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timeStamp = timeStamp;

    }
}
