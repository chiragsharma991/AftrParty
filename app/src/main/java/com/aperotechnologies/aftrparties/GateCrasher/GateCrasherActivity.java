package com.aperotechnologies.aftrparties.GateCrasher;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.History.HistoryAdapter;
import com.aperotechnologies.aftrparties.R;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 06/05/16.
 */
public class GateCrasherActivity extends Activity
{

    ListView listGateCrasher;
    GateCrasherAdapter adapter;
    Configuration_Parameter m_config;
    Context cont;

        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gatecrasher);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);

            cont = this;
            m_config=Configuration_Parameter.getInstance();
            Crouton.cancelAllCroutons();
            m_config.foregroundCont = this;


            listGateCrasher = (ListView) findViewById(R.id.listgatecrasher);
            new GetData().execute();




        }


    public class GetData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {

            try {


                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                Log.e("scanExpression", " " + scanExpression);

                PaginatedScanList<PartyTable> result = m_config.mapper.scan(
                        PartyTable.class, scanExpression);

                Log.e("result size", " " + result.size());

//                        for (int i = 0; i < result.size(); i++) {
//                                Log.e("Created By ", " " + result.get(i).getPartyID());
//
//
//                        }

                adapter = new GateCrasherAdapter(GateCrasherActivity.this, result) {

                };



            } catch (Exception ex) {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listGateCrasher.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
