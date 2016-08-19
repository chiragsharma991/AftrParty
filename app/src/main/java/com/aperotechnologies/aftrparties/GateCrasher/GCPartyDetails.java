package com.aperotechnologies.aftrparties.GateCrasher;

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
import com.aperotechnologies.aftrparties.History.HostCancelPartyAPI;
import com.aperotechnologies.aftrparties.History.HostProfileActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
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
 * Created by hasai on 19/08/16.
 */
public class GCPartyDetails extends Activity
{

    Configuration_Parameter m_config;
    Context cont;
    CircularImageView imgParty;
    TextView txtpartyName, txthostName, txtpartyDesc, txtpartyAddress, txtpartyStartTime, txtpartyEndTime;
    RadioButton rdbtnByobyes, rdbtnByobNo;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcpartydet);

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
        rdbtnByobyes = (RadioButton) findViewById(R.id.byobYes);
        rdbtnByobNo = (RadioButton) findViewById(R.id.byobNo);

        GCPartyParceableData partyy = (GCPartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
        final String PartyID = partyy.getPartyId();
        final String PartyName = partyy.getPartyName();
        final String HostName = partyy.getHostName();
        final String PartyStartTime = partyy.getStartTime();
        final String PartyEndTime = partyy.getEndTime();
        final String PartyByob = partyy.getEndTime();
        final String PartyAddress = partyy.getPartyaddress();
        final String Desc = partyy.getDescription();




        txthostName.setText(HostName);
        txtpartyName.setText(PartyName);
        txtpartyDesc.setText(Desc);
        txtpartyAddress.setText(PartyAddress);


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        //Log.e(" party starttime"," "+party.getStartTime()+" "+party.getEndTime());
        String StartTime = PartyStartTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(StartTime));
        String partystrDate = Validations.getMonthNo(calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + Validations.showTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        //Log.e(" partystrDate"," "+partystrDate);

        String EndDate = PartyEndTime;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(Long.parseLong(EndDate));
        String partyendDate = Validations.getMonthNo(calendar1.get(Calendar.MONTH)) + "/" + calendar1.get(Calendar.DAY_OF_MONTH) + "/" + calendar1.get(Calendar.YEAR) + " " + Validations.showTime(calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));
        //Log.e(" partyendDate"," "+partyendDate);

        txtpartyStartTime.setText(partystrDate);
        txtpartyEndTime.setText(partyendDate);

        if(PartyByob.equals("Yes")){
            rdbtnByobyes.setChecked(true);
            rdbtnByobNo.setChecked(false);
        }else{
            rdbtnByobyes.setChecked(false);
            rdbtnByobNo.setChecked(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }
}
