package com.aperotechnologies.aftrparties.GateCrasher;

/**
 * Created by hasai on 24/08/16.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.FriendsInvitationAWS;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBPushNotifications;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.FBFriendsList;
import com.aperotechnologies.aftrparties.model.FriendsSelectedList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsListActivity extends FragmentActivity {

    private FBFriendsListAdapter fbfriendsAdapter ;
    Configuration_Parameter m_config;
    public static List <String> unregisterfbFriends;
    Context cont;
    public static Activity friendlist;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist_activity);

        final ListView mainListView = (ListView) findViewById(R.id.mainListView);
        Button btnInvite  = (Button) findViewById(R.id.btnInvite);
        m_config = Configuration_Parameter.getInstance();
        cont = this;
        friendlist = this;
        unregisterfbFriends = new ArrayList();

        String hostfbid = getIntent().getExtras().getString("hostfbid");
        final String partyid = getIntent().getExtras().getString("partyid");

        // Set our custom array adapter as the ListView's adapter.
        fbfriendsAdapter = new FBFriendsListAdapter(this, GateCrasherSearchActivity.friendsList, hostfbid);
        mainListView.setAdapter(fbfriendsAdapter);



        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                UserTable user;
                PartyTable party = null;

                Log.e("fbfriendsSelection", " "+fbfriendsAdapter.fbFriendsSelected.size());

                if(fbfriendsAdapter.fbFriendsSelected.size() != 0)
                {

                    GenerikFunctions.sDialog(cont, "Sending Request for Party");

                    // Add Req to FriendsList
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
                    String reqFbid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
                    String reqName = sharedPreferences.getString(m_config.Entered_User_Name,"");
                    FriendsSelectedList selList = new FriendsSelectedList(reqFbid, reqName, "me", true);
                    Log.e("selList"," "+selList);
                    fbfriendsAdapter.fbFriendsSelected.add(selList);

                    Log.e("after adding me", " "+fbfriendsAdapter.fbFriendsSelected.size());

                    for(int i = fbfriendsAdapter.fbFriendsSelected.size() - 1; i >= 0; i--)
                    {

                        List<FriendsSelectedList> friendsList = fbfriendsAdapter.fbFriendsSelected;

                        String friendFbId = friendsList.get(i).getId();
                        String friendFbName = friendsList.get(i).getFbname();
                        String fromWhere = friendsList.get(i).getType();

                        Log.e("friendFbName", " "+friendFbName);

                        try {
                            user = m_config.mapper.load(UserTable.class, friendFbId);
                            party = m_config.mapper.load(PartyTable.class, partyid);

                            if (user != null)
                            {
//                                if (user.getRegistrationStatus().equals("Yes"))
//                                {
                                    new FriendsInvitationAWS.addPartyinUserwithFriends(user, party, cont, "Pending", fromWhere, i).execute();

//                                }
//                                else
//                                {
                                    //unregisterfbFriends.add(friendFbName);

                                //}

                            }
                        }
                        catch (Exception e)
                        {
                            FriendsListActivity.friendlist.finish();
                            GenerikFunctions.showToast(cont,"Party Request Failed, Please try after some time.");
                            GenerikFunctions.hDialog();
                        }

                    }



                    
                }
                else
                {
                    GenerikFunctions.showToast(FriendsListActivity.this,"Please select your friends for invitation");

                }




            }
        });


    }


}
