package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.facebook.FacebookSdk;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hasai on 06/05/16.
 */
public class LoginActivity extends Activity
{
    //Harshada
    private static final String TAG = "LoginActivity";
    static final String APP_ID = "34621";
    static final String AUTH_KEY = "q6aK9sm6GCSmtru";
    static final String AUTH_SECRET = "uTOm5-R4zYyR-DV";
    static final String ACCOUNT_KEY = "bzbtQDLez742xU468TXt";

    QBUser primary_user;
    PlayServicesHelper playServicesHelper;

    //Meghana
    Button btn_login;
    CallbackManager callbackManager;
    LoginManager loginManager;
    ArrayList<String> permissions;
    AccessToken token;

    EditText edt_usr_name,edt_usr_email,edt_usr_phone;
    RelativeLayout layout_parent_login;

    String inputToastDisplay="";
    Iterator iterator;
    //General
    Configuration_Parameter m_config;
    Context cont;
    SharedPreferences sharedpreferences;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        cont=this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //Harshada
        primary_user = new QBUser();

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

         // Initialize the Amazon Cognito credentials provider
         final CognitoCachingCredentialsProvider credentialsProvider
                 = new CognitoCachingCredentialsProvider(getApplicationContext(),
                 getResources().getString(R.string.identity_pool_id),Regions.US_EAST_1);

        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        //DynamoDB client to create an Object m_config.mapper:
        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
        Log.e("m_config.mapper ", " " + m_config.mapper);

        //Meghana
        //UI Components
        edt_usr_name = (EditText)findViewById(R.id.edt_usr_name);
        edt_usr_email = (EditText)findViewById(R.id.edt_usr_email);
        edt_usr_phone = (EditText)findViewById(R.id.edt_usr_phone);
        btn_login = (Button)findViewById(R.id.btn_login);
        layout_parent_login = (RelativeLayout)findViewById(R.id.layout_parent_login);

        //FB Variables
        callbackManager = CallbackManager.Factory.create();
        loginManager=LoginManager.getInstance();

        permissions=new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");
        permissions.add("user_friends");
     //   permissions.add("taggable_friends");

       // getDebugHashKey();

        if(sharedpreferences.getString(m_config.Entered_User_Name,"").length()>0)
        {
            edt_usr_name.setText(sharedpreferences.getString(m_config.Entered_User_Name,"UserName"));
            edt_usr_email.setText(sharedpreferences.getString(m_config.Entered_Email,"Email"));
            edt_usr_phone.setText(sharedpreferences.getString(m_config.Entered_Contact_No,"Contact No"));
        }



