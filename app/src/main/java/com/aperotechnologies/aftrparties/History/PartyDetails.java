package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    PartyTable party;
    RadioButton rdbtnByobyes, rdbtnByobNo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partydet);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;

        imgParty = (CircularImageView)findViewById(R.id.partyimage);
        txtpartyName = (TextView)findViewById(R.id.partyname);
        txthostName = (TextView)findViewById(R.id.hostname);
        txtpartyDesc = (TextView)findViewById(R.id.partydesc);
        txtpartyAddress = (TextView)findViewById(R.id.partyaddress);
        txtpartyStartTime = (TextView)findViewById(R.id.partystarttime);
        txtpartyEndTime = (TextView)findViewById(R.id.partyendtime);
        btnRequestant = (Button)findViewById(R.id.btnRequestantList);
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


            String StartTime = party.getStartTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(StartTime));
            String partystrDate = Validations.getMonthNo(calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + Validations.showTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            String EndDate = party.getEndTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(Long.parseLong(EndDate));
            String partyendDate = Validations.getMonthNo(calendar1.get(Calendar.MONTH)) + "/" + calendar1.get(Calendar.DAY_OF_MONTH) + "/" + calendar1.get(Calendar.YEAR) + " " + Validations.showTime(calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));

            txtpartyStartTime.setText(partystrDate);
            txtpartyEndTime.setText(partyendDate);

            if(party.getBYOB().equals("Yes")){
                rdbtnByobyes.setChecked(true);
                rdbtnByobNo.setChecked(false);
            }else{
                rdbtnByobyes.setChecked(false);
                rdbtnByobNo.setChecked(true);
            }


            String url = party.getPartyImage();

            if(url.equals(null) || url.equals("") || url.equals("N/A")){

                url = "";
//
            }else{
                url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
            }

            if(!url.equals("") || !url.equals(null) || !url.equals("N/A")){
                Picasso.with(cont).load(url).fit().centerCrop()
                        .placeholder(R.drawable.placeholder_user)
                        .error(R.drawable.placeholder_user)
                        .into(imgParty);
            }


        }
        catch (Exception e)
        {

        }


        if(PartyStatus.equals("Created")){
            btnRequestant.setVisibility(View.VISIBLE);
        }else{
            btnRequestant.setVisibility(View.GONE);
        }


        btnRequestant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("----- "," "+PartyID);
                Intent i = new Intent(cont,RequestantListActivity.class);
                PartyParceableData party1 = new PartyParceableData();
                party1.setPartyId(PartyID);
                party1.setPartyName(PartyName);
                party1.setStartTime(PartyStartTime);
                party1.setEndTime(PartyEndTime);
                party1.setPartyStatus(PartyStatus);
                Bundle mBundles = new Bundle();
                mBundles.putSerializable(ConstsCore.SER_KEY, party1);
                i.putExtras(mBundles);
                cont.startActivity(i);
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
