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
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
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
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static com.aperotechnologies.aftrparties.Login.LoginTableColumns.FB_USER_ID;


/**
 * Created by hasai on 06/05/16.
 */

/*LinkedIn App
Client ID: 752m6fkgel868f
Client Secret: yxNWdXkj0iZwG3wq*/
public class LoginActivity extends Activity
{
    //Harshada
    private static final String TAG = "LoginActivity";
    static final String APP_ID = "40454";//"34621";
    static final String AUTH_KEY = "sYpuKrOrGT4pG6d";//"q6aK9sm6GCSmtru";
    static final String AUTH_SECRET = "hVx9RNMT4emBK5K";//"uTOm5-R4zYyR-DV";
    static final String ACCOUNT_KEY = "VLBr2asUuw9uHDFC7qgb";//"bzbtQDLez742xU468TXt";

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
    LoggedInUserInformation loggedInUserInfo;
    String AdvancedConnectionsLinkedIn = "https://api.linkedin.com/v1/people/~:(id,first-name,email-address,last-name,num-connections,picture-url,positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,field-of-study,start-date,end-date,notes),publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)?format=json";
    LIUserInformation liUserInformation;
    Gson gson;
    EditText edt_usr_name, edt_usr_email, edt_usr_phone;
    RelativeLayout layout_parent_login;
    String inputToastDisplay = "";
    String linkedinStart = "";
    String Token;
    Iterator iterator;
    int total_friends_count = 0;
    FbProfilePictureData fbProfilePictureData;

    //General
    Configuration_Parameter m_config;
    static Context cont;
    SharedPreferences sharedpreferences;
    SQLiteDatabase sqldb;
    DBHelper helper;
    TelephonyManager mTelephony;
    private static final  int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    String regId;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        cont = this;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        gson = new Gson();
        helper = DBHelper.getInstance(cont);
        sqldb = helper.getWritableDatabase();
        m_config.pDialog = new ProgressDialog(cont);


        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

//        // Initialize the Amazon Cognito credentials provider
//        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
//                Regions.US_EAST_1 // Region
//        );
//
//
//        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
//        //DynamoDB client to create an Object m_config.mapper:
//        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
//        //Log.e("m_config.mapper ", " " + m_config.mapper);

        //Meghana
        //UI Components
        edt_usr_name = (EditText) findViewById(R.id.edt_usr_name);
        edt_usr_email = (EditText) findViewById(R.id.edt_usr_email);
        edt_usr_phone = (EditText) findViewById(R.id.edt_usr_phone);
        btn_login = (Button) findViewById(R.id.btn_login);
        layout_parent_login = (RelativeLayout) findViewById(R.id.layout_parent_login);

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

        // permissions.add("taggable_friends");
        getDebugHashKey();

