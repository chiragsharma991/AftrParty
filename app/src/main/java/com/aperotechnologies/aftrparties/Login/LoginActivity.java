package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
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


/**
 * Created by hasai on 06/05/16.
 */
public class LoginActivity extends Activity
{


    private static final String TAG = "LoginActivity";
    static final String APP_ID = "34621";
    static final String AUTH_KEY = "q6aK9sm6GCSmtru";
    static final String AUTH_SECRET = "uTOm5-R4zYyR-DV";
    static final String ACCOUNT_KEY = "bzbtQDLez742xU468TXt";

    Configuration_Parameter m_config;
    QBUser primary_user;
    PlayServicesHelper playServicesHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);



        primary_user = new QBUser();

        // Initialize the Amazon Cognito credentials provider
//        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "us-east-1:c8b768b7-6479-4291-82b3-71ad3697ff04", // Identity Pool ID
//                Regions.US_EAST_1 // Region
//        );

         // Initialize the Amazon Cognito credentials provider
         final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        m_config= Configuration_Parameter.getInstance();
        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        //DynamoDB client to create an Object m_config.mapper:
        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);
        Log.e("m_config.mapper ", " " + m_config.mapper);



    }



    public void QBStartSession(){
        QBAuth.createSession(new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                Log.e("createSession", "onsuccess");
                try {
                    String token = QBAuth.getBaseService().getToken();
                    Log.e("New token", token);
                } catch (Exception e) {

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

    public void loginWithFbQuickBlox(String accessToken, final String FullName, final String avatarUrl){


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
                        if(!user.getFullName().equals(FullName)){
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
                                chatLogin();

//
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

    public void uploadprofilePic(QBUser user, String avatarUrl){
        Log.e("here",":1----");


        if(user.getCustomData() == null || !user.getCustomData().equals(avatarUrl)){
            user.setCustomData(avatarUrl);
            Log.e("here",":2----");
        }


        QBUsers.updateUser(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                Log.e("user image "," "+user.getCustomData());
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });

    }


    public void chatLogin(){
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

    public void chatLogout(){
        boolean isLoggedIn = m_config.chatService.isLoggedIn();

        if(isLoggedIn) {

            m_config.chatService.logout(new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    m_config.chatService.destroy();
                }

                @Override
                public void onError(QBResponseException errors) {
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
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
}
