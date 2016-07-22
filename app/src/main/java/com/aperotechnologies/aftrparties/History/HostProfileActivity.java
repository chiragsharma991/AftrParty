package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class HostProfileActivity extends AppCompatActivity
{
    public static Configuration_Parameter m_config;
    public static Context cont;
  //  public static CardContainer mCardContainer;
    public static HostAdapter adapter;
    public static  String fbid, qbid, liid, img_url;
    TextView txt_header;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;
        viewPager = (ViewPager)findViewById(R.id.pager);
        txt_header = (TextView) findViewById(R.id.activity_title);
        txt_header.setText("Host");


        fbid = getIntent().getStringExtra("FBID");
        liid = getIntent().getStringExtra("LIID");
        qbid = getIntent().getStringExtra("QBID");
        img_url = getIntent().getStringExtra("IMG");

        Log.e("in Host Activity","11");
        adapter = new HostAdapter(cont,getSupportFragmentManager(),fbid,liid,qbid,img_url);
        viewPager.setAdapter(adapter);
        Log.e("in Host Activity","22");
        viewPager.setOnPageChangeListener(new CircularViewPagerHandler(viewPager));

    }



}
