package com.android.reseller;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by suridx on 3/3/2018.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {

   private static final String TAG = "MyFMService";

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            // Handle data payload of FCM messages.
            Toast.makeText(getApplicationContext(),remoteMessage.getData().size(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
            Log.e(TAG, "FCM Notification Message: " +
                    remoteMessage.getNotification());
            Log.e(TAG, "FCM Data Message: " + remoteMessage.getData());

            if(remoteMessage.getData().size()>0)
            {
                RemoteMessage.Notification notif = remoteMessage.getNotification();
                Toast.makeText(getApplicationContext(),notif.getBody(),Toast.LENGTH_SHORT).show();
            }

            else
            {

                RemoteMessage.Notification notif = remoteMessage.getNotification();
                Toast.makeText(getApplicationContext(),notif.getTitle(),Toast.LENGTH_SHORT).show();

            }


        }

    @Override
    public void onDeletedMessages() {
        Toast.makeText(getApplicationContext(),"Mess Del",Toast.LENGTH_SHORT).show();
    }
}
