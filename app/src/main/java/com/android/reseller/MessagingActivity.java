package com.android.reseller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingActivity extends AppCompatActivity {

    static final String SenderID = "<your sender id here>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

    }

    public void registerUser(View view) {
        EditText username = (EditText) findViewById(R.id.messagingUsernameText);

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SenderID + "@gcm.googleapis.com")
                    .setMessageId(getRandomMessageId())
        .addData("action", "REGISTER")
        .addData("account", username.getText().toString())
        .build());
    }

    static Random random = new Random();

    public String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }

    public void sendMessage(View view) {
        EditText recipient = (EditText) findViewById(R.id.messagingRecipientText);
        EditText message = (EditText) findViewById(R.id.messagingMessageText);

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SenderID + "@gcm.googleapis.com")
                .setMessageId(getRandomMessageId())
                .addData("action", "Message")
                .addData("recipient", recipient.getText().toString())
                .addData("message", message.getText().toString())
                .build());
    }
}
