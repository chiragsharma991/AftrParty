package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPaymentOperations;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class HostProfileActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public static Configuration_Parameter m_config;
    public static Context cont;
    public static HostAdapter adapter;
    public static  String fbid, qbid, liid, img_url;
    TextView txt_header;
    private ViewPager viewPager;
    public static BillingProcessor bpHostChat;
    public static  boolean readyToPurchaseHChat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;
        viewPager = (ViewPager)findViewById(R.id.pager);
        txt_header = (TextView) findViewById(R.id.activity_title);
        txt_header.setText("Host");

        if(!BillingProcessor.isIabServiceAvailable(cont)) {
            GenerikFunctions.showToast(cont,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bpHostChat = new BillingProcessor(cont, ConstsCore.base64EncodedPublicKey, this);


        fbid = getIntent().getStringExtra("FBID");
        liid = getIntent().getStringExtra("LIID");
        qbid = getIntent().getStringExtra("QBID");
        img_url = getIntent().getStringExtra("IMG");


        adapter = new HostAdapter(cont,getSupportFragmentManager(),fbid,liid,qbid,img_url);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new CircularViewPagerHandler(viewPager));

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!bpHostChat.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bpHostChat != null)
            bpHostChat.release();

        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        GenerikFunctions.sDialog(cont,"Creating 1-1 Chat...");
        Long subscriptiondate = Validations.getCurrentTime() + ConstsCore.FifteenDayVal;
        if(m_config.DialogidforHostInappPChat.equals("")){
            //Here fbid refers to oppfbid
            QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid), cont, subscriptiondate, fbid, "1-1 Chat Created Successfully");
        }
        else{
            new AWSPaymentOperations.storePrivateChat(cont, subscriptiondate , m_config.DialogidforHostInappPChat, fbid, null, "1-1 Chat Updated Successfully").execute();
            //QBChatDialogCreation.deletePrivateDialog(m_config.DialogidforHostInappPChat);
            //QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid), cont, subscriptiondate, fbid, "1-1 Chat Created Successfully");
        }
        Boolean consumed = bpHostChat.consumePurchase(ConstsCore.ITEM_PRIVATECHAT_SKU);
        if (consumed)
        {
            //GenerikFunctions.showToast(cont,"Successfully consumed");
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
        //GenerikFunctions.showToast(cont,"onPurchaseHistoryRestored");
        for(String sku : bpHostChat.listOwnedProducts())
            Log.d("", "Owned Managed Product: " + sku);
        for(String sku : bpHostChat.listOwnedSubscriptions())
            Log.d("", "Owned Subscription: " + sku);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if(!m_config.DialogidforHostInappPChat.equals(""))
        {
            String oppfbid = fbid;
            String facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
            QBChatDialogCreation.deletePrivateDialog(cont, m_config.DialogidforHostInappPChat, facebookid, oppfbid);
        }

        //GenerikFunctions.showToast(cont,"onBillingError: " + Integer.toString(errorCode));
    }

    @Override
    public void onBillingInitialized() {
        //GenerikFunctions.showToast(cont,"onBillingInitialized");
        Boolean consumed = bpHostChat.consumePurchase(ConstsCore.ITEM_PRIVATECHAT_SKU);
        readyToPurchaseHChat = true;
    }


    @Override
    public void onBackPressed()
    {
        finish();
    }
}
