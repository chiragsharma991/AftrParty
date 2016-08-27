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

    static Configuration_Parameter m_config;
    static List inviteesId = new ArrayList();


    /** Adding Parties to userTable at time of creating party*/
    public static class addPartyinUserwithFriends extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        UserTable user;
        List PartiesList;
        PartyTable partytable;
        String Status;
        String fromWhere;
        boolean value = false;
        boolean existParty = false;
        int position;


        public addPartyinUserwithFriends(UserTable user, PartyTable partytable, Context cont, String Status, String fromWhere, int position)
        {
            this.cont = cont;
            this.user = user;
            this.partytable = partytable;
            this.Status = Status;
            this.fromWhere = fromWhere;
            this.position = position;
            m_config = Configuration_Parameter.getInstance();

        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {

                PartiesList = user.getParties();

                if (PartiesList == null || PartiesList.size() == 0)
                {
                    PartiesList = new ArrayList();
                    existParty = false;
                    value = true;

                } else {
                    //add new entry to existing array

                    PartiesList = user.getParties();
                    for(int i  = 0 ; i < user.getParties().size(); i++)
                    {
                        if(user.getParties().get(i).getPartyid().equals(partytable.getPartyID()))
                        {
                            existParty = true;
                            value = true;
                            break;
                        }
                        else
                        {
                            existParty = false;
                            value = true;
                        }

                    }

                }


            } catch (Exception ex) {
                ex.printStackTrace();
                value = false;

            }
            finally
            {

                return value;

            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- addPartyinUserwithFriends"," "+v);

            if(v == true)
            {

                if(existParty == false && user.getRegistrationStatus().equals("Yes"))
                {
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
                }
                else
                {
                    if(existParty == false)
                    {
                        FriendsListActivity.unregisterfbFriends.add(user.getFBUserName());
                    }
                }


                new addGCtoPartywithFriends(cont, user,  partytable, "Pending", fromWhere, position).execute();

            }
            else
            {
                GenerikFunctions.hDialog();
                FriendsListActivity.friendlist.finish();
                GenerikFunctions.showToast(cont,"Party Request Failed, Please try again after some time.");


            }

        }
    }


    /** Adding GateCrashers to partyTable at time of requesting party*/
    public static class addGCtoPartywithFriends extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        UserTable user;
        PartyTable partytable;
        String Status;
        List GateCrasherList;
        String GCFBID,GCFBProfilePic,GCLKID,GCQBID;
        boolean value = false;
        boolean existGC = false;
        String fromWhere;
        int position;


        public addGCtoPartywithFriends(Context cont, UserTable user, PartyTable partytable, String Status, String fromWhere, int position)
        {
            this.cont = cont;
            this.user = user;
            this.partytable = partytable;
            this.Status = Status;
            this.fromWhere = fromWhere;
            this.position = position;
            m_config = Configuration_Parameter.getInstance();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                GCFBID = user.getFacebookID();
                GCFBProfilePic = user.getProfilePicUrl().get(0);
                GCLKID = user.getLinkedInID();
                GCQBID = user.getQuickBloxID();
                GateCrasherList = partytable.getGatecrashers();

                if (GateCrasherList == null || GateCrasherList.size() == 0) {
                    GateCrasherList = new ArrayList();
                    existGC = false;
                    value = true;


                } else {
                    //add new entry to existing array
                    GateCrasherList = partytable.getGatecrashers();

                    for (int i = 0; i < partytable.getGatecrashers().size(); i++)
                    {

                        if (partytable.getGatecrashers().get(i).getGatecrasherid().equals(GCFBID))
                        {
                            existGC = true;
                            value = true;
                            break;

                        }
                        else
                        {
                            existGC = false;
                            value = true;

                        }


                    }

                }

            } catch (Exception ex) {
                //Log.e("", "Error Update retrieving data");
                ex.printStackTrace();
                value = false;
            }

            finally {

                return value;

            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- addGCtoPartyTable"," "+position);

            if(v == true) {

                if(existGC == false && user.getRegistrationStatus().equals("Yes"))
                {

                    Log.e("came here ", " "+GCFBID);

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
                }
                else
                {
                    if(existGC == false){
                        FriendsListActivity.unregisterfbFriends.add(user.getFBUserName());
                    }

                }

                if(fromWhere.equals("friend"))
                {
                    //ids for notification to all invitees
                    inviteesId.add(Integer.valueOf(GCQBID));
                }



                if(position == 0)
                {

                    if(FriendsListActivity.unregisterfbFriends.size() != 0)
                    {
                        String name = "";

                        for(int i = 0; i < FriendsListActivity.unregisterfbFriends.size(); i++){
                            if(FriendsListActivity.unregisterfbFriends.size() == 1)
                            {
                                name += FriendsListActivity.unregisterfbFriends.get(i);
                            }
                            else
                            {
                                name += FriendsListActivity.unregisterfbFriends.get(i) + ",";
                            }
                        }


                        GenerikFunctions.showToast(cont, "Request to "+name+" was unable to send as their registration process is incomplete.");

                    }


                    //notification to host
                    JSONObject jsonObj = new JSONObject();
                    try {
                        jsonObj.put("HostQBID",partytable.getHostQBID());
                        jsonObj.put("HostFBID",partytable.getHostFBID());
                        jsonObj.put("PartyName",partytable.getPartyName());
                        jsonObj.put("PartyID",partytable.getPartyID());
                        jsonObj.put("PartyStartTime",partytable.getStartTime());
                        jsonObj.put("PartyEndTime",partytable.getEndTime());

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    sendRequestPN(jsonObj, cont);
                    sendPNtoFriends(inviteesId, partytable.getPartyName(), cont);
                    FriendsListActivity.friendlist.finish();
                    GenerikFunctions.showToast(cont,"Request has been send to Party");
                    GenerikFunctions.hDialog();

                }

            }
            else
            {
                GenerikFunctions.hDialog();
                FriendsListActivity.friendlist.finish();
                GenerikFunctions.showToast(cont,"Party Request Failed, Please try after some time.");


            }

        }
    }


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


        m_config = Configuration_Parameter.getInstance();
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
            jsonbody.put("type","requestSend");

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
                Log.e("notification ","success "+qbEvent);
//                GenerikFunctions.hDialog();
//                GenerikFunctions.showToast(cont,"Invitation sent successfully");


            }

            @Override
            public void onError(QBResponseException errors) {

//                GenerikFunctions.hDialog();
//                GenerikFunctions.showToast(cont,"Invitation sent successfully, Notification sending failed");

            }
        });

    }


}
