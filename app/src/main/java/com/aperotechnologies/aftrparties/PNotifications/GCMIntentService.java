package com.aperotechnologies.aftrparties.PNotifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.Login.LoginActivity;
import com.aperotechnologies.aftrparties.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;


public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = GCMIntentService.class.getSimpleName();
    private NotificationManager notificationManager;

    Configuration_Parameter m_config;
    public GCMIntentService() {
        super(ConstsCore.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "new push");
        m_config = Configuration_Parameter.getInstance();


        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = googleCloudMessaging.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(TAG,"messageType1---"+messageType);
                processNotification(ConstsCore.GCM_SEND_ERROR, extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(TAG,"messageType2---"+messageType);
                processNotification(ConstsCore.GCM_DELETED_MESSAGE, extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                Log.e(TAG,"messageType3---"+messageType);
                processNotification(ConstsCore.GCM_RECEIVED, extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void processNotification(String type, Bundle extras) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random myRandom = new Random();
        int notId = myRandom.nextInt();
        Log.e("--notId- ","    "+notId);
        final String messageValue = extras.getString(ConstsCore.EXTRA_MESSAGE);

        Log.e("messageValue ","--- "+messageValue.contains("data.message"));

        String data_type = "";
        String data_msg = "";

        if(!messageValue.contains("data.message")){//offline users
            data_msg = messageValue;
            Log.e("data_msg ", "--- " + data_msg);
            m_config.notificationManager = notificationManager;
        }else{//online users
            String[] data = messageValue.split(",");
            Log.e("data", " " + data);
            String[] data_msg1 = data[0].split("=");
            data_msg = data_msg1[1];
            String[] data_type1 = data[1].split("=");
            data_type = data_type1[1];

            Log.e("data_msg ", "--- " + data_msg);
            Log.e("data_type ", "--- " + data_type);
        }

        Intent intent = null;
        if(!messageValue.contains("data.message")){//offline users & chat messages

            if (m_config.chatService == null) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, DialogsActivity.class);
            }
        }else{
            if(data_type.equals("requestAccepted}")) {
                intent = new Intent(this, LoginActivity.class);

            }
        }

        intent.putExtra(ConstsCore.EXTRA_MESSAGE, messageValue);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("AftrParties")
                        .setContentText(data_msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);


        notificationManager.notify(notId, mBuilder.build());


        // notify activity
        Intent intentNewPush = new Intent(ConstsCore.NEW_PUSH_EVENT);
        intentNewPush.putExtra(ConstsCore.EXTRA_MESSAGE, messageValue);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);

        Log.i(TAG, "Broadcasting event " + ConstsCore.NEW_PUSH_EVENT + " with data: " + messageValue);
    }
}