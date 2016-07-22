package com.aperotechnologies.aftrparties.PNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.linkedin.platform.LISessionManager;

/**
 * Created by hasai on 22/07/16.
 */
public class ForegroundFunctionCall {

    // application is in foreground
    public  ForegroundFunctionCall(Bundle extras, String messagetype, String message, GCMIntentService gcmIntentService)
    {

        Log.e("ForegroundFunctionCall messageType "," "+messagetype);
        //*//when request is send to host and it is received by host
        if (messagetype.equals("requestSend"))
        {
            String PartyId = extras.getString("PartyID");
            String PartyName = extras.getString("PartyName");
            String PartyStartTime = extras.getString("PartyStartTime");
            String PartyEndTime = extras.getString("PartyEndTime");
            String PartyStatus = extras.getString("PartyStatus");
            String GCQBID = extras.getString("GCQBID");
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
                i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestSend");
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
                i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestSend");
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
                i.putExtra("from", "requestApproved");
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Intent i = new Intent(gcmIntentService, TransparentActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestApproved");
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
                i.putExtra("from", "requestDeclined");
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {

                Intent i = new Intent(gcmIntentService, TransparentActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestDeclined");
                i.putExtra("message", message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //CreateNotification(intent, message);
                gcmIntentService.startActivity(i);
            }

        }//***//
    }
}
