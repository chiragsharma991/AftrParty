package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.InAppPurchase.InAppBillingActivity;
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
        //FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config=Configuration_Parameter.getInstance();

        // Initialize the Amazon Cognito credentials provider
        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
//
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
//                    if (sharedpreferences.getBoolean(m_config.FBLoginDone, false) == false)
//                    {
//                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(i);


                    Intent i = new Intent(SplashActivity.this, InAppBillingActivity.class);
                    startActivity(i);


//                    }
//                    else
//                    {
//                        GenerikFunctions.showToast(cont,"Facebook login id done. Go for LinkedIn");
//
//                    }


                }
            }
        };
        timer.start();
    }
}
///https://docs.aws.amazon.com/devicefarm/latest/developerguide/getting-started.html