package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Settings.SettingsActivity;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import static com.aperotechnologies.aftrparties.Reusables.Validations.getUrlfromCloudinary;

/**
 * Created by hasai on 15/06/16.
 */
public class AWSLoginOperations {


    public static Context cont;
    public static SharedPreferences sharedPreferences;
    public static Configuration_Parameter m_config;


    //function for adding fb data while login in UserTable
    public static class addFBUserInfo extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        LoggedInUserInformation loggedInUserInfo;
        boolean value = false;
        String from;
        SQLiteDatabase sqldb;

        public addFBUserInfo(Context cont, LoggedInUserInformation loggedInUserInfo, String from,SQLiteDatabase sqldb)
        {
            this.cont = cont;
            this.loggedInUserInfo = loggedInUserInfo;
            this.from = from;
            m_config = Configuration_Parameter.getInstance();
            this.sqldb = sqldb;
            m_config.pDialog = new ProgressDialog(cont);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try
            {
                String FacebookID = loggedInUserInfo.getFB_USER_ID();
                String SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();

                if (SocialEmail.equals(null) || SocialEmail.equals("") || SocialEmail.equals("N/A"))
                {
                    SocialEmail = "N/A";
                }
                else
                {
                    SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();
                }

                String FBUserName = loggedInUserInfo.getFB_USER_NAME();
                String FBCurrentLocation = loggedInUserInfo.getFB_USER_CURRENT_LOCATION_NAME();
                String FBHomeLocation = loggedInUserInfo.getFB_USER_HOMETOWN_NAME();
                String BirthDate = loggedInUserInfo.getFB_USER_BIRTHDATE();
                int FBFriendsCount;
                if (loggedInUserInfo.getFB_USER_FRIENDS() == null)
                {
                    FBFriendsCount = 0;
                }
                else
                {
                    FBFriendsCount = Integer.parseInt(loggedInUserInfo.getFB_USER_FRIENDS());
                }
                String Gender = loggedInUserInfo.getFB_USER_GENDER();
                String FBProfilePicUrl = loggedInUserInfo.getFB_USER_PROFILE_PIC();
                List ProfilePicUrlList = new ArrayList();
                ProfilePicUrlList.add(FBProfilePicUrl);

                String Name = sharedPreferences.getString(m_config.Entered_User_Name, "");
                String Email = sharedPreferences.getString(m_config.Entered_Email, "");
                String PhoneNumber = sharedPreferences.getString(m_config.Entered_Contact_No, "");

                UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
                Log.e("selUserClass", " " + selUserData);

                if(selUserData == null)
                {
                    UserTable user = new UserTable();
                    Log.e("", "Inserting FBUser");
                    user.setFacebookID(FacebookID);
                    user.setQuickBloxID("N/A");
                    user.setLinkedInID("N/A");
                    user.setSocialEmail(SocialEmail);
                    user.setFBUserName(FBUserName);
                    user.setFBCurrentLocation(FBCurrentLocation);
                    user.setFBHomeLocation(FBHomeLocation);
                    user.setBirthDate(BirthDate);
                    user.setFBFriendsCount(FBFriendsCount);
                    user.setGender(Gender);
                    user.setProfilePicUrl(ProfilePicUrlList);
                    user.setLKProfilePicUrl("N/A");
                    user.setLKConnectionsCount(0);
                    user.setLKHeadLine("N/A");
                    user.setName(Name);
                    user.setEmail(Email);
                    user.setPhoneNumber(PhoneNumber);
                    user.setProfileStatus("N/A");
                    user.setDeviceToken("N/A");
                    user.setRegistrationStatus("No");
                    user.setcurrentmaskstatus("No");
                    user.setImageflag("No");

                    Log.e("AWS FBUser inserting ", " " + user.toString());
                    m_config.mapper.save(user);
                    Log.e("", "AWS FBUser Inserted ---"+user.getRegistrationStatus());
                    value = true;
                }
                else
                {
                    selUserData.setFBUserName(FBUserName);
                    selUserData.setFBCurrentLocation(FBCurrentLocation);
                    selUserData.setFBHomeLocation(FBHomeLocation);
                    selUserData.setBirthDate(BirthDate);
                    selUserData.setFBFriendsCount(FBFriendsCount);
                    selUserData.setGender(Gender);
                    selUserData.setName(Name);
                    selUserData.setEmail(Email);
                    selUserData.setPhoneNumber(PhoneNumber);
                    ProfilePicUrlList.remove(0);
                    if(selUserData.getImageflag().equals("Yes"))
                    {

                    }
                    else
                    {
                        ProfilePicUrlList.add(0, FBProfilePicUrl);

                        String Update = "Update " + LoginTableColumns.USERTABLE + " set "
                                + LoginTableColumns.FB_USER_PROFILE_PIC  + " = '" + FBProfilePicUrl + "'"
                                + " where " + LoginTableColumns.FB_USER_ID + " = '" + FacebookID + "'";

                        Log.i("update User  "+ LoginTableColumns.FB_USER_ID , Update);
                        sqldb.execSQL(Update);
                    }
                    selUserData.setProfilePicUrl(ProfilePicUrlList);

                    Log.e("AWS FBUser updated ", " " + selUserData.toString());
                    m_config.mapper.save(selUserData);
                    Log.e("", "AWS FBUser updated");
                    value = true;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                value = false;

            }

            finally {

                return value;

            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- "," "+"FBAWS");

            if(v == true)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(m_config.LoggedInFBUserID, loggedInUserInfo.getFB_USER_ID());
                editor.putString(m_config.FBLoginDone,"Yes");
                editor.apply();

                if(from.equals("Registration"))
                {
                    RegistrationActivity.startLinkedInProcess();
                }
                else
                {
                    Welcome.startLinkedInProcess();
                }
            }
            else
            {

                Log.e("", "Error retrieving data" +m_config.pDialog.getContext());
                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
            }

        }
    }

