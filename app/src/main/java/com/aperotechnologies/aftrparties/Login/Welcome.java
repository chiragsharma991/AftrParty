package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.quickblox.chat.QBChatService;

/**
 * Created by mpatil on 25/05/16.
 */
public class Welcome extends Activity
{
    Button btn_login,btn_register;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config= Configuration_Parameter.getInstance();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initializeProcess();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

    public void initializeProcess()
    {
        if(LoginValidations.isFBLoggedIn())
        {
            //Initialise here
            // initialize Chat service
            try
            {

                if (sharedPreferences.getString(m_config.QBFBLoginDone,"No").equals("Yes"))
                {
                    QBChatService.init(getApplicationContext());
                    final QBChatService chatService = QBChatService.getInstance();
                    m_config.chatService = chatService;
                    Log.e("m_config.chatService",chatService.toString() +"   aaa");
                    Log.e("m_config.chatService token",m_config.chatService.getToken() +"   aaa");
                    Log.e("m_config.chatService token",m_config.chatService +"   aaa");
                    Log.e("Chat Service successfully initialised","At login");

                }
                else
                {

                }

                //chatLogout(user);//
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
