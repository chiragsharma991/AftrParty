package com.aperotechnologies.aftrparties.PNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.linkedin.platform.LISessionManager;

/**
 * Created by hasai on 22/07/16.
 */
public class BackgroundFunctionCall {

    // application is in background or close
    public BackgroundFunctionCall(Bundle extras, String messagetype, String message, GCMIntentService gcmIntentService) {

        //*//when request is send to host and it is received by host
        if (messagetype.equals("requestSend")) {

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
                i.putExtra("PartyName", PartyName);
                i.putExtra("PartyStartTime", PartyStartTime);
                i.putExtra("PartyEndTime", PartyEndTime);
                i.putExtra("PartyStatus", PartyStatus);
                i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestSend");
                GCMIntentService.CreateNotification(i, message, gcmIntentService);

            }
            else
            {
                Intent i = new Intent(gcmIntentService, RequestantActivity.class);
                PartyParceableData party1 = new PartyParceableData();
                party1.setPartyId(PartyId);
                party1.setPartyName(PartyName);
                party1.setStartTime(PartyStartTime);
                party1.setEndTime(PartyEndTime);
                party1.setPartyStatus(PartyStatus);
                i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestSend");
                Bundle mBundles = new Bundle();
                mBundles.putSerializable(ConstsCore.SER_KEY, party1);
                i.putExtras(mBundles);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
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

                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestApproved");
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
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

                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", "requestDeclined");
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }

        }//***//

    }






}
