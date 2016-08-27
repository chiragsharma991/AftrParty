package com.aperotechnologies.aftrparties.PNotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.linkedin.platform.LISessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by hasai on 22/07/16.
 */
public class BackgroundFunctionCall {

    // application is in background or close
    public BackgroundFunctionCall(Bundle extras, String messagetype, String message, GCMIntentService gcmIntentService) {

        Log.e("BackgroundFunctionCall messageType "," "+messagetype);
        if(messagetype == null || messagetype.equals(null))
        {
            return;
        }

        //*//when request is send to host and it is received by host
        if (messagetype.equals("requestSend")) {

            JSONObject json = null;
            String PartyId = null, PartyName = null , PartyStartTime = null, PartyEndTime = null, PartyStatus = null, GCFBID = null;
            try {
                json = new JSONObject(extras.getString("body"));
                PartyId = json.getString("PartyID");
                PartyName = json.getString("PartyName");
                PartyStartTime = json.getString("PartyStartTime");
                PartyEndTime = json.getString("PartyEndTime");
                PartyStatus = json.getString("PartyStatus");
                GCFBID = json.getString("GCFBID");

                Log.e("PartyId "," "+PartyId+ " PartyName "+PartyName+" PartyStartTime "+PartyStartTime+" PartyEndTime "+PartyEndTime+" PartyStatus "+PartyStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("PartyId", PartyId);
                i.putExtra("PartyName", PartyName);
                i.putExtra("PartyStartTime", PartyStartTime);
                i.putExtra("PartyEndTime", PartyEndTime);
                i.putExtra("PartyStatus", PartyStatus);
                //i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
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
                //i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                Bundle mBundles = new Bundle();
                mBundles.putSerializable(ConstsCore.SER_KEY, party1);
                i.putExtras(mBundles);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }

        }//*//

        //**//This dialog/notification will be displayed on GC end after approving request
         if (messagetype.equals("requestApproved"))
        {

            JSONObject json;
            String PartyID = null, GCFBID = null;

            try {
                json = new JSONObject(extras.getString("body"));
                PartyID = json.getString("PartyID");
                GCFBID = json.getString("GCFBID");
                Log.e("GCFBID", " " + GCFBID + " PartyID" + " " + PartyID );

            } catch (JSONException e) {
                e.printStackTrace();
            }

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

                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }

        }//**//


        //***//This dialog/notification will be displayed on GC end after declining request
        if (messagetype.equals("requestDeclined"))
        {

            JSONObject json;
            String PartyID = null, GCFBID = null;

            try {
                json = new JSONObject(extras.getString("body"));
                PartyID = json.getString("PartyID");
                GCFBID = json.getString("GCFBID");
                Log.e("GCFBID", " " + GCFBID + " PartyID" + " " + PartyID );

            } catch (JSONException e) {
                e.printStackTrace();
            }

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

                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }

        }//***//


        //****//
        //Notification after Party cancel to all GateCrashers
        if (messagetype.equals("partyCancelled"))
        {


            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {
                Intent i = new Intent(gcmIntentService, HistoryActivity.class);
                i.putExtra("from", messagetype);
                i.putExtra("message", message);
                GCMIntentService.CreateNotification(i, message, gcmIntentService);

            }


        }
        //****//


        //****//
        //Notification after 1-1 Chat dialog Creation
        if (messagetype.equals("1-1 Chat"))
        {

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
                gcmIntentService.startActivity(i);

            }


        }
        //****//


        //*****//
        if (messagetype.equals("1-1 Chat OfflineMsg"))
        {

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(gcmIntentService).getSession() == null || LISessionManager.getInstance(gcmIntentService).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(gcmIntentService, Welcome.class);
                i.putExtra("from","1-1 Chat OfflineMsg");
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }
            else
            {
                Intent i = new Intent(gcmIntentService, DialogsActivity.class);
                i.putExtra("from","1-1 Chat OfflineMsg");
                GCMIntentService.CreateNotification(i, message, gcmIntentService);
            }

        }


    }






}
