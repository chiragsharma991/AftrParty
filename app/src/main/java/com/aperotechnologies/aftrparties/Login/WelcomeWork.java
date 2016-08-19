package com.aperotechnologies.aftrparties.Login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
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
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.aperotechnologies.aftrparties.Login.LoginTableColumns.FB_USER_ID;

/**
 * Created by mpatil on 25/05/16.
 */
public class WelcomeWork extends Activity
{
    Button btn_login, btn_register;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    static Context cont;
    CallbackManager callbackManager;
    ArrayList<String> permissions;
    public static String linkedinStart="";
    public static String Token;

    Iterator iterator;
    AccessToken token;
    FaceOverlayView faceOverlayView;
    Target mTarget;
    int faces=0;

    FbUserInformation fbUserInformation;
    FBCurrentLocationInformation fBCurrentLocationInformation;
    FbHomelocationInformation fbHomelocationInformation;
    FbProfilePictureData fbProfilePictureData;
    LoggedInUserInformation loggedInUserInfo;
    String AdvancedConnectionsLinkedIn="https://api.linkedin.com/v1/people/~:(id,first-name,api-standard-profile-request,email-address,last-name,public-profile-url,num-connections,picture-urls::(original),positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,field-of-study,start-date,end-date,notes),publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)?format=json";
    LIUserInformation liUserInformation;
    LIPictureData liPictureData;
    private static final  int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    Gson gson;
    SQLiteDatabase sqldb;
    DBHelper helper;
    TelephonyManager mTelephony;

    LoginManager loginManager;


