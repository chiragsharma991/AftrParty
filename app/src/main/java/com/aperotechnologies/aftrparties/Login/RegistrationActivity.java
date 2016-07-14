package com.aperotechnologies.aftrparties.Login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;

import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
//import com.aperotechnologies.aftrparties.MyLifecycleHandler;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.SplashActivity;
import com.aperotechnologies.aftrparties.model.FBCurrentLocationInformation;
import com.aperotechnologies.aftrparties.model.FbHomelocationInformation;

import com.aperotechnologies.aftrparties.model.FbProfilePictureData;
import com.aperotechnologies.aftrparties.model.FbUserInformation;
import com.aperotechnologies.aftrparties.model.LIPictureData;
import com.aperotechnologies.aftrparties.model.LIUserInformation;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.quickblox.core.QBSettings;

import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.aperotechnologies.aftrparties.Login.LoginTableColumns.FB_USER_ID;


/**
 * Created by hasai on 06/05/16.
 */
//Test Release Hash by Harshda for in app billing : U2a248y9BNwUWs2YwdA7v3w3Do8=

/*LinkedIn App
Client ID: 752m6fkgel868f
Client Secret: yxNWdXkj0iZwG3wq*/
public class RegistrationActivity extends Activity
{
    //Harshada
    private static final String TAG = "LoginActivity";

    PlayServicesHelper playServicesHelper;

    //Meghana
    Button btn_login;
    CallbackManager callbackManager;
    LoginManager loginManager;
    ArrayList<String> permissions;
    AccessToken token;
    FbUserInformation fbUserInformation;
    FBCurrentLocationInformation fBCurrentLocationInformation;
    FbHomelocationInformation fbHomelocationInformation;
    FbProfilePictureData fbProfilePictureData;
    LoggedInUserInformation loggedInUserInfo;
   // LIPictureInformation liPictureInformation;
    //picture-urls::(original)
//    String AdvancedConnectionsLinkedIn="https://api.linkedin.com/v1/people/~:(id,first-name,email-address,last-name,num-connections,picture-url,positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,field-of-study,start-date,end-date,notes),publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)?format=json";

    String AdvancedConnectionsLinkedIn="https://api.linkedin.com/v1/people/~:(id,first-name,api-standard-profile-request,email-address,last-name,public-profile-url,num-connections,picture-urls::(original),positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,field-of-study,start-date,end-date,notes),publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)?format=json";
    LIUserInformation liUserInformation;
    LIPictureData liPictureData;
    Gson gson;
    EditText edt_usr_name, edt_usr_email, edt_usr_phone;
    RelativeLayout layout_parent_login;
    String inputToastDisplay = "";

    public static String linkedinStart="";
    public static String Token;
    Iterator iterator;
    int total_friends_count = 0;
    //For Face Detection

    //General
    Configuration_Parameter m_config;
    static Context cont;
    SharedPreferences sharedpreferences;
    SQLiteDatabase sqldb;
    DBHelper helper;
    TelephonyManager mTelephony;
    private static final  int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    String regId;
    String verifyStatus = "0";
    public static ProgressDialog reg_pd = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        gson=new Gson();
        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();
        Welcome.wl_pd = null;
        reg_pd = new ProgressDialog(this);
        reg_pd.setCancelable(false);
        SplashActivity.pd = null;

        //Meghana
        //UI Components
        edt_usr_name = (EditText) findViewById(R.id.edt_usr_name);
        edt_usr_email = (EditText) findViewById(R.id.edt_usr_email);
        edt_usr_phone = (EditText) findViewById(R.id.edt_usr_phone);
        btn_login = (Button) findViewById(R.id.btn_login);
        layout_parent_login = (RelativeLayout) findViewById(R.id.layout_parent_login);
      //  m_config.faceOverlayView = new FaceOverlayView(LoginActivity.this);
        //m_config.faceOverlayView.setTag(1);
        //Log.i("Face Tag",m_config.faceOverlayView.getTag()+"");

        //FB Variables
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");
        permissions.add("user_friends");
        permissions.add("user_hometown");
        permissions.add("user_photos");

         getDebugHashKey();

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
                Log.e("Login Clicked", "yes");
                dismissCursor();
                validateUserInput();
            }
        });

        try
        {
          Log.e("Shrd Pref in Login",sharedpreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
          sharedpreferences.getString(m_config.Entered_Email,"N/A") + "   "
                    + sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
        }
        finally
        {
            Log.e("Preferences cleared","yes");
        }

        if (sharedpreferences.getString(m_config.Entered_User_Name, "").length() > 0)
        {
            edt_usr_name.setText(sharedpreferences.getString(m_config.Entered_User_Name, "UserName"));
            edt_usr_email.setText(sharedpreferences.getString(m_config.Entered_Email, "Email"));
            edt_usr_phone.setText(sharedpreferences.getString(m_config.Entered_Contact_No, "Contact No"));
        }

        edt_usr_email.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                {
                   // Log.e("Has focus","Has Focus");
                }
                else
                {
                    Log.e("removedfocus","yes");
                    String enteredmail = edt_usr_email.getText().toString().trim()+"";
                    edt_usr_email.setText(enteredmail);
                }
            }
        });

        edt_usr_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            Boolean handled = false;
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_usr_phone.clearFocus();
                    btn_login.performClick();
                    handled = true;
                }
                return handled;
            }
        });

