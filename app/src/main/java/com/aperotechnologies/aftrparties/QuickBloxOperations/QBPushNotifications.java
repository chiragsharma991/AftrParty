package com.aperotechnologies.aftrparties.QuickBloxOperations;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hasai on 15/06/16.
 */
public class QBPushNotifications {

    static Configuration_Parameter m_config;
    String TAG = ".QBPushNotifications";


    //PushNotification to Host after Party Request
    //public static void sendRequestPN(String HostQBID, String HostFBID, String partyName, String partyID,  Context cont) {
    public static void sendRequestPN(JSONObject jsonObject,  Context cont) {

        Log.e("get jsonObj TAG"," "+jsonObject);

        m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        String username = sharedPreferences.getString(m_config.Entered_User_Name,"");

        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        try {
            userIds.add(Integer.valueOf(jsonObject.getString("HostQBID")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //userIds.add(Integer.valueOf(sharedPreferences.getString(m_config.QuickBloxID,"")));
        QBEvent event = new QBEvent();

        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        String GCFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        try {
            json.put("message", username +" has sent you request for "+jsonObject.getString("PartyName"));
            json.put("type","requestSend");
            // custom parameters
//            json.put("PartyID", jsonObject.getString("PartyID"));//request for Party
//            json.put("HostQBID", jsonObject.getString("HostQBID"));//Host QB Id
//            json.put("HostFBID",jsonObject.getString("HostFBID"));//Host FB Id
//            json.put("GCQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//RequestantQBID
//            json.put("GCFBID", GCFBID);//RequestantFBID

            json.put("PartyID", jsonObject.getString("PartyID"));//request for Party
            json.put("PartyName", jsonObject.getString("PartyName"));
            json.put("PartyStartTime",jsonObject.getString("PartyStartTime"));
            json.put("PartyEndTime", jsonObject.getString("PartyEndTime"));
            json.put("PartyStatus", "Pending");
            json.put("GCQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//RequestantQBID
            json.put("GCFBID", GCFBID);//RequestantFBID


            Log.e("set sendRequestPN obj"," "+json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());




        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
            }
        });

    }



    //PushNotification to Guest after Party request is Approved
    public static void sendApprovedPN(String GCFBID, String GCQBID, String PartyID, String partyName, Context cont) {

        m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);


        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(Integer.valueOf(GCQBID));
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        //String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {
            json.put("message",  "Your request has been approved for "+partyName);
            json.put("type","requestApproved");
            // custom parameters
            json.put("PartyID", PartyID);//approval for Party
            //json.put("GCQBID", GCQBID);//GCQBId(Approved ID)
            json.put("GCFBID",GCFBID);//GCFBId(Approved ID)
//            json.put("HostQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//HostQBID
//            json.put("HostFBID", HostFBID);//HostFBID

            Log.e("set sendApprovedPN obj"," "+json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());



        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
            }
        });

    }


    //PushNotification to Guest after Party request is Declined
    public static void sendDeclinedPN(String GCFBID, String GCQBID, String PartyID, String partyName, Context cont) {

        m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);


        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(Integer.valueOf(GCQBID));
        QBEvent event = new QBEvent();

        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {
            json.put("message",  "Your request has been declined for "+partyName);
            json.put("type","requestDeclined");
            // custom parameters
            json.put("PartyID", PartyID);//approval for Party
//          json.put("GCQBID", GCQBID);//GCQBId(Delined ID)
            json.put("GCFBID",GCFBID);//GCFBId(Delined ID)
//          json.put("HostQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//HostQBID
//          json.put("HostFBID", HostFBID);//HostFBID

        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());



        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
            }
        });

    }


    //PushNotification for 1-1 Chat Feature
    public static void sendPeerChatNotification(Context cont, Integer occupantId) {

        m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(Integer.valueOf(occupantId));
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject json = new JSONObject();

        try {
            json.put("message",  "You have been added to Peer Chat");
            json.put("type","1-1 Chat");
            // custom parameters


        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());


        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
            }
        });

    }



}
