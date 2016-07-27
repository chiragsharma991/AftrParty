package com.aperotechnologies.aftrparties.Reusables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.AsyncAgeCalculation;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.Login.OTPActivity;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.SplashActivity;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


/**
 * Created by mpatil on 10/05/16.
 */
public  class LoginValidations
{

    static int faces=0;
    static FaceOverlayView faceOverlayView;

    static PlayServicesHelper playServicesHelper;
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
        String FBprofilePic = initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();

        String profilePic = FBprofilePic;
        if (profilePic.equals(null) || profilePic.equals(""))
        {
            profilePic = "";
        }
        else
        {
            profilePic = FBprofilePic;
        }
       // Log.e("fb token", " " + getFBAccessToken().getToken());

        Log.e("for very first time, create session","");
        new createSession(getFBAccessToken().getToken(), profilePic, cont).execute();
    }



    //Harshada
    //function for new session creation
    private static class createSession extends AsyncTask<String, Void, Void>
    {
        Context cont;
        String token;
        String profilePic;

        public createSession(String token, String profilePic, Context cont)
        {
            this.cont = cont;
            this.token = token;
            this.profilePic = profilePic;
        }

        @Override
        protected Void doInBackground(String... params)
        {

            Thread t = new Thread(new CreateSessionLooper(token, profilePic, cont));
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
    private static class loginWithFbQuickBlox extends AsyncTask<String, Void, String>
    {

        Context cont;
        String avatarUrl;
        String accessToken;

        public loginWithFbQuickBlox(String accessToken, String avatarUrl, Context cont)
        {
            this.cont = cont;
            this.accessToken = accessToken;
            this.avatarUrl = avatarUrl;
        }

        @Override
        protected String doInBackground(String... params)
        {


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
    public static class uploadprofilePic extends AsyncTask<String, Void, String>
    {

        Context cont;
        QBUser user;
        String avatarUrl;


        public uploadprofilePic(QBUser user, String avatarUrl, final Context cont)
        {
            this.cont = cont;
            this.user = user;
            this.avatarUrl = avatarUrl;
        }

        @Override
        protected String doInBackground(String... params)
        {

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
    private static class chatLogin extends AsyncTask<String, Void, String>
    {

        Context cont;
        QBUser user;

        public chatLogin(QBUser user, Context cont)
        {
            this.cont = cont;
            this.user = user;


        }

        @Override
        protected String doInBackground(String... params)
        {

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
    public static class subscribeToPushNotifications extends AsyncTask<String, Void, String>
    {

        Context cont;
        String regId;
        TelephonyManager mTelephony;

        public subscribeToPushNotifications(String regId, TelephonyManager mTelephony, Activity cont)
        {
            this.cont = cont;
            this.regId = regId;
            this.mTelephony = mTelephony;
        }

        @Override
        protected String doInBackground(String... params)
        {
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


    static class CreateSessionLooper implements Runnable
    {
        private Looper myLooper;
        Context cont;
        String token;
        String profilePic;

        public CreateSessionLooper(String token, String profilePic, Context cont)
        {
            this.cont = cont;
            this.token = token;
            this.profilePic = profilePic;
        }

        @Override
        public void run()
        {
            Looper.prepare();
            // code that needed a separated thread
            //createSession(cont);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
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
                        Log.e("qbtoken ", qbtoken);
                    }
                    catch (Exception e)
                    {
                        callToHomePageActivity(cont);

                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    try
                    {
                        editor.putString(m_config.SessionToken, BaseService.getBaseService().getToken());
                        editor.putLong("SessionExpirationDate", BaseService.getBaseService().getTokenExpirationDate().getTime());


                    }
                    catch (BaseServiceException e)
                    {
                        e.printStackTrace();

                        callToHomePageActivity(cont);
                    }

                    editor.apply();
                    //call to QuickBlox Login
                    new loginWithFbQuickBlox(getFBAccessToken().getToken(), profilePic, cont).execute();//"https://graph.facebook.com/129419790774542/picture?type=large");

                }

                @Override
                public void onError(QBResponseException e)
                {
                    Log.e("createSession", "onerror");
                    e.printStackTrace();
                    callToHomePageActivity(cont);

                }
            });

            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }


    };


    static class loginWithFbQuickBloxLooper implements Runnable
    {

        private Looper myLooper;
        Context cont;
        String accessToken;
        String avatarUrl;


        public loginWithFbQuickBloxLooper(String accessToken, String avatarUrl, Context cont)
        {
            this.cont = cont;
            this.accessToken = accessToken;
            this.avatarUrl = avatarUrl;

        }

        @Override
        public void run()
        {
            Looper.prepare();
            // code that needed a separated thread

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();

            QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                    null, new QBEntityCallback<QBUser>()
                    {
                        @Override
                        public void onSuccess(final QBUser user, Bundle args)
                        {

                            Log.e("Facebook login","Success"+" ");
                            Log.e("user",""+user);
                            user.setFullName(sharedPreferences.getString(m_config.Entered_User_Name,""));
                            SharedPreferences.Editor editor= sharedPreferences.edit();
                            editor.putString(m_config.QuickBloxID, String.valueOf(user.getId()));
                            editor.apply();

                            if(cont instanceof RegistrationActivity)
                            {
                                Log.e("camee ","here");
                                new AWSLoginOperations.addUserQuickBloxId(cont, user, avatarUrl).execute();
                            }

                            if(cont instanceof Welcome)
                            {
                                Log.e("camee11111 ","here");
                                checkChatLoginExist(cont, user);
                            }

                            if(cont instanceof SplashActivity)
                            {
                                Log.e("camee2222 ","here");
                                checkChatLoginExist(cont, user);
                            }


                        }

                        @Override
                        public void onError(QBResponseException e)
                        {
                            Log.e("Facebook login","OnError");
                            e.printStackTrace();
                            callToHomePageActivity(cont);
                        }
                    });

            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    static class uploadprofilePicLooper implements Runnable
    {
        private Looper myLooper;
        Context cont;
        QBUser user;
        String avatarUrl;

        public uploadprofilePicLooper(QBUser user, String avatarUrl, Context cont)
        {
            this.cont = cont;
            this.user = user;
            this.avatarUrl = avatarUrl;

        }

        @Override
        public void run()
        {
            Looper.prepare();
            // code that needed a separated thread
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
            final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
            user.setCustomData(avatarUrl);

            QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
            {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    //Log.e("user image "," "+user.getCustomData());
                    Log.e("updated user successfully--"," "+user);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(m_config.QBLoginDone, "Yes");
                    editor.apply();


                    if(user.equals(null) || user==null)
                    {
                        //Log.e("primary user",null+"  null");
                    }
                    else
                    {

                        Log.e("upload ","tettrtwetyr");
                        // Initialise ChatService
                        checkChatLoginExist(cont, user);

                    }
                }

                @Override
                public void onError(QBResponseException errors)
                {
                    callToHomePageActivity(cont);
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

            QBChatService.getInstance().login(qb_user, new QBEntityCallback()
            {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    Log.e("ChatServicelogin","Success ");

                    //call to PlayServiceHelper
                    new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));


                }

                @Override
                public void onError(QBResponseException e)
                {
                    // errror
                    Log.e("ChatServicelogin","OnError "+e.toString());
                    e.printStackTrace();
                    callToHomePageActivity(cont);




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

            if (mTelephony.getDeviceId() != null)
            {
                deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            }
            else
            {
                deviceId = Settings.Secure.getString(cont.getContentResolver(),
                        Settings.Secure.ANDROID_ID); //*** use for tablets
            }
            Log.e("deviceId"," "+deviceId +" "+regId);

            subscription.setDeviceUdid(deviceId);
            subscription.setRegistrationID(regId);

            QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>()
            {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args)
                {
                    Log.e("subscription","OnSuccess");
                    // Persist the regID - no need to register again.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(m_config.REG_ID, regId);
                    editor.apply();
                    //AWSPartyOperations.createUser(cont, initialiseLoggedInUser(cont));
                    //AWSLoginOperations.addUserDeviceToken(cont,initialiseLoggedInUser(cont));
                    new AWSLoginOperations.addUserDeviceToken(cont,initialiseLoggedInUser(cont)).execute();

                }

                @Override
                public void onError(QBResponseException e)
                {
                    Log.e("subscription","onError");
                    e.printStackTrace();
                    callToHomePageActivity(cont);

                }
            });


            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


    //Harshada
    //function for chat logout
    public static void chatLogout(){

        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        boolean isLoggedIn = QBChatService.getInstance().isLoggedIn();
        if(isLoggedIn) {
            QBChatService.getInstance().logout(new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    QBChatService.getInstance().destroy();
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

    public static void FaceDetect(final Context cont)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        final Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        Activity act = (Activity)cont;
        faceOverlayView = (FaceOverlayView)act.findViewById(R.id.face_overlay);
        try
        {
            GenerikFunctions.showDialog(m_config.pDialog,"Validationg...");
            LoggedInUserInformation loggedInUserInformation = initialiseLoggedInUser(cont);
            // Log.e("Inside try","yes");
            String  url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
            Log.e("URL for FB",url+"");
            if(url.equals(null) || url.equals("") || url.equals("N/A"))
            {
              // Log.e("Users FB Pic not Avail","Yes");
                url = "";
            }
            else
            {
                url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
            }
            // Log.e("URL",url);
            if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
            {
                // Log.e("Before Picasso play service","yes");
                Target mTarget = new Target()
                {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
                    {
                        Log.e("FB bitmap loaded ","sucessfully  " + bitmap.toString() );
                        faces = faceOverlayView.setBitmap(bitmap);
                        Log.e("No of faces from post",faces+"");

                        if(faces>0)
                        {
                            Log.e("There is face in pic",faces+"");
                            Log.e("GO for OTP","Yes");
                            //Set  Face detect flag here to true
                            SharedPreferences.Editor editorq = sharedPreferences.edit();
                            editorq.putString(m_config.FaceDetectDone,"Yes");
                            editorq.apply();
                            Intent intent = new Intent(cont,OTPActivity.class);
                            cont.startActivity(intent);
                        }
                        else
                        {
                            //Set  Face detect flag here to false
                            Log.e("There is no face in pic","");
                            Handler h = new Handler(cont.getMainLooper());
                            h.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(RegistrationActivity.reg_pd!=null)
                                    {
                                        if(RegistrationActivity.reg_pd.isShowing())
                                        {
                                            RegistrationActivity.reg_pd.dismiss();
                                        }
                                    }
                                    if(Welcome.wl_pd!=null)
                                    {
                                        if(Welcome.wl_pd.isShowing())
                                        {
                                            Welcome.wl_pd.dismiss();
                                        }
                                    }
                                    if(SplashActivity.pd!=null)
                                    {
                                        if(SplashActivity.pd.isShowing())
                                        {
                                            SplashActivity.pd.dismiss();
                                        }
                                    }

                                }
                            });
//                            if(RegistrationActivity.reg_pd.isShowing())
//                            {
//                                RegistrationActivity.reg_pd.dismiss();
//                            }

                            SharedPreferences.Editor editorq = sharedPreferences.edit();
                            editorq.putString(m_config.FaceDetectDone,"No");
                            editorq.apply();
                            GenerikFunctions.showToast(cont,"There is no face in your profile pic");
                        }
                       // GenerikFunctions.hideDialog(m_config.pDialog);
                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable)
                    {
                        Log.e("On FB bitmap failed",drawable.toString());
                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable)
                    {
                    }
                };

                Picasso.with(cont)
                        .load(url)
                        .into(mTarget);
                faceOverlayView.setTag(mTarget);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void checkPendingLoginFlags(Context cont)
    {
        Log.e("checkPendingLoginFlags","checkPendingLoginFlags");
        LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        Log.e("In Check Pending Flags",sharedPreferences.getString(m_config.BasicFBLIValidationsDone,"No")
                +"    " +sharedPreferences.getString(m_config.FaceDetectDone,"No") +"     " +
                sharedPreferences.getString(m_config.OTPValidationDone,"No"));

        if(sharedPreferences.getString(m_config.BasicFBLIValidationsDone,"No").equals("Yes"))
        {
            if(sharedPreferences.getString(m_config.FaceDetectDone,"No").equals("Yes"))
            {
                if(sharedPreferences.getString(m_config.OTPValidationDone,"No").equals("Yes"))
                {

                    new AWSLoginOperations.addUserRegStatus(cont,loggedInUserInformation).execute();///only for testing

                    //  GenerikFunctions.hideDialog(m_config.pDialog);
                }
                else
                {
                   //  new AWSLoginOperations.addUserRegStatus(cont,loggedInUserInformation).execute();///only for testing

                    //Uncomment this later


                    if(RegistrationActivity.reg_pd!=null)
                    {
                        if(RegistrationActivity.reg_pd.isShowing())
                        {
                            RegistrationActivity.reg_pd.dismiss();
                        }
                    }
                    if(Welcome.wl_pd!=null)
                    {
                        if(Welcome.wl_pd.isShowing())
                        {
                            Welcome.wl_pd.dismiss();
                        }
                    }
                    if(SplashActivity.pd!=null)
                    {
                        if(SplashActivity.pd.isShowing())
                        {
                            SplashActivity.pd.dismiss();
                        }
                    }
                    Log.e("Here at OTP Activity","Yes");

                    Intent intent = new Intent(cont,OTPActivity.class);
                    cont.startActivity(intent);
                }
            }
            else
            {
                Log.e("Go for Face Detect","Yes");
                FaceDetect(cont);
            }
        }
        else
        {
            new AsyncAgeCalculation(cont).execute();
        }
    }

    public static void callToHomePageActivity(final Context cont){
        Handler h = new Handler(cont.getMainLooper());
        h.post(new Runnable()
        {
            @Override
            public void run()
            {

                if(RegistrationActivity.reg_pd != null)
                {
                    if (RegistrationActivity.reg_pd.isShowing())
                    {
                        RegistrationActivity.reg_pd.dismiss();

                    }
                    GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                }
                else if (Welcome.wl_pd != null)
                {
                    if (Welcome.wl_pd.isShowing())
                    {
                        Welcome.wl_pd.dismiss();
                    }
                    GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                }
                else  if(SplashActivity.pd != null)
                {
                    if(SplashActivity.pd.isShowing())
                    {
                        SplashActivity.pd.dismiss();
                    }
                    Intent i = new Intent(cont, HomePageActivity.class);
                    cont.startActivity(i);
                }
                else{
                    GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                }




            }
        });
    }

    public static void checkChatLoginExist(final Context cont, final QBUser user){
        boolean isLoggedIn = QBChatService.getInstance().isLoggedIn();
        Log.e("isLoggedIn "," "+isLoggedIn);
        if(isLoggedIn)
        {
            Log.e("here" ," inLoggedIn");
            //if chat is LoggedIn give a call to PlayServiceHelper
            new PlayServicesHelper((Activity)cont, initialiseLoggedInUser(cont));
        }
        else
        {
            Log.e("here" ," if not inLoggedIn");
            try
            {
                user.setPassword(BaseService.getBaseService().getToken());
                new chatLogin(user, cont).execute();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Handler h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run() {
                        if (RegistrationActivity.reg_pd != null) {
                            if (RegistrationActivity.reg_pd.isShowing()) {
                                RegistrationActivity.reg_pd.dismiss();
                            }
                            GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                        } else {
                            GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                        }

                        if (Welcome.wl_pd != null) {
                            if (Welcome.wl_pd.isShowing()) {
                                Welcome.wl_pd.dismiss();
                            }
                            GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");

                        } else {
                            GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                        }

                        if (SplashActivity.pd != null) {
                            if (SplashActivity.pd.isShowing()) {
                                SplashActivity.pd.dismiss();
                                Intent i = new Intent(cont, HomePageActivity.class);
                                cont.startActivity(i);
                            } else {
                                Intent i = new Intent(cont, HomePageActivity.class);
                                cont.startActivity(i);
                            }
                        }
                    }
                });

            }
            //call to chatLogin

        }
    }


}