        layout_parent_login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismissCursor();
                return true;
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Login Clicked", "yes");
                validateUserInput();
            }
        });

        if (sharedpreferences.getString(m_config.Entered_User_Name, "").length() > 0) {
            edt_usr_name.setText(sharedpreferences.getString(m_config.Entered_User_Name, "UserName"));
            edt_usr_email.setText(sharedpreferences.getString(m_config.Entered_Email, "Email"));
            edt_usr_phone.setText(sharedpreferences.getString(m_config.Entered_Contact_No, "Contact No"));
        }

        if (sharedpreferences.getBoolean(m_config.FBLoginDone, false) == false) {
            linkedinStart = "";
        } else {
            linkedinStart = "Yes";
        }
    }


    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (linkedinStart.equals("")) {
            //For FB
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            //For LI
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            Log.i("Request Code phase 2", requestCode + "   " + resultCode + "  " + data);
            Log.e("Token from start", Token + "");
            if (Token == null) {
                GenerikFunctions.showToast(cont, "LI Login Failed");
                startLinkedInProcess();
            } else {
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(cont, AdvancedConnectionsLinkedIn, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse result) {
                        try {
                            setLIUserProfile(result.getResponseDataAsJson());
                            JSONObject jsonObject = result.getResponseDataAsJson();
                            Log.e("jsonresponse", "aa" + jsonObject.toString() + " ");
                        } catch (Exception e) {
                            e.printStackTrace();
                            GenerikFunctions.showToast(cont, "Li Error  " + e.toString());
                            //Harshada
                            GenerikFunctions.showDialog(m_config.pDialog, "Loading...");
                            LoginValidations.QBStartSession(cont);
                        }
                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        Log.e("Linked In Error", error.toString());
                        GenerikFunctions.showDialog(m_config.pDialog, "Loading...");
                        LoginValidations.QBStartSession(cont);
                    }
                });
            }
        }
    }

    public  void  setLIUserProfile(JSONObject response)
    {
        Log.e("Response ",response.toString()+"");
        liUserInformation= gson.fromJson(response.toString(),LIUserInformation.class);
        Log.e("LI Email ", liUserInformation.getEmailAddress());
        Log.e("LI Connections " ,liUserInformation.getNumConnections());
        Log.e("LI Id",liUserInformation.getId());
        Log.e("LI Pic",liUserInformation.getPictureUrl());
        if(liUserInformation.getNumConnections().equals(null))
        {

        }
        else
        {
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
                        + LoginTableColumns.LI_USER_ID + " = '" + liUserInformation.getId() + "', "
                        + LoginTableColumns.LI_USER_EMAIL + " = '" + liUserInformation.getEmailAddress() + "', "
                        + LoginTableColumns.LI_USER_PROFILE_PIC + " = '" + liUserInformation.getPictureUrl() + "', "
                        + LoginTableColumns.LI_USER_CONNECTIONS + " = '" + liUserInformation.getNumConnections() + "', "
                        + LoginTableColumns.LI_USER_FIRST_NAME + " = '" + liUserInformation.getFirstName() + "', "
                        + LoginTableColumns.LI_USER_LAST_NAME + " = '" + liUserInformation.getLastName() + "', "
                        + LoginTableColumns.LI_USER_HEADLINE + " = '" + liUserInformation.getHeadline() + "' "
                        + " where " + LoginTableColumns.FB_USER_ID + " = '" + fbUserInformation.getFbId().trim() + "'";

                // Log.i("update Brands "+brand_id[i], Update);
                sqldb.execSQL(Update);

                loggedInUserInfo.setLI_USER_ID(liUserInformation.getId());
                loggedInUserInfo.setLI_USER_FIRST_NAME(liUserInformation.getFirstName());
                loggedInUserInfo.setLI_USER_LAST_NAME(liUserInformation.getLastName());
                loggedInUserInfo.setLI_USER_EMAIL(liUserInformation.getEmailAddress());
                loggedInUserInfo.setLI_USER_PROFILE_PIC(liUserInformation.getPictureUrl());
                loggedInUserInfo.setLI_USER_CONNECTIONS(liUserInformation.getNumConnections());
                loggedInUserInfo.setLI_USER_HEADLINE(liUserInformation.getHeadline());

                SharedPreferences.Editor editor= sharedpreferences.edit();
                editor.putString(m_config.LILoginDone,"Yes");
                editor.apply();;



                //Harshada
                GenerikFunctions.showDialog(m_config.pDialog, "Loading...");
                LoginValidations.QBStartSession(cont);


            }

//            Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
//                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
//                            Log.i("User Query  : ", Query);
//                             cursor = sqldb.rawQuery(Query, null);
//                            cursor.moveToFirst();
//                            Log.e("Cursor ",cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_ID)) +"   "
//                            + cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_CONNECTIONS)) +"  " +
//                            cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_EMAIL)) +"   " +
//                            cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_PROFILE_PIC))+"   \\n    ===FB====     "
//                            + cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_NAME)) +"   " +
//                            cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_FRIENDS))+"    " +
//                            cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_GENDER)));

        }

    }

    //Harshada
    @Override
    public void onBackPressed()
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
    public void getDebugHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.aperotechnologies.aftrparties", PackageManager.GET_SIGNATURES);
            for (Signature mysignature : info.signatures) {
                MessageDigest mymd = MessageDigest.getInstance("SHA");
                mymd.update(mysignature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(mymd.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //meghana
    //dismiss cursor on other screen area tab
    //clear edittext focus
    public void dismissCursor() {
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
        if (LoginValidations.isEmpty(edt_usr_name) || LoginValidations.isEmpty(edt_usr_email) || LoginValidations.isEmpty(edt_usr_phone)) {
            if (LoginValidations.isEmpty(edt_usr_name))
            {
                inputToastDisplay = "Please enter user name";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
            else if (LoginValidations.isEmpty(edt_usr_email))
            {
                inputToastDisplay = "Please enter email";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
            else if (LoginValidations.isEmpty(edt_usr_phone))
            {
                inputToastDisplay = "Please enter contact no";
                GenerikFunctions.showToast(cont, inputToastDisplay);
            }
        }
        else
        {
            //Check for valid email pattern
            if (LoginValidations.isValidEmailId(edt_usr_email.getText().toString().trim()))
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
                }
                else
                {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                }
            }
            else
            {
                GenerikFunctions.showToast(cont, "EnterValid Mail");
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
            Log.e("Inside start login", "yes");
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
        for (int i = 0; i < declined_perm.size(); i++)
        {
            Log.e("From dec perm func", declined_perm.get(i));
        }
        loginManager.logInWithReadPermissions(LoginActivity.this, declined_perm);

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
                    // App code
                    Log.e("Login Success", "Yes");
                    token = loginResult.getAccessToken();
                    Log.e("AccessToken", token.toString() + "");

                    Set<String> given_perm = token.getPermissions();
                    iterator = given_perm.iterator();

                    while (iterator.hasNext())
                    {
                        String perm_name = iterator.next().toString();
                       // Log.e("Given Permission: ", perm_name + " ");
                    }

                    ArrayList<String> declined_permissions = new ArrayList<String>();
                    Set<String> declined_perm = token.getDeclinedPermissions();
                    iterator = declined_perm.iterator();
                    while (iterator.hasNext())
                    {
                        String perm_name = iterator.next().toString();
                      //  Log.e("declined_permission : ", perm_name + " ");
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
                    // App code
                    edt_usr_email.setText("");
                    edt_usr_phone.setText("");
                    edt_usr_name.setText("");
                    Log.e("Login onCancel", "Yes");
                    GenerikFunctions.showToast(cont,"Please provide permissions for app login");
                }

                @Override
                public void onError(FacebookException error)
                {
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

    //Meghana
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
                Log.e("Given Permission from already logged in : ", perm_name + " ");
            }

            ArrayList<String> declined_permissions = new ArrayList<String>();
            Set<String> declined_perm = token.getDeclinedPermissions();
            iterator = declined_perm.iterator();
            while (iterator.hasNext())
            {
                String perm_name = iterator.next().toString();
                Log.e("declined_permission from already loggoed in : ", perm_name + " ");
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
                        String emptyFields="";

                        // Application code
                        Log.i("Me Request", object.toString());

                        fbUserInformation = gson.fromJson(object.toString(), FbUserInformation.class);
                        Log.e("User Information --->","Information");
                        Log.e("getFbId Id: " , fbUserInformation.getFbId());
                        Log.e("getGender: " , fbUserInformation.getGender());
                        Log.e("getFbUserName: " , fbUserInformation.getFbUserName());
                        Log.e("getEmail: " ,fbUserInformation.getEmail());
                        Log.e("getBirthday: " ,fbUserInformation.getBirthday());

                        if(fbUserInformation.getBirthday().equals(null))
                        {
                            emptyFields="Birthday";
                        }
                        else
                        {
                            emptyFields="";
                        //    Log.e("getBirthday : " , fbUserInformation.getBirthday());
                        }

                        if (fbUserInformation.getEmail().equals(null)) {
                            fbUserInformation.setEmail("N/A");
                        } else {
                            Log.e("getEmail: ", fbUserInformation.getEmail());
                        }

                        if (fbUserInformation.getFBLocationInformation() == null) {
                            fBCurrentLocationInformation = new FBCurrentLocationInformation();
                            fBCurrentLocationInformation.setLocationId("N/A");
                            fBCurrentLocationInformation.setLocationName("N/A");
                        } else {
                            fBCurrentLocationInformation = fbUserInformation.getFBLocationInformation();
                        }

                        Log.e("CUrrent Location --->", "Details");
                        Log.e("getLocationId Id: ", fBCurrentLocationInformation.getLocationId());
                        Log.e("getLocationName ", fBCurrentLocationInformation.getLocationName());


                        if (fbUserInformation.getFbProfilePictureData().equals(null)) {

                            fbProfilePictureData = fbUserInformation.getFbProfilePictureData();
                            fbProfilePictureData.getFbPictureInformation().setUrl("N/A");
                        } else {
                            fbProfilePictureData = fbUserInformation.getFbProfilePictureData();

                        }

                        Log.e("getImgLink", fbProfilePictureData.getFbPictureInformation().getUrl());


                        if (fbUserInformation.getFbHomelocationInformation() == null) {
                            fbHomelocationInformation = new FbHomelocationInformation();
                            fbHomelocationInformation.setLocationId("N/A");
                            fbHomelocationInformation.setLocationName("N/A");
                        } else {
                            fbHomelocationInformation = fbUserInformation.getFbHomelocationInformation();
                        }

                        Log.e("Home Location --->", "Details");
                        Log.e("getLocationId Id: ", fbHomelocationInformation.getLocationId() + "  aa");
                        Log.e("getLocationName ", fbHomelocationInformation.getLocationName() + "  aa");

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
                                //Clear and insert Here
                                sqldb.delete(LoginTableColumns.USERTABLE, LoginTableColumns.FB_USER_ID + " = '"
                                        + fbUserInformation.getFbId().trim() + "'", null);
                                storeUserInDb();
                            }
                            cursor.close();

                            //Make FB Login Flag true
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(m_config.LoggedInFBUserID, fbUserInformation.getFbId());
                            editor.putBoolean(m_config.FBLoginDone,true);
                            editor.apply();
                            startLinkedInProcess();
                        }
                        else
                        {
//                            Log.e("emptyFields " ,emptyFields);
                            GenerikFunctions.showToast(cont,"Please specify your "+ emptyFields + " in Facebook");
                        }
                    }
                });

        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "id,name,birthday,gender,email,location,picture,hometown");
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

        Log.i("Inserted User ", fbUserInformation.getFbId().trim() + "");

        loggedInUserInfo =new LoggedInUserInformation();

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

        //Total No of friends
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
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
                            //        JSONArray dataArray = graphObject.getJSONArray("data");
                            //        JSONObject paging = graphObject.getJSONObject("paging");
                            JSONObject summary = graphObject.getJSONObject("summary");
                            String totCount = summary.getString("total_count");
                            Log.e("Summary  totCount ", summary + "      " + totCount);

                            total_friends_count = (Integer.parseInt(totCount.trim()));


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

                                //   Log.i("update Brands "+brand_id[i], Update);
                                sqldb.execSQL(Update);
                            }
                            //Check for DB Updation
