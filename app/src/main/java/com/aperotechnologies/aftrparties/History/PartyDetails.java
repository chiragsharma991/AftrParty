package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 24/06/16.
 */
public class PartyDetails extends Activity
{

    Configuration_Parameter m_config;
    Context cont;
    CircularImageView imgParty;
    TextView txtpartyName, txthostName, txtpartyDesc, txtpartyAddress, txtpartyStartTime, txtpartyEndTime;
    Button btnRequestant;
    TextView btnReqCancel;
    PartyTable party;
    RadioButton rdbtnByobyes, rdbtnByobNo;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partydet);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        imgParty = (CircularImageView)findViewById(R.id.partyimage);
        imgParty.setVisibility(View.GONE);
        txtpartyName = (TextView)findViewById(R.id.partyname);
        txthostName = (TextView)findViewById(R.id.hostname);
        txtpartyDesc = (TextView)findViewById(R.id.partydesc);
        txtpartyAddress = (TextView)findViewById(R.id.partyaddress);
        txtpartyStartTime = (TextView)findViewById(R.id.partystarttime);
        txtpartyEndTime = (TextView)findViewById(R.id.partyendtime);
        btnRequestant = (Button)findViewById(R.id.btnRequestantList);
        btnReqCancel = (TextView)findViewById(R.id.btnReqCancel);
        btnReqCancel.setVisibility(View.GONE);
        rdbtnByobyes = (RadioButton) findViewById(R.id.byobYes);
        rdbtnByobNo = (RadioButton) findViewById(R.id.byobNo);

        PartyParceableData partyy = (PartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
        final String PartyID = partyy.getPartyId();
        final String PartyName = partyy.getPartyName();
        final String PartyStartTime = partyy.getStartTime();
        final String PartyEndTime = partyy.getEndTime();
        final String PartyStatus = partyy.getPartyStatus();


        try
        {
            party = m_config.mapper.load(PartyTable.class, PartyID);// retrieve using particular key/primary key
            txthostName.setText(party.getHostName());
            txtpartyName.setText(party.getPartyName());
            txtpartyDesc.setText(party.getPartyDescription());
            txtpartyAddress.setText(party.getPartyAddress());


            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

            //Log.e(" party starttime"," "+party.getStartTime()+" "+party.getEndTime());
            String StartTime = party.getStartTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(StartTime));
            String partystrDate = Validations.getMonthNo(calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + Validations.showTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            //Log.e(" partystrDate"," "+partystrDate);

            String EndDate = party.getEndTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(Long.parseLong(EndDate));
            String partyendDate = Validations.getMonthNo(calendar1.get(Calendar.MONTH)) + "/" + calendar1.get(Calendar.DAY_OF_MONTH) + "/" + calendar1.get(Calendar.YEAR) + " " + Validations.showTime(calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));
            //Log.e(" partyendDate"," "+partyendDate);

            txtpartyStartTime.setText(partystrDate);
            txtpartyEndTime.setText(partyendDate);

            if(party.getBYOB().equals("Yes")){
                rdbtnByobyes.setChecked(true);
                rdbtnByobNo.setChecked(false);
            }else{
                rdbtnByobyes.setChecked(false);
                rdbtnByobNo.setChecked(true);
            }


//            String url = party.getPartyImage();
//
//            if(url.equals(null) || url.equals("") || url.equals("N/A")){
//
//                url = "";
////
//            }
//
//            if(!url.equals("") || !url.equals(null) || !url.equals("N/A")){
//                Picasso.with(cont).load(url).fit().centerCrop()
//                        .placeholder(R.drawable.placeholder_user)
//                        .error(R.drawable.placeholder_user)
//                        .into(imgParty);
//            }


        }
        catch (Exception e)
        {

            e.printStackTrace();
            Toast.makeText(PartyDetails.this,"There is an error while retrieving data, Please Try again after sometime",Toast.LENGTH_SHORT).show();
            finish();
        }


        final Long currentTime = Validations.getCurrentTime();//System.currentTimeMillis();
        final String partyStartTime = party.getStartTime();


        if(PartyStatus.equals("Created"))
        {
            //Logged user is Host
            btnRequestant.setText("Requestant");

            if(currentTime < Long.parseLong(partyStartTime)){
                btnReqCancel.setText("Cancel Party");//Host cancel
                btnReqCancel.setVisibility(View.VISIBLE);
            }

        }
        if(PartyStatus.equals("Approved"))
        {
            //Logged user is GateCrasher
            btnRequestant.setText("Host");
            if(currentTime < Long.parseLong(partyStartTime)) {
                btnReqCancel.setText("Cancel Request");// GC Cancel
                btnReqCancel.setVisibility(View.VISIBLE);
            }
        }
        if(PartyStatus.equals("Cancelled")) {
            //Logged user is GC but status is Cancelled
            LoggedInUserInformation loggedInUserInformation = new LoggedInUserInformation();
            String Username = sharedPreferences.getString(m_config.Entered_User_Name, "");
            if (party.getHostName().equals(Username))
            {
                btnRequestant.setText("Requestant");
            }
            else
            {
                btnRequestant.setText("Host");
            }
            btnReqCancel.setVisibility(View.GONE);
        }

        if(PartyStatus.equals("Pending") || PartyStatus.equals("Declined"))
        {
            //Logged user is GC but status is Pending, Declined
            btnRequestant.setText("Host");
            btnReqCancel.setVisibility(View.GONE);
        }


        btnRequestant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(btnRequestant.getText().toString().equals("Requestant"))
                {
                    if(party.getGatecrashers() == null || party.getGatecrashers().size() == 0)
                    {
                        GenerikFunctions.showToast(cont, "There are no requestants for this party");
                    }
                    else
                    {

                        //open details of requestant
                        Log.e("----- ", " " + PartyID);
                        Intent i = new Intent(cont, RequestantActivity.class);
                        PartyParceableData party1 = new PartyParceableData();
                        party1.setPartyId(PartyID);
                        party1.setPartyName(PartyName);
                        party1.setStartTime(PartyStartTime);
                        party1.setEndTime(PartyEndTime);
                        party1.setPartyStatus(PartyStatus);
                        i.putExtra("from","partydetails");
                        Bundle mBundles = new Bundle();
                        mBundles.putSerializable(ConstsCore.SER_KEY, party1);
                        i.putExtras(mBundles);
                        cont.startActivity(i);
                    }
                }
                else if (btnRequestant.getText().toString().equals("Host"))
                {
                    //open details of Host
                    //open details of Host
                    try
                    {
                        UserTable host = m_config.mapper.load(UserTable.class,party.getHostFBID());
                        Intent i = new Intent(cont,HostProfileActivity.class);
                        i.putExtra("FBID",host.getFacebookID());
                        i.putExtra("LIID",host.getLinkedInID());
                        i.putExtra("QBID",host.getQuickBloxID());
                        i.putExtra("IMG",host.getProfilePicUrl().get(0));
                        cont.startActivity(i);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }

            }
        });


        btnReqCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (btnReqCancel.getText().toString().equals("Cancel Request"))
                {
                    //GC cancel party request button click

                    if(currentTime < Long.parseLong(partyStartTime)) {
                        GenerikFunctions.sDialog(cont, "Cancelling Request...");
                        String GCID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
                        new AWSPartyOperations.updateGCinPartyTable(GCID, PartyID, "Cancelled", cont, btnReqCancel).execute();
                    }
                    else
                    {
                        GenerikFunctions.showToast(cont, "Cannot cancel party");
                    }

                }
                else if (btnReqCancel.getText().toString().equals("Cancel Party"))
                {
                    // Host Cancel Party Button click

                    if(currentTime < Long.parseLong(partyStartTime))
                    {
                        //call for Host cancel Party
                        GenerikFunctions.sDialog(cont, "Cancelling Party...");
                        new HostCancelPartyAPI(cont, PartyID, btnReqCancel);
                    }
                    else
                    {
                        GenerikFunctions.showToast(cont, "Cannot cancel party");
                    }

                }
            }

        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }
}
