package com.aperotechnologies.aftrparties.Reusables;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.Login.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.facebook.AccessToken;
import com.quickblox.auth.QBAuth;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.regex.Pattern;

/**
 * Created by mpatil on 10/05/16.
 */
public  class LoginValidations
{
//Meghana

    public static Configuration_Parameter m_config = Configuration_Parameter.getInstance();
    //Meghana

    public static boolean isValidEmailId(String email)
    {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

//Meghana
    public static boolean isEmpty(EditText etText)
    {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isValidMobile(EditText edt )
    {
        String text=edt.getText().toString().trim();
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", text))
        {
            if(text.length() < 10 || text.length() > 13)
            {
                check = false;
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }

//Meghana
    public static boolean isFBLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    //Meghana
    public void chatLogout(final QBUser user, final Context cont)
    {
        boolean isLoggedIn = m_config.chatService.isLoggedIn();

        if (isLoggedIn)
        {
            m_config.chatService.logout(new QBEntityCallback()
            {
                @Override
                public void onSuccess(Object o, Bundle bundle)
                {
                    m_config.chatService.destroy();
                    chatLogin(user, cont);
                }

                @Override
                public void onError(QBResponseException errors)
                {
                }
            });
        } else {
            chatLogin(user, cont);
        }
    }

    ////Meghana
    //function for chat login
    public void chatLogin(QBUser qb_user, final Context cont)
    {
        m_config.chatService.login(qb_user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                PlayServicesHelper PlayServicesHelper;
                Log.e("ChatServicelogin", "Success ");
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(cont);
                QBGroupChatManager groupChatManager = m_config.chatService.getGroupChatManager();
                QBPrivateChatManager privateChatManager = m_config.chatService.getPrivateChatManager();
                m_config.groupChatManager = groupChatManager;
                m_config.privateChatManager = privateChatManager;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ChatLogin", "Yes");
                editor.apply();

                PlayServicesHelper playServicesHelper = new PlayServicesHelper((Activity) cont, initialiseLoggedInUser(cont));
            }

            @Override
            public void onError(QBResponseException error)
            {
                // errror
                Log.e("ChatServicelogin", "OnError " + error.toString());
                error.printStackTrace();
            }
        });
    }
//Meghana
    public static LoggedInUserInformation initialiseLoggedInUser(Context cont)
    {
        DBHelper helper = DBHelper.getInstance(cont);

        SQLiteDatabase sqldb = helper.getWritableDatabase();
        String Query = "Select * from UserTable";
        Cursor cursor = sqldb.rawQuery(Query, null);
        cursor.moveToFirst();
        LoggedInUserInformation loggedInUserInfo = new LoggedInUserInformation();

        loggedInUserInfo.setFB_USER_ID(cursor.getString(cursor.getColumnIndex("fb_user_id")));
        loggedInUserInfo.setFB_USER_NAME(cursor.getString(cursor.getColumnIndex("fb_user_name")));
        loggedInUserInfo.setFB_USER_GENDER(cursor.getString(cursor.getColumnIndex("fb_user_gender")));
        loggedInUserInfo.setFB_USER_BIRTHDATE(cursor.getString(cursor.getColumnIndex("fb_user_birthdate")));
        loggedInUserInfo.setFB_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_EMAIL)));
        loggedInUserInfo.setFB_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_PROFILE_PIC)));
        loggedInUserInfo.setFB_USER_HOMETOWN_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_ID)));
        loggedInUserInfo.setFB_USER_HOMETOWN_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_HOMETOWN_NAME)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_ID)));
        loggedInUserInfo.setFB_USER_CURRENT_LOCATION_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME)));

        loggedInUserInfo.setLI_USER_ID(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_ID)));
        loggedInUserInfo.setLI_USER_FIRST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_FIRST_NAME)));
        loggedInUserInfo.setLI_USER_LAST_NAME(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_LAST_NAME)));
        loggedInUserInfo.setLI_USER_EMAIL(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_EMAIL)));
        loggedInUserInfo.setLI_USER_PROFILE_PIC(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_PROFILE_PIC)));
        loggedInUserInfo.setLI_USER_CONNECTIONS(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_CONNECTIONS)));
        loggedInUserInfo.setLI_USER_HEADLINE(cursor.getString(cursor.getColumnIndex(LoginTableColumns.LI_USER_HEADLINE)));


        /*
        public static final String FB_USER_FRIENDS                = "fb_user_friends";

       */

        return loggedInUserInfo;
    }




}
