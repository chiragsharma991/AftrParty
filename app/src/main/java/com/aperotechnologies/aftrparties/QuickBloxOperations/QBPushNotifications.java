package com.aperotechnologies.aftrparties.QuickBloxOperations;

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

import org.json.JSONObject;

/**
 * Created by hasai on 15/06/16.
 */
public class QBPushNotifications {

    static Configuration_Parameter m_config;


    //PushNotification to Host after Party Request
    public static void sendRequestPN(String HostQBID, String HostFBID, String partyName, String partyID,  Context cont) {

        m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        String username = sharedPreferences.getString(m_config.Entered_User_Name,"");

        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(Integer.valueOf(HostQBID));
        //userIds.add(Integer.valueOf(sharedPreferences.getString(m_config.QuickBloxID,"")));
        QBEvent event = new QBEvent();

        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);


        JSONObject json = new JSONObject();
        String GCFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        try {
            json.put("message", username +" has sent you request for "+partyName+" Party");
            json.put("type","requestSend");
            // custom parameters
            json.put("PartyID", partyID);//request for Party
            json.put("HostQBID", HostQBID);//Host QB Id
            json.put("HostFBID",HostFBID);//Host FB Id
            json.put("GCQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//RequestantQBID
            json.put("GCFBID", GCFBID);//RequestantFBID
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



    //PushNotification to Guest after Party Approved
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
        String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {
            json.put("message",  "Your request has been approved for "+partyName);
            json.put("type","requestApproved");
            // custom parameters
            json.put("PartyID", PartyID);//approval for Party
            json.put("GCQBID", GCQBID);//GCQBId(Approved ID)
            json.put("GCFBID",GCFBID);//GCFBId(Approved ID)
            json.put("HostQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//HostQBID
            json.put("HostFBID", HostFBID);//HostFBID

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



    //PushNotification to Guest after Party Cancellation
    public static void sendCancelledPN(String GCFBID, String GCQBID, String PartyID, String partyName, Context cont) {

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
            json.put("message",  "");
            json.put("type","requestApproved");
            // custom parameters
            json.put("PartyID", PartyID);//approval for Party
            json.put("GCQBID", GCQBID);//GCQBId(Approved ID)
            json.put("GCFBID",GCFBID);//GCFBId(Approved ID)
            json.put("HostQBID", sharedPreferences.getString(m_config.QuickBloxID,""));//HostQBID
            json.put("HostFBID", HostFBID);//HostFBID

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
