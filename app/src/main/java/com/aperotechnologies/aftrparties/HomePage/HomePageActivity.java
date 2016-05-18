package com.aperotechnologies.aftrparties.HomePage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.github.siyamed.shapeimageview.CircularImageView;

/**
 * Created by hasai on 06/05/16.
 */
public class HomePageActivity extends Activity
{

    CircularImageView imguser;
    TextView txtuserName, txtuserEmail, txtuserDOB, txtuserGender;
    Context cont;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();

        imguser = (CircularImageView) findViewById(R.id.userimage);
        txtuserName = (TextView) findViewById(R.id.userName);
        txtuserEmail = (TextView) findViewById(R.id.userEmail);
        txtuserDOB = (TextView) findViewById(R.id.userDOB);
        txtuserGender = (TextView) findViewById(R.id.userGender);

        txtuserName.setText(sharedPreferences.getString(m_config.Entered_User_Name,""));
        txtuserEmail.setText(sharedPreferences.getString(m_config.Entered_Email,""));
        txtuserDOB.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE());
        txtuserGender.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_GENDER());


    }
}
