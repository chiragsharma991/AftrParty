package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by mpatil on 13/07/16.
 */
public class HostDetailsCardsActivity extends Activity
{
    public static Configuration_Parameter m_config;
    public static  Context cont;
    public static CardContainer mCardContainer;
    public static HostCardAdapter adapter;
    public static  String fbid, qbid, liid, img_url;
    TextView txt_header;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_cards);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;

        txt_header = (TextView) findViewById(R.id.activity_title);
        txt_header.setText("Host");

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);
        adapter = new HostCardAdapter(cont);
        fbid = getIntent().getStringExtra("FBID");
        liid = getIntent().getStringExtra("LIID");
        qbid = getIntent().getStringExtra("QBID");
        img_url = getIntent().getStringExtra("IMG");

        Log.e("Retrieved ",fbid + "   " + qbid + "    " + liid + "     " +  img_url);


        CardModel cm = new CardModel(fbid+"*"+liid+"*"+qbid,  img_url, getApplicationContext().getResources().getDrawable(R.drawable.picture1));

        adapter.add(cm);
        mCardContainer.setAdapter(adapter);

    }

    public static void refillAdapter()
    {

        try
        {
            adapter = null;
            adapter = new HostCardAdapter(cont);

            CardModel cm = new CardModel(fbid+"*"+liid+"*"+qbid,  img_url, cont.getResources().getDrawable(R.drawable.picture1));

            adapter.add(cm);
            mCardContainer.setAdapter(adapter);

        }
        catch (Exception ex)
        {

        }
    }

}
