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

import org.json.JSONException;
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

    //Meghana
    static Button btn_register;
    CallbackManager callbackManager;
    LoginManager loginManager;
    ArrayList<String> permissions;
    AccessToken token;
    FbUserInformation fbUserInformation;
    FBCurrentLocationInformation fBCurrentLocationInformation;
    FbHomelocationInformation fbHomelocationInformation;
    FbProfilePictureData fbProfilePictureData;
    LoggedInUserInformation loggedInUserInfo;

    //String AdvancedConnectionsLinkedIn="https://api.linkedin.com/v1/people/~:(id,first-name,api-standard-profile-request,email-address,last-name,public-profile-url,num-connections,picture-urls::(original)?format=json";

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
    public static Activity register;
    SharedPreferences sharedpreferences;
    SQLiteDatabase sqldb;
    DBHelper helper;
    TelephonyManager mTelephony;
    private static final  int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

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
        register = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        gson=new Gson();
        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();
        reg_pd = new ProgressDialog(this);
        reg_pd.setCancelable(false);
        Welcome.wl_pd = null;
        SplashActivity.pd = null;

        //Meghana
        //UI Components
        edt_usr_name = (EditText) findViewById(R.id.edt_usr_name);
        edt_usr_email = (EditText) findViewById(R.id.edt_usr_email);
        edt_usr_phone = (EditText) findViewById(R.id.edt_usr_phone);
        btn_register = (Button) findViewById(R.id.btn_login);
        layout_parent_login = (RelativeLayout) findViewById(R.id.layout_parent_login);

        //FB Variables
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");
        //permissions.add("user_birthday");
        permissions.add("user_friends");
        permissions.add("user_hometown");
        permissions.add("user_photos");

        //Function to generate Debug hash key
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

        btn_register.setOnClickListener(new View.OnClickListener()
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
          Log.e("Shrd Pref in Login",sharedpreferences.getString(m_config.TempEntered_User_Name,"N/A") + "   " +
          sharedpreferences.getString(m_config.TempEntered_Email,"N/A") + "   "
                    + sharedpreferences.getString(m_config.TempEntered_Contact_No,"N/A"));
        }
        finally
        {
            Log.e("Preferences cleared","yes");
        }

        //If local storage of entered values exists then set to UI
        if (sharedpreferences.getString(m_config.TempEntered_User_Name, "").length() > 0)
        {
            edt_usr_name.setText(sharedpreferences.getString(m_config.TempEntered_User_Name, "UserName"));
            edt_usr_email.setText(sharedpreferences.getString(m_config.TempEntered_Email, "Email"));
            edt_usr_phone.setText(sharedpreferences.getString(m_config.TempEntered_Contact_No, "Contact No"));
        }

        //Focus Listener of Email edit text
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

        //Focus Listener of Mobile no. edit text
        edt_usr_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            Boolean handled = false;
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_usr_phone.clearFocus();
                    btn_register.performClick();
                    handled = true;
                }
                return handled;
            }
        });

    }

    //Meghana
    //Call back for FB and LI Login
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

                if(reg_pd != null){
                    if(reg_pd.isShowing())
                    {
                        reg_pd.dismiss();
                    }
                }

                GenerikFunctions.showToast(cont, "Linked In Login Failed");

//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
//                alertDialogBuilder
//                        .setMessage("You have declined permissions without those permissions you can't proceed to registration process ")
//                        .setCancelable(false)
//                        .setNegativeButton("Don't Allow", new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                dialog.cancel();
//                            }
//                        })
//
//                        .setPositiveButton("Allow",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        if (GenerikFunctions.chkStatus(cont))
//                                        {
//                                            btn_register.performClick();
//                                        }
//                                        else
//                                        {
//                                            GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
//                                        }
//                                        dialog.dismiss();
//                                        dialog.cancel();
//                                    }
//                                });
//                alertDialogBuilder.show();

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
                            if(reg_pd != null){
                                if(reg_pd.isShowing())
                                {
                                    reg_pd.dismiss();
                                }
                            }
                            Toast.makeText(cont,"Linked In Login Failed",Toast.LENGTH_LONG).show();


                            //Harshada 22 Jul
