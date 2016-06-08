package com.aperotechnologies.aftrparties.HomePage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherActivity;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherSearchActivity;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.SettingsActivity;
import com.aperotechnologies.aftrparties.TipsActivity;
import com.aperotechnologies.aftrparties.utils.ResizableButton;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by hasai on 06/05/16.
 */
public class HomePageActivity extends Activity
{

    ImageButton btn_logout;
    CircularImageView imguser;
    TextView txtuserName, txtuserEmail, txtuserDOB, txtuserGender;
    Context cont;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    ResizableButton btnTips, btnSettings, btnChat, btnHistory,  btnHost, btnGateCrasher;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();

        btn_logout = (ImageButton) findViewById(R.id.btn_logout);

        imguser = (CircularImageView) findViewById(R.id.userimage);
        txtuserName = (TextView) findViewById(R.id.userName);
        txtuserEmail = (TextView) findViewById(R.id.userEmail);
        txtuserDOB = (TextView) findViewById(R.id.userDOB);
        txtuserGender = (TextView) findViewById(R.id.userGender);
        btnTips = (ResizableButton) findViewById(R.id.btnTips);
        btnSettings = (ResizableButton) findViewById(R.id.btnSettings);
        btnChat = (ResizableButton) findViewById(R.id.btnChat);
        btnHistory = (ResizableButton) findViewById(R.id.btnHistory);
        btnHost = (ResizableButton) findViewById(R.id.btnHost);
        btnGateCrasher = (ResizableButton) findViewById(R.id.btnGateCrasher);

        txtuserName.setText(sharedPreferences.getString(m_config.Entered_User_Name,""));
        txtuserEmail.setText(sharedPreferences.getString(m_config.Entered_Email,""));
        txtuserDOB.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE());
        txtuserGender.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_GENDER());

        String url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();

        if(url.equals(null) || url.equals("") || url.equals("N/A")){
            //if(LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC() == null || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("") || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("N/A")) {
                url = "";
//            }else{
//                url = LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC();
//            }
        }else{
            url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
        }

        if(!url.equals("") || !url.equals(null) || !url.equals("N/A")){
            Picasso.with(cont).load(url).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_user)
                    .error(R.drawable.placeholder_user)
                    .into(imguser);
        }


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoginValidations.QBSessionLogOut();
//                LoginValidations.chatLogout();
            }
        });

        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, TipsActivity.class);
                startActivity(i);

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("----"," came here");
                Intent i = new Intent(HomePageActivity.this, SettingsActivity.class);
                startActivity(i);

            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, DialogsActivity.class);
                startActivity(i);

            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, HistoryActivity.class);
                startActivity(i);

            }
        });

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, HostActivity.class);
                startActivity(i);

            }
        });

        btnGateCrasher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, GateCrasherSearchActivity.class);
                startActivity(i);

            }
        });


    }

    @Override
    public void onBackPressed() {
        //Harshada
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);
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
