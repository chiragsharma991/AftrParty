package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.AsyncAgeCalculation;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;

import java.util.ArrayList;
import java.util.List;

import static com.aperotechnologies.aftrparties.Reusables.Validations.getUrlfromCloudinary;

/**
 * Created by hasai on 16/05/16.
 */
public class AWSDBOperations {


    //Meghana
    //public static  String profilePicAvailable="No";
    //public static String validAge="No";
    //public static String validFriendsCount="No";
    //public static String validConnCount="No";

    public static Context cont;
    public static SharedPreferences sharedPreferences;
    public static Configuration_Parameter m_config;

    public static FaceOverlayView faceOverlayView;
    public static Bitmap imageBitmap = null;
    public static boolean hasFBImage = false;
    public static boolean hasLIImage = false;

    /** create user in UserTable at time of Login **/
    public static void createUser(Context context, LoggedInUserInformation loggedInUserInfo) {

        cont = context;
        m_config = Configuration_Parameter.getInstance();
        m_config.pDialog = new ProgressDialog(cont);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Activity act=(Activity)context;
        faceOverlayView = (FaceOverlayView) ((Activity) context).findViewById(R.id.face_overlay);

        try {

            String FacebookID = loggedInUserInfo.getFB_USER_ID();
            String QuickBloxID = String.valueOf(m_config.chatService.getUser().getId());
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

            String FBUserName = loggedInUserInfo.getFB_USER_NAME();
            String FBCurrentLocation = loggedInUserInfo.getFB_USER_CURRENT_LOCATION_NAME();
            String FBHomeLocation = loggedInUserInfo.getFB_USER_HOMETOWN_NAME();
            String BirthDate = loggedInUserInfo.getFB_USER_BIRTHDATE();
            int FBFriendsCount;
            if (loggedInUserInfo.getFB_USER_FRIENDS() == null) {
                FBFriendsCount = 0;
            } else {
                FBFriendsCount = Integer.parseInt(loggedInUserInfo.getFB_USER_FRIENDS());
            }
            String Gender = loggedInUserInfo.getFB_USER_GENDER();
            String FBProfilePicUrl = loggedInUserInfo.getFB_USER_PROFILE_PIC();
            List ProfilePicUrlList = new ArrayList();
            ProfilePicUrlList.add(FBProfilePicUrl);

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
            String Name = sharedPreferences.getString(m_config.Entered_User_Name, "");
            String Email = sharedPreferences.getString(m_config.Entered_Email, "");
            String PhoneNumber = sharedPreferences.getString(m_config.Entered_Contact_No, "");
            String DeviceToken = sharedPreferences.getString(m_config.REG_ID, "");

            UserTable selUserData = m_config.mapper.load(UserTable.class, FacebookID);
            Log.e("selUserClass", " " + selUserData);

            if (selUserData == null)
            {
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
                user.setProfilePicUrl(ProfilePicUrlList);
                user.setLKProfilePicUrl(LKProfilePicUrl);
                user.setLKConnectionsCount(LKConnectionsCount);
                user.setLKHeadLine(LKHeadLine);
                user.setName(Name);
                user.setEmail(Email);
                user.setPhoneNumber(PhoneNumber);
                user.setProfileStatus("N/A");
                if(DeviceToken.equals("")){
                    user.setDeviceToken("N/A");
                }
                else
                {
                    user.setDeviceToken(DeviceToken);
                }



                Log.e("AWS User insert ", " " + user.toString());
                m_config.mapper.save(user);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(m_config.AWSUserDataDone, "Yes");
                editor.apply();
                Log.e("", "User Inserted");
                //Validations Meghana
                Log.e("call to next activity","");
                //Validations for login
                //Age in bg  - 16
                //Profile pic availability
                //No of friends and connections -50 50
                //Meghana


                //Calculate Age
                new AsyncAgeCalculation(context).execute();

//                Intent intent = new Intent(cont,HomePageActivity.class);
//                cont.startActivity(intent);

                GenerikFunctions.hideDialog(m_config.pDialog);


            }
            else {

                //if (selUserData.getFacebookID().equals(FacebookID)) {

                    selUserData.setQuickBloxID(QuickBloxID);
                    selUserData.setLinkedInID(LinkedInID);
                    selUserData.setSocialEmail(SocialEmail);
                    selUserData.setFBUserName(FBUserName);
                    selUserData.setFBCurrentLocation(FBCurrentLocation);
                    selUserData.setFBHomeLocation(FBHomeLocation);
                    selUserData.setBirthDate(BirthDate);
                    selUserData.setFBFriendsCount(FBFriendsCount);
                    selUserData.setGender(Gender);
                    selUserData.setProfilePicUrl(ProfilePicUrlList);
                    selUserData.setLKProfilePicUrl(LKProfilePicUrl);
                    selUserData.setLKConnectionsCount(LKConnectionsCount);
                    selUserData.setLKHeadLine(LKHeadLine);
                    selUserData.setName(Name);
                    selUserData.setEmail(Email);
                    selUserData.setPhoneNumber(PhoneNumber);
                    selUserData.setDeviceToken(DeviceToken);

                    m_config.mapper.save(selUserData);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(m_config.AWSUserDataDone, "Yes");
                    editor.apply();
                    Log.e("---", " updated successfully");
                    // Valdations Meghana
                    Log.e("call to next activity","");

                    //Validations for login
                    //Age in bg  - 16
                    //Profile pic availability
                    //No of friends and connections -50 50
                    //Meghana

                    //Calculate Age
                    new AsyncAgeCalculation(context).execute();

//                  Intent intent = new Intent(cont,HomePageActivity.class);
//                  cont.startActivity(intent);

                    GenerikFunctions.hideDialog(m_config.pDialog);
                //}

            }


        } catch (Exception ex) {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.hideDialog(m_config.pDialog);
            GenerikFunctions.showToast(cont,"Login Failed, Please try again after some time");
        }
    }

