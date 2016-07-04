package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.aperotechnologies.aftrparties.R;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;

/**
 * Created by hasai on 23/06/16.
 */
public class PartyRetentionActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String PartyName = getIntent().getExtras().getString("PartyName");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Arrives");
        alertDialogBuilder.setMessage("Do you want to retain chat for"+PartyName+"?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }




}