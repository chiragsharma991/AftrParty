package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

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

        if(from.equals("PartyRetention")){
            alertPartyRetentionDialog(from);
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



    @Override
    public void onBackPressed() {
        return;
    }
}