        layout_parent_login.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                dismissCursor();
                return true;
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Login Clicked","yes");
                validateUserInput();
               // processLogin();
            }
        });

        if(isFBLoggedIn())
        {
            token = AccessToken.getCurrentAccessToken();

            Set<String> given_perm = token.getPermissions();
            iterator = given_perm.iterator();

            while (iterator.hasNext())
            {
                String perm_name=iterator.next().toString();
                Log.e("Given Permission: ",perm_name + " ");
            }

            ArrayList<String> declined_permissions=new ArrayList<String>();
            Set<String> declined_perm = token.getDeclinedPermissions();
            iterator = declined_perm.iterator();
            while (iterator.hasNext())
            {
                String perm_name=iterator.next().toString();
                Log.e("declined_permission : ",perm_name + " ");
                declined_permissions.add(perm_name);
            }
            if(declined_perm.size()>0)
            {
                askForDeclinedFBPermissions(declined_permissions);
            }
        }
        else
        {
        }
    }

    //Harshada
    public void QBStartSession()
    {
        QBAuth.createSession(new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("createSession", "onsuccess");
                try
                {
                    String token = QBAuth.getBaseService().getToken();
                    Log.e("New token", token);
                }
                catch (Exception e)
                {

                }


                loginWithFbQuickBlox("CAAGvR03Lv3ABAEYwSlAxqLrNOqNi7Ecp0avE9W9ULI0LB2ZBFZAXFJn1dZBS7kybcsprJt4bViAjDVdUsxD7ZBUUrNWsOj1hoLAn8GZCqSUGhzqZCk5oarzbwkhx6PumvtlZAktIsHRcAZBzD6edYzm0UdNZCHHyk0ZAAz8ZACS5yqYxCLXgZB9UQtxCl1cnHEw0YuAFZBZAlVToousGGfLfBZCl0yQU1DM4TaoPjX64u95pRhmdAZDZD","Meghana Apero","https://graph.facebook.com/129419790774542/picture?type=large");


            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("createSession","onerror");
                e.printStackTrace();
            }

        });
    }

    //Harshada
    public void loginWithFbQuickBlox(String accessToken, final String FullName, final String avatarUrl)
    {
        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, accessToken,
                null, new QBEntityCallback<QBUser>()
                {
                    @Override
                    public void onSuccess(QBUser user, Bundle args)
                    {
                        //Log.e("On success qbuser",user.toString() +"   " +user.getFullName());
                        primary_user=user;
                        m_config.primary_user=primary_user;
                        Log.e("Facebook login","Success"+" ");
                        Log.e("user",""+user);
                        if(!user.getFullName().equals(FullName))
                        {
                            user.setFullName(FullName);
                        }

                        uploadprofilePic(user, avatarUrl);

                        if(primary_user.equals(null) || primary_user==null)
                        {
                            //Log.e("primary user",null+"  null");
                        }
                        else
                        {
                            try
                            {
                                primary_user.setPassword(BaseService.getBaseService().getToken());
                            }
                            catch(BaseServiceException e)
                            {
                                e.printStackTrace();
                                // means you have not created a session before
                            }

                            // initialize Chat service
                            try
                            {
                                QBChatService.init(getApplicationContext());
                                final QBChatService chatService = QBChatService.getInstance();
                                m_config.chatService = chatService;
                                //playServicesHelper = new PlayServicesHelper(LoginActivity.this);
                                chatLogout();
                                chatLogin();//
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(QBResponseException errors)
                    {
                        Log.e("Facebook login","OnError");
                        errors.printStackTrace();
                    }
                });

    }

    //Harshada
    public void uploadprofilePic(QBUser user, String avatarUrl)
    {
        Log.e("here",":1----");

        if(user.getCustomData() == null || !user.getCustomData().equals(avatarUrl))
        {
            user.setCustomData(avatarUrl);
            Log.e("here",":2----");
        }


        QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
        {
            @Override
            public void onSuccess(QBUser user, Bundle args)
            {
                Log.e("user image "," "+user.getCustomData());
            }

            @Override
            public void onError(QBResponseException errors)
            {
            }
        });

    }

    //Harshada
    public void chatLogin()
    {
        m_config.chatService.login(primary_user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("ChatServicelogin","Success ");
                QBGroupChatManager groupChatManager  = m_config.chatService.getGroupChatManager();
                QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                m_config.groupChatManager=groupChatManager;
                m_config.privateChatManager=privateChatManager;
                playServicesHelper = new PlayServicesHelper(LoginActivity.this);
                Intent intent=new Intent(LoginActivity.this,HomePageActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(QBResponseException error)
            {
                // errror
                Log.e("ChatServicelogin","OnError "+error.toString());
                error.printStackTrace();
            }
        });
    }
    //Harshada
    public void chatLogout()
    {
        boolean isLoggedIn = m_config.chatService.isLoggedIn();

        if(isLoggedIn)
        {
            m_config.chatService.logout(new QBEntityCallback()
            {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    m_config.chatService.destroy();
                }

                @Override
                public void onError(QBResponseException errors)
                {
                }
            });
        }
    }

    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Harshada
    @Override
    public void onBackPressed()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_usr_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_usr_email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_usr_phone.getWindowToken(), 0);
        edt_usr_name.clearFocus();
        edt_usr_email.clearFocus();
        edt_usr_phone.clearFocus();
        edt_usr_name.setCursorVisible(false);
        edt_usr_email.setCursorVisible(false);
        edt_usr_phone.setCursorVisible(false);

        //Harshada
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        alertDialogBuilder.show();
    }

    //Meghana
    public void getDebugHashKey()
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.aperotechnologies.aftrparties", PackageManager.GET_SIGNATURES);
            for (Signature mysignature : info.signatures)
            {
                MessageDigest mymd = MessageDigest.getInstance("SHA");
                mymd.update(mysignature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(mymd.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //meghana
    //dismiss cursor on other screen area tab
    //clear edittext focus
    public  void dismissCursor()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_usr_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_usr_email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_usr_phone.getWindowToken(), 0);
        edt_usr_name.clearFocus();
        edt_usr_email.clearFocus();
        edt_usr_phone.clearFocus();
       // edt_usr_name.setCursorVisible(false);
       // edt_usr_email.setCursorVisible(false);
       // edt_usr_phone.setCursorVisible(false);

    }

    //Meghana
    public void validateUserInput()
    {
        //CHeck for empty or invalid input
        if(LoginValidations.isEmpty(edt_usr_name) || LoginValidations.isEmpty(edt_usr_email) || LoginValidations.isEmpty(edt_usr_phone))
        {
            if(LoginValidations.isEmpty(edt_usr_name))
            {
                inputToastDisplay="Please enter user name";
                GenerikFunctions.showToast(cont,inputToastDisplay);
            }
            else if(LoginValidations.isEmpty(edt_usr_email))
            {
                inputToastDisplay="Please enter email";
                GenerikFunctions.showToast(cont,inputToastDisplay);
            }
            else if(LoginValidations.isEmpty(edt_usr_phone))
            {
                inputToastDisplay="Please enter contact no";
                GenerikFunctions.showToast(cont,inputToastDisplay);
            }
        }
        else
        {
            //Check for valid email pattern
            if(LoginValidations.isValidEmailId(edt_usr_email.getText().toString().trim()))
            {
                //check for network availability
                if(GenerikFunctions.chkStatus(cont))
                {
                    //Start Facebook Login Here

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(m_config.Entered_User_Name, edt_usr_name.getText().toString().trim());
                    editor.putString(m_config.Entered_Email, edt_usr_email.getText().toString().trim());
                    editor.putString(m_config.Entered_Contact_No, edt_usr_phone.getText().toString().trim());
                    editor.apply();

                    processLogin();
                }
                else
                {
                    GenerikFunctions.showToast(cont,"Check Your Network Connectivity");
                }
            }
            else
            {
                GenerikFunctions.showToast(cont,"EnterValid Mail");
                edt_usr_email.setText("");
            }
        }
    }

    //Meghana
    public void processLogin()
    {
        try
        {
            loginManager.logInWithReadPermissions(LoginActivity.this, permissions);
            Log.e("Inside start login","yes");


            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>()
                    {
                        @Override
                        public void onSuccess(LoginResult loginResult)
                        {
                            // App code
                            Log.e("Login Success","Yes");
                            token=loginResult.getAccessToken();
                            Log.e("AccessToken",token.toString()+"");

                            Set<String> given_perm = token.getPermissions();
                            iterator = given_perm.iterator();

                            while (iterator.hasNext())
                            {
                                String perm_name=iterator.next().toString();
                                Log.e("Given Permission: ",perm_name + " ");
                            }

                            ArrayList<String> declined_permissions=new ArrayList<String>();
                            Set<String> declined_perm = token.getDeclinedPermissions();
                            iterator = declined_perm.iterator();
                            while (iterator.hasNext())
                            {
                                String perm_name=iterator.next().toString();
                                Log.e("declined_permission : ",perm_name + " ");
                                declined_permissions.add(perm_name);

                            }
                            if(declined_perm.size()>0)
                            {
                                askForDeclinedFBPermissions(declined_permissions);

                            }

                            GraphRequest request1 = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback()
                                    {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response)
                                        {
                                            // Application code
                                            Log.i("Me Request", object + "    " + response.getJSONObject() + "     " + response);
                                        }
                                    });
                            Bundle parameters1 = new Bundle();
                            parameters1.putString("fields", "id,name,link,birthday,gender,age_range,email,location");
                            request1.setParameters(parameters1);
                            request1.executeAsync();


                            //DELETE /{user-id}/permissions/{permission-name}
                        }

                        @Override
                        public void onCancel()
                        {
                            // App code
                            Log.e("Login onCancel","Yes");
                        }

                        @Override
                        public void onError(FacebookException error)
                        {
                            Log.e("Login error","error"+error.toString());
                        }
                    });

        }
        catch(Exception e)
        {
            Log.e("External Exception",e.toString());
            e.printStackTrace();
        }
    }

    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {
        for(int i=0;i<declined_perm.size();i++)
        {
            Log.e("From dec perm func",declined_perm.get(i));
        }
        loginManager.logInWithReadPermissions(LoginActivity.this, declined_perm);

//        Set<String> given_perm = token.getPermissions();
//        iterator = given_perm.iterator();
//
//        while (iterator.hasNext())
//        {
//            String perm_name=iterator.next().toString();
//            Log.e("Given Permission: ",perm_name + " ");
//        }
//
//        ArrayList<String> declined_permissions=new ArrayList<String>();
//        Set<String> declined_permission = token.getDeclinedPermissions();
//        iterator = declined_permission.iterator();
//        while (iterator.hasNext())
//        {
//            String perm_name=iterator.next().toString();
//            Log.e("declined_permission : ",perm_name + " ");
//            declined_permissions.add(perm_name);
//
//        }
//        if(declined_perm.size()>0)
//        {
//            askForDeclinedFBPermissions(declined_permissions);
//
//        }
    }

    public boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }
}