    public static  ProgressDialog wl_pd = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.welcome);
        cont=this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        gson=new Gson();
        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();
        loginManager = LoginManager.getInstance();


        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        faceOverlayView = (FaceOverlayView) ((Activity)cont).findViewById(R.id.face_overlay);
        wl_pd = new ProgressDialog(this);
        RegistrationActivity.reg_pd = null;
        SplashActivity.pd = null;
        //FB Variables
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        //Permissions needed for FB Login
        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        //permissions.add("user_location");
        //permissions.add("user_birthday");
        permissions.add("user_friends");
        //permissions.add("user_hometown");
        //permissions.add("user_photos");


        //Start Registration activity on Register button click
        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);

            }
        });

        //Login Button Click
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Shrd Pref in Welcome on login click",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "  + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A")+"    "+
                        sharedPreferences.getString(m_config.LoggedInFBUserID,"N/A"));


                if(GenerikFunctions.chkStatus(cont))
                {
                    //Check for local storage of FB ID
                    if(sharedPreferences.getString(m_config.LoggedInFBUserID,"N/A").equals("N/A"))
                    {
                        //If not stored locally, initialise the FB login
                        linkedinStart = "";

                        try
                        {
                            loginManager.logInWithReadPermissions(WelcomeWork.this,permissions);
                            Log.e("Inside login", "yes");
                            FacebookDataRetieval();
                        }
                        catch (Exception e)
                        {
                            Log.e("External Exception", e.toString());
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        //If locally stored then initialise the FB Session
                        linkedinStart="";
                        Log.e("FB Session","");
                        loginManager.logInWithReadPermissions(WelcomeWork.this,permissions);
                        FacebookDataRetievalNew();
                    }
                }
                else{
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                }




            }
        });
    }

    //FB Login to retrieve FB ID
    public void FacebookDataRetievalNew()
    {
        Log.e("Inside FacebookDataRetievalNew","Yes");
        linkedinStart = "";

        try
        {
            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>()
                    {
                        @Override
                        public void onSuccess(LoginResult loginResult)
                        {
                            Log.e("FB Login Success Welcome FacebookDataRetievalNew","");
                            token = loginResult.getAccessToken();
                            Log.e("AccessToken from Welcome FacebookDataRetievalNew", token.toString() + "    " +token);
                            if(wl_pd == null)
                            {
                                wl_pd = new ProgressDialog(WelcomeWork.this);
                            }
                            RegistrationActivity.reg_pd = null;
                            SplashActivity.pd = null;
                            wl_pd.setMessage("Loading Data...");
                            wl_pd.setCancelable(false);
                            wl_pd.show();

                            LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
                            Log.e("FB data in FBnewRetrieval",loggedInUserInformation.getFB_USER_ID() +"   " +loggedInUserInformation.getFB_USER_HOMETOWN_NAME());

                            fbUserInformation = new FbUserInformation();
                            fbUserInformation.setFbId(loggedInUserInformation.getFB_USER_ID() );
                            fbUserInformation.setEmail(loggedInUserInformation.getFB_USER_EMAIL());

                            new checkFBUserInfo(loggedInUserInformation).execute();
                        }

                        @Override
                        public void onCancel()
                        {
                            GenerikFunctions.showToast(cont, "FB Login failed");
                            Log.e("FBLogin onCancel Welcome FacebookDataRetievalNew", "Yes");
                        }

                        @Override
                        public void onError(FacebookException error)
                        {
                            Log.e("FB Login error Welcome FacebookDataRetievalNew",error.toString());
                            GenerikFunctions.showToast(cont, "FB Login failed");
                            error.printStackTrace();
                        }
                    });
        }
        catch(Exception e)
        {
        }
    }
    //Meghana
    //For FB and LI login call back sessions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("onActivityResult","linkedinStart :   "+linkedinStart);
        if (linkedinStart.equals(""))
        {
            //For FB
            Log.e("onActivityResult","fb :");
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            //For LI
            Log.e("onActivityResult","linkedIn");
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            Log.e("Token LI on ActivityRes", Token + "");
            if (Token == null)
            {
                GenerikFunctions.showToast(cont,"LI Login Failed");
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
                            Log.e("LI jsonresponse", "=====  " + result.toString() + " ");
                            setLIUserProfile(jsonObject.toString());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

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

                        try
                        {
                            setLIUserProfile("");
                            liUserInformation= new LIUserInformation();
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    //Function to parse update LI data to AWS and SQLite
    public  void  setLIUserProfile(String response)
    {
        try
        {
            Log.e("setLIUserProfile response", response + "");
            if (response.length() == 0 || response.equals(""))
            {
                liUserInformation = new LIUserInformation();
                liPictureData = new LIPictureData();
            }
            else {
                liUserInformation = gson.fromJson(response.toString(), LIUserInformation.class);


                if (liUserInformation.getLIPictureData() != null) {
                    liPictureData = new LIPictureData();
                    if (liPictureData.equals(liUserInformation.getLIPictureData())) {
                        Log.e("Both LI  Pics Empty  ", "Is Empty");
                    } else {
                        liPictureData = liUserInformation.getLIPictureData();
                    }
                } else {
                    liPictureData = new LIPictureData();
                }
                Log.e("LI Pic Info ", liPictureData.get_total() + "     " + liPictureData.getvalues().size() + "    " + liPictureData.getvalues().get(0));
            }


            Log.e("LoginTableColumns.USERTABLE"," "+LoginTableColumns.FB_USER_ID+ "    "+fbUserInformation.getFbId().trim());
            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                    LoginTableColumns.FB_USER_ID +" = '" + fbUserInformation.getFbId() + "'";
            Log.i("User Query  : ", Query);
            Cursor cursor = sqldb.rawQuery(Query, null);
           // Log.e("Cursor count",cursor.getCount()+"");
            if(cursor.getCount() == 0)
            {
                Log.e("Onside update if","Yes");
            }
            else
            {

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


                //AWS Storage of LI data

                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(m_config.LILoginDone,"Yes");
                editor.apply();

                String from = getIntent().getExtras().getString("from");
                Log.e("from"," "+from);

                Log.e("Shrd Pref aftr LILginDne",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "
                        + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A"));

                //checkfromWhere(from);
            }

        }
        catch (Exception e)
        {
            Log.e("setLIUserProfile catch", "");
            e.printStackTrace();
        }
    }

    //Harshada
    @Override
    public void onBackPressed()
    {
        //Meghana
        //Clear Focus from all edit texts
        //Harshada
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WelcomeWork.this);
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

    //Ask user again for declined FB permissions
    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {
        loginManager.logInWithReadPermissions(WelcomeWork.this, declined_perm);
        FacebookDataRetieval();
    }

    //Check for user permissions acceptance for FB
    public void FacebookDataRetieval()
    {
        Log.e("Inside FB data retreive","Yes");
        linkedinStart="";

        try
        {
            Log.e("Inside try","Yes");
            loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        // App code
                        Log.e("Login Success", "Yes");
                        token = loginResult.getAccessToken();
                        Log.e("AccessToken", token.toString() + "    " +token);


                        Set<String> given_perm = token.getPermissions();
                        iterator = given_perm.iterator();

                        ArrayList<String> declined_permissions = new ArrayList<String>();
                        Set<String> declined_perm = token.getDeclinedPermissions();
                        iterator = declined_perm.iterator();
                        while (iterator.hasNext())
                        {
                            String perm_name = iterator.next().toString();
                            //Log.e("declined_permission in: ", perm_name + " ");
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

                    }

                    @Override
                    public void onCancel()
                    {
                        Log.e("Login onCancel", "Yes");
                        GenerikFunctions.showToast(cont,"Please provide permissions for app login");
                        if(wl_pd.isShowing())
                        {
                            wl_pd.dismiss();
                        }
                    }

                    @Override
                    public void onError(FacebookException error)
                    {
                        error.printStackTrace();
                        Log.e("Login error", "error" + error.toString());

                        if(wl_pd.isShowing())
                        {
                            wl_pd.dismiss();
                        }
                        if (error instanceof FacebookAuthorizationException)
                        {
                            if (AccessToken.getCurrentAccessToken() != null)
                            {
                                LoginManager.getInstance().logOut();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(m_config.Entered_User_Name, "");
                                editor.putString(m_config.Entered_Email, "");
                                editor.putString(m_config.Entered_Contact_No, "");
                                editor.putString(m_config.TempEntered_User_Name, "");
                                editor.putString(m_config.TempEntered_Email, "");
                                editor.putString(m_config.TempEntered_Contact_No, "");
                                editor.commit();
                                startFBLoginScenario();
                            }
                        }
                    }
                });

            Log.e("After ","After");
        }
        catch(Exception e)
        {
            if(wl_pd.isShowing())
            {
                wl_pd.dismiss();
            }
            Log.e("Error in print ",e.toString());
            e.printStackTrace();
        }
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
           // processLogin();
        }
    }

    //Meghana
    //Get user data from FB
    public void retrieveFBMeData()
    {
        GraphRequest request1 = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        if(wl_pd != null){
                            wl_pd.setMessage("Loading Data...");
                            wl_pd.setCancelable(false);
                            wl_pd.show();
                        }

                        String emptyFields="";
                        // Application code
                        Log.i("Me Request", object.toString());
//                      AddFriends addFriends = new AddFriends();
//                      addFriends.requestInvitableFriends(cont,token);
                        fbUserInformation = gson.fromJson(object.toString(), FbUserInformation.class);
                        Log.e("User Information --->","Information");
                        Log.e("getFbId Id: " , fbUserInformation.getFbId());

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
                            Log.e("getEmail: " ,fbUserInformation.getEmail());
                        }
                        if(fbUserInformation.getFBLocationInformation()!= null)
                        {
                            fBCurrentLocationInformation = new FBCurrentLocationInformation();
                            if(fBCurrentLocationInformation.equals(fbUserInformation.getFBLocationInformation()))
                            {
                                Log.e("Both current Location ","Is Empty");
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
//
                        if(fbUserInformation.getFbHomelocationInformation() != null)
                        {
                            fbHomelocationInformation = new FbHomelocationInformation();
                            if(fbHomelocationInformation.equals(fbUserInformation.getFbHomelocationInformation()))
                            {
                               // Log.e("Both Home Location ","Is Empty");
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

                      //  Log.e("getImgLink",fbProfilePictureData.getFbPictureInformation().getUrl());


                        if(emptyFields.equals(""))
                        {
                            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                                    LoginTableColumns.FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
//                            Log.i("User Query  : ", Query);
                            Cursor cursor = sqldb.rawQuery(Query, null);
//                            Log.e("Cursir count",cursor.getCount()+"");
                            if(cursor.getCount() == 0)
                            {
                                //  Store use info in SQLIte
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

                                Log.i("update User  ==="+ LoginTableColumns.FB_USER_ID , updateUser);
                                sqldb.execSQL(updateUser);

                                loggedInUserInfo = new LoggedInUserInformation();

                                loggedInUserInfo.setFB_USER_ID(fbUserInformation.getFbId());
                                loggedInUserInfo.setFB_USER_NAME(fbUserInformation.getFbUserName());
                                loggedInUserInfo.setFB_USER_GENDER(fbUserInformation.getGender());
                                //loggedInUserInfo.setFB_USER_BIRTHDATE(fbUserInformation.getBirthday());
                                loggedInUserInfo.setFB_USER_AGE(fbUserInformation.getAge());
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
                            if(wl_pd.isShowing())
                            {
                                wl_pd.dismiss();
                            }
                            GenerikFunctions.showToast(cont,"Please specify your "+ emptyFields + " in Facebook");
                        }
                    }
                });

        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "id,name,gender,email,location,picture.type(large),hometown,age_range");//,albums.fields(name,photos.fields(name,picture,source,created_time))");
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

        loggedInUserInfo =new LoggedInUserInformation();

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

        //Total No of friends
        getFbFriendsCount();
    }

    //Function to retrieve FB friends count and update in SQLite and update user FB data to AWS
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
                            Log.e("graphObject",graphObject.toString());
                            JSONObject summary = graphObject.getJSONObject("summary");
                            String totCount = summary.getString("total_count");
                            Log.e("Summary  totCount ", summary + "      " + totCount);

                            int total_friends_count = (Integer.parseInt(totCount.trim()));

                            String Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
                            Log.i("User Query  : ", Query);
                            Cursor cursor = sqldb.rawQuery(Query, null);
                            Log.e("Cursor count",cursor.getCount()+"");

                            if(cursor.getCount() == 0)
                            {
                            }
                            else
                            {
                                String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                                        + LoginTableColumns.FB_USER_FRIENDS  + " = '" + total_friends_count + "'"
                                        + " where " + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                                Log.i("update User  "+ LoginTableColumns.FB_USER_ID , Update);
                                sqldb.execSQL(Update);

                                //AWS Storage of FB Data
                                Log.e("Before FB AWS Storage","Yes");

                                new checkFBUserInfo(loggedInUserInfo).execute();

                            }



                        }
                        catch (Exception e)
                        {
                            if(wl_pd.isShowing())
                            {
                                wl_pd.dismiss();
                            }
                            System.out.println("Exception=" + e);
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
    //LI permissions
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    //LI session initialition
    public  void startLinkedInProcess(final UserTable user)
    {
        Log.e("In startLinkedInProcess","Yes");
        LISessionManager.getInstance(cont).init((Activity)cont, buildScope(), new AuthListener()
        {
            @Override
            public void onAuthSuccess()
            {
                Token = LISessionManager.getInstance(cont).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token", Token+"");

                String updateUser = "Update " + LoginTableColumns.USERTABLE + " set " +
                        LoginTableColumns.FB_USER_NAME + " = '" + user.getFBUserName() + "', " +
                        LoginTableColumns.FB_USER_GENDER + " = '" + user.getGender().trim() + "', " +
                        //LoginTableColumns.FB_USER_BIRTHDATE + " = '" + user.getBirthDate().trim() + "', " +
                        LoginTableColumns.FB_USER_AGE + " = '" + user.getBirthDate().trim() + "', " +
                        LoginTableColumns.FB_USER_EMAIL + " = '" + user.getSocialEmail().trim() + "', " +
                        LoginTableColumns.FB_USER_HOMETOWN_NAME + " = '" + user.getFBHomeLocation().trim() + "', " +
                        LoginTableColumns.FB_USER_PROFILE_PIC + " = '" + user.getProfilePicUrl().get(0) + "', " +
                        LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " = '" + user.getFBCurrentLocation().trim() + "'  where "
                        + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

                Log.i("update User "+user.getFacebookID().trim(), updateUser);
                sqldb.execSQL(updateUser);


                String li_userprofile_pic = user.getLKProfilePicUrl().toString();
                if(user.getLKProfilePicUrl().toString() == null || user.getLKProfilePicUrl().toString().equals(null) || user.getLKProfilePicUrl().toString().equals("N/A"))
                {
                    li_userprofile_pic = "N/A";
                }
                else
                {
                    li_userprofile_pic = user.getLKProfilePicUrl().toString();
                }

                String li_user_headline = user.getLKHeadLine().toString();
                if(user.getLKHeadLine().toString() == null || user.getLKHeadLine().toString().equals(null) || user.getLKHeadLine().toString().equals("N/A"))
                {
                    li_user_headline = "N/A";
                }
                else
                {
                    li_user_headline = user.getLKHeadLine().toString();
                }

                String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                        + LoginTableColumns.LI_USER_ID  + " = '" + user.getLinkedInID() + "', "
                        + LoginTableColumns.LI_USER_PROFILE_PIC  + " = '" + li_userprofile_pic + "', "
                        + LoginTableColumns.LI_USER_CONNECTIONS  + " = '" + user.getLKConnectionsCount() + "', "
                        + LoginTableColumns.LI_USER_HEADLINE + " = '" + li_user_headline + "' "
                        + " where " + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

                Log.i("update User "+ user.getFacebookID().trim(), Update);
                sqldb.execSQL(Update);

                SharedPreferences.Editor editor = sharedPreferences.edit();
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

                Log.e("Updated all flags","yes");
                String from = getIntent().getExtras().getString("from");
                Log.e("from"," "+from);

                checkfromWhere(from);

            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");
                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    startLinkedInProcess(user);
                }
                else //if(error.toString().trim().contains("UNKNOWN_ERROR"))
                {
                    Log.e("Inside Else","Yes");
                    if(wl_pd != null)
                    {
                        if(wl_pd.isShowing())
                        {
                            wl_pd.dismiss();
                        }
                    }

                    //GenerikFunctions.showToast(cont, "failed  linked in login " + error.toString());
                }
            }
        }, true);
    }

    //Update local SQLite storage and shared preferences
    public void updateUserTableAndPrefs(UserTable user)
    {
        Log.e("Inside Update User flags","Yes");
        if(LISessionManager.getInstance(cont).getSession().getAccessToken() == null)
        {
            Log.e("LI out","Yes");
            linkedinStart="Yes";
            startLinkedInProcess(user);
        }
        else
        {
            Log.e("LI out","No");
            String  litok = LISessionManager.getInstance(cont).getSession().getAccessToken().toString();
            Log.e("litok","litok");

            String updateUser = "Update " + LoginTableColumns.USERTABLE + " set " +
                    LoginTableColumns.FB_USER_NAME + " = '" + user.getFBUserName() + "', " +
                    LoginTableColumns.FB_USER_GENDER + " = '" + user.getGender().trim() + "', " +
                    //LoginTableColumns.FB_USER_BIRTHDATE + " = '" + user.getBirthDate().trim() + "', " +
                    LoginTableColumns.FB_USER_AGE + " = '" + user.getBirthDate().trim() + "', " +
                    LoginTableColumns.FB_USER_EMAIL + " = '" + user.getSocialEmail().trim() + "', " +
                    LoginTableColumns.FB_USER_HOMETOWN_NAME + " = '" + user.getFBHomeLocation().trim() + "', " +
                    LoginTableColumns.FB_USER_PROFILE_PIC + " = '" + user.getProfilePicUrl().get(0) + "', " +
                    LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " = '" + user.getFBCurrentLocation().trim() + "'  where "
                    + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

            Log.i("update User "+user.getFacebookID().trim(), updateUser);
            sqldb.execSQL(updateUser);


            String li_userprofile_pic = user.getLKProfilePicUrl().toString();
            if(user.getLKProfilePicUrl().toString() == null || user.getLKProfilePicUrl().toString().equals(null) || user.getLKProfilePicUrl().toString().equals("N/A"))
            {
                li_userprofile_pic = "N/A";
            }
            else
            {
                li_userprofile_pic = user.getLKProfilePicUrl().toString();
            }

            String li_user_headline = user.getLKHeadLine().toString();
            if(user.getLKHeadLine().toString() == null || user.getLKHeadLine().toString().equals(null) || user.getLKHeadLine().toString().equals("N/A"))
            {
                li_user_headline = "N/A";
            }
            else
            {
                li_user_headline = user.getLKHeadLine().toString();
            }




            String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                    + LoginTableColumns.LI_USER_ID  + " = '" + user.getLinkedInID() + "', "
                    + LoginTableColumns.LI_USER_PROFILE_PIC  + " = '" + li_userprofile_pic + "', "
                    + LoginTableColumns.LI_USER_CONNECTIONS  + " = '" + user.getLKConnectionsCount() + "', "
                    + LoginTableColumns.LI_USER_HEADLINE + " = '" + li_user_headline + "' "
                    + " where " + LoginTableColumns.FB_USER_ID + " = '" + user.getFacebookID().trim() + "'";

            Log.i("update User "+ user.getFacebookID().trim() , Update);
            sqldb.execSQL(Update);




            SharedPreferences.Editor editor = sharedPreferences.edit();
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

            Log.e("Check LI Token"," "+LISessionManager.getInstance(cont).getSession().getAccessToken().toString());
            //Start LI Session
            if(LISessionManager.getInstance(cont).getSession().getAccessToken().toString() == null)
            {
                Log.e("LI out","Yes");
                linkedinStart="Yes";
                startLinkedInProcess(user);
            }
            else
            {
                //LoginValidations.QBStartSession(cont);

                String from = getIntent().getExtras().getString("from");
                Log.e("from"," "+from);

                checkfromWhere(from);
            }


        }


        //Start QB Session Here
//
//        Intent intent = new Intent(cont, HomePageActivity.class);
//        startActivity(intent);


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
                Log.e("User exists in AWS","Yes");
                //Check login done flag
                if(selUserData.getRegistrationStatus().equals("Yes"))
                {
                    Log.e("User registration status in AWS","Yes");
                    //Registration is already done
                    updateUserTableAndPrefs(selUserData);

                }
                else
                {
                    if(wl_pd != null){
                        if(wl_pd.isShowing())
                        {
                            wl_pd.dismiss();
                        }
                    }



                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(m_config.TempEntered_User_Name, selUserData.getName());
                    editor.putString(m_config.TempEntered_Email, selUserData.getEmail());
                    editor.putString(m_config.TempEntered_Contact_No, selUserData.getPhoneNumber());
                    editor.commit();


                    Log.e("User registration status in AWS","No");
                    Toast.makeText(cont,"Your Registration Process is incomplete. Please Complete...",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(WelcomeWork.this, WelcomeWork.class);
                    startActivity(i);
                    finish();



                }
            }
            else
            {
                if(wl_pd != null)
                {
                    if(wl_pd.isShowing())
                    {
                        wl_pd.dismiss();
                    }
                }

                Log.e("User not exists in AWS","No");
                //If not exists then call  normal registration flow
                Toast.makeText(cont,"Not a user. Please Register...",Toast.LENGTH_LONG).show();
                Intent i = new Intent(WelcomeWork.this, WelcomeWork.class);
                startActivity(i);
                finish();
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
                        new android.app.AlertDialog.Builder(WelcomeWork.this)
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

                Toast.makeText(context, "Without this permission the app will be unable to receive Push Notifications.",Toast.LENGTH_LONG).show();

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

    @Override
    protected void onResume()
    {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;

        // Initialize the Amazon Cognito credentials provider
        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
    }



    public void checkfromWhere(String from)
    {
        Log.e("here "," "+sharedPreferences.getString(m_config.Entered_User_Name,""));

        if(from.equals("registration") || from.equals("splash") || from.equals("homepage"))
        {
            LoginValidations.QBStartSession(cont);
        }
        else if(from.equals("requestSend"))
        {
            String PartyId = getIntent().getExtras().getString("PartyId");
            String PartyName = getIntent().getExtras().getString("PartyName");
            String PartyStartTime = getIntent().getExtras().getString("PartyStartTime");
            String PartyEndTime = getIntent().getExtras().getString("PartyEndTime");
            String PartyStatus = getIntent().getExtras().getString("PartyStatus");
            String GCQBID = getIntent().getExtras().getString("GCQBID");
            String GCFBID = getIntent().getExtras().getString("GCFBID");
            String message = getIntent().getExtras().getString("message");

            Intent i = new Intent(WelcomeWork.this, RequestantActivity.class);
            PartyParceableData party1 = new PartyParceableData();
            party1.setPartyId(PartyId);
            party1.setPartyName(PartyName);
            party1.setStartTime(PartyStartTime);
            party1.setEndTime(PartyEndTime);
            party1.setPartyStatus(PartyStatus);
            i.putExtra("from", "requestSend");
            i.putExtra("GCQBID",GCQBID);
            i.putExtra("GCFBID", GCFBID);
            i.putExtra("message", message);
            Bundle mBundles = new Bundle();
            mBundles.putSerializable(ConstsCore.SER_KEY, party1);
            i.putExtras(mBundles);
            startActivity(i);

            if(wl_pd!=null)
            {
                if(wl_pd.isShowing())
                {
                    wl_pd.dismiss();
                }
            }

        }
        else if(from.equals("chatoffline"))
        {
            Intent i = new Intent(WelcomeWork.this, DialogsActivity.class);
            startActivity(i);

            if(wl_pd!=null)
            {
                if(wl_pd.isShowing())
                {
                    wl_pd.dismiss();
                }
            }
        }
        else if(from.equals("requestApproved"))
        {
            String GCFBID = getIntent().getExtras().getString("GCFBID");
            String message = getIntent().getExtras().getString("message");
            Intent i = new Intent(WelcomeWork.this, HistoryActivity.class);
            i.putExtra("GCFBID", GCFBID);
            i.putExtra("from", "requestApproved");
            i.putExtra("message", message);
            startActivity(i);


            if(wl_pd!=null)
            {
                if(wl_pd.isShowing())
                {
                    wl_pd.dismiss();
                }
            }
        }
        else if(from.equals("requestDeclined"))
        {
            String GCFBID = getIntent().getExtras().getString("GCFBID");
            String message = getIntent().getExtras().getString("message");
            Intent i = new Intent(WelcomeWork.this, HistoryActivity.class);
            i.putExtra("GCFBID", GCFBID);
            i.putExtra("from", "requestDeclined");
            i.putExtra("message", message);
            startActivity(i);


            if(wl_pd!=null)
            {
                if(wl_pd.isShowing())
                {
                    wl_pd.dismiss();
                }
            }
        }
    }
}
