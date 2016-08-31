package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.RatingsOperations;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;

/**
 * Created by mpatil on 27/06/16.
 */
public class RatePartyActivity extends Activity
{
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    Context cont = this;
    TextView txtParty, txtHost, txtRatings;
    RatingBar ratingBar;
    Button btnrate;
    public static Activity rateparty;
    String PartyName,PartyId, GCFBID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_party);

        Bundle extras = getIntent().getExtras();

        final String rateBy = extras.getString("rateBy");

        if(rateBy.equals("Host"))
        {
            PartyName = extras.getString("PartyName");
            PartyId = extras.getString("PartyId");
            GCFBID = extras.getString("GCFBID");

        }
        else
        {
            PartyId = extras.getString("PartyId");
            GCFBID = extras.getString("GCFBID");
        }


        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();
        rateparty = this;

        txtParty = (TextView)findViewById(R.id.lblparty);
        txtHost = (TextView)findViewById(R.id.lblhost);
        txtRatings = (TextView)findViewById(R.id.txtRatingValue);
        ratingBar =  (RatingBar) findViewById(R.id.ratingBar);
        btnrate = (Button) findViewById(R.id.rate);


        txtParty.setText(PartyName);
        //txtHost.setText(HostName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser)
            {
                txtRatings.setText(String.valueOf(rating));
             }
        });

        btnrate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

               // Toast.makeText(RatePartyActivity.this, String.valueOf(ratingBar.getRating()), Toast.LENGTH_LONG).show();
                //update Ratings to AWS Function
                GenerikFunctions.sDialog(cont, "Rating...");


                if(rateBy.equals("Host"))
                {
                    new RatingsOperations.RatingsByHostinPartyTable(GCFBID, PartyId, cont, String.valueOf(ratingBar.getRating())).execute();
                }
                else
                {
                    new RatingsOperations.RatingsByGCinPartyTable(GCFBID,PartyId, cont, String.valueOf(ratingBar.getRating())).execute();
                }



            }
        });
    }



}
