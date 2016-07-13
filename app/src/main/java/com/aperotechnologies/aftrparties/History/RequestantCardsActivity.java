package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by mpatil on 12/07/16.
 */
public class RequestantCardsActivity extends AppCompatActivity
{
    public static CardContainer mCardContainer;
    public static CustomisedAdapterCards adapter;
    CardModel card;
    public static Context cont;
    public static ProgressBar pd;
    public static Configuration_Parameter m_config;

    public static PartyParceableData partyy;
    List GCList, listadptGC;
    public static List gc_list;

    public static TextView txt_no_requests;
    public static ArrayList<CardModel> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_cards);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;
        pd = (ProgressBar) findViewById(R.id.pd);
        txt_no_requests = (TextView)findViewById(R.id.txt_no_requests);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);

        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
        // mCardContainer.setOrientation(Orientation.Disordered);

        adapter = new CustomisedAdapterCards(this);
        GCList = new ArrayList();
        gc_list = new ArrayList<GateCrashersClass>();
        listadptGC = new ArrayList();

        try
        {
            partyy = (PartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
            String PartyID = partyy.getPartyId();

            Log.e("partyID "," "+partyy.getPartyId() );
            PartyTable selparty = m_config.mapper.load(PartyTable.class, PartyID); // retrieve using particular key/primary key
            gc_list = selparty.getGatecrashers();
            cards = new ArrayList<>();
            Log.e("finalGCList ", " " + gc_list.size());
            if(gc_list == null)
            {
                txt_no_requests.setVisibility(View.VISIBLE);
                pd.setVisibility(View.GONE);
                return;
            }
            else
            {
                for(int i=0;i<gc_list.size();i++)
                {
                    Log.e("GCList  " +i,gc_list.get(i)+"    aa");
                    GateCrashersClass gc = (GateCrashersClass) gc_list.get(i);
                    Log.e("GCLis ID",gc.getGatecrasherid() +"   " + gc.getGcrequeststatus() + "   " + gc.getGcattendancestatus() + "  \n  " + gc.getgcfbprofilepic() + "  \n  " + gc.getgclkid() + "  " + gc.getgcqbid());

                    CardModel cm = new CardModel(gc.getGatecrasherid()+"*"+gc.getgclkid()+"*"+gc.getgcqbid()+"*"+gc.getGcrequeststatus(),  gc.getgcfbprofilepic(), getApplicationContext().getResources().getDrawable(R.drawable.picture1));

                    adapter.add(cm);
                }
            }
          //  setGCListAdapter();
        }
        catch (Exception ex)
        {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.showToast(cont,"There was an error loading data");
            txt_no_requests.setVisibility(View.VISIBLE);
            pd.setVisibility(View.GONE);
            pd.setVisibility(View.GONE);
        }

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
        // mCardContainer.setOrientation(Orientation.Disordered);

        mCardContainer.setAdapter(adapter);
    }

    public static void refillAdapter()
    {

        try
        {
            adapter = null;
            adapter = new CustomisedAdapterCards(cont);



//            partyy = (PartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
            String PartyID = partyy.getPartyId();

            Log.e("partyID from refillAdapter "," "+partyy.getPartyId() );
            PartyTable selparty = m_config.mapper.load(PartyTable.class, PartyID); // retrieve using particular key/primary key
            gc_list = selparty.getGatecrashers();
            cards = new ArrayList<>();
            Log.e("finalGCList ", " " + gc_list.size());
            if(gc_list == null)
            {
                txt_no_requests.setVisibility(View.VISIBLE);
                pd.setVisibility(View.GONE);
                return;
            }
            else
            {
                for(int i=0;i<gc_list.size();i++)
                {
                    Log.e("GCList  " +i,gc_list.get(i)+"    aa");
                    GateCrashersClass gc = (GateCrashersClass) gc_list.get(i);
                    Log.e("GCLis ID",gc.getGatecrasherid() +"   " + gc.getGcrequeststatus() + "   " + gc.getGcattendancestatus() + "  \n  " + gc.getgcfbprofilepic() + "  \n  " + gc.getgclkid() + "  " + gc.getgcqbid());

                    CardModel cm = new CardModel(gc.getGatecrasherid()+"*"+gc.getgclkid()+"*"+gc.getgcqbid()+"*"+gc.getGcrequeststatus(),  gc.getgcfbprofilepic(), cont.getResources().getDrawable(R.drawable.picture1));
                    adapter.add(cm);
                }
                mCardContainer.setAdapter(adapter);
                pd.setVisibility(View.GONE);
            }
            //  setGCListAdapter();
        }
        catch (Exception ex)
        {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.showToast(cont,"There was an error loading data");
            pd.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }
}
