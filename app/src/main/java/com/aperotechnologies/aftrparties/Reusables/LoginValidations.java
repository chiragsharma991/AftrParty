package com.aperotechnologies.aftrparties.Reusables;

import android.widget.EditText;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.Login.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.facebook.AccessToken;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by mpatil on 10/05/16.
 */
public  class LoginValidations
{
    public static boolean isValidEmailId(String email)
    {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

//Meghana
    public static boolean isEmpty(EditText etText)
    {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isValidMobile(EditText edt )
    {
        String text=edt.getText().toString().trim();
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", text))
        {
            if(text.length() < 10 || text.length() > 13)
            {
                check = false;
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }

//Meghana
    public static boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    //Meghana
    public void chatLogout(final QBUser user, final Context cont)
    {
        boolean isLoggedIn = m_config.chatService.isLoggedIn();

        if (isLoggedIn)
        {
            m_config.chatService.logout(new QBEntityCallback()
            {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    m_config.chatService.destroy();
                    chatLogin(user, cont);
                }

                @Override
                public void onError(QBResponseException errors)
                {
                }
            });
        } else {
            chatLogin(user, cont);
        }
    }

    ////Meghana
    //function for chat login
    public void chatLogin(QBUser qb_user, final Context cont)
    {
        m_config.chatService.login(qb_user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                PlayServicesHelper PlayServicesHelper;
                Log.e("ChatServicelogin", "Success ");
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
                QBGroupChatManager groupChatManager = m_config.chatService.getGroupChatManager();
                QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                m_config.groupChatManager = groupChatManager;
                m_config.privateChatManager = privateChatManager;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ChatLogin", "Yes");
                editor.apply();

                PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity) cont, initialiseLoggedInUser(cont));
            }

            @Override
            public void onError(QBResponseException error)
            {
                // errror
                Log.e("ChatServicelogin", "OnError " + error.toString());
                error.printStackTrace();
            }
        });
    }
