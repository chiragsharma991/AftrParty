package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 19/05/16.
 */
public class HistoryActivity extends Activity
{

    ListView listHistory;
    TextView txtnoHistory;
    HistoryAdapter adapter;
    Configuration_Parameter m_config;
    Context cont;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);



        cont = this;
        m_config=Configuration_Parameter.getInstance();


        listHistory = (ListView) findViewById(R.id.listhistory);
        txtnoHistory = (TextView) findViewById(R.id.noHistory);

        List PartiesList = new ArrayList();
        Log.e("m_config.mapper ", " ------ " + m_config.mapper);
        try {

            String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
            UserTable selectedUser = m_config.mapper.load(UserTable.class, FacebookID);// retrieve using particular key/primary key
            PartiesList = selectedUser.getParties();
            Log.e("finalpartyIdstatus ", " " + PartiesList);
            if(PartiesList == null){
                txtnoHistory.setVisibility(View.VISIBLE);
                return;
            }else{
                txtnoHistory.setVisibility(View.GONE);
            }

            adapter = new HistoryAdapter(HistoryActivity.this, PartiesList) {

            };

            listHistory.setAdapter(adapter);

        } catch (Exception ex) {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();

        }

    }
}
