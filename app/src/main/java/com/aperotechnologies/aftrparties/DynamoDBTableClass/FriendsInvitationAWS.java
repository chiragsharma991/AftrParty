package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.GateCrasher.FriendsListActivity;
import com.aperotechnologies.aftrparties.GateCrasher.PartyConversion;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBPushNotifications;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 27/08/16.
 */
public class FriendsInvitationAWS {



    /** Adding Parties to userTable at time of creating party*/
    public static void addPartyinUserwithFriends(UserTable user, PartyTable partytable, Context cont, String Status, String fromWhere, int position) {

        List PartiesList;
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        Boolean existParty = false;


        try
        {
            if(user.getRegistrationStatus().equals("Yes"))
            {
                Log.e(" addPartyinUserwithFriends RegistrationStatus===", " "+"Yes");

                PartiesList = user.getParties();
                if (PartiesList == null || PartiesList.size() == 0)
                {
                    PartiesList = new ArrayList();
                    PartiesClass Parties = new PartiesClass();
                    Parties.setPartyid(partytable.getPartyID());
                    Parties.setPartyname(partytable.getPartyName());
                    Parties.setPartystatus(Status);
                    Parties.setStarttime(partytable.getStartTime());
                    Parties.setEndtime(partytable.getEndTime());
                    Parties.setRatingsbyhost("0");
                    Parties.setRatingsbygc("0");
                    PartiesList.add(Parties);
                    user.setParties(PartiesList);
                    m_config.mapper.save(user);
                    addGCtoPartywithFriends(cont, user,  partytable, "Pending", fromWhere, position);

                }
                else
                {
                    PartiesList = user.getParties();
                    for(int i  = 0 ; i < PartiesList.size(); i++)
                    {
                        if(user.getParties().get(i).getPartyid().equals(partytable.getPartyID()))
                        {

                            existParty = true;
                            break;
                        }
                        else
                        {

                            existParty = false;

                        }
                    }

                    if(existParty == false)
                    {

                        Log.e("existParty"," "+"false");

                        PartiesClass Parties = new PartiesClass();
                        Parties.setPartyid(partytable.getPartyID());
                        Parties.setPartyname(partytable.getPartyName());
                        Parties.setPartystatus(Status);
                        Parties.setStarttime(partytable.getStartTime());
                        Parties.setEndtime(partytable.getEndTime());
                        Parties.setRatingsbyhost("0");
                        Parties.setRatingsbygc("0");
                        PartiesList.add(Parties);
                        user.setParties(PartiesList);
                        m_config.mapper.save(user);
                        addGCtoPartywithFriends(cont, user,  partytable, "Pending", fromWhere, position);
                    }else
                    {
                        Log.e("existParty"," "+"true");
                        addGCtoPartywithFriends(cont, user,  partytable, "Pending", fromWhere, position);
                    }
                }

            }
            else
            {
                Log.e(" addPartyinUserwithFriends RegistrationStatus===", " "+"No");

                FriendsListActivity.unregisterfbFriends.add(user.getFBUserName());
                addGCtoPartywithFriends(cont, user,  partytable, "Pending", fromWhere, position);
            }


        }
        catch (Exception e)
        {
            GenerikFunctions.hDialog();
        }

    }


