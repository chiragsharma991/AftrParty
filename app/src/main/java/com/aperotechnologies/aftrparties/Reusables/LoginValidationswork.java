package com.aperotechnologies.aftrparties.Reusables;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
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


/**
 * Created by mpatil on 10/05/16.
 */
public  class LoginValidationswork
{


    //Meghana
    public static boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

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

        loggedInUserInfo.setFB_USER_ID(cursor.getString(cursor.getColumnIndexOrThrow("fb_user_id")));
        loggedInUserInfo.setFB_USER_NAME(cursor.getString(cursor.getColumnIndexOrThrow("fb_user_name")));
        loggedInUserInfo.setFB_USER_GENDER(cursor.getString(cursor.getColumnIndexOrThrow("fb_user_gender")));
        loggedInUserInfo.setFB_USER_BIRTHDATE(cursor.getString(cursor.getColumnIndexOrThrow("fb_user_birthdate")));
        loggedInUserInfo.setFB_USER_EMAIL(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_EMAIL)));
        loggedInUserInfo.setFB_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_PROFILE_PIC)));
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_HOMETOWN_ID)));
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_HOMETOWN_NAME)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_CURRENT_LOCATION_ID)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME)));
        loggedInUserInfo.setFB_USER_FRIENDS(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.FB_USER_FRIENDS)));

        loggedInUserInfo.setLI_USER_ID(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_ID)));
        loggedInUserInfo.setLI_USER_FIRST_NAME(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_FIRST_NAME)));
        loggedInUserInfo.setLI_USER_LAST_NAME(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_LAST_NAME)));
        loggedInUserInfo.setLI_USER_EMAIL(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_EMAIL)));
        loggedInUserInfo.setLI_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_PROFILE_PIC)));
        loggedInUserInfo.setLI_USER_CONNECTIONS(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_CONNECTIONS)));
        loggedInUserInfo.setLI_USER_HEADLINE(cursor.getString(cursor.getColumnIndexOrThrow(LoginTableColumns.LI_USER_HEADLINE)));


        /*
        public static final String FB_USER_FRIENDS                = "fb_user_friends";

       */

        return loggedInUserInfo;
    }


    public static AccessToken getFBAccessToken()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken;

    }



    //Harshada
    //function for starting session of quickblox
    public static void QBStartSession(final Context cont)
    {
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();


//        String token = null;
//        Long expDate = null;
//        try {
//            token = sharedpreferences.getString(m_config.SessionToken,null);
//            expDate = sharedpreferences.getLong("SessionExpirationDate",0);
//            // save to secure storage when your application goes offline or to the background
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
//            Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
//            t.start();
//
//
//        }
//
//        Date expirationDate = new Date(expDate);
//        Date currentDate = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(currentDate);
//        cal.add(Calendar.HOUR, 2);
//        Date twoHoursAfter = cal.getTime();
//
//        if(token != null && expirationDate != null){
//            if(expirationDate.before(twoHoursAfter)){
//                try {
//                    QBAuth.createFromExistentToken(token, expirationDate);
//                    QBChatService.init(cont);
//                    final QBChatService chatService = QBChatService.getInstance();
//                    m_config.chatService = chatService;
//                    //if session is not expired give call to PlayServiceHelper
//                    PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));
//                    Log.e("if session exist, chat Login","");
//
//
//
//                } catch (BaseServiceException e) {
//                    e.printStackTrace();
//                    //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
//                    t.start();
//                }
//            }else {
//                //if session is expired PlayServiceHelper
//                // recreate session on next start app
//                //createSession(cont);
//
//                Log.e("if session expired, create session","");
//                new createSession(cont).execute();
//            }
//        }else{
//            //for very first time when app is installed and session is not created
//            //createSession(cont);

            Log.e("for very first time, create session","");
            new createSession(cont).execute();

//        }


    }

    //Harshada
    //function for new session creation
    private static class createSession extends AsyncTask<String, Void, Void> {

        Context cont;

        public createSession(Context cont) {
            this.cont = cont;
        }

        @Override
        protected Void doInBackground(String... params) {

            Thread t = new Thread(new CreateSessionLooper(cont));
            t.start();

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void v)
        {


        }
    }


    //Harshada
    //function for Login with  Quickblox(FB)
    private static class loginWithFbQuickBlox extends AsyncTask<String, Void, String> {

        Context cont;
        String avatarUrl;
        String accessToken;

        public loginWithFbQuickBlox(String accessToken, String avatarUrl, Context cont) {
            this.cont = cont;
            this.accessToken = accessToken;
            this.avatarUrl = avatarUrl;
        }

        @Override
        protected String doInBackground(String... params) {

            Thread t = new Thread(new loginWithFbQuickBloxLooper(accessToken, avatarUrl, cont));
            t.start();

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String profilePic)
        {

        }
    }

    //Harshada
    //function for profile pic upload in Quickblox user table
    private static class uploadprofilePic extends AsyncTask<String, Void, String> {

        Context cont;
        QBUser user;
        String avatarUrl;


        public uploadprofilePic(QBUser user, String avatarUrl, final Context cont) {
            this.cont = cont;
            this.user = user;
            this.avatarUrl = avatarUrl;
        }

        @Override
        protected String doInBackground(String... params) {

            Thread t = new Thread(new uploadprofilePicLooper(user, avatarUrl, cont));
            t.start();

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String profilePic)
        {

        }
    }

    //Harshada
    //function for chat login
    private static class chatLogin extends AsyncTask<String, Void, String> {

        Context cont;
        QBUser user;

        public chatLogin(QBUser user, Context cont) {
            this.cont = cont;
            this.user = user;


        }

        @Override
        protected String doInBackground(String... params) {

            Thread t = new Thread(new chatLoginLooper(user, cont));
            t.start();

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String profilePic)
        {

        }
    }


    //subscribe deviceId and regId in quickblox for PushNotifications
    public static class subscribeToPushNotifications extends AsyncTask<String, Void, String> {

        Context cont;
        String regId;
        TelephonyManager mTelephony;

        public subscribeToPushNotifications(String regId, TelephonyManager mTelephony, Activity cont) {
            this.cont = cont;
            this.regId = regId;
            this.mTelephony = mTelephony;



        }

        @Override
        protected String doInBackground(String... params) {

            Thread t = new Thread(new subscribeToPushNotificationsLooper(regId,  mTelephony, (Activity) cont));
            t.start();

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String profilePic)
        {

        }
    }






    static class CreateSessionLooper implements Runnable {

        private Looper myLooper;
        Context cont;




        public CreateSessionLooper(Context cont) {
            this.cont = cont;

        }

        @Override
        public void run() {
            Looper.prepare();
            // code that needed a separated thread
            //createSession(cont);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

            QBAuth.createSession(new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    Log.e("createSession", "onsuccess");

                    try {
                        String qbtoken = QBAuth.getBaseService().getToken();
                        Log.e("qbtoken ", qbtoken);
                    } catch (Exception e) {

                    }

                    QBChatService.init(cont);
                    final QBChatService chatService = QBChatService.getInstance();
                    m_config.chatService = chatService;

                    String FBprofilePic = initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
                    //String LIprofilePic = initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC();

                    String profilePic = FBprofilePic;
                    if (profilePic.equals(null) || profilePic.equals("")) {
                        //if (LIprofilePic == null || LIprofilePic.equals("")) {
                            profilePic = "";
//                        } else {
//                            profilePic = LIprofilePic;
//                        }
                    } else {
                        profilePic = FBprofilePic;
                    }
                    Log.e("fb token", " " + getFBAccessToken().getToken());

                    //loginWithFbQuickBlox(getFBAccessToken().getToken(), profilePic, cont);//"https://graph.facebook.com/129419790774542/picture?type=large");

                    //call to QuickBlox Login
                    new loginWithFbQuickBlox(getFBAccessToken().getToken(), profilePic, cont).execute();//"https://graph.facebook.com/129419790774542/picture?type=large");


                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("createSession", "onerror");
                    e.printStackTrace();
                    //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                    Thread t = new Thread(new ToastDispLooper(cont, "Login Failed, Please try again after some time"));
                    t.start();
                    GenerikFunctions.hideDialog(m_config.pDialog);
                }

            });



            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    static class loginWithFbQuickBloxLooper implements Runnable {

        private Looper myLooper;
        Context cont;
        String accessToken;
        String avatarUrl;


        public loginWithFbQuickBloxLooper(String accessToken, String avatarUrl, Context cont) {
            this.cont = cont;
            this.accessToken = accessToken;
            this.avatarUrl = avatarUrl;

        }

        @Override
        public void run() {
            Looper.prepare();
            // code that needed a separated thread

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

            QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                    null, new QBEntityCallback<QBUser>()
                    {
                        @Override
                        public void onSuccess(QBUser user, Bundle args)
                        {
                            Log.e("Facebook login","Success"+" ");
                            Log.e("user",""+user);
                            user.setFullName(sharedPreferences.getString(m_config.Entered_User_Name,""));
                            new uploadprofilePic(user, avatarUrl, cont).execute();

                        }

                        @Override
                        public void onError(QBResponseException e)
                        {
                            Log.e("Facebook login","OnError");
                            e.printStackTrace();
                            //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                            Thread t = new Thread(new ToastDispLooper(cont, "Login Failed, Please try again after some time"));
                            t.start();
                            GenerikFunctions.hideDialog(m_config.pDialog);
                        }
                    });

            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    static class uploadprofilePicLooper implements Runnable {

        private Looper myLooper;
        Context cont;
        QBUser user;
        String avatarUrl;


        public uploadprofilePicLooper(QBUser user, String avatarUrl, Context cont) {
            this.cont = cont;
            this.user = user;
            this.avatarUrl = avatarUrl;

        }

        @Override
        public void run() {
            Looper.prepare();
            // code that needed a separated thread
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
            user.setCustomData(avatarUrl);

            QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
            {
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

                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString("Id", String.valueOf(user.getId()));
                            editor.putString("Password",user.getPassword());
                            editor.apply();

                        }
                        catch (BaseServiceException e)
                        {
                            e.printStackTrace();
                            //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                            // means you have not created a session before
                            Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                            t.start();

                        }

                        // initialize Chat service
                        try
                        {

//                        QBChatService.init(cont);
//                        final QBChatService chatService = QBChatService.getInstance();
//                        m_config.chatService = chatService;

                            boolean isLoggedIn = m_config.chatService.isLoggedIn();
                            Log.e("isLoggedIn "," "+isLoggedIn);
                            if(isLoggedIn)
                            {
                                //if chat is LoggedIn give a call to PlayServiceHelper
                                PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));
                            }
                            else
                            {
                                //call to chatLogin
                                //chatLogin(user, cont);
                                new chatLogin(user, cont).execute();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                            Thread t = new Thread(new ToastDispLooper(cont, "Login Failed, Please try again after some time"));
                            t.start();
                            GenerikFunctions.hideDialog(m_config.pDialog);
                        }
                    }


                }

                @Override
                public void onError(QBResponseException errors)
                {

                }
            });


            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    static class chatLoginLooper implements Runnable {

        private Looper myLooper;
        Context cont;
        QBUser qb_user;


        public chatLoginLooper(QBUser qb_user, Context cont) {
            this.cont = cont;
            this.qb_user = qb_user;

        }

        @Override
        public void run() {
            Looper.prepare();
            // code that needed a separated thread
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

            m_config.chatService.login(qb_user, new QBEntityCallback()
            {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    Log.e("ChatServicelogin","Success ");

                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    try {
                        editor.putString(m_config.SessionToken, BaseService.getBaseService().getToken());
                        editor.putLong("SessionExpirationDate", BaseService.getBaseService().getTokenExpirationDate().getTime());


                    } catch (BaseServiceException e) {
                        e.printStackTrace();
                        //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                        Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                        t.start();
                    }

                    editor.apply();


                    //QBGroupChatManager groupChatManager  = m_config.chatService.getGroupChatManager();
                    //QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                    //m_config.groupChatManager = groupChatManager;
                    //m_config.privateChatManager = privateChatManager;

                    //call to PlayServiceHelper
                    PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));


                }

                @Override
                public void onError(QBResponseException e)
                {
                    // errror
                    Log.e("ChatServicelogin","OnError "+e.toString());
                    e.printStackTrace();
                    Thread t = new Thread(new ToastDispLooper(cont, "Login Failed, Please try again after some time"));
                    t.start();
                    GenerikFunctions.hideDialog(m_config.pDialog);

                }
            });

            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    static class subscribeToPushNotificationsLooper implements Runnable
    {

        private Looper myLooper;
        Context cont;
        String regId;
        TelephonyManager mTelephony;

        public subscribeToPushNotificationsLooper(String regId, TelephonyManager mTelephony, Activity cont)
        {
            this.cont = cont;
            this.regId = regId;
            this.mTelephony = mTelephony;
        }

        @Override
        public void run()
        {
            Looper.prepare();
            // code that needed a separated thread
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

            QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
            subscription.setEnvironment(QBEnvironment.DEVELOPMENT);

            String deviceId;

            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            } else {
                deviceId = Settings.Secure.getString(cont.getContentResolver(),
                        Settings.Secure.ANDROID_ID); //*** use for tablets
            }
            Log.e("deviceId"," "+deviceId +" "+regId);

            subscription.setDeviceUdid(deviceId);
            subscription.setRegistrationID(regId);

            QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>() {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                    Log.e("subscription","OnSuccess");
                    // Persist the regID - no need to register again.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(m_config.REG_ID, regId);
                    editor.apply();
                    //AWSPartyOperations.createUser(cont, initialiseLoggedInUser(cont));

                }

                @Override
                public void onError(QBResponseException error) {
                    Log.e("subscription","onError");
                    error.printStackTrace();
                    //AWSPartyOperations.createUser(cont, initialiseLoggedInUser(cont));
                }
            });


            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };

    public static class ToastDispLooper implements Runnable
    {

        private Looper myLooper;
        Context cont;
        String message;

        public ToastDispLooper(Context cont, String message)
        {
            this.cont = cont;
            this.message = message;
        }

        @Override
        public void run()
        {
            Looper.prepare();
            // code that needed a separated thread
            Toast.makeText(cont," "+message,Toast.LENGTH_SHORT).show();

            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    //Harshada
    //function for chat logout
    public static void chatLogout(){

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


    public static void QBSessionLogOut(){
        QBUsers.signOut(new QBEntityCallback(){

            @Override
            public void onSuccess(Object o, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }


}



    /*public static void createSession(final Context cont){


        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        QBAuth.createSession(new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("createSession", "onsuccess");

                try
                {
                    String qbtoken = QBAuth.getBaseService().getToken();
                    Log.e("New token", qbtoken);
                }
                catch (Exception e)
                {

                }

                QBChatService.init(cont);
                final QBChatService chatService = QBChatService.getInstance();
                m_config.chatService = chatService;

                String FBprofilePic = initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
                String LIprofilePic = initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC();

                String profilePic = FBprofilePic;
                if(profilePic.equals(null) || profilePic.equals(""))
                {
                    if(LIprofilePic == null || LIprofilePic.equals(""))
                    {
                        profilePic = "";
                    }
                    else
                    {
                        profilePic = LIprofilePic;
                    }
                }
                else
                {
                    profilePic = FBprofilePic;
                }
                Log.e("token", " " + getFBAccessToken().getToken());


                loginWithFbQuickBlox(getFBAccessToken().getToken(), profilePic, cont);//"https://graph.facebook.com/129419790774542/picture?type=large");
                //call to QuickBlox Login

            }

            @Override
            public void onError(QBResponseException e)
            {
                Log.e("createSession","onerror");
                e.printStackTrace();
                //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                t.start();
            }

        });

    }*/


    //Harshada
    //function for Login with  Quickblox(FB)
    /*public static void loginWithFbQuickBlox(String accessToken, final String avatarUrl, final Context cont)
    {

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
                    public void onError(QBResponseException e)
                    {
                        Log.e("Facebook login","OnError");
                        e.printStackTrace();
                        //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                        Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                        t.start();
                    }
                });

    }*/



    //Harshada
    //function for chat login
    /*public static void chatLogin(QBUser qb_user, final Context cont){

        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        m_config.chatService.login(qb_user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("ChatServicelogin","Success ");

                SharedPreferences.Editor editor= sharedpreferences.edit();
                try {
                    editor.putString(m_config.SessionToken,BaseService.getBaseService().getToken());
                    editor.putLong("SessionExpirationDate",BaseService.getBaseService().getTokenExpirationDate().getTime());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                    //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                    Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                    t.start();
                }

                editor.apply();


                //QBGroupChatManager groupChatManager  = m_config.chatService.getGroupChatManager();
                //QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                //m_config.groupChatManager = groupChatManager;
                //m_config.privateChatManager = privateChatManager;

                //call to PlayServiceHelper
                PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));


            }

            @Override
            public void onError(QBResponseException e)
            {
                // errror
                Log.e("ChatServicelogin","OnError "+e.toString());
                e.printStackTrace();
               // Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                t.start();

            }
        });
    }*/

    //Harshada
    //function for profile pic upload in Quickblox user table
   /* public static void uploadprofilePic(QBUser user, String avatarUrl, final Context cont)
    {
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        user.setCustomData(avatarUrl);

        QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
        {
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
                        //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                        // means you have not created a session before
                        Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                        t.start();

                    }

                    // initialize Chat service
                    try
                    {

//                        QBChatService.init(cont);
//                        final QBChatService chatService = QBChatService.getInstance();
//                        m_config.chatService = chatService;

                        boolean isLoggedIn = m_config.chatService.isLoggedIn();
                        if(isLoggedIn)
                        {
                            //if chat is LoggedIn give a call to PlayServiceHelper
                            PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));
                        }
                        else
                        {
                            //call to chatLogin
                            //chatLogin(user, cont);
                            new chatLogin(user, cont).execute();
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        //Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
                        Thread t = new Thread(new ToastDispLooper(cont, e.getMessage()));
                        t.start();
                    }
                }


            }

            @Override
            public void onError(QBResponseException errors)
            {

            }
        });

    }

    //subscribe deviceId and regId in quickblox for PushNotifications
    /*public static void subscribeToPushNotifications(final String regId, TelephonyManager mTelephony, final Activity context) {

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
        Log.e("deviceId"," "+deviceId +" "+regId);

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

    }*/



