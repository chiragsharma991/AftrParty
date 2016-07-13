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

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
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

//    static final String APP_ID = "40454";//"34621";
//    static final String AUTH_KEY = "GXzMGfcx-pAQOBP";//"sYpuKrOrGT4pG6d";//"q6aK9sm6GCSmtru";
//    static final String AUTH_SECRET = "TZC8fTnAUDXSuYs";//"hVx9RNMT4emBK5K";//"uTOm5-R4zYyR-DV";
//    static final String ACCOUNT_KEY = "VLBr2asUuw9uHDFC7qgb";//"VLBr2asUuw9uHDFC7qgb";//"bzbtQDLez742xU468TXt";

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
        m_config.pDialog = new ProgressDialog(cont);


//        // Initialize the Amazon Cognito credentials provider
//        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
//                Regions.US_EAST_1 // Region
//        );
//
//        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
//        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
//
//
//        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
//        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);




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

                    Log.e("All Flags ","FB : " + sharedpreferences.getString(m_config.FBLoginDone,"No")
                    + "    LI : " + sharedpreferences.getString(m_config.LILoginDone,"No")
                    + "    BasicVal : " + sharedpreferences.getString(m_config.BasicFBLIValidationsDone,"No") +
                    "    Face :   " + sharedpreferences.getString(m_config.FaceDetectDone,"No") +
                            "    OTP :   " + sharedpreferences.getString(m_config.OTPValidationDone,"No") +
                            "    FinalStepDone :   " + sharedpreferences.getString(m_config.FinalStepDone,"No"));


                    if(sharedpreferences.getString(m_config.FinalStepDone,"No").equals("Yes"))
                    {
                        Log.e("Inside if","yes");
                        Handler h = new Handler(cont.getMainLooper());
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                m_config.pDialog.setMessage("Loading...");
                                m_config.pDialog.setCancelable(false);
                                if (!m_config.pDialog.isShowing())
                                {
                                    m_config.pDialog.show();
                                }
                            }
                        });

                        LoginValidations.QBStartSession(cont);
                    }
                    else
                    {
                        Log.e("Inside else","yes");
                        Intent intent = new Intent(cont,Welcome.class);
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
    protected void onPause() {
        super.onPause();

    }

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
                    //GenerikFunctions.showToast(cont, "failed  linked in login " + error.toString());
                }
            }
        }, true);
    }

}

