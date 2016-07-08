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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.*;
import com.aperotechnologies.aftrparties.model.LIPictureData;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.aperotechnologies.aftrparties.Login.LoginTableColumns.FB_USER_ID;

/**
 * Created by mpatil on 25/05/16.
 */
public class Welcome extends Activity
{
    Button btn_login, btn_register;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    static Context cont;
    CallbackManager callbackManager;
    LoginManager loginManager;
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


    ProgressDialog progressDialog;

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

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        faceOverlayView = (FaceOverlayView) ((Activity)cont).findViewById(R.id.face_overlay);

        //FB Variables
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        m_config.pDialog = new ProgressDialog(cont);

        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");
        permissions.add("user_friends");
        permissions.add("user_hometown");
        permissions.add("user_photos");


            Log.e("Shrd Pref in Welcome",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                    sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "  + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A"));



        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
                //   Intent intent = new Intent(cont, HomePageActivity.class);
                //      startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Shrd Pref in Welcome on login click",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "  + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A"));



                if(sharedPreferences.getString(m_config.Entered_User_Name,"N/A").equals("N/A"))
                {
                    Log.e("Inside if","Launch registration");
                    Intent intent = new Intent(Welcome.this,RegistrationActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Log.e("11","11");
                    //FB Login First
                    linkedinStart = "";
                    try
                    {
                        loginManager.logInWithReadPermissions(Welcome.this, permissions);
                        Log.e("Inside start login", "yes");
                        FacebookDataRetieval();
                    }
                    catch (Exception e)
                    {
                        Log.e("External Exception", e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //Meghana
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("onActivityResult","linkedinStart :   "+linkedinStart);
        if (linkedinStart.equals(""))
        {
            //For FB
            Log.e("onActivityResult","linkedinStart :   "+linkedinStart +"   11");
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            //For LI
            Log.e("onActivityResult","linkedinStart :   "+linkedinStart +"   22");
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            Log.i("Request Code phase 2", requestCode + "   " + resultCode + "  " + data);
            Log.e("Token from start", Token + "");
            if (Token == null)
            {
                GenerikFunctions.showToast(cont,"LI Login Failed");
                GenerikFunctions.hideDialog(m_config.pDialog);
                m_config.pDialog.dismiss();

                try
                {

                    //setLIUserProfile("");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                //   startLinkedInProcess();
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
                            //   liUserInformation = gson.fromJson(result.toString(),LIUserInformation.class);
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

                            //New Harshada Code for QB Session

                           // LoginValidations.QBStartSession(cont);
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
//

                        //New Harshada Code for QB Session
                      //  LoginValidations.QBStartSession(cont);
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
//
//            Log.e("LI Email ", liUserInformation.getEmailAddress());
//
//            Log.e("LI Id", liUserInformation.getId());
//
//            Log.e("LI emailAddress", liUserInformation.getEmailAddress());
//            Log.e("LI numConnections", liUserInformation.getNumConnections());
//            Log.e("LI headline", liUserInformation.getHeadline());
//            Log.e("LI firstName", liUserInformation.getFirstName());
//            Log.e("LI lastName", liUserInformation.getLastName());

                liPictureData = new LIPictureData();
                if (liPictureData.equals(liUserInformation.getLIPictureData()))
                {
                    Log.e("Empty Pictures", "Equal both lipicdata local and received  " + "  both empty");
                }
                else
                {
                    Log.e("Not equal ", "Has LI Puicture data");
                    liPictureData = liUserInformation.getLIPictureData();
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
                //Log.e("Onside update else","Yes");
                // Log.e("LI emailAddress", liUserInformation.getEmailAddress());
                //  Log.e("LI numConnections", liUserInformation.getNumConnections());
                //  Log.e("LI Id", liUserInformation.getId());

                // Log.e("LI headline", liUserInformation.getHeadline());
                Log.e("LI firstName", liUserInformation.getFirstName());
                Log.e("LI lastName", liUserInformation.getLastName());

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
                new AWSLoginOperations.addLIUserInfo(cont,loggedInUserInformation).execute();

                Log.e("Shrd Pref aftr LILginDne",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                        sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "
                        + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A"));
            }

        }
        catch (Exception e)
        {
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Welcome.this);
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

    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {
        for (int i = 0; i < declined_perm.size(); i++)
        {
            Log.e("From dec perm func", declined_perm.get(i));
        }

        loginManager.logInWithReadPermissions(Welcome.this, declined_perm);
        FacebookDataRetieval();
    }

    public void FacebookDataRetieval()
    {
        Log.e("Inside FB data retreive","Yes");
        linkedinStart="";

        Log.e("Inside FB data retreive","Yes   " +linkedinStart +"   aa  " +loginManager.getLoginBehavior().toString());

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

                        while (iterator.hasNext())
                        {
                            String perm_name = iterator.next().toString();
                            Log.e("Given Permission in: ", perm_name + " ");
                        }

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
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(m_config.Entered_User_Name, "");
                                editor.putString(m_config.Entered_Email, "");
                                editor.putString(m_config.Entered_Contact_No, "");
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
                        GenerikFunctions.showDialog(m_config.pDialog, "Loading...");

                        // Application code
                        Log.i("Me Request", object.toString());

//                        AddFriends addFriends = new AddFriends();
//                        addFriends.requestInvitableFriends(cont,token);

                        fbUserInformation = gson.fromJson(object.toString(), FbUserInformation.class);
                        Log.e("User Information --->","Information");
                        Log.e("getFbId Id: " , fbUserInformation.getFbId());

//                        Log.e("getGender: " , fbUserInformation.getGender());
//                        Log.e("getFbUserName: " , fbUserInformation.getFbUserName());
//                        Log.e("getEmail: " ,fbUserInformation.getEmail());
//                        Log.e("getBirthday: " ,fbUserInformation.getBirthday());

                        if(fbUserInformation.getBirthday().equals(null))
                        {
                            fbUserInformation.setBirthday("N/A");
                        }

                        Log.e("getBirthday : " , fbUserInformation.getBirthday());

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
                        Log.e("CUrrent Location --->","Details");
                        Log.e("getLocationId Id: " , fBCurrentLocationInformation.getLocationId());
                        Log.e("getLocationName " , fBCurrentLocationInformation.getLocationName());
                        if(fbUserInformation.getFbHomelocationInformation() != null)
                        {
                            fbHomelocationInformation = new FbHomelocationInformation();
                            if(fbHomelocationInformation.equals(fbUserInformation.getFbHomelocationInformation()))
                            {
                                Log.e("Both Home Location ","Is Empty");
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

                        Log.e("Home Location --->","Details");
                        Log.e("getLocationId Id: " , fbHomelocationInformation.getLocationId() +"  aa");
                        Log.e("getLocationName " , fbHomelocationInformation.getLocationName()+"  aa");

                        if(fbUserInformation.getFbProfilePictureData().equals(null))
                        {
                            fbProfilePictureData = fbUserInformation.getFbProfilePictureData();
                            fbProfilePictureData.getFbPictureInformation().setUrl("N/A");
                        }
                        else
                        {
                            fbProfilePictureData=fbUserInformation.getFbProfilePictureData();
                        }

                        Log.e("getImgLink",fbProfilePictureData.getFbPictureInformation().getUrl());

                        Log.e("Empty Fields","Yes   " + emptyFields +"    aa");

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

                                loggedInUserInfo =new LoggedInUserInformation();

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
                            GenerikFunctions.hideDialog(m_config.pDialog);
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
                            Log.e("graphObject",graphObject.toString());
                            //        JSONArray dataArray = graphObject.getJSONArray("data");
                            //        JSONObject paging = graphObject.getJSONObject("paging");
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
                                LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
                                Log.e("Info in storage",loggedInUserInformation.getFB_USER_BIRTHDATE() +"   " +loggedInUserInformation.getFB_USER_HOMETOWN_NAME());
                                new AWSLoginOperations.addFBUserInfo(cont,loggedInUserInformation,"Welcome",sqldb).execute();

                            }

                            // Check for DB Updation
//                             Query = "Select * from "+ LoginTableColumns.USERTABLE + " where " +
//                                    FB_USER_ID +" = '" + fbUserInformation.getFbId().trim() + "'";
//                            Log.i("User Query for Friends retrieve : ", Query);
//                            cursor = sqldb.rawQuery(Query, null);
//                            cursor.moveToFirst();
//                            Log.e("Cursor  ",cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_NAME)) +"   "
//                                    + cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_FRIENDS)));

                        }
                        catch (Exception e)
                        {
                            GenerikFunctions.hideDialog(m_config.pDialog);
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

    public static void startLinkedInProcess()
    {
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
                GenerikFunctions.showToast(cont, "failed  linked in " + error.toString());
                if(error.toString().trim().contains("USER_CANCELLED"))
                {
                    GenerikFunctions.showToast(cont, "Please accept permissions " );
                    linkedinStart="Yes";
                    startLinkedInProcess();
                }
                else //if(error.toString().trim().contains("UNKNOWN_ERROR"))
                {
                    Log.e("Inside Else","Yes");
                    //GenerikFunctions.showToast(cont, "failed  linked in login " + error.toString());
                }
            }
        }, true)
        ;
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
                        new android.app.AlertDialog.Builder(Welcome.this)
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
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }

}
