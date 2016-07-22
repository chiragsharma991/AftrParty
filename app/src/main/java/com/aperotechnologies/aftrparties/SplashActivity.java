package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.Login.OTPActivity;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import io.fabric.sdk.android.Fabric;

/**
 * Created by hasai on 06/05/16.
 */
public class SplashActivity extends Activity {
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;
    Context cont = this;
    String LIToken;
    public static  ProgressDialog pd = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        pd = new ProgressDialog(cont);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        RegistrationActivity.reg_pd = null;
        Welcome.wl_pd = null;
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
                    //Print all flags from local storage
                    Log.e("All Flags ","FB : " + sharedpreferences.getString(m_config.FBLoginDone,"No")
                    + "    LI : " + sharedpreferences.getString(m_config.LILoginDone,"No")
                    + "    BasicVal : " + sharedpreferences.getString(m_config.BasicFBLIValidationsDone,"No") +
                    "    Face :   " + sharedpreferences.getString(m_config.FaceDetectDone,"No") +
                            "    OTP :   " + sharedpreferences.getString(m_config.OTPValidationDone,"No") +
                            "    FinalStepDone :   " + sharedpreferences.getString(m_config.FinalStepDone,"No"));

                    //If all validations are donr then start Home Page
                    if(sharedpreferences.getString(m_config.FinalStepDone,"No").equals("Yes"))
                    {
                        Log.e("Inside if","yes");
                        Handler h = new Handler(cont.getMainLooper());
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                               if(pd!=null)
                               {
                                   pd.show();
                               }
                            }
                        });


                        //Condition to check LI session existance check
                        if(LISessionManager.getInstance(cont).getSession().getAccessToken() == null)
                        {
                            startLinkedInProcess();
                        }


                        //Start QB Session
                        Intent i = new Intent(cont, HomePageActivity.class);
                        cont.startActivity(i);
                        //LoginValidations.QBStartSession(cont);
                        //new AWSPartyOperations.RemovePendingPartiesAPI(cont, "155325838204047_1468823718211","137075106698189").execute();
                    }
                    else
                    {
                        //Else go forLogin Page
                        Log.e("Inside else","yes");
                        Intent intent = new Intent(cont,Welcome.class);
                        intent.putExtra("from","splash");
                        cont.startActivity(intent);
                    }
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    //Callback for Linked in session initialization
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //For LI
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        Log.i("Request Code phase 2", requestCode + "   " + resultCode + "  " + data);
        Log.e("Token from start", LIToken + "");
        if (LIToken == null)
        {
            Log.e("LI TOken Null","Yes");
        }
    }
    //Permissions from linked login
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public  void startLinkedInProcess()
    {
        Log.e("In startLinkedInProcess","Yes");
        LISessionManager.getInstance(cont).init((Activity)cont, buildScope(), new AuthListener()
        {
            @Override
            public void onAuthSuccess()
            {
                LIToken = LISessionManager.getInstance(cont).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token",LIToken+"");
                //  GenerikFunctions.showToast(cont,"success   Linked login" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");
                if(error.toString().trim().contains("USER_CANCELLED"))
                {

                    startLinkedInProcess();
                }
                else //if(error.toString().trim().contains("UNKNOWN_ERROR"))
                {
                    Log.e("Inside Else","Yes");
                    Toast.makeText(cont,"Linked In Login Failed",Toast.LENGTH_LONG).show();
                }
            }
        }, true);
    }

}

