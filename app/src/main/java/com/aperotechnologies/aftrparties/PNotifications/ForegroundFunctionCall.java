package com.aperotechnologies.aftrparties.PNotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.aperotechnologies.aftrparties.Chats.ChatActivity;
import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.linkedin.platform.LISessionManager;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by hasai on 22/07/16.
 */
public class ForegroundFunctionCall {

    Configuration_Parameter m_config;
    // application is in foreground
    public  ForegroundFunctionCall(Bundle extras, String messagetype, String message, final GCMIntentService gcmIntentService) {

        Log.e("ForegroundFunctionCall messageType "," "+messagetype);
        if(messagetype == null || messagetype.equals(null))
        {
            return;
        }

        //*//when request is send to host and it is received by host
        if (messagetype.equals("requestSend"))
        {
            String PartyId = extras.getString("PartyID");
            String PartyName = extras.getString("PartyName");
            String PartyStartTime = extras.getString("PartyStartTime");
            String PartyEndTime = extras.getString("PartyEndTime");
            String PartyStatus = extras.getString("PartyStatus");
            //String GCQBID = extras.getString("GCQBID");
            String GCFBID = extras.getString("GCFBID");


            Log.e("PartyId "," "+PartyId+ " PartyName "+PartyName+" PartyStartTime "+PartyStartTime+" PartyEndTime "+PartyEndTime+" PartyStatus "+PartyStatus);

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("PartyId", PartyId);
                i.putExtra("PartyName",PartyName);
                i.putExtra("PartyStartTime",PartyStartTime);
                i.putExtra("PartyEndTime",PartyEndTime);
                i.putExtra("PartyStatus",PartyStatus);
                //i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Intent i = new Intent(gcmIntentService, TransparentActivity.class);
                i.putExtra("PartyId", PartyId);
                i.putExtra("PartyName", PartyName);
                i.putExtra("PartyStartTime", PartyStartTime);
                i.putExtra("PartyEndTime", PartyEndTime);
                i.putExtra("PartyStatus", PartyStatus);
                //i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //CreateNotification(intent, message);
                gcmIntentService.startActivity(i);
            }

        }//*//

        //**//This dialog/notification will be displayed on GC end after approving request
         if (messagetype.equals("requestApproved"))
        {

            String PartyID = extras.getString("PartyID");
            String GCFBID = extras.getString("GCFBID");
            Log.e("GCFBID", " " + GCFBID + " PartyID" + " " + PartyID );

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Intent i = new Intent(gcmIntentService, TransparentActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //CreateNotification(intent, message);
                gcmIntentService.startActivity(i);
            }

        }//**//

        //***//This dialog/notification will be displayed on GC end after declining request
        if (messagetype.equals("requestDeclined"))
        {

            String PartyID = extras.getString("PartyID");
            String GCFBID = extras.getString("GCFBID");
            Log.e("GCFBID", " " + GCFBID + " PartyID" + " " + PartyID );

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Intent i = new Intent(gcmIntentService, TransparentActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //CreateNotification(intent, message);
                gcmIntentService.startActivity(i);
            }

        }//***//

        //****//
        //Notification after 1-1 Chat dialog Creation
        if (messagetype.equals("1-1 Chat"))
        {

            m_config = Configuration_Parameter.getInstance();

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("from", messagetype);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {
                Intent i = new Intent(gcmIntentService, DialogsActivity.class);
                i.putExtra("from", messagetype);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                gcmIntentService.startActivity(i);

            }


        }
        //****//


        //****//
        //Notification after Party cancel to all GateCrashers
        if (messagetype.equals("partyCancelled"))
        {

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("from", messagetype);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {
                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("from", messagetype);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                gcmIntentService.startActivity(i);

            }


        }
        //****//

        //*****//
        if (messagetype.equals("1-1 Chat OfflineMsg"))
        {
            String dialogid = extras.getString("DialogId");

            m_config = Configuration_Parameter.getInstance();

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("from", "1-1 Chat OfflineMsg");
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Style style = new Style.Builder()
                        .setBackgroundColor(R.color.colorAccent)
                        .setPaddingInPixels(5)
                        .setGravity(Gravity.CENTER)
                        .setTextColor(android.R.color.white)
                        .setHeight(100)
                        .build();


                Crouton crouton = Crouton.makeText((Activity) m_config.foregroundCont, message, style);

                if ((m_config.foregroundCont instanceof ChatActivity && m_config.peerchatDialogId.equals(dialogid))) {

                }
                else {
                    crouton.show();
                    crouton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Log.e("here ", "  click of crouton");
                            Intent i = new Intent(m_config.foregroundCont, DialogsActivity.class);
                            i.putExtra("from", "1-1 Chat OfflineMsg");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            gcmIntentService.startActivity(i);
                        }
                    });
                }
            }

        }
        //*****//


    }
}
