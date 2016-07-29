package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.andtinder.model.CardModel;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.QBSessionClass;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by mpatil on 19/07/16.
 */
public class RequestantActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private ViewPager viewPager;
    CallbackManager callbackManager;
    LoginManager loginManager;
    ArrayList<String> permissions;
    Gson gson;
    AccessToken token;
    SharedPreferences sharedpreferences;
    SQLiteDatabase sqldb;
    DBHelper helper;
    ArrayList<String> status,facebookId,liId, QbId,imageArray;

    public static Context cont;

    public static Configuration_Parameter m_config;

    public static PartyParceableData partyy;
    List GCList, listadptGC;
    public static List gc_list;
    public static String linkedinStart="";
    public static String Token;
    Iterator iterator;
    int total_friends_count = 0;
    public static BillingProcessor bpReqChat;
    public static  boolean readyToPurchaseRChat = false;
    String fragmentPosition;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestants);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;


        if(!BillingProcessor.isIabServiceAvailable(cont)) {
            GenerikFunctions.showToast(cont,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bpReqChat = new BillingProcessor(cont, ConstsCore.base64EncodedPublicKey, this);


        //   pd = (ProgressBar)findViewById(R.id.pd);
        viewPager = (ViewPager)findViewById(R.id.pager);

        GCList = new ArrayList();
        gc_list = new ArrayList<GateCrashersClass>();

        status = new ArrayList<>();
        facebookId = new ArrayList<>();
        liId = new ArrayList<>();
        QbId = new ArrayList<>();
        imageArray  = new ArrayList<>();

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        gson=new Gson();
        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();
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

        //created by Imran for notification check
        //checkFBLILoginState();


        try
        {
            partyy = (PartyParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);
            String PartyID = partyy.getPartyId();

            Log.e("partyID "," "+partyy.getPartyId() );
            PartyTable selparty = m_config.mapper.load(PartyTable.class, PartyID); // retrieve using particular key/primary key
            gc_list = selparty.getGatecrashers();
            //Log.e("finalGCList ", " " + gc_list.size());
            if(gc_list == null)
            {
                //  pd.setVisibility(View.GONE);
                return;
            }
            else
            {
                for(int i=0;i<gc_list.size();i++)
                {
                    GateCrashersClass gc = (GateCrashersClass) gc_list.get(i);
                    Log.e("GCLis ID",gc.getGatecrasherid() +"   " + gc.getGcrequeststatus() + "   " + gc.getGcattendancestatus() + "  \n  " + gc.getgcfbprofilepic() + "  \n  " + gc.getgclkid() + "  " + gc.getgcqbid());
                    facebookId.add(gc.getGatecrasherid().toString());
                    liId.add(gc.getgclkid());
                    QbId.add(gc.getgcqbid());
                    imageArray.add(gc.getgcfbprofilepic());
                    status.add(gc.getGcrequeststatus());
                }

            }
            //  setGCListAdapter();
            RequestantPagerAdapter adapter = new RequestantPagerAdapter(cont,getSupportFragmentManager(),facebookId,liId, QbId,imageArray,status);
            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(new CircularViewPagerHandler(viewPager));




            if(!getIntent().getExtras().getString("from").equals("partydetails") || getIntent().getExtras().getString("from").equals("requestSend")) {
                //Harshada
                for (int i = 0; i < facebookId.size(); i++) {

                    Log.e("came here", "");
                    String GCFBID = getIntent().getExtras().getString("GCFBID");
                    if (facebookId.get(i).equals(GCFBID)) {
                        viewPager.setCurrentItem(i);
                    }
                }
            }
            //Harshada



        }
        catch (Exception ex)
        {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.showToast(cont,"There was an error loading data");
//            pd.setVisibility(View.GONE);
//            pd.setVisibility(View.GONE);
        }
    }



    public void checkFBLILoginState()
    {
        //FB Case
        if(LoginValidations.isFBLoggedIn())
        {
            //do nothing
            Log.e("RequesrtantAcitivity", "FB");
        }
        else
        {
            try
            {
                loginManager.logInWithReadPermissions(RequestantActivity.this, permissions);
                FacebookDataRetieval();
            }

            catch (Exception e)
            {
                Log.e("External Exception", e.toString());
                e.printStackTrace();
            }
        }

        //LI Login Case

        //Condition to check LI session existance check
        if(LISessionManager.getInstance(cont).getSession().getAccessToken() == null)
        {
            startLinkedInProcess();
        }
        else
        {
            //do nothing
            Log.e("RequesrtantAcitivity", "LI");
        }





    }

    public void FacebookDataRetieval()
    {

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
                        Log.e("AccessToken in RequestantActivity", token.toString() + "    " +token);

                        Set<String> given_perm = token.getPermissions();
                        iterator = given_perm.iterator();
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

                            }
                        }
                    }
                });

    }

    public void askForDeclinedFBPermissions(ArrayList<String> declined_perm)
    {

        loginManager.logInWithReadPermissions(RequestantActivity.this, declined_perm);
        FacebookDataRetieval();
    }
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }
    public  void startLinkedInProcess()
    {
        linkedinStart="Yes";
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
                    GenerikFunctions.showToast(cont, "Please accept permissions " );
                    startLinkedInProcess();
                }

            }
        }, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
            GenerikFunctions.showToast(cont,m_config.QbIdforInappPChat);

            if (!bpReqChat.handleActivityResult(requestCode, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data);


//        else {
//            if (linkedinStart.equals(""))
//            {
//                //For FB
//                super.onActivityResult(requestCode, resultCode, data);
//                callbackManager.onActivityResult(requestCode, resultCode, data);
//            } else {
//                //For LI
//                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
//            }
//        }
    }




    @Override
    public void onDestroy() {
        if (bpReqChat != null)
            bpReqChat.release();

        super.onDestroy();
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        GenerikFunctions.showToast(cont,"Purchase Successful");
        Boolean consumed = bpReqChat.consumePurchase(ConstsCore.ITEM_PRIVATECHAT_SKU);
        GenerikFunctions.sDialog(cont, "Creating 1-1 Chat...");

        if (consumed)
        {
            //GenerikFunctions.showToast(cont,"Successfully consumed");
            QBChatDialogCreation.createPrivateChat(Integer.valueOf(m_config.QbIdforInappPChat), cont);
        }
        else{
            GenerikFunctions.hDialog();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //GenerikFunctions.showToast(cont,"onPurchaseHistoryRestored");
        for(String sku : bpReqChat.listOwnedProducts())
            Log.d("", "Owned Managed Product: " + sku);
        for(String sku : bpReqChat.listOwnedSubscriptions())
            Log.d("", "Owned Subscription: " + sku);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        GenerikFunctions.showToast(cont,"onBillingError: " + Integer.toString(errorCode));
    }

    @Override
    public void onBillingInitialized() {
        //GenerikFunctions.showToast(cont,"onBillingInitialized");
        readyToPurchaseRChat = true;
    }

    @Override
    public void onBackPressed() {

        m_config.QbIdforInappPChat = "";
        finish();
    }

}

