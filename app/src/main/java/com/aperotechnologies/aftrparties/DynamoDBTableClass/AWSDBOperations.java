package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Login.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;

/**
 * Created by hasai on 16/05/16.
 */
public class AWSDBOperations {



    public static void createUser(Context context, LoggedInUserInformation loggedInUserInfo) {

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        try {

             String FacebookID = loggedInUserInfo.getFB_USER_ID();
             String QuickBloxID = String.valueOf(m_config.chatService.getUser().getId());
             String LinkedInID = loggedInUserInfo.getLI_USER_ID();
             String SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();
             if(SocialEmail.equals(null) || SocialEmail.equals("") || SocialEmail.equals("N/A")){
                 if(loggedInUserInfo.getLI_USER_EMAIL() == null || loggedInUserInfo.getLI_USER_EMAIL().equals("") || loggedInUserInfo.getLI_USER_EMAIL().equals("N/A")) {
                     SocialEmail = "N/A";
                 }else{
                     SocialEmail = loggedInUserInfo.getLI_USER_EMAIL();
                 }
             }else{
                 SocialEmail = loggedInUserInfo.getFB_USER_EMAIL();
             }

             String FBUserName = loggedInUserInfo.getFB_USER_NAME();
             String FBCurrentLocation = loggedInUserInfo.getFB_USER_CURRENT_LOCATION_NAME();
             String FBHomeLocation = loggedInUserInfo.getFB_USER_HOMETOWN_NAME();
             String BirthDate = loggedInUserInfo.getFB_USER_BIRTHDATE();
             int FBFriendsCount;
             if(loggedInUserInfo.getFB_USER_FRIENDS() == null){
                FBFriendsCount = 0;
             } else{
                FBFriendsCount = Integer.parseInt(loggedInUserInfo.getFB_USER_FRIENDS());
             }
             String Gender = loggedInUserInfo.getFB_USER_GENDER();
             String FBProfilePicUrl = loggedInUserInfo.getFB_USER_PROFILE_PIC();
             String LKProfilePicUrl = loggedInUserInfo.getLI_USER_PROFILE_PIC();
             int LKConnectionsCount;
             if(loggedInUserInfo.getLI_USER_CONNECTIONS() == null){
                LKConnectionsCount = 0;
             }else{
                LKConnectionsCount = Integer.parseInt(loggedInUserInfo.getLI_USER_CONNECTIONS());
            }

             String LKHeadLine = loggedInUserInfo.getLI_USER_HEADLINE();
             String Name = sharedPreferences.getString(m_config.Entered_User_Name,"");
             String Email = sharedPreferences.getString(m_config.Entered_Email,"");
             String PhoneNumber = sharedPreferences.getString(m_config.Entered_Contact_No,"");
             String DeviceToken = sharedPreferences.getString(m_config.REG_ID,"");

            UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
            Log.e("selUserClass", " " + selUserData);
            
            if (selUserData == null) {
                UserTable user = new UserTable();
                Log.e("", "Inserting User");
                user.setFacebookID(FacebookID);
                user.setQuickBloxID(QuickBloxID);
                user.setLinkedInID(LinkedInID);
                user.setSocialEmail(SocialEmail);
                user.setFBUserName(FBUserName);
                user.setFBCurrentLocation(FBCurrentLocation);
                user.setFBHomeLocation(FBHomeLocation);
                user.setBirthDate(BirthDate);
                user.setFBFriendsCount(FBFriendsCount);
                user.setGender(Gender);
                user.setFBProfilePicUrl(FBProfilePicUrl);
                user.setLKProfilePicUrl(LKProfilePicUrl);
                user.setLKConnectionsCount(LKConnectionsCount);
                user.setLKHeadLine(LKHeadLine);
                user.setName(Name);
                user.setEmail(Email);
                user.setPhoneNumber(PhoneNumber);
                user.setDeviceToken(DeviceToken);
                Log.e("AWS User insert "," " +user.toString());
                m_config.mapper.save(user);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(m_config.AWSUserDataDone,"Yes");
                editor.apply();
                Log.e("", "User Inserted");

                UserTable savedUserClass = m_config.mapper.load(UserTable.class, FacebookID);
                if (savedUserClass.getFacebookID().equals(FacebookID)) {
                    Log.e("---", " inserted successfully");

                } else {
                    Log.e("---", " not inserted");
                }

                GenerikFunctions generikFunctions = new GenerikFunctions();
                generikFunctions.hideDialog(m_config.pDialog);

            } else {

                if (selUserData.getFacebookID().equals(FacebookID)) {
                    if(selUserData.getDeviceToken().equals(null) || selUserData.getDeviceToken().equals("")){
                        selUserData.setDeviceToken(DeviceToken);
                    }else {

                        selUserData.setQuickBloxID(QuickBloxID);
                        selUserData.setLinkedInID(LinkedInID);
                        selUserData.setSocialEmail(SocialEmail);
                        selUserData.setFBUserName(FBUserName);
                        selUserData.setFBCurrentLocation(FBCurrentLocation);
                        selUserData.setFBHomeLocation(FBHomeLocation);
                        selUserData.setBirthDate(BirthDate);
                        selUserData.setFBFriendsCount(FBFriendsCount);
                        selUserData.setGender(Gender);
                        selUserData.setFBProfilePicUrl(FBProfilePicUrl);
                        selUserData.setLKProfilePicUrl(LKProfilePicUrl);
                        selUserData.setLKConnectionsCount(LKConnectionsCount);
                        selUserData.setLKHeadLine(LKHeadLine);
                        selUserData.setName(Name);
                        selUserData.setEmail(Email);
                        selUserData.setPhoneNumber(PhoneNumber);
                        selUserData.setDeviceToken(DeviceToken);
                    }

                    m_config.mapper.save(selUserData);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString(m_config.AWSUserDataDone,"Yes");
                    editor.apply();;
                    UserTable savedUserClass = m_config.mapper.load(UserTable.class, FacebookID);
                    if (savedUserClass.getFacebookID().equals(FacebookID)) {
                        Log.e("---", " updated successfully");
                    } else {
                        Log.e("---", " not updated");
                    }

                    GenerikFunctions generikFunctions = new GenerikFunctions();
                    generikFunctions.hideDialog(m_config.pDialog);
                }

            }


        } catch (Exception ex) {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.hideDialog(m_config.pDialog);
        }
    }



}
