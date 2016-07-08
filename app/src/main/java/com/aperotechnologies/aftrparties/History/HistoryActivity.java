package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.GateCrasher.PartyConversion;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 19/05/16.
 */
public class HistoryActivity extends Activity
{

    ListView listHistory;
    private ProgressBar footerView;
    TextView txtnoHistory;
    HistoryAdapter adapter;
    Configuration_Parameter m_config;
    Context cont;
    private ProgressBar progressBar;
    List PartiesList;
    List listadptParties;
    private int index = 0;
    private int count = 10;
    private int iterations;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        cont = this;
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        index = 0;
        count = 10;




        listHistory = (ListView) findViewById(R.id.listhistory);
        txtnoHistory = (TextView) findViewById(R.id.noHistory);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        footerView = (ProgressBar) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        listHistory.addFooterView(footerView);

        PartiesList = new ArrayList();
        listadptParties = new ArrayList();

        try
        {

            String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
            UserTable selectedUser = m_config.mapper.load(UserTable.class, FacebookID);// retrieve using particular key/primary key
            PartiesList = selectedUser.getParties();
            Log.e("finalpartyIdstatus ", " " + PartiesList);



            if(PartiesList == null)
            {
                txtnoHistory.setVisibility(View.VISIBLE);
                //GenerikFunctions.hideDialog(m_config.pDialog);
                progressBar.setVisibility(View.GONE);
                return;
            }
            else
            {
                txtnoHistory.setVisibility(View.GONE);

            }
            Collections.reverse(PartiesList);

            if(PartiesList.size()%10 == 0)
            {
                iterations = (int) PartiesList.size()/10;
            }
            else
            {
                iterations =  (int)(PartiesList.size()/10) +1;
            }

            Log.e("iterations"," "+iterations +"   "+index);


            int maxLimit = count;
            if (maxLimit > PartiesList.size())
            {
                maxLimit = PartiesList.size();
                listHistory.removeFooterView(footerView);
            }
            else
            {
                listHistory.removeFooterView(footerView);
                listHistory.addFooterView(footerView);
            }

            for(int i = index; i < maxLimit; i++)
            {
                PartiesClass Parties = (PartiesClass) PartiesList.get(i);
                listadptParties.add(Parties);

            }

            setHistoryListAdapter();

            listHistory.setOnScrollListener(onScrollListener());



        }
        catch (Exception ex)
        {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.showToast(cont,"There was an error loading data");
            progressBar.setVisibility(View.GONE);

        }

    }


    private void setHistoryListAdapter()
    {
        adapter = new HistoryAdapter(HistoryActivity.this, listadptParties);
        listHistory.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        index++;

    }


    private AbsListView.OnScrollListener onScrollListener() {


        return new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                Log.e("count "," "+count+" "+PartiesList.size());


                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if(index < iterations)
                    {
                        // Execute LoadMoreDataTask AsyncTask

                        int maxLimit = (index + 1) * count;
                        if (maxLimit > PartiesList.size())
                        {
                            maxLimit = PartiesList.size();
                            listHistory.removeFooterView(footerView);
                        }
                        else
                        {
                            listHistory.removeFooterView(footerView);
                            listHistory.addFooterView(footerView);
                        }

                        for (int i = (index * count); i < maxLimit; i++)
                        {
                            PartiesClass Parties = (PartiesClass) PartiesList.get(i);
                            listadptParties.add(Parties);
                        }


                        adapter.notifyDataSetChanged();
                        index++;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount)
            {

            }

        };
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
