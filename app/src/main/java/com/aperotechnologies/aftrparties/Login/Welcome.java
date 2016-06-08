package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LIUserInformation;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.quickblox.chat.QBChatService;
import com.facebook.AccessToken;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mpatil on 25/05/16.
 */
public class Welcome extends Activity
{
    Button btn_login,btn_register;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;

    Context cont = this;
    CallbackManager callbackManager;
    LoginManager loginManager;

    ArrayList<String> permissions;
    String linkedinStart="";
    String Token;
    String AdvancedConnectionsLinkedIn="https://api.linkedin.com/v1/people/~:(id,first-name,email-address,last-name,num-connections,picture-urls::(original),positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,field-of-study,start-date,end-date,notes),publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)?format=json";


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.welcome);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config= Configuration_Parameter.getInstance();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");
        permissions.add("user_friends");
        permissions.add("user_hometown");
        permissions.add("user_photos");


        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(i);
                Intent intent = new Intent(cont, HomePageActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(LoginValidations.isFBLoggedIn())
                {
                    Log.e("FB Logged In","Yes");

                    try
                    {
                        Log.e("Inside try","Yes");
                        Log.e("AccessToken",LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue().toString());
                        Intent intent = new Intent(cont, HomePageActivity.class);
                        startActivity(intent);

                    }
                    catch(NullPointerException  e)
                    {
                        Log.e("Inside NullPointerException","Yes");
                        Log.e("Calling Linked in login","Yes");
                        startLinkedInProcess();
                    }
                }
                else
                {
                    Log.e("FB Logged Out","Yes");
                    loginManager.logInWithReadPermissions(Welcome.this, permissions);
                    Log.e("Before Login FB","Yes");
                    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
                    {
                        @Override
                        public void onSuccess(LoginResult loginResult)
                        {
                            Log.e("1", "LoginManager FacebookCallback onSuccess");
                            AccessToken token = loginResult.getAccessToken();
                            Log.e("AccessToken", token.toString() + "");
                            if(loginResult.getAccessToken() != null)
                            {
                                Log.i("1", "Access Token:: " + loginResult.getAccessToken());

                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putString(m_config.FBLoginDone,"Yes");
                                editor.apply();

                                try
                                {
                                    Log.e("LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue().toString()",LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue().toString() +"   aa");
                                    Intent intent = new Intent(cont, HomePageActivity.class);
                                    startActivity(intent);
                                }
                                catch(NullPointerException  e)
                                {
                                    Log.e("Cslling Linked in login","Yes");
                                    startLinkedInProcess();
                                }
                            }
                        }

                        @Override
                        public void onCancel()
                        {
                            Log.e("OnCancel", "LoginManager FacebookCallback onCancel");
                        }

                        @Override
                        public void onError(FacebookException e)
                        {
                            Log.e("OnError", "LoginManager FacebookCallback onError");
                        }
                    });
                    Log.e("After Login FB","Check for LI login status");



                }
            }
        });

    }


    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void startLinkedInProcess()
    {
        linkedinStart="Yes";
        Log.e("In startLinkedInProcess","Yes");
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener()
        {
            @Override
            public void onAuthSuccess()
            {
                Token = LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token",Token+"");
            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");
                GenerikFunctions.showToast(cont, "failed  linked in " + error.toString());
                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    GenerikFunctions.showToast(cont, "Please accept permissions " );
                    linkedinStart="Yes";
                    startLinkedInProcess();
                }
                else if(error.toString().trim().contains("UNKNOWN_ERROR"))
                {
                    Log.e("Inside Else","Yes");
                    GenerikFunctions.showToast(cont, "failed  linked in login " + error.toString());
                }
            }
        }, true);
    }

    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //For FB
        Log.e("Inside OnActivityResult","Yes");
        if (linkedinStart.equals(""))
        {
            //For FB
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            //For LI
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            Log.i("Request Code phase 2", requestCode + "   " + resultCode + "  " + data);
            Log.e("Token from start", Token + "");
            if (Token == null)
            {
                GenerikFunctions.showToast(cont,"LI Login Failed");

                //startLinkedInProcess();
            }
            else
            {
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(cont, AdvancedConnectionsLinkedIn, new ApiListener()
                {
                    @Override
                    public void onApiSuccess(ApiResponse result)
                    {
                        SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.putString(m_config.LILoginDone,"Yes");
                        editor.apply();

                        Intent intent = new Intent(cont, HomePageActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onApiError(LIApiError error)
                    {
                        Log.e("Linked In Error", error.toString());
                        GenerikFunctions.showToast(cont,"Li Error  onApiError  "+ error.toString());


                    }
                });
            }
        }

    }


}