//                             Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
//                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
//                            Log.i("User Query  : ", Query);
//                             cursor = sqldb.rawQuery(Query, null);
//                            cursor.moveToFirst();
//                            Log.e("Cursor ",cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_NAME)) +"   "
//                                    + cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_FRIENDS)));

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

    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void startLinkedInProcess()
    {
        linkedinStart="Yes";
        Log.e("Inside startLinkedInProcess","Yes");
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener()
        {
            @Override
            public void onAuthSuccess()
            {
                Token=LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue().toString();
                Log.e("LI Token",Token+"");
                GenerikFunctions.showToast(cont,"success       Linked login" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error)
            {
                Log.e("LI Login Error",error.toString()+"");
                GenerikFunctions.showToast(cont, "failed  linked in " + error.toString());
                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    GenerikFunctions.showToast(cont, "Please accept permissions " );
                    startLinkedInProcess();
                }
            }
        }, true);
    }


    //function for enabling TelephonyManager for fetching deviceId
    public void getDeviceIdAndroid(String regId, Activity context){
        this.regId = regId;

        if ((int) Build.VERSION.SDK_INT < 23)
        {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);

           LoginValidations.subscribeToPushNotifications(regId,mTelephony,context);
        }
        else
        {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                Log.e("If permission is not granted",", request for permission");


            }else{
                mTelephony = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                LoginValidations.subscribeToPushNotifications(regId,mTelephony, context);
                Log.e("If Permission is granted","");
            }

        }

    }

    //Callback function for Permission
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("Permission request is accepted","");
                    mTelephony = (TelephonyManager) cont.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    LoginValidations.subscribeToPushNotifications(regId, mTelephony, (Activity) cont);

                } else {

                    // permission denied
                    Log.e("Permission request is denied","");
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

                    boolean should = ActivityCompat.shouldShowRequestPermissionRationale((Activity) cont, Manifest.permission.READ_PHONE_STATE);
                    if(should){
                        //user denied without Never ask again, just show rationale explanation
                        new android.app.AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage("Without this permission the app will be unable to receive Push Notifications.Are you sure you want to deny this permission?")
                                .setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        Log.e("Click of Retry, If permission request is denied",",ask request for permission again");
                                        ActivityCompat.requestPermissions((Activity) cont,
                                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                                    }
                                })
                                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        Log.e("Click of I m sure",", permission request is denied");
                                        if(sharedPreferences.getString(m_config.AWSUserDataDone,"No").equals("Yes"))
                                        {
                                            GenerikFunctions.hideDialog(m_config.pDialog);
                                            Log.e("call to next activity","");
                                            Intent i = new Intent(cont, HomePageActivity.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            AWSDBOperations.createUser(cont, LoginValidations.initialiseLoggedInUser(cont));
                                        }

                                    }
                                })

                                .show();

                    }else{
                        //user has denied with `Never Ask Again`
                        Log.e("Click of Never ask again",", permission request is denied");
                        if(sharedPreferences.getString(m_config.AWSUserDataDone,"No").equals("Yes"))
                        {
                            GenerikFunctions.hideDialog(m_config.pDialog);
                            Log.e("call to next activity","");
                            Intent i = new Intent(cont, HomePageActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            AWSDBOperations.createUser(cont, LoginValidations.initialiseLoggedInUser(cont));
                        }
                    }
                }
                break;
            }


        }
    }

}
/*
*

//
//new GraphRequest(
//        AccessToken.getCurrentAccessToken(),
//        "/" + fbUserInformation.getFbId().trim() + "/taggable_friends",
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