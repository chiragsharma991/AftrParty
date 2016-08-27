package com.aperotechnologies.aftrparties.QuickBloxOperations;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.listeners.QBMessageSentListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hasai on 15/06/16.
 */
public class QBPushNotifications {

    static Configuration_Parameter m_config;
    String TAG = ".QBPushNotifications";

    //PushNotification to Host after Party Request
    public static void sendRequestPN(JSONObject jsonObject, final Context cont) {

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

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject json = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();


        String GCFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        try {
            jsonbody.put("message", username +" has sent you request for "+jsonObject.getString("PartyName"));
            jsonbody.put("type","requestSend");
            jsonbody.put("PartyID", jsonObject.getString("PartyID"));//request for Party
            jsonbody.put("PartyName", jsonObject.getString("PartyName"));
            jsonbody.put("PartyStartTime",jsonObject.getString("PartyStartTime"));
            jsonbody.put("PartyEndTime", jsonObject.getString("PartyEndTime"));
            jsonbody.put("PartyStatus", "Pending");
            jsonbody.put("GCFBID", GCFBID);//RequestantFBID
            //Log.e("set sendRequestPN obj"," "+json);

            jsonaps.put("alert",username +" has sent you request for "+jsonObject.getString("PartyName"));
            jsonaps.put("badge","1");
            jsonaps.put("content-available","1");

            json.put("body", jsonbody);
            json.put("aps",jsonaps);

        } catch (Exception e)
        {
            e.printStackTrace();
        }


        event.setMessage(json.toString());
        //Log.e("json.toString"," "+json.toString());

        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont,"Request has been send to Party");


            }

            @Override
            public void onError(QBResponseException errors) {

                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont,"Request has been send to Party, Notification sending failed");

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
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();
        //String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {
            jsonbody.put("message",  "Your request has been approved for "+partyName);
            jsonbody.put("type","requestApproved");
            // custom parameters
            jsonbody.put("PartyID", PartyID);//approval for Party
            jsonbody.put("GCFBID",GCFBID);//GCFBId(Approved ID)

            jsonaps.put("alert","Your request has been approved for "+partyName);
            jsonaps.put("badge","1");
            jsonaps.put("content-available","1");

            json.put("body", jsonbody);
            json.put("aps",jsonaps);

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
    public static void sendDeclinedPN(String GCFBID, String GCQBID, String PartyID, String partyName, final Context cont) {

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
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();

        String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {
            jsonbody.put("message",  "Your request has been declined for "+partyName);
            jsonbody.put("type","requestDeclined");
            // custom parameters
            jsonbody.put("PartyID", PartyID);//approval for Party
            jsonbody.put("GCFBID",GCFBID);//GCFBId(Delined ID)


            jsonaps.put("alert","Your request has been declined for "+partyName);
            jsonaps.put("badge","1");
            jsonaps.put("content-available","1");

            json.put("body", jsonbody);
            json.put("aps",jsonaps);

        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());



        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);
                Toast.makeText(cont, "Request has been declined",Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();

            }

            @Override
            public void onError(QBResponseException errors) {

                Toast.makeText(cont, "Request has been declined, Notification sending failed",Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
            }
        });

    }


    //PushNotification when user is added 1-1 Chat Feature
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
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();

        try {
            jsonbody.put("message",  "You have been added to Peer Chat");
            jsonbody.put("type","1-1 Chat");


            jsonaps.put("alert","You have been added to Peer Chat");
            jsonaps.put("badge","1");
            jsonaps.put("content-available","1");

            json.put("body", jsonbody);
            json.put("aps",jsonaps);


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


    //PushNotifications when host cancel party
    public static void sendPartyCancelledPN(List<Integer> gcqbidlist, String PartyID, String partyName, final Context cont, final String dialogID) {

        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();

        for(int i = 0; i < gcqbidlist.size(); i++){
            userIds.add(Integer.valueOf(gcqbidlist.get(i)));
        }

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject json = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();

        try {
            jsonbody.put("message",  partyName+ " Party has been cancelled ");
            jsonbody.put("type","partyCancelled");
            // custom parameters
//            jsonbody.put("PartyID", PartyID);
//            jsonbody.put("PartyName", partyName);


            jsonaps.put("alert","You have been added to Peer Chat");
            jsonaps.put("badge","1");
            jsonaps.put("content-available","1");

            json.put("body", jsonbody);
            json.put("aps",jsonaps);

        } catch (Exception e) {
            e.printStackTrace();
        }

        event.setMessage(json.toString());

        com.quickblox.messages.QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
                Log.e("notification ","success "+qbEvent);
                QBChatDialogCreation.deleteGroupDialog(cont,dialogID);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("notification ","errors");
                GenerikFunctions.hDialog();
            }
        });

    }


    //PushNotification for messages when user is offline
    public static void sendPrivateChatmessagePN(Context cont, Integer occupantId, String currentUser, String dialogId, String msg) {

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
            json.put("message",  currentUser+": "+msg);
            json.put("DialogId",dialogId);
            json.put("type","1-1 Chat OfflineMsg");
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