    /** Adding GateCrashers to partyTable at time of requesting party*/
    public static void addGCtoPartywithFriends(Context cont, UserTable user, PartyTable partytable, String Status, String fromWhere, int position)
    {
        List GateCrasherList;
        String GCFBID,GCFBProfilePic,GCLKID,GCQBID;
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        Boolean existGC = false;

        try
        {
            if(user.getRegistrationStatus().equals("Yes"))
            {

                Log.e(" addGCtoPartywithFriends RegistrationStatus===", " "+"Yes");

                GCFBID = user.getFacebookID();
                GCFBProfilePic = user.getProfilePicUrl().get(0);
                GCLKID = user.getLinkedInID();
                GCQBID = user.getQuickBloxID();
                GateCrasherList = partytable.getGatecrashers();

                if (GateCrasherList == null || GateCrasherList.size() == 0)
                {
                    GateCrasherList = new ArrayList();
                    GateCrashersClass GateCrashers = new GateCrashersClass();
                    GateCrashers.setGatecrasherid(GCFBID);
                    GateCrashers.setGcrequeststatus(Status);
                    GateCrashers.setgcfbprofilepic(GCFBProfilePic);
                    GateCrashers.setgclkid(GCLKID);
                    GateCrashers.setgcqbid(GCQBID);
                    GateCrashers.setGcattendancestatus("No");
                    GateCrashers.setRatingsbyhost("0");
                    GateCrashers.setRatingsbygc("0");
                    GateCrasherList.add(GateCrashers);
                    partytable.setGatecrashers(GateCrasherList);
                    m_config.mapper.save(partytable);

                    if(fromWhere.equals("friend"))
                    {
                        //ids for notification to all invitees
                        m_config.inviteesId.add(Integer.valueOf(GCQBID));
                    }

                }
                else
                {
                    //add new entry to existing array
                    GateCrasherList = partytable.getGatecrashers();

                    for (int i = 0; i < partytable.getGatecrashers().size(); i++)
                    {

                        if (partytable.getGatecrashers().get(i).getGatecrasherid().equals(GCFBID))
                        {
                            existGC = true;
                            break;
                        }
                        else
                        {
                            existGC = false;

                        }

                    }

                    if(existGC == false)
                    {
                        GateCrashersClass GateCrashers = new GateCrashersClass();
                        GateCrashers.setGatecrasherid(GCFBID);
                        GateCrashers.setGcrequeststatus(Status);
                        GateCrashers.setgcfbprofilepic(GCFBProfilePic);
                        GateCrashers.setgclkid(GCLKID);
                        GateCrashers.setgcqbid(GCQBID);
                        GateCrashers.setGcattendancestatus("No");
                        GateCrashers.setRatingsbyhost("0");
                        GateCrashers.setRatingsbygc("0");
                        GateCrasherList.add(GateCrashers);
                        partytable.setGatecrashers(GateCrasherList);
                        m_config.mapper.save(partytable);

                        if(fromWhere.equals("friend"))
                        {
                            //ids for notification to all invitees
                            m_config.inviteesId.add(Integer.valueOf(GCQBID));
                        }
                    }
                    else
                    {

                    }

                }
            }



        } catch (Exception ex)
        {
            //Log.e("", "Error Update retrieving data");
            ex.printStackTrace();
            GenerikFunctions.hDialog();

        }


    }


    public static void setNotification(Context cont, PartyTable partytable) {

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();


        Log.e("inviteesId"," "+ m_config.inviteesId);


        if (FriendsListActivity.unregisterfbFriends.size() != 0) {
            String name = "";


            for (int i = 0; i < FriendsListActivity.unregisterfbFriends.size(); i++) {
                if (FriendsListActivity.unregisterfbFriends.size() == 1) {
                    name += FriendsListActivity.unregisterfbFriends.get(i);
                    Log.e("name ", " " + name);
                } else {
                    name += FriendsListActivity.unregisterfbFriends.get(i) + ",";
                    Log.e("name ", " ==== " + name);
                }
            }


            GenerikFunctions.showToast(cont, "Request to " + name + " was unable to send as their registration process is incomplete.");

        }


        //notification to host
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("HostQBID", partytable.getHostQBID());
            jsonObj.put("HostFBID", partytable.getHostFBID());
            jsonObj.put("PartyName", partytable.getPartyName());
            jsonObj.put("PartyID", partytable.getPartyID());
            jsonObj.put("PartyStartTime", partytable.getStartTime());
            jsonObj.put("PartyEndTime", partytable.getEndTime());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendRequestPN(jsonObj, cont);
        sendPNtoFriends(m_config.inviteesId, partytable.getPartyName(), cont);
        FriendsListActivity.friendlist.finish();
        GenerikFunctions.showToast(cont, "Request has been send to Party");
        GenerikFunctions.hDialog();

    }



    //PushNotification to Host after Party Request
    public static void sendRequestPN(JSONObject jsonObject, final Context cont) {

        Log.e("get jsonObj TAG"," "+jsonObject);

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
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
                //GenerikFunctions.hDialog();
                //GenerikFunctions.showToast(cont,"Request has been send to Party");


            }

            @Override
            public void onError(QBResponseException errors) {

                //GenerikFunctions.hDialog();
                //GenerikFunctions.showToast(cont,"Request has been send to Party, Notification sending failed");

            }
        });

    }


    //PushNotification to all invitees of facebook
    public static void sendPNtoFriends(List<Integer> inviteesId, String PartyName, final Context cont) {


        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        String username = sharedPreferences.getString(m_config.Entered_User_Name,"");

        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();

        for(int i = 0; i < inviteesId.size(); i++)
        {
            userIds.add(inviteesId.get(i));

        }

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject json = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        JSONObject jsonaps = new JSONObject();



        try {
            jsonbody.put("message", username +" has invited you for  "+PartyName);
            jsonbody.put("type","invited");

            jsonaps.put("alert", username +" has invited you for  "+PartyName);
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
                m_config.inviteesId = new ArrayList();
                Log.e("notification ","success "+qbEvent+" "+m_config.inviteesId);


            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });

    }


}
