package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Login.LoginActivity;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by hasai on 06/05/16.
 */
public class SplashActivity extends Activity
{

    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    Context cont = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config=Configuration_Parameter.getInstance();

        Thread timer = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (sharedpreferences.getBoolean(m_config.FBLoginDone, false) == false)
                    {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        GenerikFunctions.showToast(cont,"Facebook login id done. Go for LinkedIn");
                    }
                }
            }
        };
        timer.start();
    }
}