//        if(sharedpreferences.getString(m_config.FBLoginDone,"").equals("") || sharedpreferences.getString(m_config.FBLoginDone,"").equals("No"))
//        {
//            linkedinStart="";
//        }
//        else
//        {
//            linkedinStart="Yes";
//        }
    }

    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
                try
                {
                   // setLIUserProfile("");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(cont, AdvancedConnectionsLinkedIn, new ApiListener()
                {
                    @Override
                    public void onApiSuccess(ApiResponse result)
                    {
                        try
                        {
                            JSONObject jsonObject = result.getResponseDataAsJson();
                            setLIUserProfile(jsonObject.toString());
                            Log.e("jsonresponse", "aa  " + result.toString() + " ");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            //GenerikFunctions.showToast(cont,"Li Error catch   "+ e.toString());

                            try
                            {
                                setLIUserProfile("");

                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onApiError(LIApiError error)
                    {
                        Log.e("Linked In Error", error.toString());
                        //GenerikFunctions.showToast(cont,"Li Error  onApiError  "+ error.toString());
                        try
                        {
                            setLIUserProfile("");
                            liUserInformation= new LIUserInformation();
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
//                        //Harshada
                    }
                });
            }
        }
    }

    public  void  setLIUserProfile(String response)
    {
        try
        {
            Log.e("setLIUserProfile response", response + "");
            if (response.length() == 0 || response.equals(""))
            {
                Log.e("In If", "setLIUserProfile");
                liUserInformation = new LIUserInformation();
                liPictureData = new LIPictureData();
            }
            else
            {
                Log.e("In else", "setLIUserProfile");
                liUserInformation = gson.fromJson(response.toString(), LIUserInformation.class);

//            Log.e("Response In Else ", response.toString() + "");
//            Log.e("LI Email ", liUserInformation.getEmailAddress());
//            Log.e("LI Id", liUserInformation.getId());
//            Log.e("LI emailAddress", liUserInformation.getEmailAddress());
//            Log.e("LI numConnections", liUserInformation.getNumConnections());
//            Log.e("LI headline", liUserInformation.getHeadline());
//            Log.e("LI firstName", liUserInformation.getFirstName());
//            Log.e("LI lastName", liUserInformation.getLastName());

                if(liUserInformation.getLIPictureData() != null)
                {
                    liPictureData = new LIPictureData();
                    if(liPictureData.equals(liUserInformation.getLIPictureData()))
                    {
                        Log.e("Both LI  Pics Empty  ","Is Empty");
                    }
                    else
                    {
                        liPictureData = liUserInformation.getLIPictureData();
                    }
                }
                else
                {
                    liPictureData = new LIPictureData();
                }
                Log.e("LI Pic Info ", liPictureData.get_total() + "     " + liPictureData.getvalues().size() +  "    " + liPictureData.getvalues().get(0));
          }

            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
            Log.i("User Query  : ", Query);
            Cursor cursor = sqldb.rawQuery(Query, null);
            Log.e("Cursor count",cursor.getCount()+"");
            if(cursor.getCount() == 0)
            {
                Log.e("Onside update if","Yes");
            }
            else
            {
                Log.e("LI firstName", liUserInformation.getFirstName());
                Log.e("LI lastName", liUserInformation.getLastName());

                Log.e("--------"," "+liPictureData);

                String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                        + LoginTableColumns.LI_USER_ID  + " = '" + liUserInformation.getId() + "', "
                        + LoginTableColumns.LI_USER_EMAIL  + " = '" + liUserInformation.getEmailAddress() + "', "
                        + LoginTableColumns.LI_USER_PROFILE_PIC  + " = '" + liPictureData.getvalues().get(0).toString() + "', "
                        + LoginTableColumns.LI_USER_CONNECTIONS  + " = '" + liUserInformation.getNumConnections() + "', "
                        + LoginTableColumns.LI_USER_FIRST_NAME + " = '" + liUserInformation.getFirstName() + "', "
                        + LoginTableColumns.LI_USER_LAST_NAME + " = '" + liUserInformation.getLastName() + "', "
                        + LoginTableColumns.LI_USER_HEADLINE + " = '" + liUserInformation.getHeadline() + "' "
                        + " where " + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                Log.i("update User "+fbUserInformation.getFbId().trim(), Update);
                sqldb.execSQL(Update);

                loggedInUserInfo.setLI_USER_ID(liUserInformation.getId());
                loggedInUserInfo.setLI_USER_FIRST_NAME(liUserInformation.getFirstName());
                loggedInUserInfo.setLI_USER_LAST_NAME(liUserInformation.getLastName());
                loggedInUserInfo.setLI_USER_EMAIL(liUserInformation.getEmailAddress());
                loggedInUserInfo.setLI_USER_PROFILE_PIC(liPictureData.getvalues().get(0).toString());
                loggedInUserInfo.setLI_USER_CONNECTIONS(liUserInformation.getNumConnections());
                loggedInUserInfo.setLI_USER_HEADLINE(liUserInformation.getHeadline());

                //AWS Storage of LI data
                Log.e("Before LI AWS Storage","Yes");
                LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
                //AWSLoginOperations.addLIUserInfo(cont,loggedInUserInformation);
                new AWSLoginOperations.addLIUserInfo(cont,loggedInUserInformation).execute();

                Log.e("Shrd Pref aftr LILginDne",sharedpreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedpreferences.getString(m_config.Entered_Email,"N/A") + "   "
                        + sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
            }
        }
        catch (Exception e)
        {
            if(reg_pd.isShowing())
            {
                reg_pd.dismiss();
            }
            Toast.makeText(cont,"Linked In Login Failed",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    //Harshada
    @Override
    public void onBackPressed()
    {
        //Meghana
        //Clear Focus from all edit texts
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(edt_usr_name.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(edt_usr_email.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(edt_usr_phone.getWindowToken(), 0);
//        edt_usr_name.clearFocus();
//        edt_usr_email.clearFocus();
//        edt_usr_phone.clearFocus();
//        edt_usr_name.setCursorVisible(false);
//        edt_usr_email.setCursorVisible(false);
//        edt_usr_phone.setCursorVisible(false);

        edt_usr_name.clearFocus();
        edt_usr_email.clearFocus();
        edt_usr_phone.clearFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null){
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        finish();

//        //Harshada
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationActivity.this);
//        alertDialogBuilder
//                .setTitle("Exit")
//                .setMessage("Are you sure you want to exit?")
//                .setCancelable(false)
//                .setNegativeButton("No", null)
//                .setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id)
//                            {
//                                finish();
////                                Intent intent = new Intent(Intent.ACTION_MAIN);
////                                intent.addCategory(Intent.CATEGORY_HOME);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                startActivity(intent);
//                                Intent intent = new Intent(RegistrationActivity.this,Welcome.class);
//                                startActivity(intent);
//                            }
//                        });
//        alertDialogBuilder.show();
        super.onBackPressed();
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
    public void dismissCursor()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        if (Validations.isEmpty(edt_usr_name) || Validations.isEmpty(edt_usr_email) || Validations.isEmpty(edt_usr_phone))
        {
            if (Validations.isEmpty(edt_usr_name))
            {
                inputToastDisplay = "Please enter user name";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
            else if (Validations.isEmpty(edt_usr_email))
            {
                inputToastDisplay = "Please enter email";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
            else if (Validations.isEmpty(edt_usr_phone))
            {
                inputToastDisplay = "Please enter contact no";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
        }
        else
        {
            //Check for valid email pattern
            if (Validations.isValidEmailId(edt_usr_email.getText().toString().trim()))
            {
                //Check for valid contact no
                if(Validations.isValidMobile(edt_usr_phone))
                {
                    //check for network availability
                    if (GenerikFunctions.chkStatus(cont))
                    {
                        //Start Facebook Login Here
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(m_config.Entered_User_Name, edt_usr_name.getText().toString().trim());
                        editor.putString(m_config.Entered_Email, edt_usr_email.getText().toString().trim());
                        editor.putString(m_config.Entered_Contact_No, edt_usr_phone.getText().toString().trim());
                        editor.apply();

                        processLogin();
                        //EmailVerification(sharedpreferences.getString(m_config.Entered_Email,"Email"));
                    }
                    else
                    {
                        GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    }
                }
                else
                {
                    GenerikFunctions.showToast(cont, "Enter Valid Contact No");
                    //edt_usr_phone.setText("");
                }
            }
            else
            {
                GenerikFunctions.showToast(cont, "EnterValid Mail");
                //edt_usr_email.setText("");
            }
        }
    }

    //Meghana
    public void processLogin()
    {
        try
        {
            loginManager.logInWithReadPermissions(RegistrationActivity.this, permissions);

//            ArrayList<String> publish_perm = new ArrayList<>();
//            publish_perm.add("publish_actions");//
//           loginManager.logInWithPublishPermissions(RegistrationActivity.this,publish_perm);

        //    Log.e("Inside start login", "yes");
            FacebookDataRetieval();
        }
        catch (Exception e)
        {
            Log.e("External Exception", e.toString());
            e.printStackTrace();
        }
    }

    //Meghana
    //Ask for declined FB permissions
    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {
//        for (int i = 0; i < declined_perm.size(); i++)
//        {
//            Log.e("From dec perm func", declined_perm.get(i));
//        }

        loginManager.logInWithReadPermissions(RegistrationActivity.this, declined_perm);
        FacebookDataRetieval();
    }

    //Meghana
    //Check if user logged in FB app or not
    public boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.e("Current AccessToken","Yes");
        return accessToken != null;

    }
    //Meghana 13-5-16
    //After all permissions get FB Data
    public void FacebookDataRetieval()
    {
        Log.e("Inside FB data retreive","Yes");
        linkedinStart="";
        loginManager.registerCallback(callbackManager,
            new FacebookCallback<LoginResult>()
            {
                @Override
                public void onSuccess(LoginResult loginResult)
                {

                 //   https://graph.facebook.com/v2.6/me/messages?access_token=<PAGE_ACCESS_TOKEN>

//                    // App code
                    Log.e("FB Login Success", "Yes");
                    token = loginResult.getAccessToken();
                    Log.e("AccessToken", token.toString() + "    " +token);

                    Set<String> given_perm = token.getPermissions();
                    iterator = given_perm.iterator();

//                    while (iterator.hasNext())
//                    {
//                        String perm_name = iterator.next().toString();
//                        Log.e("Given Permission in: ", perm_name + " ");
//                    }

                    ArrayList<String> declined_permissions = new ArrayList<String>();
                    Set<String> declined_perm = token.getDeclinedPermissions();
                    iterator = declined_perm.iterator();
                    while (iterator.hasNext())
                    {
                        String perm_name = iterator.next().toString();
                 //       Log.e("declined_permission in: ", perm_name + " ");
                        declined_permissions.add(perm_name);
                    }
                    if (declined_perm.size() > 0)
                    {
                        askForDeclinedFBPermissions(declined_permissions);
                    }
                    else
                    {
                        retrieveFBMeData();
                    }
                    //DELETE /{user-id}/permissions/{permission-name}
                }

                @Override
                public void onCancel()
                {

                    Log.e("Login onCancel", "Yes");
                    GenerikFunctions.showToast(cont,"Please provide permissions for app login");
                }

                @Override
                public void onError(FacebookException error)
                {
                    error.printStackTrace();
                    Log.e("Login error", "error" + error.toString());

                    if (error instanceof FacebookAuthorizationException)
                    {
                        if (AccessToken.getCurrentAccessToken() != null)
                        {
                            LoginManager.getInstance().logOut();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(m_config.Entered_User_Name, "");
                            editor.putString(m_config.Entered_Email, "");
                            editor.putString(m_config.Entered_Contact_No, "");
                            editor.commit();
                            startFBLoginScenario();
                        }
                    }
                }
            });
    }

    //If already logged in FB
    public void startFBLoginScenario()
    {
        if (isFBLoggedIn())
        {
            token = AccessToken.getCurrentAccessToken();
            Log.e("Token of current user","yes");
            Set<String> given_perm = token.getPermissions();
            iterator = given_perm.iterator();
            while (iterator.hasNext())
            {
                String perm_name = iterator.next().toString();
        //        Log.e("Given Permission from already logged in : ", perm_name + " ");
            }

            ArrayList<String> declined_permissions = new ArrayList<String>();
            Set<String> declined_perm = token.getDeclinedPermissions();
            iterator = declined_perm.iterator();
            while (iterator.hasNext())
            {
                String perm_name = iterator.next().toString();
                //
                // Log.e("declined_permission from already loggoed in : ", perm_name + " ");
                declined_permissions.add(perm_name);
            }

            //If permissions are declined then ask for the permissions else get FB user data
            if (declined_perm.size() > 0)
            {
                askForDeclinedFBPermissions(declined_permissions);
            }
            else
            {
                retrieveFBMeData();
            }
        }
        else
        {
            processLogin();
        }
    }

    //Meghana
    //Get user data
    public void retrieveFBMeData()
    {
        GraphRequest request1 = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {

                        Log.e("Inside requestMeFbData","Yes");
                        reg_pd.setMessage("Loading data...");
                        reg_pd.show();

                        String emptyFields="";

                        // Application code
                       // Log.i("Me Request", object.toString());

//                        AddFriends addFriends = new AddFriends();
//                        addFriends.requestInvitableFriends(cont,token);

                        fbUserInformation = gson.fromJson(object.toString(), FbUserInformation.class);
                        Log.e("User FB Information --->","Information");
                        Log.e("getFbId Id: " , fbUserInformation.getFbId());
//                      Log.e("getGender: " , fbUserInformation.getGender());
//                      Log.e("getFbUserName: " , fbUserInformation.getFbUserName());
//                      Log.e("getEmail: " ,fbUserInformation.getEmail());
//                      Log.e("getBirthday: " ,fbUserInformation.getBirthday());


                        if(fbUserInformation.getBirthday().equals(null))
                        {
                            fbUserInformation.setBirthday("N/A");
                        }

                       // Log.e("getBirthday : " , fbUserInformation.getBirthday());

                        if(fbUserInformation.getEmail().equals(null))
                        {
                            fbUserInformation.setEmail("N/A");
                        }
                        else
                        {
                          //  Log.e("getEmail: " ,fbUserInformation.getEmail());
                        }
                        if(fbUserInformation.getFBLocationInformation()!= null)
                        {
                            fBCurrentLocationInformation = new FBCurrentLocationInformation();
                            if(fBCurrentLocationInformation.equals(fbUserInformation.getFBLocationInformation()))
                            {
                               // Log.e("Both current Location ","Is Empty");
                            }
                            else
                            {
                                fBCurrentLocationInformation = fbUserInformation.getFBLocationInformation();
                            }
                        }
                        else
                        {
                            fBCurrentLocationInformation = new FBCurrentLocationInformation();
                        }
//                        Log.e("CUrrent Location --->","Details");
//                        Log.e("getLocationId Id: " , fBCurrentLocationInformation.getLocationId());
//                        Log.e("getLocationName " , fBCurrentLocationInformation.getLocationName());
                        if(fbUserInformation.getFbHomelocationInformation() != null)
                        {
                            fbHomelocationInformation = new FbHomelocationInformation();
                            if(fbHomelocationInformation.equals(fbUserInformation.getFbHomelocationInformation()))
                            {
                              //  Log.e("Both Home Location ","Is Empty");
                            }
                            else
                            {
                                fbHomelocationInformation = fbUserInformation.getFbHomelocationInformation();
                            }
                        }
                        else
                        {
                            fbHomelocationInformation = new FbHomelocationInformation();
                        }

//                        Log.e("Home Location --->","Details");
//                        Log.e("getLocationId Id: " , fbHomelocationInformation.getLocationId() +"  aa");
//                        Log.e("getLocationName " , fbHomelocationInformation.getLocationName()+"  aa");

                        if(fbUserInformation.getFbProfilePictureData().equals(null))
                        {
                            fbProfilePictureData = fbUserInformation.getFbProfilePictureData();
                            fbProfilePictureData.getFbPictureInformation().setUrl("N/A");
                        }
                        else
                        {
                            fbProfilePictureData=fbUserInformation.getFbProfilePictureData();
                        }
//
                       Log.e("getImgLink",fbProfilePictureData.getFbPictureInformation().getUrl());
//
//                        Log.e("Empty Fields","Yes   " + emptyFields +"    aa");

                        if(emptyFields.equals(""))
                        {
                            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
//                            Log.i("User Query  : ", Query);
                            Cursor cursor = sqldb.rawQuery(Query, null);
//                            Log.e("Cursir count",cursor.getCount()+"");
                            if(cursor.getCount() == 0)
                            {
                                //  Store here
                                storeUserInDb();
                            }
                            else
                            {
                              //Update User Entry
                                String updateUser = "Update " + LoginTableColumns.USERTABLE + " set " +
                                        LoginTableColumns.FB_USER_NAME + " = '" + fbUserInformation.getFbUserName().trim() + "', " +
                                        LoginTableColumns.FB_USER_GENDER + " = '" + fbUserInformation.getGender().trim() + "', " +
                                LoginTableColumns.FB_USER_BIRTHDATE + " = '" + fbUserInformation.getBirthday().trim() + "', " +
                                LoginTableColumns.FB_USER_EMAIL + " = '" + fbUserInformation.getEmail().trim() + "', " +
                                LoginTableColumns.FB_USER_HOMETOWN_ID + " = '" + fbHomelocationInformation.getLocationId().trim() + "', " +
                                LoginTableColumns.FB_USER_HOMETOWN_NAME + " = '" + fbHomelocationInformation.getLocationName().trim() + "', " +
                                LoginTableColumns.FB_USER_CURRENT_LOCATION_ID + " = '" + fBCurrentLocationInformation.getLocationId().trim() + "', " +
                                LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " = '" + fBCurrentLocationInformation.getLocationName().trim() + "'  where "
                                 + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                                Log.i("update User  "+ LoginTableColumns.FB_USER_ID , updateUser);
                                sqldb.execSQL(updateUser);

                                loggedInUserInfo = new LoggedInUserInformation();

                                loggedInUserInfo.setFB_USER_ID(fbUserInformation.getFbId());
                                loggedInUserInfo.setFB_USER_NAME(fbUserInformation.getFbUserName());
                                loggedInUserInfo.setFB_USER_GENDER(fbUserInformation.getGender());
                                loggedInUserInfo.setFB_USER_BIRTHDATE(fbUserInformation.getBirthday());
                                loggedInUserInfo.setFB_USER_EMAIL(fbUserInformation.getEmail());
                              //  loggedInUserInfo.setFB_USER_PROFILE_PIC(fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl());
                                loggedInUserInfo.setFB_USER_HOMETOWN_ID(fbHomelocationInformation.getLocationId().trim());
                                loggedInUserInfo.setFB_USER_HOMETOWN_NAME(fbHomelocationInformation.getLocationName().trim());
                                loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(fBCurrentLocationInformation.getLocationId().trim());
                                loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(fBCurrentLocationInformation.getLocationName().trim());

                                getFbFriendsCount();
                            }


                            cursor.close();
                        }
                        else
                        {

//                            Log.e("emptyFields " ,emptyFields);
                            GenerikFunctions.showToast(cont,"Please specify your "+ emptyFields + " in Facebook");
                        }
                    }
                });

        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "id,name,birthday,gender,email,location,picture.type(large),hometown");//,albums.fields(name,photos.fields(name,picture,source,created_time))");
        request1.setParameters(parameters1);
        request1.executeAsync();
    }

    //Meghana
    //Store User Information In database
    public void storeUserInDb()
    {
        ContentValues values = new ContentValues();
        values.put(LoginTableColumns.FB_USER_ID,fbUserInformation.getFbId().trim());
        values.put(LoginTableColumns.FB_USER_NAME,fbUserInformation.getFbUserName().trim());
        values.put(LoginTableColumns.FB_USER_GENDER,fbUserInformation.getGender().trim());
        values.put(LoginTableColumns.FB_USER_BIRTHDATE,fbUserInformation.getBirthday().trim());
        values.put(LoginTableColumns.FB_USER_EMAIL,fbUserInformation.getEmail().trim());
        values.put(LoginTableColumns.FB_USER_PROFILE_PIC,fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl().trim());
        values.put(LoginTableColumns.FB_USER_HOMETOWN_ID,fbHomelocationInformation.getLocationId().trim());
        values.put(LoginTableColumns.FB_USER_HOMETOWN_NAME, fbHomelocationInformation.getLocationName().trim());
        values.put(LoginTableColumns.FB_USER_CURRENT_LOCATION_ID,fBCurrentLocationInformation.getLocationId().trim());
        values.put(LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME, fBCurrentLocationInformation.getLocationName().trim());
        sqldb.insert(LoginTableColumns.USERTABLE, null, values);

        Log.i("Inserted User ", fbUserInformation.getFbId().trim() + "   " +
                fbHomelocationInformation.getLocationId() +"   aa   " + fbHomelocationInformation.getLocationName());

        loggedInUserInfo = new LoggedInUserInformation();

        loggedInUserInfo.setFB_USER_ID(fbUserInformation.getFbId());
        loggedInUserInfo.setFB_USER_NAME(fbUserInformation.getFbUserName());
        loggedInUserInfo.setFB_USER_GENDER(fbUserInformation.getGender());
        loggedInUserInfo.setFB_USER_BIRTHDATE(fbUserInformation.getBirthday());
        loggedInUserInfo.setFB_USER_EMAIL(fbUserInformation.getEmail());
        loggedInUserInfo.setFB_USER_PROFILE_PIC(fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl());
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(fbHomelocationInformation.getLocationId().trim());
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(fbHomelocationInformation.getLocationName().trim());
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(fBCurrentLocationInformation.getLocationId().trim());
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(fBCurrentLocationInformation.getLocationName().trim());

        /******/
        getFbFriendsCount();
    }

    private void getFbFriendsCount()
    {
        //Total No of friends
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/friends",    null,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        Log.e(" user friends list", response + "");
                        try
                        {
                            JSONObject graphObject = response.getJSONObject();
                            //Log.e("graphObject",graphObject.toString());

                            JSONObject summary = graphObject.getJSONObject("summary");
                            String totCount = summary.getString("total_count");
                            Log.e("Friend  totCount ", summary + "      " + totCount);

                            total_friends_count = (Integer.parseInt(totCount.trim()));

                            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
                           // Log.i("User Query  : ", Query);
                            Cursor cursor = sqldb.rawQuery(Query, null);
                           // Log.e("Cursor count",cursor.getCount()+"");

                            if(cursor.getCount() == 0)
                            {
                            }
                            else
                            {
                                String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                                        + LoginTableColumns.FB_USER_FRIENDS  + " = '" + total_friends_count + "'"
                                        + " where " + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                                //Log.i("update User  "+ LoginTableColumns.FB_USER_ID , Update);
                                sqldb.execSQL(Update);


                                //AWS Storage of FB Data
                                Log.e("Before FB AWS Storage","Yes");
                                LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
                                Log.e("FB Info in storage",loggedInUserInformation.getFB_USER_BIRTHDATE() +"   " +loggedInUserInformation.getFB_USER_HOMETOWN_NAME());

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(m_config.LoggedInFBUserID, loggedInUserInformation.getFB_USER_ID().trim());
                                editor.apply();


                                Log.e("Inside requestMeFbData","Yes");
                                new  checkFBUserInfo(loggedInUserInformation).execute();

                            //    new AWSLoginOperations.addFBUserInfo(cont,loggedInUserInformation,"Registration",sqldb).execute();

                            }


                        }
                        catch (Exception e)
                        {

                            System.out.println("Exception=" + e);
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void updateUserTableAndPrefs(UserTable user)
    {
        String updateUser = "Update " + LoginTableColumns.USERTABLE + " set " +
                LoginTableColumns.FB_USER_NAME + " = '" + user.getFBUserName() + "', " +
                LoginTableColumns.FB_USER_GENDER + " = '" + user.getGender().trim() + "', " +
                LoginTableColumns.FB_USER_BIRTHDATE + " = '" + user.getBirthDate().trim() + "', " +
                LoginTableColumns.FB_USER_EMAIL + " = '" + user.getSocialEmail().trim() + "', " +
                LoginTableColumns.FB_USER_HOMETOWN_NAME + " = '" + user.getFBHomeLocation().trim() + "', " +
                LoginTableColumns.FB_USER_PROFILE_PIC + " = '" + user.getProfilePicUrl().get(0) + "', " +
                LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " = '" + user.getFBCurrentLocation().trim() + "'  where "
                + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

        Log.i("update User "+fbUserInformation.getFbId().trim(), updateUser);
        sqldb.execSQL(updateUser);

        String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                + LoginTableColumns.LI_USER_ID  + " = '" + user.getLinkedInID() + "', "
                + LoginTableColumns.LI_USER_PROFILE_PIC  + " = '" + user.getLKProfilePicUrl().toString() + "', "
                + LoginTableColumns.LI_USER_CONNECTIONS  + " = '" + user.getLKConnectionsCount() + "', "
                + LoginTableColumns.LI_USER_HEADLINE + " = '" + user.getLKHeadLine() + "' "
                + " where " + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

        Log.i("update User "+fbUserInformation.getFbId().trim(), Update);
        sqldb.execSQL(Update);

        LoginValidations.QBStartSession(cont);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(m_config.Entered_User_Name,user.getName());
        editor.putString(m_config.Entered_Email,user.getEmail());
        editor.putString(m_config.Entered_Contact_No,user.getPhoneNumber());
        editor.putString(m_config.LoggedInFBUserID, user.getFacebookID().trim());
        editor.putString(m_config.FBLoginDone,"Yes");
        editor.putString(m_config.LILoginDone,"Yes");
        editor.putString(m_config.EmailValidationDone,"Yes");
        editor.putString(m_config.BasicFBLIValidationsDone,"Yes");
        editor.putString(m_config.OTPValidationDone,"Yes");
        editor.putString(m_config.FaceDetectDone,"Yes");
        editor.putString(m_config.FinalStepDone,"Yes");
        editor.apply();
        //Start QB Session Here
    }

//Check whether user exists at AWS or not
    public class checkFBUserInfo extends AsyncTask<Void, Void, Void>
    {
        UserTable selUserData = null;
        LoggedInUserInformation loggedInUserInformation;
        public checkFBUserInfo(LoggedInUserInformation loggedInUserInformation)
        {
            this.loggedInUserInformation = loggedInUserInformation;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            selUserData = m_config.mapper.load(UserTable.class, loggedInUserInformation.getFB_USER_ID());
            Log.e("selUserClass from Async", " " + selUserData);


            return null;
        }
        @Override
        protected void onPostExecute(Void v)
        {
            if(selUserData != null)
            {
                //Check login done flag
                if(selUserData.getRegistrationStatus().equals("Yes"))
                {
                    //Registration is already done
                    GenerikFunctions.showToast(cont," You are an existing user. Please Login...");
                    Intent i = new Intent(RegistrationActivity.this, Welcome.class);
                    startActivity(i);

                    if(reg_pd.isShowing()){
                        reg_pd.dismiss();
                    }

                    //updateUserTableAndPrefs(selUserData);
                }
                else
                {
                    new AWSLoginOperations.addFBUserInfo(cont,loggedInUserInformation,"Registration",sqldb).execute();
                }
            }
            else
            {
                //If not exists then call  normal registration flow
                new AWSLoginOperations.addFBUserInfo(cont,loggedInUserInformation,"Registration",sqldb).execute();

            }
        }
    }
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public static void startLinkedInProcess()
    {
        RegistrationActivity.reg_pd.show();

        linkedinStart="Yes";
        Log.e("In startLinkedInProcess","Yes");
        LISessionManager.getInstance(cont).init((Activity)cont, buildScope(), new AuthListener()
        {
            @Override
            public void onAuthSuccess()
            {
                Token = LISessionManager.getInstance(cont).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token",Token+"");
                //  GenerikFunctions.showToast(cont,"success   Linked login" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");
                GenerikFunctions.showToast(cont, "Linked In Login failed.");
                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    if(RegistrationActivity.reg_pd.isShowing())
                    {
                        RegistrationActivity.reg_pd.dismiss();
                    }
                    GenerikFunctions.showToast(cont, "Please accept permissions " );
                    linkedinStart="Yes";
                    startLinkedInProcess();
                }
                else //if(error.toString().trim().contains("UNKNOWN_ERROR"))
                {
                    if(RegistrationActivity.reg_pd.isShowing())
                    {
                        RegistrationActivity.reg_pd.dismiss();
                    }
                    Log.e("Inside Else","Yes");
                    //GenerikFunctions.showToast(cont, "failed  linked in login " + error.toString());
                }
            }
        }, true);
    }



    //function for enabling TelephonyManager for fetching deviceId
    public void getDeviceIdAndroid(String regId, Activity context)
    {


        if ((int) Build.VERSION.SDK_INT < 23)
        {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);

            new LoginValidations.subscribeToPushNotifications(regId,mTelephony,context).execute();
        }
        else
        {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                Log.e("If permission is not granted",", request for permission");

                Toast.makeText(cont, "Without this permission the app will be unable to receive Push Notifications.",Toast.LENGTH_LONG).show();

            }
            else
            {
                mTelephony = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                new LoginValidations.subscribeToPushNotifications(regId,mTelephony, context).execute();
                Log.e("If Permission is granted","");
            }

        }

    }

    //Callback function for Android M Permission
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        Log.e("grantResults.length"," "+grantResults.length+" "+grantResults[0]);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("Permission request is accepted","");
                    mTelephony = (TelephonyManager) cont.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cont);
                    String regId = pref.getString(m_config.temp_regId,"");
                    new LoginValidations.subscribeToPushNotifications(regId, mTelephony, (Activity) cont).execute();
                }
                else
                {
                    // permission denied
                    Log.e("Permission request is denied","");
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

                    boolean should = ActivityCompat.shouldShowRequestPermissionRationale((Activity) cont, Manifest.permission.READ_PHONE_STATE);
                    if(should)
                    {
                        //user denied without Never ask again, just show rationale explanation
                        new android.app.AlertDialog.Builder(RegistrationActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage("Without this permission the app will be unable to receive Push Notifications.Are you sure you want to deny this permission?")
                                .setPositiveButton("RE-TRY", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        Log.e("Click of Retry, If permission request is denied",",ask request for permission again");
                                        ActivityCompat.requestPermissions((Activity) cont,
                                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                                    }
                                })
                                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // do nothing
                                        Log.e("Click of I m sure",", permission request is denied");
                                        LoginValidations.checkPendingLoginFlags(cont);
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        //user has denied with `Never Ask Again`
                        Log.e("Click of Never ask again",", permission request is denied");
                        LoginValidations.checkPendingLoginFlags(cont);
                    }
                }
                break;
            }
        }
    }

    public void EmailVerification(String EmailId)
    {
        // Instantiate the RequestQueue.
        Log.e("Email Id","Yes  " + EmailId);
        RequestQueue queue = Volley.newRequestQueue((Activity)cont);
        String url ="http://api.verify-email.org/api.php?usr=harshadaasai&pwd=harshada26&check="+EmailId;
       // String url = "http://www.google.com";
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        // display response
                        Log.d("Response", response.toString());
                        try
                        {
                            JSONObject json = response;
                            verifyStatus = json.getString("verify_status");
                            Log.e("verifyStatus 11",verifyStatus + "   11");
                            if(verifyStatus.equals("1"))
                            {


                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(m_config.EmailValidationDone,"Yes");
                                editor.apply();

                                processLogin();
                            }
                            else
                            {
                                if(RegistrationActivity.reg_pd.isShowing())
                                {
                                    RegistrationActivity.reg_pd.dismiss();
                                }
                                GenerikFunctions.showToast(cont,"Email Verification Failed, Please Check your EmailId...");
                            }
                        }
                        catch(Exception e)
                        {
                            if(RegistrationActivity.reg_pd.isShowing())
                            {
                                RegistrationActivity.reg_pd.dismiss();
                            }
                            verifyStatus = "0";
                            Log.i("verifyStatus 22",verifyStatus + "   22");
                            GenerikFunctions.showToast(cont,"Email Verification Failed, Please Check your EmailId...");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if(RegistrationActivity.reg_pd.isShowing())
                        {
                            RegistrationActivity.reg_pd.dismiss();
                        }
                        Log.d("Error.Response", error.toString());
                        verifyStatus = "0";
                        Log.i("verifyStatus 33",verifyStatus + "   33");
                        GenerikFunctions.showToast(cont,"Email Verification Failed, Please Check your EmailId...");
                    }
                });

        RetryPolicy policy = new DefaultRetryPolicy(70000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
        // add it to the RequestQueue
        queue.add(getRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }


}
/*
*

//
//new GraphRequest(
//        AccessToken.getCurrentAccessToken(),
//        "/" + fbUserInformation.getFbId().trim() + "/
ble_friends",
//        null,
//        HttpMethod.GET,
//        new GraphRequest.Callback()
//        {
//public void onCompleted(GraphResponse response)
//        {
//        JSONObject res = response.getJSONObject();
//        Log.e("Taggable user friends list", response + "");
//        try
//        {
//        JSONObject graphObject = response.getJSONObject();
//        JSONArray dataArray = graphObject.getJSONArray("data");
//        Log.e("Taggable Array length", dataArray.length() + "");
//        JSONObject paging = graphObject.getJSONObject("paging");
//        Log.e("Taggable Paging", paging + "");
//        String next = paging.getString("next");
//        Log.e("Taggable next", next + "");
//        for (int i = 0; i < dataArray.length(); i++)
//        {
//        try
//        {
//        // here all that you want
//        JSONObject object = dataArray.getJSONObject(i);
//
//        // get facebook user id,name and picture
//        String str_id = object.getString("id");
//
//        String str_name = object.getString("name");
//
//
//        JSONObject picture_obj = object.getJSONObject("picture");
//
//        JSONObject data_obj = picture_obj.getJSONObject("data");
//
//        String str_url = data_obj.getString("url");
//        Log.e(i + "Taggable Info", str_id + "    " + str_name + "     " + "       " + str_url + "     " + picture_obj + "    " + data_obj);
//
//        }
//        catch (Exception e)
//        {
//        e.printStackTrace();
//        }
//        }
//        }
//        catch (Exception e)
//        {
//        System.out.println("Exception=" + e);
//        e.printStackTrace();
//        }
//        }
//        }
//        ).executeAsync();
//
//        */