    //function for adding li data while login in UserTable
    public static class addLIUserInfo extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        LoggedInUserInformation loggedInUserInfo;
        boolean value = false;


        public addLIUserInfo(Context cont, LoggedInUserInformation loggedInUserInfo)
        {
            this.cont = cont;
            this.loggedInUserInfo = loggedInUserInfo;
            m_config = Configuration_Parameter.getInstance();

            m_config.pDialog = new ProgressDialog(cont);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            String FacebookID = loggedInUserInfo.getFB_USER_ID();
            String LinkedInID = loggedInUserInfo.getLI_USER_ID();
            String SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();
            if (SocialEmail.equals(null) || SocialEmail.equals("") || SocialEmail.equals("N/A")) {
                if (loggedInUserInfo.getLI_USER_EMAIL() == null || loggedInUserInfo.getLI_USER_EMAIL().equals("") || loggedInUserInfo.getLI_USER_EMAIL().equals("N/A")) {
                    SocialEmail = "N/A";
                } else {
                    SocialEmail = loggedInUserInfo.getLI_USER_EMAIL();
                }
            } else {
                SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();
            }

            String LKProfilePicUrl = loggedInUserInfo.getLI_USER_PROFILE_PIC();
            int LKConnectionsCount;
            if (loggedInUserInfo.getLI_USER_CONNECTIONS() == null)
            {
                LKConnectionsCount = 0;
            }
            else
            {
                LKConnectionsCount = Integer.parseInt(loggedInUserInfo.getLI_USER_CONNECTIONS());
            }

            String LKHeadLine = loggedInUserInfo.getLI_USER_HEADLINE();

            try
            {
                UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
                Log.e("selUserClass", " " + selUserData);
                Log.e("", "Inserting LIUser");
                selUserData.setLinkedInID(LinkedInID);
                selUserData.setSocialEmail(SocialEmail);
                selUserData.setLKProfilePicUrl(LKProfilePicUrl);
                selUserData.setLKConnectionsCount(LKConnectionsCount);
                selUserData.setLKHeadLine(LKHeadLine);

                Log.e("AWS LIUser inserted ", " " + selUserData.toString());
                m_config.mapper.save(selUserData);
                Log.e("", "AWS LIUser Inserted");
                value = true;

            }
            catch (Exception ex)
            {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
//                GenerikFunctions.hideDialog(m_config.pDialog);
//                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                value = false;
            }

            finally {

                return value;

            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- "," "+v);

            if(v == true)
            {
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(m_config.LILoginDone,"Yes");
                editor.apply();
                //Harshada
                LoginValidations.QBStartSession(cont);

            }
            else
            {
                Log.e("", "Error retrieving data");
                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
            }
        }
    }

    //function for adding devicetoken while login in UserTable
    public static class addUserDeviceToken extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        LoggedInUserInformation loggedInUserInfo;
        boolean value = false;


        public addUserDeviceToken(Context cont, LoggedInUserInformation loggedInUserInfo)
        {
            this.cont = cont;
            this.loggedInUserInfo = loggedInUserInfo;
            m_config = Configuration_Parameter.getInstance();

            m_config.pDialog = new ProgressDialog(cont);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            String FacebookID = loggedInUserInfo.getFB_USER_ID();
            String DeviceToken = sharedPreferences.getString(m_config.REG_ID, "");

            try{
                UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
                Log.e("selUserClass", " " + selUserData);
                Log.e("", "Inserting DeviceToken");
                selUserData.setDeviceToken(DeviceToken);

                m_config.mapper.save(selUserData);
                Log.e("", "AWS DeviceToken Inserted");

                value = true;

            }
            catch (Exception ex) {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
//                GenerikFunctions.hideDialog(m_config.pDialog);
//                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                value = false;

            }

            finally {

                return value;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- addUserDeviceToken "," "+v);

            if(v == true)
            {
                LoginValidations.checkPendingLoginFlags(cont);
            }
            else
            {
                Log.e("", "Error retrieving data");
                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
            }
        }
    }


