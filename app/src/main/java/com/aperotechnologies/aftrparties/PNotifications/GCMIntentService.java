package com.aperotechnologies.aftrparties.PNotifications;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.MyLifecycleHandler;
import com.aperotechnologies.aftrparties.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.chat.QBChatService;

import java.util.Random;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class GCMIntentService extends IntentService {

    //public static final int NOTIFICATION_ID = 1;
    private static final String TAG = ".GCMIntentService";
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


        Log.e(""," messageType  " +messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(TAG,"messageType2---"+messageType);

                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                processNotification(ConstsCore.GCM_RECEIVED, extras);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void processNotification(String type, Bundle extras) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String message = extras.getString(ConstsCore.EXTRA_MESSAGE);
        String dialog_id = extras.getString(ConstsCore.DIALOG_ID);
        String messagetype = extras.getString("type");

        Boolean isApplicationForeGround = MyLifecycleHandler.isApplicationInForeground();
        Intent intent = null;
        Log.e("here", " "+message+" "+messagetype);

        Log.e("isApplicationForeground ","---- "+ MyLifecycleHandler.isApplicationInForeground());



        if(messagetype == null)
        {
            if(dialog_id == null)
            {
                //no chat
                intent = new Intent(this, Welcome.class);
                CreateNotification(intent, message);



            }
            else
            {
                m_config.notificationManager = notificationManager;
                //chat
                if(isApplicationForeGround == true)
                {
                    // application is in foreground
                    Log.e("activity"," "+m_config.foregroundCont+"----"+message);

                    Style style = new Style.Builder()
                            .setBackgroundColor(R.color.black)
                            .setPaddingInPixels(5)
                            .setGravity(Gravity.CENTER)
                            .setTextColor(android.R.color.white)
                            .setHeight(100)
                            .build();

                    Crouton crouton = Crouton.makeText((Activity) m_config.foregroundCont,message,style);
                    crouton.show();
                    crouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i  = new Intent(m_config.foregroundCont, DialogsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });


                }else{
                    // application is in background or close
                    intent = new Intent(this, DialogsActivity.class);
                    CreateNotification(intent, message);
                }

            }

        }else {
            //when request is send to host and it is received by host
            if (messagetype.equals("requestSend")) {
                String HostFBID = extras.getString("HostFBID");
                String HostQBID = extras.getString("HostQBID");
                String PartyID = extras.getString("PartyID");
                String GCQBID = extras.getString("GCQBID");
                String GCFBID = extras.getString("GCFBID");
                Log.e("HostFBID", " " + HostFBID + " HostQBID" + " " + HostQBID + " PartyID" + " " + PartyID + " GateCrasherID" + " " + GCQBID);

                intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("HostFBID", HostFBID);
                intent.putExtra("HostQBID", HostFBID);
                intent.putExtra("PartyID", PartyID);
                intent.putExtra("GCQBID", GCQBID);
                intent.putExtra("GCFBID", GCFBID);

                CreateNotification(intent, message);
            }

            //when request is approvedBy to host and it is received by GC
            if (messagetype.equals("requestApproved")) {

                String HostFBID = extras.getString("HostFBID");
                String HostQBID = extras.getString("HostQBID");
                String PartyID = extras.getString("PartyID");
                String GCQBID = extras.getString("GCQBID");
                String GCFBID = extras.getString("GCFBID");
                Log.e("HostFBID", " " + HostFBID + " HostQBID" + " " + HostQBID + " PartyID" + " " + PartyID + " GateCrasherID" + " " + GCQBID);

                intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("HostFBID", HostFBID);
                intent.putExtra("HostQBID", HostFBID);
                intent.putExtra("PartyID", PartyID);
                intent.putExtra("GCQBID", GCQBID);
                intent.putExtra("GCFBID", GCFBID);

                CreateNotification(intent, message);

            }
        }



//            mBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.ic_launcher) // notification icon
//                    .setContentTitle("Button notification") // notification title
//                    .setContentText("Expand to show the buttons...") // content text
//                    .setTicker("Showing button notification") // status bar message
//                    .addAction(R.color.white, "Accept", contentIntent) // accept notification button
//                    .addAction(R.color.white, "Cancel", contentIntent); // cancel notification button
//            mBuilder.setAutoCancel(true);



        // notify activity
        Intent intentNewPush = new Intent(ConstsCore.NEW_PUSH_EVENT);
        intentNewPush.putExtra(ConstsCore.EXTRA_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);

        Log.i(TAG, "Broadcasting event " + ConstsCore.NEW_PUSH_EVENT + " with data: " + message);
    }


    public void CreateNotification(Intent intent, String message){
        Random myRandom = new Random();
        int notId = myRandom.nextInt();

        NotificationCompat.Builder mBuilder = null;
        // application is in background or close
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                .setContentTitle("AftrParties")
                .setContentText(message);

        //mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        notificationManager.notify(notId, mBuilder.build());



    }


}