package com.aperotechnologies.aftrparties.QuickBloxOperations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Chats.ChatActivity;
import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.LocalNotifications.SetLocalNotifications;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 15/06/16.
 */
public class QBChatDialogCreation {


    public static void createPrivateChat(final Integer occupantId, final Context cont){

        final QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();

        privateChatManager.createDialog(occupantId, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(final QBDialog dialogs, Bundle args) {
                Log.e("onSuccess", " " + "privateChat"+ dialogs.getOccupants());

                QBPushNotifications.sendPeerChatNotification(cont, occupantId);

                List<Integer> usersIDs = new ArrayList<Integer>();
                for(Integer dialog : dialogs.getOccupants()){

                    Log.e("Dialog Occupants "," "+dialogs.getOccupants());
                    usersIDs.add(dialog);
                }

                // Get all occupants info
                //
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(usersIDs.size());
                //
                QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                        Log.e(":ChatService users"," "+users);
                        ChatService.getInstance().setDialogsUsers(users);


                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstsCore.EXTRA_DIALOG, dialogs);
                        Intent i = new Intent(cont,ChatActivity.class);
                        i.putExtras(bundle);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cont.startActivity(i);
                        GenerikFunctions.hDialog();
                        GenerikFunctions.showToast(cont, "1-1 Chat Created Successfully.");

                    }

                    @Override
                    public void onError(QBResponseException e) {

                        GenerikFunctions.hDialog();
                        GenerikFunctions.showToast(cont, "1-1 Chat Created Successfully.");
                        e.printStackTrace();
                    }



                });
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("onError", " " + privateChatManager);
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont, "Unable to create 1-1 chat, Please try again after some time");
            }




        });
    }


    public static void createGroupChat(final String PartyName, String PartyImage, final String GCQBID, final String GCFBID, final String PartyID, final Context cont, final String PartyEndTime)
    {
        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();

        /**Creating Group Chat*/
        ArrayList<Integer> occupantIdsList = getOccupantIdsWithUser(Integer.valueOf(GCQBID), cont);
        Log.e("occupantId", " " + GCQBID);
        Log.e("occupantIdsList", " " + occupantIdsList);

        QBDialog dialog = new QBDialog();
        dialog.setName(PartyName);
        dialog.setType(QBDialogType.GROUP);
        dialog.setPhoto(PartyImage);
        dialog.setOccupantsIds(occupantIdsList);


        groupChatManager.createDialog(dialog, new QBEntityCallback<QBDialog>()
        {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args)
            {
                Log.e("onSuccess", " ");

                AWSPartyOperations.updateDialogId(cont, dialog.getDialogId(), GCFBID, GCQBID, PartyID, PartyName);
                SetLocalNotifications.setLNotificationPartyRetention(cont, PartyName, PartyID , dialog.getDialogId(), PartyEndTime);




            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("onError", " ");
            }


        });

    }

    public static void updateGroupChat(final String PartyName, String DialogID, final String GCQBID, final String GCFBID, final String PartyID, final Context cont)
    {
        //578382b1a28f9ace8100000b

        Log.e("GCQBID"," "+GCQBID+" DialogID "+DialogID+" ");

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();

        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
        requestBuilder.addUsers(Integer.parseInt(GCQBID));

        QBDialog dialog = new QBDialog();
        dialog.setType(QBDialogType.GROUP);
        String val = DialogID;
        dialog.setDialogId(val);

        groupChatManager.updateDialog(dialog, requestBuilder, new QBEntityCallback<QBDialog>() {

            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                Log.e("updateDialog "," onSuccess");
                GenerikFunctions.showToast(cont, "Request has been approved");
                QBPushNotifications.sendApprovedPN(GCFBID, GCQBID, PartyID, PartyName, cont);
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Log.e("updateDialog "," onError");
            }
        });
    }

    public static ArrayList<Integer> getOccupantIdsWithUser(Integer GCQBID, Context cont) {
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        occupantIdsList.add(GCQBID);
        occupantIdsList.add(Integer.valueOf(sharedPreferences.getString(m_config.QuickBloxID,"")));
        return occupantIdsList;
    }



    public static void deleteGroupDialog(String DialogId)
    {

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.deleteDialog(DialogId, true, new QBEntityCallback<Void>()
        {

            @Override
            public void onSuccess(Void aVoid, Bundle bundle)
            {
                Log.e("Onsuccess","delete Dialog ");
                GenerikFunctions.hDialog();


            }

            @Override
            public void onError(QBResponseException errors)
            {
                Log.e("onError","delete Dialog ");
                GenerikFunctions.hDialog();
            }
        });
    }


}