    //function for adding devicetoken while login in UserTable
    public static class addUserQuickBloxId extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        QBUser user;
        String avatarUrl;
        boolean value = false;


        public addUserQuickBloxId(Context cont, QBUser user, String avatarUrl)
        {
            this.cont = cont;
            this.user = user;
            this.avatarUrl = avatarUrl;
            m_config = Configuration_Parameter.getInstance();

            m_config.pDialog = new ProgressDialog(cont);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }



        @Override
        protected Boolean doInBackground(String... params)
        {

            String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
            String QuickBloxID = sharedPreferences.getString(m_config.QuickBloxID,"");

            try{
                UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
                Log.e("selUserClass", " " + selUserData);
                Log.e("", "Inserting QuickBloxId");
                selUserData.setQuickBloxID(QuickBloxID);

                m_config.mapper.save(selUserData);
                Log.e("", "AWS QuickBloxId Inserted");

                value = true;

            }
            catch (Exception ex) {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
//                GenerikFunctions.hideDialog(m_config.pDialog);
//                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                value = false;

            }

            finally {

                return value;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- addQuickBloxId "," "+v);

            if(v == true)
            {
                new LoginValidations.uploadprofilePic(user, avatarUrl, cont).execute();
            }
            else
            {
                Log.e("", "Error retrieving data");
                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
            }
        }
    }

    //function for adding Registration verifystatus while login in UserTable
    public static class addUserRegStatus extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        LoggedInUserInformation loggedInUserInfo;
        boolean value = false;

        public addUserRegStatus(Context cont,LoggedInUserInformation loggedInUserInfo)
        {
            this.cont = cont;
            this.loggedInUserInfo = loggedInUserInfo;
            m_config = Configuration_Parameter.getInstance();
            m_config.pDialog = new ProgressDialog(cont);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            String FacebookID = loggedInUserInfo.getFB_USER_ID();

            try
            {
                UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
                Log.e("", "Inserting RegistrationStatus");
                if (selUserData.getRegistrationStatus().equals("No"))
                {
                    selUserData.setRegistrationStatus("Yes");
                    m_config.mapper.save(selUserData);
                    Log.e("", "AWS RegistrationStatus Inserted");
                }

                value = true;


            }
            catch (Exception ex)
            {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
//                GenerikFunctions.hideDialog(m_config.pDialog);
//                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
                value = false;


            }

            finally {

                return value;

            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            Log.e("--onPostEx- 22 "," "+v);

            if(v == true)
            {
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(m_config.FinalStepDone,"Yes");
                editor.apply();

                Intent intent = new Intent(cont, HomePageActivity.class);
                cont.startActivity(intent);
            }
            else
            {
                Log.e("", "Error retrieving data");
                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont, "Login Failed, Please try again after some time");
            }
        }
    }



    /** create FBUser at time of sending Request **/

    public void createReqFBUser()
    {
        String FacebookID = "";
        String FBEmail = "";
        String FBUserName = "";

        UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
        Log.e("selUserClass", " " + selUserData);

        if (selUserData == null) {
            UserTable user = new UserTable();
            Log.e("", "Inserting User");
            user.setFacebookID(FacebookID);
            user.setQuickBloxID("N/A");
            user.setLinkedInID("N/A");
            if(FBEmail.equals("")){
                user.setSocialEmail("N/A");
            }else{
                user.setSocialEmail(FBEmail);
            }
            user.setFBUserName(FBUserName);
            user.setFBCurrentLocation("N/A");
            user.setFBHomeLocation("N/A");
            user.setBirthDate("N/A");
            user.setFBFriendsCount(0);
            user.setGender("N/A");
            user.setProfilePicUrl(null);
            user.setLKProfilePicUrl("N/A");
            user.setLKConnectionsCount(0);
            user.setLKHeadLine("N/A");
            user.setName("N/A");
            user.setEmail("N/A");
            user.setPhoneNumber("N/A");
            user.setProfileStatus("N/A");
            user.setDeviceToken("N/A");

            Log.e("AWS User insert ", " " + user.toString());
            m_config.mapper.save(user);

        }
        else
        {

        }

    }







}