//Meghana
    public static LoggedInUserInformation initialiseLoggedInUser(Context cont)
    {
        DBHelper helper = DBHelper.getInstance(cont);

        SQLiteDatabase sqldb = helper.getWritableDatabase();
        String Query = "Select * from UserTable";
        Cursor cursor = sqldb.rawQuery(Query, null);
        cursor.moveToFirst();
        LoggedInUserInformation loggedInUserInfo = new LoggedInUserInformation();

        loggedInUserInfo.setFB_USER_ID(cursor.getString(cursor.getColumnIndex("fb_user_id")));
        loggedInUserInfo.setFB_USER_NAME(cursor.getString(cursor.getColumnIndex("fb_user_name")));
        loggedInUserInfo.setFB_USER_GENDER(cursor.getString(cursor.getColumnIndex("fb_user_gender")));
        loggedInUserInfo.setFB_USER_BIRTHDATE(cursor.getString(cursor.getColumnIndex("fb_user_birthdate")));
        loggedInUserInfo.setFB_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_EMAIL)));
        loggedInUserInfo.setFB_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_PROFILE_PIC)));
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_ID)));
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_NAME)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_ID)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME)));

        loggedInUserInfo.setLI_USER_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_ID)));
        loggedInUserInfo.setLI_USER_FIRST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_FIRST_NAME)));
        loggedInUserInfo.setLI_USER_LAST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_LAST_NAME)));
        loggedInUserInfo.setLI_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_EMAIL)));
        loggedInUserInfo.setLI_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_PROFILE_PIC)));
        loggedInUserInfo.setLI_USER_CONNECTIONS(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_CONNECTIONS)));
        loggedInUserInfo.setLI_USER_HEADLINE(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_HEADLINE)));


        /*
        public static final String FB_USER_FRIENDS                = "fb_user_friends";

       */

        return loggedInUserInfo;
    }


    public static boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    public static AccessToken getFBAccessToken()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken;

    }


    //Meghana
    public static LoggedInUserInformation initialiseLoggedInUser(Context cont)
    {
        DBHelper helper = DBHelper.getInstance(cont);

        SQLiteDatabase sqldb = helper.getWritableDatabase();
        String Query = "Select * from UserTable";
        Cursor cursor = sqldb.rawQuery(Query, null);
        cursor.moveToFirst();
        LoggedInUserInformation loggedInUserInfo = new LoggedInUserInformation();

        loggedInUserInfo.setFB_USER_ID(cursor.getString(cursor.getColumnIndex("fb_user_id")));
        loggedInUserInfo.setFB_USER_NAME(cursor.getString(cursor.getColumnIndex("fb_user_name")));
        loggedInUserInfo.setFB_USER_GENDER(cursor.getString(cursor.getColumnIndex("fb_user_gender")));
        loggedInUserInfo.setFB_USER_BIRTHDATE(cursor.getString(cursor.getColumnIndex("fb_user_birthdate")));
        loggedInUserInfo.setFB_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_EMAIL)));
        loggedInUserInfo.setFB_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_PROFILE_PIC)));
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_ID)));
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_NAME)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_ID)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME)));

        loggedInUserInfo.setLI_USER_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_ID)));
        loggedInUserInfo.setLI_USER_FIRST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_FIRST_NAME)));
        loggedInUserInfo.setLI_USER_LAST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_LAST_NAME)));
        loggedInUserInfo.setLI_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_EMAIL)));
        loggedInUserInfo.setLI_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_PROFILE_PIC)));
        loggedInUserInfo.setLI_USER_CONNECTIONS(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_CONNECTIONS)));
        loggedInUserInfo.setLI_USER_HEADLINE(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_HEADLINE)));

        /*
        public static final String FB_USER_FRIENDS                = "fb_user_friends";

       */

        return loggedInUserInfo;
    }

    //Harshada
    //function for starting session of quickblox
    public static void QBStartSession(final Context cont){


        QBAuth.createSession(new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Log.e("createSession", "onsuccess");
                try {
                    String qbtoken = QBAuth.getBaseService().getToken();
                    Log.e("New token", qbtoken);
                } catch (Exception e) {

                }

                String FBprofilePic = initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
                String LIprofilePic = initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC();
                Log.e("FBprofilePic", " " + FBprofilePic);
                Log.e("LIprofilePic", " " + LIprofilePic);
                String profilePic = "";
                if (FBprofilePic == null || FBprofilePic.equals("")) {
                    profilePic = LIprofilePic;
                } else {
                    profilePic = FBprofilePic;
                }
                Log.e("token", " " + getFBAccessToken().getToken());
                loginWithFbQuickBlox(getFBAccessToken().getToken(), profilePic, cont);//"https://graph.facebook.com/129419790774542/picture?type=large");
                //call to QuickBlox Login

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("createSession","onerror");
                e.printStackTrace();
            }

        });
    }

    //Harshada
    //function for Login with  Quickblox(FB)
    public static void loginWithFbQuickBlox(String accessToken, final String avatarUrl, final Context cont){

        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                null, new QBEntityCallback<QBUser>()
                {
                    @Override
                    public void onSuccess(QBUser user, Bundle args)
                    {
                        Log.e("Facebook login","Success"+" ");
                        Log.e("user",""+user);
                        user.setFullName(sharedpreferences.getString(m_config.Entered_User_Name,""));
                        uploadprofilePic(user, avatarUrl, cont);

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
    //function for profile pic upload in Quickblox user table
    public static void uploadprofilePic(QBUser user, String avatarUrl, final Context cont){

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        user.setCustomData(avatarUrl);

        QBUsers.updateUser(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.e("user image "," "+user.getCustomData());
                Log.e("updated user---",""+user);

                if(user.equals(null) || user==null)
                {
                    //Log.e("primary user",null+"  null");
                }
                else {
                    try
                    {
                        user.setPassword(BaseService.getBaseService().getToken());

                    }
                    catch (BaseServiceException e)
                    {
                        e.printStackTrace();
                        // means you have not created a session before
                    }

                    // initialize Chat service
                    try
                    {

                        QBChatService.init(cont);
                        final QBChatService chatService = QBChatService.getInstance();
                        m_config.chatService = chatService;
                        //chatLogout(user);
                        boolean isLoggedIn = m_config.chatService.isLoggedIn();
                        if(isLoggedIn)
                        {
                            //if chat is LoggedIn give a call to PlayServiceHelper
                            PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));
                        }
                        else
                        {
                            //call to chatLogin
                            chatLogin(user, cont);
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onError(QBResponseException errors)
            {

            }
        });

    }

    //Harshada
    //function for chat login
    public static void chatLogin(QBUser qb_user, final Context cont){

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        m_config.chatService.login(qb_user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("ChatServicelogin","Success ");
                //QBGroupChatManager groupChatManager  = m_config.chatService.getGroupChatManager();
                //QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                //m_config.groupChatManager = groupChatManager;
                //m_config.privateChatManager = privateChatManager;

                //call to PlayServiceHelper
                PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));


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
    //function for chat logout
    public void chatLogout(final QBUser user){

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        boolean isLoggedIn = m_config.chatService.isLoggedIn();
        if(isLoggedIn) {
            m_config.chatService.logout(new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    m_config.chatService.destroy();
                }

                @Override
                public void onError(QBResponseException errors)
                {
                    errors.printStackTrace();
                    Log.e("onerror","Chat Logout");
                }
            });
        }else{

        }

    }

    //subscribe deviceId and regId in quickblox for PushNotifications
    public static void subscribeToPushNotifications(final String regId, TelephonyManager mTelephony, final Activity context) {

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.DEVELOPMENT);

        String deviceId;

        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        Log.e("deviceId"," "+deviceId);

        subscription.setDeviceUdid(deviceId);
        subscription.setRegistrationID(regId);

        QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>() {

            @Override
            public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                Log.e("subscription","OnSuccess");
                // Persist the regID - no need to register again.
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(m_config.REG_ID, regId);
                editor.apply();
                AWSDBOperations.createUser(context, initialiseLoggedInUser(context));
            }

            @Override
            public void onError(QBResponseException error) {
                Log.e("subscription","onError");
                error.printStackTrace();
            }
        });

    }




}