    /** update user in UserTable in settings page **/
    public static void updateUserSettings(String picturePath, EditText edt_usermsgStatus, Context cont) {

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try {

            if (picturePath.equals("")) {

                String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
                UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);
                userTable.setProfileStatus(profileStatus);
                m_config.mapper.save(userTable);
                GenerikFunctions.showToast(cont, "Profile updated successfully");
                GenerikFunctions.hideDialog(m_config.pDialog);
            } else {
                String url = getUrlfromCloudinary(cont, picturePath);
                Log.e("url", " " + url);

                String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
                UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);

                List ProfilePicUrlList = new ArrayList();
                if (userTable.getProfilePicUrl() != null) {
                    ProfilePicUrlList = userTable.getProfilePicUrl();
                }

                ProfilePicUrlList.add(url);

                userTable.setProfileStatus(profileStatus);
                userTable.setProfilePicUrl(ProfilePicUrlList);
                m_config.mapper.save(userTable);
                GenerikFunctions.showToast(cont, "Profile updated successfully");
                GenerikFunctions.hideDialog(m_config.pDialog);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            GenerikFunctions.showToast(cont, "Profile updation failed, Please try again after some time");
            GenerikFunctions.hideDialog(m_config.pDialog);

        }
    }


    static String flagCheckParty = "No";

    /** create party in PartyTable in hostActivity **/
    public static void createParty(Context cont, PartyTable partyTable){
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        try {

            String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

            UserTable selUserTable = m_config.mapper.load(UserTable.class, FacebookID);
            List PartiesList = new ArrayList();
            PartiesList = selUserTable.getParties();
            Log.e("PartiesList"," "+PartiesList);

            if (PartiesList != null) {
                PartiesClass Parties = new PartiesClass();
                for (int j = 0; j < PartiesList.size(); j++) {
                    Parties = (PartiesClass) PartiesList.get(j);
                    if (Parties.getPartyStatus().equals("Pending") || Parties.getPartyStatus().equals("Approved") || Parties.getPartyStatus().equals("Created"))
                    {
                        long prevPartyStartTime = Long.parseLong(Parties.getStartTime());
                        long prevPartyEndTime = Long.parseLong(Parties.getEndTime());
                        long currentPartyStartTime = Long.parseLong(partyTable.getStartTime());

                        //PartyStartTime lies between result party start and endtime
                        if((currentPartyStartTime >= prevPartyStartTime) && (currentPartyStartTime <= prevPartyEndTime)){
                            flagCheckParty = "Yes";
                        }else{
                            flagCheckParty = "No";
                        }

                    }
                }
            }


            if (flagCheckParty.equals("Yes")) {
                Log.e("---", " ---already created a party or exist in a party--- ");
                GenerikFunctions.hideDialog(m_config.pDialog);

                //generikFunctions.showToast(cont,"already created a party or exist in a party");
            } else {
                //PartyTable party = new PartyTable();
                Log.e("MainActivity---", "Inserting party");
//                party.setPartyID("");
//                party.setPartyName("");
//                party.setStartTime("");
//                party.setEndTime("");
//                party.setDate("");
//                party.setHostFBID("");
//                party.setHostQBID("");
//                party.setHostName("");
//                party.setPartyType("");
//                party.setPartyDescription("");
//                party.setBYOB("");
//                party.setPartyAddress("");
//                party.setPartyLatLong("");
//                party.setPartyImage("");
//                party.setMaskStatus("");
//                party.setDialogID(null);
//                party.setGateCrashers(null);

                String url = getUrlfromCloudinary(cont, partyTable.getPartyImage());
                Log.e("url", " " + url);
                partyTable.setPartyImage(url);

                m_config.mapper.save(partyTable);
                Log.e("MainActivity---", "Party inserted");
                addPartiestoUserTable(selUserTable, partyTable, cont);
            }



        } catch (Exception ex) {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.hideDialog(m_config.pDialog);
            GenerikFunctions.showToast(cont,"Party creation failed, Please try again after some time");
        }
    }


    /** Adding Parties to userTable at time of creating party*/
    public static void addPartiestoUserTable(UserTable selUserTable, PartyTable partytable, Context cont) {
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();


        try {

            if (selUserTable.getParties() == null) {
                PartiesClass Parties = new PartiesClass();
                Parties.setPartyId(partytable.getPartyID());
                Parties.setPartyName(partytable.getPartyName());
                Parties.setPartyStatus("Created");
                Parties.setStartTime(partytable.getStartTime());
                Parties.setEndTime(partytable.getEndTime());

                List PartiesList = new ArrayList();
                PartiesList.add(Parties);
                selUserTable.setParties(PartiesList);
                m_config.mapper.save(selUserTable);

                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont,"Party Created");




            } else {
                //add new entry to existing array
                PartiesClass Parties = new PartiesClass();
                Parties.setPartyId(partytable.getPartyID());
                Parties.setPartyName(partytable.getPartyName());
                Parties.setPartyStatus("Created");
                Parties.setStartTime(partytable.getStartTime());
                Parties.setEndTime(partytable.getEndTime());

                List PartiesList = new ArrayList();
                PartiesList = selUserTable.getParties();
                Log.e("finalpartyIdstatus size", " " + PartiesList.size());
                PartiesList.add(Parties);
                selUserTable.setParties(PartiesList);
                m_config.mapper.save(selUserTable);

                GenerikFunctions.hideDialog(m_config.pDialog);
                GenerikFunctions.showToast(cont,"Party Created");


            }




        } catch (Exception ex) {
            Log.e("", "Error Update retrieving data");
            ex.printStackTrace();
        }
    }

    /** Updating UserTable at time of Status Updation **/
    public static void updatePartiesinUserTable(){

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        UserTable selectedUser = m_config.mapper.load(UserTable.class, "");//"" - FBID
        Log.e("getPartyIdStatus", " " + selectedUser.getParties());
        if (selectedUser.getParties() == null) {
            PartiesClass Parties = new PartiesClass();
            Parties.setPartyId("Party 1");
            Parties.setPartyName("Partynem");
            Parties.setPartyStatus("Created");
            Parties.setStartTime("Created");
            Parties.setEndTime("Created");


            List finalPartiesList = new ArrayList();
            finalPartiesList.add(Parties);
            selectedUser.setParties(finalPartiesList);
            m_config.mapper.save(selectedUser);

        }else{

            //Update Item at particular Position
            List finalPartiesList = new ArrayList();
            finalPartiesList = selectedUser.getParties();
            Log.e("finalpartyIdstatus size", " " + finalPartiesList.size());
            Log.e("object at pos 1---", " " + finalPartiesList.get(0));

            PartiesClass Parties = (PartiesClass) finalPartiesList.get(0);
            Parties.setPartyId(((PartiesClass) finalPartiesList.get(0)).getPartyId());
            Parties.setStartTime(((PartiesClass) finalPartiesList.get(0)).getStartTime());
            Parties.setEndTime(((PartiesClass) finalPartiesList.get(0)).getEndTime());

            finalPartiesList.remove(0);
            Log.e("PartyId at pos 1---", " " + Parties.getPartyId());
            Log.e("PartyStatus at pos 1---", " " + Parties.getPartyStatus());
            Parties.setPartyStatus("Attended");
            finalPartiesList.add(0, Parties);
            selectedUser.setParties(finalPartiesList);
            m_config.mapper.save(selectedUser);

        }
    }


    /** Adding GateCrashers to partyTable at time of creating party*/
    public static void addGateCrasherstoPartyTable() {
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        String PartyID = "";

        try {
            PartyTable selPartyTable = m_config.mapper.load(PartyTable.class, PartyID);

            if (selPartyTable.getGateCrashers() == null) {
                GateCrashersClass GateCrashers = new GateCrashersClass();
                GateCrashers.setGateCrasherID("434543");
                GateCrashers.setGCRequestStatus("434543");
                GateCrashers.setGCAttendanceStatus("434543");


                List GateCrasherList = new ArrayList();
                GateCrasherList.add(GateCrashers);
                selPartyTable.setGateCrashers(GateCrasherList);
                m_config.mapper.save(selPartyTable);

            } else {
                //add new entry to existing array
                GateCrashersClass GateCrashers = new GateCrashersClass();
                GateCrashers.setGateCrasherID("434543");
                GateCrashers.setGCRequestStatus("434543");
                GateCrashers.setGCAttendanceStatus("434543");

                List GateCrasherList = new ArrayList();
                GateCrasherList = selPartyTable.getGateCrashers();
                Log.e("finalpartyIdstatus size", " " + GateCrasherList.size());
                GateCrasherList.add(GateCrashers);
                selPartyTable.setGateCrashers(GateCrasherList);
                m_config.mapper.save(selPartyTable);

            }


        } catch (Exception ex) {
            Log.e("", "Error Update retrieving data");
            ex.printStackTrace();
        }
    }

    /** Updating PartyTable at time of Status Updation **/
    public static void updateGateCrashersinPartyTable(){

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        PartyTable selPartyTable = m_config.mapper.load(PartyTable.class, "");//"" - FBID
        Log.e("getPartyIdStatus", " " + selPartyTable.getGateCrashers());
        if (selPartyTable.getGateCrashers() == null) {
            GateCrashersClass GateCrashers = new GateCrashersClass();
            GateCrashers.setGateCrasherID("434543");
            GateCrashers.setGCRequestStatus("434543");
            GateCrashers.setGCAttendanceStatus("434543");

            List finalGateCrasherList = new ArrayList();
            finalGateCrasherList.add(GateCrashers);
            selPartyTable.setGateCrashers(finalGateCrasherList);
            m_config.mapper.save(selPartyTable);

        }else{

            //Update Item at particular Position
            List finalGateCrasherList = new ArrayList();
            finalGateCrasherList = selPartyTable.getGateCrashers();
            Log.e("finalpartyIdstatus size", " " + finalGateCrasherList.size());
            Log.e("object at pos 1---", " " + finalGateCrasherList.get(0));

            GateCrashersClass GateCrashers = new GateCrashersClass();
            GateCrashers = (GateCrashersClass) finalGateCrasherList.get(0);
            GateCrashers.setGateCrasherID("434543");
            finalGateCrasherList.remove(0);
            Log.e("PartyId at pos 1---", " " + GateCrashers.getGateCrasherID());
            Log.e("PartyStatus at pos 1---", " " + GateCrashers.getGCRequestStatus());
            GateCrashers.setGCRequestStatus("Attended");
            GateCrashers.setGCAttendanceStatus("Attended");
            finalGateCrasherList.add(0, GateCrashers);
            selPartyTable.setGateCrashers(finalGateCrasherList);
            m_config.mapper.save(selPartyTable);

        }
    }


    /** create FBUser at time of sending Request **/

    public void createReqFBUser() {

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



