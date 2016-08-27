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
import android.widget.FrameLayout;

import com.aperotechnologies.aftrparties.BaseLifeCycleCallbacks;
import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
//import com.aperotechnologies.aftrparties.MyLifecycleHandler;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.linkedin.platform.LISessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class GCMIntentService extends IntentService {

    //public static final int NOTIFICATION_ID = 1;
    private static final String TAG = GCMIntentService.class.getSimpleName();
    private static NotificationManager notificationManager;

    Configuration_Parameter m_config;

    public GCMIntentService() {
        super(ConstsCore.GCM_INTENT_SERVICE);
    }


   protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "new push");
        m_config = Configuration_Parameter.getInstance();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = googleCloudMessaging.getMessageType(intent);

       Log.e("", " body  " +" ----  "+extras.getString("body"));
       Log.e("", " aps  " +" ----  "+extras.getString("aps"));

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(TAG, "messageType2---" + messageType);

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


        //String message = extras.getString(ConstsCore.EXTRA_MESSAGE);
        String dialog_id = extras.getString(ConstsCore.DIALOG_ID);
        //String messagetype = extras.getString("type");

        JSONObject jsonbody = null;
        String messagetype = null;
        String message = null;

        Log.e("extras "," ---- "+extras+" ");

        try
        {
            if(dialog_id == null || dialog_id.equals(null))
            {
                // no chat notification
                jsonbody = new JSONObject(extras.getString("body"));
                messagetype = jsonbody.getString("type");
                message = jsonbody.getString("message");

            }
            else
            {
                // chat notificn.
                jsonbody = null;
                message = extras.getString(ConstsCore.EXTRA_MESSAGE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Boolean isApplicationForeGround = BaseLifeCycleCallbacks.applicationStatus();//MyLifecycleHandler.isApplicationInForeground();

        Log.e("isApplicationForeground ", "---- " + BaseLifeCycleCallbacks.applicationStatus());//MyLifecycleHandler.isApplicationInForeground());

        m_config.notificationManager = notificationManager;

        if (jsonbody == null || jsonbody.equals(null))
        {
            if (dialog_id == null) {
                //no chat

            }
            else
            {

                //offline chat notification navigation
                if (isApplicationForeGround == true) {
                    // application is in foreground
                    Log.e("activity", " " + m_config.foregroundCont + "----" + message +" ");

                    if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(this).getSession() == null || LISessionManager.getInstance(this).getSession().getAccessToken() == null)
                    {
                        Intent i = new Intent(this, Welcome.class);
                        i.putExtra("from","chatoffline");
                        CreateNotification(i, message, this);
                    }
                    else {



                        // Define configuration options
                        Configuration croutonConfiguration = new Configuration.Builder()
                                .setDuration(5000).build();
                        Style style = new Style.Builder()
                                .setConfiguration(croutonConfiguration)
                                .setBackgroundColor(R.color.colorAccent)
                                .setPaddingInPixels(5)
                                .setGravity(Gravity.CENTER)
                                .setTextColor(android.R.color.white)
                                .setHeight(100)
                                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                                .build();


                        Crouton crouton = Crouton.makeText((Activity) m_config.foregroundCont, message, style);
                        crouton.show();
//                        if (m_config.foregroundCont instanceof DialogsActivity) {
//
//                        } else {
                        crouton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.e("here ", "  click of crouton");
                                Intent i = new Intent(m_config.foregroundCont, DialogsActivity.class);
                                i.putExtra("from","chatoffline");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                }
                            });
                        //}
                    }

                }
                else
                {
                    // application is in background or close
                    if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(this).getSession() == null || LISessionManager.getInstance(this).getSession().getAccessToken() == null)
                    {
                        Intent i = new Intent(this, Welcome.class);
                        i.putExtra("from","chatoffline");
                        CreateNotification(i, message, this);
                    }
                    else
                    {
                        Intent i = new Intent(this, DialogsActivity.class);
                        i.putExtra("from","chatoffline");
                        CreateNotification(i, message, this);
                    }
                }

            }

        }
        else
        {


            if (isApplicationForeGround == true)
            {
                new ForegroundFunctionCall(extras, messagetype, message, this);
            }
            else{
                new BackgroundFunctionCall(extras, messagetype, message, this);
            }


        }

        // notify activity
        Intent intentNewPush = new Intent(ConstsCore.NEW_PUSH_EVENT);
        intentNewPush.putExtra(ConstsCore.EXTRA_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);

        Log.i(TAG, "Broadcasting event " + ConstsCore.NEW_PUSH_EVENT + " with data: " + message);
    }




    public static void CreateNotification(Intent intent, String message, GCMIntentService gcmIntentService) {
        Random myRandom = new Random();
        int notId = myRandom.nextInt();

        NotificationCompat.Builder mBuilder = null;
        // application is in background or close
        PendingIntent contentIntent = PendingIntent.getActivity(gcmIntentService, 0, intent, 0);
        mBuilder = new NotificationCompat.Builder(gcmIntentService)
                .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                .setContentTitle("AftrParties")
                .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        notificationManager.notify(notId, mBuilder.build());


    }



}
