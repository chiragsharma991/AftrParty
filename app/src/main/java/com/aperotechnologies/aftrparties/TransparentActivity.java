package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

/**
 * Created by hasai on 01/07/16.
 */
public class TransparentActivity extends Activity implements BillingProcessor.IBillingHandler {
    public static BillingProcessor bp;
    public static boolean readyToPurchase = false;
    Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = this;

        String from = getIntent().getExtras().getString("from");

        if(from.equals("PartyRetention"))
        {
            alertPartyRetentionDialog(from);
        }
        else if(from.equals("requestSend"))
        {
            alertRequestSend(from);
        }
        else if(from.equals("requestApproved"))
        {
            alertRequestApproved(from);
        }
        else if(from.equals("requestDeclined"))
        {
            alertRequestDeclined(from);
        }

        if(!BillingProcessor.isIabServiceAvailable(cont)) {
            GenerikFunctions.showToast(cont,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(TransparentActivity.this, ConstsCore.base64EncodedPublicKey, this);

    }


    //Alert Dialog for Party Retention
    private void alertPartyRetentionDialog(String from)
    {

        String PartyName = getIntent().getExtras().getString("PartyName");
        String PartyId = getIntent().getExtras().getString("PartyId");
        final String DialogId = getIntent().getExtras().getString("DialogId");
        final String message = getIntent().getExtras().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Arrives");
        alertDialogBuilder.setMessage("Do you want to retain chat for "+PartyName+"?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!readyToPurchase)
                {
                    GenerikFunctions.showToast(cont,"Billing not initialized.");
                    return;
                }
                else
                {
                    bp.purchase((Activity) cont,ConstsCore.ITEM_PARTYRETENTION_SKU);

                }
                finish();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                GenerikFunctions.sDialog(cont, "Deleting....");

                if (ChatService.getInstance().getCurrentUser() == null)
                {
                    String accessToken = LoginValidations.getFBAccessToken().getToken();

                    QBSessionClass.getInstance().getQBSession(new QBEntityCallback() {

                        @Override
                        public void onSuccess(Object o, Bundle bundle) {

                            Handler h = new Handler();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    deleteGroupDialog(DialogId);
                                }
                            });


                        }

                        @Override
                        public void onError(QBResponseException e) {
                            GenerikFunctions.hDialog();
                            finish();

                        }

                    }, accessToken, null, cont);


                }
                else
                {
                    deleteGroupDialog(DialogId);

                }


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }



    //Alert Dialog for Party Request Send
    private void alertRequestSend(final String from) {

        final String PartyId = getIntent().getExtras().getString("PartyId");
        final String PartyName = getIntent().getExtras().getString("PartyName");
        final String PartyStartTime = getIntent().getExtras().getString("PartyStartTime");
        final String PartyEndTime = getIntent().getExtras().getString("PartyEndTime");
        final String PartyStatus = getIntent().getExtras().getString("PartyStatus");
        final String message = getIntent().getExtras().getString("message");
        final String GCQBID = getIntent().getExtras().getString("GCQBID");
        final String GCFBID = getIntent().getExtras().getString("GCFBID");
        Log.e("TransparentActivity","PartyId "+PartyId+ " PartyName "+PartyName+" PartyStartTime "+PartyStartTime+" PartyEndTime "+PartyEndTime+" PartyStatus "+PartyStatus);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Accept request for Party");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(TransparentActivity.this, RequestantActivity.class);
                PartyParceableData party1 = new PartyParceableData();
                party1.setPartyId(PartyId);
                party1.setPartyName(PartyName);
                party1.setStartTime(PartyStartTime);
                party1.setEndTime(PartyEndTime);
                party1.setPartyStatus(PartyStatus);
                i.putExtra("GCQBID",GCQBID);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", from);
                Bundle mBundles = new Bundle();
                mBundles.putSerializable(ConstsCore.SER_KEY, party1);
                i.putExtras(mBundles);
                startActivity(i);
                finish();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }


    //Alert Dialog for Party Request Approved
    private void alertRequestApproved(final String from) {

        final String message = getIntent().getExtras().getString("message");
        final String GCFBID = getIntent().getExtras().getString("GCFBID");
        Log.e("TransparentActivity","GCFBID "+GCFBID+ "");


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Request Accepted for Party");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(TransparentActivity.this, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", from);
                startActivity(i);
                finish();

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    //Alert Dialog for Party Request Declined
    private void alertRequestDeclined(final String from) {

        final String message = getIntent().getExtras().getString("message");
        final String GCFBID = getIntent().getExtras().getString("GCFBID");
        Log.e("TransparentActivity","GCFBID "+GCFBID+ "");


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Request Declined for Party");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(TransparentActivity.this, HistoryActivity.class);
                i.putExtra("GCFBID", GCFBID);
                i.putExtra("from", from);
                startActivity(i);
                finish();

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        GenerikFunctions.showToast(cont,"Purchase Successful.");
        Boolean consumed = bp.consumePurchase(ConstsCore.ITEM_PARTYRETENTION_SKU);
        GenerikFunctions.sDialog(cont, "Saving Data");

        if (consumed)
        {
            GenerikFunctions.showToast(cont,"Successfully Retained");
        }
        else
        {
            GenerikFunctions.hDialog();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //GenerikFunctions.showToast(cont,"onPurchaseHistoryRestored");
        for(String sku : bp.listOwnedProducts())
            Log.d("", "Owned Managed Product: " + sku);
        for(String sku : bp.listOwnedSubscriptions())
            Log.d("", "Owned Subscription: " + sku);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        //GenerikFunctions.showToast(cont,"onBillingError: " + Integer.toString(errorCode));

    }

    @Override
    public void onBillingInitialized() {
        Boolean consumed = bp.consumePurchase(ConstsCore.ITEM_PARTYRETENTION_SKU);
        readyToPurchase = true;
    }


    private void deleteGroupDialog(String DialogId)
    {

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.deleteDialog(DialogId, true, new QBEntityCallback<Void>()
        {

            @Override
            public void onSuccess(Void aVoid, Bundle bundle)
            {
                Log.e("Onsuccess","delete Dialog ");
                GenerikFunctions.hDialog();
                finish();

            }

            @Override
            public void onError(QBResponseException errors)
            {
                Log.e("onError","delete Dialog ");
                GenerikFunctions.hDialog();
                finish();
            }
        });
    }

}
