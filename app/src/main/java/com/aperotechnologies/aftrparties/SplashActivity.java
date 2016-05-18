package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Login.LoginActivity;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBSettings;

import io.fabric.sdk.android.Fabric;

/**
 * Created by hasai on 06/05/16.
 */
public class SplashActivity extends Activity
{

    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    Context cont = this;

    static final String APP_ID = "40454";//"34621";
    static final String AUTH_KEY = "sYpuKrOrGT4pG6d";//"q6aK9sm6GCSmtru";
    static final String AUTH_SECRET = "hVx9RNMT4emBK5K";//"uTOm5-R4zYyR-DV";
    static final String ACCOUNT_KEY = "VLBr2asUuw9uHDFC7qgb";//"bzbtQDLez742xU468TXt";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config=Configuration_Parameter.getInstance();

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

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
                        initializeProcess();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
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


    public void initializeProcess()
    {
        if(LoginValidations.isFBLoggedIn())
        {
            //Initialise here
            // initialize Chat service
            try
            {

                if (sharedpreferences.getString(m_config.QBFBLoginDone,"No").equals("Yes"))
                {
                    QBChatService.init(getApplicationContext());
                    final QBChatService chatService = QBChatService.getInstance();
                    m_config.chatService = chatService;
                    Log.e("m_config.chatService",chatService.toString() +"   aaa");
                    Log.e("m_config.chatService token",m_config.chatService.getToken() +"   aaa");
                    Log.e("m_config.chatService token",m_config.chatService +"   aaa");
                    Log.e("Chat Service successfully initialised","At login");

                }
                else
                {

                }

                //chatLogout(user);//
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
