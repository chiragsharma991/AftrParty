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
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.quickblox.chat.Consts;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 14/06/16.
 */
public class RequestantListActivity extends Activity
{

    ListView listRequestant;
    private ProgressBar footerView;
    TextView txtnoRequestant;
    RequestantAdapter adapter;
    Configuration_Parameter m_config;
    Context cont;
    private ProgressBar progressBar;
    PartyParceableData partyy;
    List GCList, listadptGC;

    private int index = 0;
    private int count = 10;
    private int iterations;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestant);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;
        GCList = new ArrayList();
        listadptGC = new ArrayList();

        listRequestant = (ListView) findViewById(R.id.listRequestant);
        txtnoRequestant = (TextView) findViewById(R.id.noRequestant);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        footerView = (ProgressBar) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        listRequestant.addFooterView(footerView);


        try {

            partyy = (PartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
            String PartyID = partyy.getPartyId();

            Log.e("partyID "," "+partyy.getPartyId());
            PartyTable selparty = m_config.mapper.load(PartyTable.class, PartyID); // retrieve using particular key/primary key
            GCList = selparty.getGatecrashers();

            Log.e("finalGCList ", " " + GCList);


            if(GCList == null)
            {
                txtnoRequestant.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            else
            {
                txtnoRequestant.setVisibility(View.GONE);

            }

            if(GCList.size()%10 == 0)
            {
                iterations = (int) GCList.size()/10;
            }
            else
            {
                iterations =  (int)(GCList.size()/10) +1;
            }

            Log.e("iterations"," "+iterations +"   "+index);

            int maxLimit = count;
            if (maxLimit > GCList.size())
            {
                maxLimit = GCList.size();
                listRequestant.removeFooterView(footerView);
            }
            else
            {
                listRequestant.removeFooterView(footerView);
                listRequestant.addFooterView(footerView);
            }

            for(int i = index; i < maxLimit; i++)
            {
                GateCrashersClass GC = (GateCrashersClass) GCList.get(i);
                listadptGC.add(GC);

            }

            setGCListAdapter();

            listRequestant.setOnScrollListener(onScrollListener());



        }
        catch (Exception ex)
        {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.showToast(cont,"There was an error loading data");
            progressBar.setVisibility(View.GONE);
        }

    }


    private void setGCListAdapter()
    {
        adapter = new RequestantAdapter(RequestantListActivity.this, GCList, partyy);
        listRequestant.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        index++;

    }


    private AbsListView.OnScrollListener onScrollListener() {


        return new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                Log.e("count "," "+count+" "+GCList.size());


                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if(index < iterations)
                    {
                        // Execute LoadMoreDataTask AsyncTask

                        int maxLimit = (index + 1) * count;
                        if (maxLimit > GCList.size())
                        {
                            maxLimit = GCList.size();
                            listRequestant.removeFooterView(footerView);
                        }
                        else
                        {
                            listRequestant.removeFooterView(footerView);
                            listRequestant.addFooterView(footerView);
                        }

                        for (int i = (index * count); i < maxLimit; i++)
                        {
                            GateCrashersClass GC = (GateCrashersClass) GCList.get(i);
                            listadptGC.add(GC);
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