//                        try
//                        {
//                            setLIUserProfile("");
//                            liUserInformation= new LIUserInformation();
//                        }
//                        catch (Exception ex)
//                        {
//                            ex.printStackTrace();
//                        }//Harshada
                        }
                    }
                    @Override
                    public void onApiError(LIApiError error)
                    {
                        Log.e("Linked In Error", error.toString());
                        if(reg_pd != null){
                            if(reg_pd.isShowing())
                            {
                                reg_pd.dismiss();
                            }
                        }
                        Toast.makeText(cont,"Linked In Login Failed",Toast.LENGTH_LONG).show();


                        //Harshada 22 Jul
//                        try
//                        {
//                            setLIUserProfile("");
//                            liUserInformation= new LIUserInformation();
//                        }
//                        catch (Exception ex)
//                        {
//                            ex.printStackTrace();
//                        }
                        //Harshada
                    }
                });
            }
        }
    }

    //Function to Parse and Store LI data to SQLIte
    public  void  setLIUserProfile(String response)
    {
        try
        {
            Log.e("setLIUserProfile response", response + "");
            if (response.length() == 0 || response.equals(""))
            {
                //Log.e("In If", "setLIUserProfile");
                liUserInformation = new LIUserInformation();
                liPictureData = new LIPictureData();
            }
            else
            {
                //Log.e("In else", "setLIUserProfile");
                liUserInformation = gson.fromJson(response.toString(), LIUserInformation.class);


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

                //Store LI date to AWS
               new AWSLoginOperations.addLIUserInfo(cont,loggedInUserInformation).execute();

                Log.e("Shrd Pref aftr LILginDne",sharedpreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedpreferences.getString(m_config.Entered_Email,"N/A") + "   "
                        + sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
            }
        }
        catch (Exception e)
        {
            if(reg_pd != null){
                if(reg_pd.isShowing())
                {
                    reg_pd.dismiss();
                }
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
        Intent i = new Intent(RegistrationActivity.this, Welcome.class);
        i.putExtra("from","registration");
        startActivity(i);
    }

    //Meghana
    //Generate Debug Hash Key
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
    //Function to check for entered values in Name, email and contact no edittext fromats
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
                        editor.putString(m_config.TempEntered_User_Name,edt_usr_name.getText().toString().trim());
                        editor.putString(m_config.TempEntered_Email, edt_usr_email.getText().toString().trim());
                        editor.putString(m_config.TempEntered_Contact_No, edt_usr_phone.getText().toString().trim());
                        editor.apply();

                        processLogin();
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

    //Login with FB
    public void processLogin()
    {
        try
        {
            loginManager.logInWithReadPermissions(RegistrationActivity.this, permissions);
            FacebookDataRetieval();
        }
        catch (Exception e)
        {
            Log.e("External Exception", e.toString());
            e.printStackTrace();
        }
    }

    //Ask for declined FB permissions
    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {

        loginManager.logInWithReadPermissions(RegistrationActivity.this, declined_perm);
        FacebookDataRetieval();
    }


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

                    final ArrayList<String> declined_permissions = new ArrayList<String>();
                    Set<String> declined_perm = token.getDeclinedPermissions();
                    iterator = declined_perm.iterator();
                    String permissionname = declined_perm.toString().replace("[","");
                    permissionname = permissionname.replace("]","");

                    while (iterator.hasNext())
                    {
                        String perm_name = iterator.next().toString();
                        declined_permissions.add(perm_name);
                    }
                    if (declined_perm.size() > 0)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
                        alertDialogBuilder
                                .setMessage("You have declined some permissions without those permissions you can't proceed to registration process "+permissionname)
                                .setCancelable(false)
                                .setNegativeButton("Don't Allow", null)
                                .setPositiveButton("Allow",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                askForDeclinedFBPermissions(declined_permissions);
                                            }
                                        });
                        alertDialogBuilder.show();
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
                    //GenerikFunctions.showToast(cont,"Please provide permissions for app login");

                    Log.e("AccessToken.getCurrentAccessToken()"," "+(AccessToken.getCurrentAccessToken() != null));

                    if(AccessToken.getCurrentAccessToken() != null) {
                        token = AccessToken.getCurrentAccessToken();
                        Set<String> given_perm = token.getPermissions();
                        iterator = given_perm.iterator();

                        final ArrayList<String> declined_permissions = new ArrayList<String>();
                        Set<String> declined_perm = token.getDeclinedPermissions();
                        iterator = declined_perm.iterator();
                        String permissionname = declined_perm.toString().replace("[", "");
                        permissionname = permissionname.replace("]", "");

                        while (iterator.hasNext()) {
                            String perm_name = iterator.next().toString();
                            //Log.e("declined_permission in: ", perm_name + " ");
                            declined_permissions.add(perm_name);
                        }

                        if (declined_perm.size() > 0) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
                            alertDialogBuilder
                                    .setMessage("You have declined some permissions without those permissions you can't proceed to registration process " + permissionname)
                                    .setCancelable(false)
                                    .setNegativeButton("Don't Allow", null)
                                    .setPositiveButton("Allow",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    askForDeclinedFBPermissions(declined_permissions);
                                                }
                                            });
                            alertDialogBuilder.show();
                        } else {
                            retrieveFBMeData();
                        }
                    }else
                    {
                        GenerikFunctions.showToast(cont, "FB Login failed");
                    }
                }

                @Override
                public void onError(FacebookException error)
                {
                    error.printStackTrace();
                    //Log.e("Login error", "error" + error.toString());

                    if (error instanceof FacebookAuthorizationException)
                    {
                        if (AccessToken.getCurrentAccessToken() != null)
                        {
                            LoginManager.getInstance().logOut();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(m_config.Entered_User_Name, "");
                            editor.putString(m_config.Entered_Email, "");
                            editor.putString(m_config.Entered_Contact_No, "");
                            editor.putString(m_config.TempEntered_User_Name, "");
                            editor.putString(m_config.TempEntered_Email, "");
                            editor.putString(m_config.TempEntered_Contact_No, "");
                            editor.commit();

                         //   startFBLoginScenario();
                        }

                        else
                        {
                            //Log.e(" error "," ---- "+error.getMessage());
                            if(error.getMessage().equals("net::ERR_CONNECTION_CLOSED"))
                            {
                                GenerikFunctions.showToast(cont, "FB is blocked on your server.");
                            }else
                            {
                                GenerikFunctions.showToast(cont, "FB Login failed");
                            }

                        }
                    }
                    else
                    {

                        GenerikFunctions.showToast(cont, "FB Login failed");
                    }
                }
            });
    }

    //If already logged in FB
    public void startFBLoginScenario()
    {
        if (LoginValidations.isFBLoggedIn())
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

            final ArrayList<String> declined_permissions = new ArrayList<String>();
            Set<String> declined_perm = token.getDeclinedPermissions();
            iterator = declined_perm.iterator();
            String permissionname = declined_perm.toString().replace("[","");
            permissionname = permissionname.replace("]","");

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
                alertDialogBuilder
                        .setMessage("You have declined some permissions without those permissions you can't proceed to registration process "+permissionname)
                        .setCancelable(false)
                        .setNegativeButton("Don't Allow", null)
                        .setPositiveButton("Allow",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        askForDeclinedFBPermissions(declined_permissions);
                                    }
                                });
                alertDialogBuilder.show();
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
    //Get user data from FB and Parse and store it to SQLIte
    public void retrieveFBMeData()
    {
        GraphRequest request1 = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {

                        Log.e("Inside retrieveMeFbData","Yes");

                        if(reg_pd != null){
                            reg_pd.setMessage("Loading Data...");
                            reg_pd.setCancelable(false);
                            reg_pd.show();
                        }

                        String emptyFields="";
                        //Log.e("object ", " "+object.toString());

                        // Application code
                        fbUserInformation = gson.fromJson(object.toString(), FbUserInformation.class);
                        Log.e("fb object"," "+response.toString());
                        Log.e("getFbId Id: " , fbUserInformation.getFbId());
                        Log.e("getAgeRange: " ,""+fbUserInformation.getAgerange());


//                        if(fbUserInformation.getBirthday().equals(null))
//                        {
//                            fbUserInformation.setBirthday("N/A");
//                        }

                        //harshada
                        if(fbUserInformation.getAgerange().equals(null) || fbUserInformation.getAgerange() == null)
                        {
                            fbUserInformation.setAge("N/A");
                        }
                        else
                        {

                            Object age_range = fbUserInformation.getAgerange();
                            String age = null;
                            try {
                                JSONObject obj = new JSONObject(age_range.toString());
                                age = obj.getString("min");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String finalAge = age.replace(".0","");
                            Log.e("age_range ", " "+ finalAge);
                            fbUserInformation.setAge(finalAge);
                        }



                        if(fbUserInformation.getEmail().equals(null))
                        {
                            fbUserInformation.setEmail("N/A");
                        }
                        else
                        {
                        }
                        if(fbUserInformation.getFBLocationInformation()!= null)
                        {
                            fBCurrentLocationInformation = new FBCurrentLocationInformation();
                            if(fBCurrentLocationInformation.equals(fbUserInformation.getFBLocationInformation()))
                            {
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

                        if(fbUserInformation.getFbHomelocationInformation() != null)
                        {
                            fbHomelocationInformation = new FbHomelocationInformation();
                            if(fbHomelocationInformation.equals(fbUserInformation.getFbHomelocationInformation()))
                            {
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
                                        //LoginTableColumns.FB_USER_BIRTHDATE + " = '" + fbUserInformation.getBirthday().trim() + "', " +
                                        LoginTableColumns.FB_USER_AGE + " = '" + fbUserInformation.getAge().trim() + "', " +
                                        LoginTableColumns.FB_USER_EMAIL + " = '" + fbUserInformation.getEmail().trim() + "', " +
                                        LoginTableColumns.FB_USER_HOMETOWN_ID + " = '" + fbHomelocationInformation.getLocationId().trim() + "', " +
                                        LoginTableColumns.FB_USER_HOMETOWN_NAME + " = '" + fbHomelocationInformation.getLocationName().trim() + "', " +
                                        LoginTableColumns.FB_USER_CURRENT_LOCATION_ID + " = '" + fBCurrentLocationInformation.getLocationId().trim() + "', " +
                                        LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " = '" + fBCurrentLocationInformation.getLocationName().trim() + "'  where "
                                        + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                                Log.i("update User  "+ LoginTableColumns.FB_USER_ID , updateUser);
                                sqldb.execSQL(updateUser);

                                loggedInUserInfo = new LoggedInUserInformation();
                                String updateProfilePic = "No";
                                try{
                                    UserTable user = m_config.mapper.load(UserTable.class,fbUserInformation.getFbId());
                                    if(user != null){
                                        if(user.getImageflag().equals("No"))
                                        {
                                            updateProfilePic = "Yes";
                                        }
                                        else {
                                            updateProfilePic = "No";
                                        }
                                    }
                                }
                                catch (Exception e){

                                }



                                loggedInUserInfo.setFB_USER_ID(fbUserInformation.getFbId());
                                loggedInUserInfo.setFB_USER_NAME(fbUserInformation.getFbUserName());
                                loggedInUserInfo.setFB_USER_GENDER(fbUserInformation.getGender());
                                //loggedInUserInfo.setFB_USER_BIRTHDATE(fbUserInformation.getBirthday());
                                loggedInUserInfo.setFB_USER_AGE(fbUserInformation.getBirthday());
                                loggedInUserInfo.setFB_USER_EMAIL(fbUserInformation.getEmail());
                                //  loggedInUserInfo.setFB_USER_PROFILE_PIC(fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl());
                                loggedInUserInfo.setFB_USER_HOMETOWN_ID(fbHomelocationInformation.getLocationId().trim());
                                loggedInUserInfo.setFB_USER_HOMETOWN_NAME(fbHomelocationInformation.getLocationName().trim());
                                loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(fBCurrentLocationInformation.getLocationId().trim());
                                loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(fBCurrentLocationInformation.getLocationName().trim());

                                if(updateProfilePic.equals("Yes"))
                                {
                                    loggedInUserInfo.setFB_USER_PROFILE_PIC(fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl());
                                    String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                                            + LoginTableColumns.FB_USER_PROFILE_PIC  + " = '" + loggedInUserInfo.getFB_USER_PROFILE_PIC() + "'"
                                            + " where " + LoginTableColumns.FB_USER_ID + " = '" + loggedInUserInfo.getFB_USER_ID() + "'";

                                    Log.i("update User  "+ LoginTableColumns.FB_USER_ID , Update);
                                    sqldb.execSQL(Update);
                                }
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
        //parameters1.putString("fields", "id,name,birthday,gender,email,location,picture.type(large),hometown,age_range");//,albums.fields(name,photos.fields(name,picture,source,created_time))");
        parameters1.putString("fields", "id,name,gender,email,location,picture.type(large),hometown,age_range");//,albums.fields(name,photos.fields(name,picture,source,created_time))");
        request1.setParameters(parameters1);
        request1.executeAsync();
    }

    //Store User FB Information In database
    public void storeUserInDb()
    {
        ContentValues values = new ContentValues();
        values.put(LoginTableColumns.FB_USER_ID,fbUserInformation.getFbId().trim());
        values.put(LoginTableColumns.FB_USER_NAME,fbUserInformation.getFbUserName().trim());
        values.put(LoginTableColumns.FB_USER_GENDER,fbUserInformation.getGender().trim());
        //values.put(LoginTableColumns.FB_USER_BIRTHDATE,fbUserInformation.getBirthday().trim());
        values.put(LoginTableColumns.FB_USER_AGE,fbUserInformation.getAge().trim());
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
        //loggedInUserInfo.setFB_USER_BIRTHDATE(fbUserInformation.getBirthday());
        loggedInUserInfo.setFB_USER_AGE(fbUserInformation.getAge());
        loggedInUserInfo.setFB_USER_EMAIL(fbUserInformation.getEmail());
        loggedInUserInfo.setFB_USER_PROFILE_PIC(fbUserInformation.getFbProfilePictureData().getFbPictureInformation().getUrl());
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(fbHomelocationInformation.getLocationId().trim());
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(fbHomelocationInformation.getLocationName().trim());
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(fBCurrentLocationInformation.getLocationId().trim());
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(fBCurrentLocationInformation.getLocationName().trim());

        /******/

        /* make the API call */
        getFbFriendsCount();
    }


    //Retrieve Users FB friends count
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
                    i.putExtra("from","registration");
                    startActivity(i);

                    if(reg_pd != null){
                        if(reg_pd.isShowing())
                        {
                            reg_pd.dismiss();
                        }
                    }
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
    //LI permission module
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    //Start forLI login session
    public static void startLinkedInProcess()
    {


        if(reg_pd != null){
            if(reg_pd.isShowing())
            {
                reg_pd.dismiss();
            }
        }


        linkedinStart="Yes";
        Log.e("In startLinkedInProcess","Yes");

        LISessionManager.getInstance(cont).init((Activity)cont, buildScope(), new AuthListener()
        {


            @Override
            public void onAuthSuccess()
            {

                reg_pd.show();

                Token = LISessionManager.getInstance(cont).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token",Token+"");

            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");


                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    if(RegistrationActivity.reg_pd != null)
                    {
                        if (RegistrationActivity.reg_pd.isShowing()) {
                            RegistrationActivity.reg_pd.dismiss();
                        }
                    }
                    //GenerikFunctions.showToast(cont, "Please accept permissions " );
                    linkedinStart = "Yes";
                    //startLinkedInProcess();

                }
                else
                {
                    GenerikFunctions.showToast(cont, "Linked In Login failed.");

                    if(RegistrationActivity.reg_pd != null)
                    {
                        if (RegistrationActivity.reg_pd.isShowing())
                        {
                            RegistrationActivity.reg_pd.dismiss();
                        }
                    }
                    Log.e("Inside Else","Yes");
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
            m_config.mTelephony = mTelephony;

            //new LoginValidations.subscribeToPushNotifications(regId,mTelephony,context).execute();
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

                m_config.mTelephony = mTelephony;
                //new LoginValidations.subscribeToPushNotifications(regId,mTelephony, context).execute();
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
                    m_config.mTelephony = mTelephony;
                    //new LoginValidations.subscribeToPushNotifications(regId, mTelephony, (Activity) cont).execute();
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
                                        //LoginValidations.checkPendingLoginFlags(cont);
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        //user has denied with `Never Ask Again`
                        Log.e("Click of Never ask again",", permission request is denied");
                        //LoginValidations.checkPendingLoginFlags(cont);
                    }
                }
                break;
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }


}
