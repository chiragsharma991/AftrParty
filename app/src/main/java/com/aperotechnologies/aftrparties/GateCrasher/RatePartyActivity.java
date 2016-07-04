package com.aperotechnologies.aftrparties.GateCrasher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;

/**
 * Created by mpatil on 27/06/16.
 */
public class RatePartyActivity extends Activity
{
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    Context cont = this;
    TextView txtParty,txtHost, txtRatings;
    RatingBar ratingBar;
    Button rate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_party);

        Bundle extras = getIntent().getExtras();
        String PartyName = extras.getString("PartyName");
        String HostName = extras.getString("HostName");

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config=Configuration_Parameter.getInstance();

        txtParty = (TextView)findViewById(R.id.lblparty);
        txtHost = (TextView)findViewById(R.id.lblhost);
        txtRatings = (TextView)findViewById(R.id.txtRatingValue);
        ratingBar =  (RatingBar) findViewById(R.id.ratingBar);
        rate = (Button) findViewById(R.id.rate);


        txtParty.setText(PartyName);
        txtHost.setText(HostName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser)
            {
                txtRatings.setText(String.valueOf(rating));
             }
        });

        rate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(RatePartyActivity.this, String.valueOf(ratingBar.getRating()), Toast.LENGTH_LONG).show();

                //update Ratings to AWS Function
            }
        });
    }
}
