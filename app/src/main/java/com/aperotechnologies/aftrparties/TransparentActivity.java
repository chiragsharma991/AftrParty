package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.quickblox.chat.QBChatService;

/**
 * Created by hasai on 01/07/16.
 */
public class TransparentActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    }


    //Alert Dialog for Party Retention
    private void alertPartyRetentionDialog(String from) {

        String PartyName = getIntent().getExtras().getString("PartyName");
        String PartyId = getIntent().getExtras().getString("PartyId");
        final String DialogId = getIntent().getExtras().getString("DialogId");
        Log.e("RatingsAlarmReceiver "," PartyName "+" "+PartyId+" "+DialogId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Arrives");
        alertDialogBuilder.setMessage("Do you want to retain chat for "+PartyName+"?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(QBChatService.getInstance() == null){
                    //Go for login
                    //ask for inapp purchase


                }else{

                    QBChatDialogCreation.deleteGroupDialog(DialogId);

                }
                finish();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(QBChatService.getInstance() == null){
                    //Go for login



                }else{

                    QBChatDialogCreation.deleteGroupDialog(DialogId);

                }

                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }



    //Alert Dialog for Party Request Send
    private void alertRequestSend(String from) {

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
                i.putExtra("from", "requestSend");
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
    private void alertRequestApproved(String from) {

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
                i.putExtra("from", "requestApproved");
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
    public void onBackPressed() {
        return;
    }
}
