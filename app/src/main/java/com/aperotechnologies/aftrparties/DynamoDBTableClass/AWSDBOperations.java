package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.Login.AsyncAgeCalculation;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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

    public static void createUser(Context context, LoggedInUserInformation loggedInUserInfo) {

        cont = context;
        m_config = Configuration_Parameter.getInstance();
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
                if(DeviceToken.equals("")){
                    user.setDeviceToken("N/A");
                }
                else
                {
                    user.setDeviceToken(DeviceToken);
                }
                //user.setProfileStatus("N/A");
                //user.setParties();


                Log.e("AWS User insert ", " " + user.toString());
                m_config.mapper.save(user);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(m_config.AWSUserDataDone, "Yes");
                editor.apply();
                Log.e("", "User Inserted");

//                UserTable savedUserClass = m_config.mapper.load(UserTable.class, FacebookID);
//                if (savedUserClass.getFacebookID().equals(FacebookID))
//                {
//                    Log.e("---", " inserted successfully");
//
//                }
//                else {
//                    Log.e("---", " not inserted");
//                }


                //Validations Meghana
                Log.e("call to next activity","");
                //Validations for login
                //Age in bg  - 16
                //Profile pic availability
                //No of friends and connections -50 50
                //Meghana


                //Calculate Age
                new AsyncAgeCalculation(context).execute();

                GenerikFunctions generikFunctions = new GenerikFunctions();
                generikFunctions.hideDialog(m_config.pDialog);

            }
            else {

                if (selUserData.getFacebookID().equals(FacebookID)) {

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

//                    UserTable savedUserClass = m_config.mapper.load(UserTable.class, FacebookID);
//                    if (savedUserClass.getFacebookID().equals(FacebookID)) {
//                        Log.e("---", " updated successfully");
//                    } else {
//                        Log.e("---", " not updated");
//                    }


                    // Valdations Meghana
                    Log.e("call to next activity","");

                    //Validations for login
                    //Age in bg  - 16
                    //Profile pic availability
                    //No of friends and connections -50 50
                    //Meghana

                    //Calculate Age
                    new AsyncAgeCalculation(context).execute();

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

    public static void updateUserSettings(String picturePath, EditText edt_usermsgStatus, Context cont) {

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();


        if (picturePath.equals("")) {

            String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
            UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);
            userTable.setProfileStatus(profileStatus);
            m_config.mapper.save(userTable);
            GenerikFunctions.showToast(cont, "Profile updated successfully");

        } else {

            Map config = new HashMap();
            config.put("cloud_name", "dklb21dyh");
            config.put("api_key", "585356451553425");
            config.put("api_secret", "ylB_rZgnwVT823PH3_HtZo79Sf4");
            Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(cont));

            try {


                String Id = UUID.randomUUID().toString();
                // upload picture to cloudinary
                cloudinary.uploader().upload(picturePath, ObjectUtils.asMap("public_id", Id));
                //fetch image from cloudinary
                String url = cloudinary.url().generate(String.valueOf(Id));
                Log.e("url", " " + url);

                String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
                UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);
                userTable.setProfileStatus(profileStatus);
                //userTable.setLKProfilePicUrl(url);
                m_config.mapper.save(userTable);
                GenerikFunctions.showToast(cont, "Profile updated successfully");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static String flagCheckParty = "No";

    public static void createParty(Context context){
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        try {

            String FacebookID = LoginValidations.initialiseLoggedInUser(context).getFB_USER_ID();

            UserTable selUserTable = m_config.mapper.load(UserTable.class, FacebookID);
            List PartiesList = new ArrayList();
            PartiesList = selUserTable.getParties();

            if (selUserTable.getParties() != null) {
                PartiesClass Parties = new PartiesClass();
                for (int j = 0; j < PartiesList.size(); j++) {
                    Parties = (PartiesClass) PartiesList.get(j);
                    if (Parties.getPartyStatus().equals("Pending") || Parties.getPartyStatus().equals("Approved") || Parties.getPartyStatus().equals("Created")) {
//                if(PartyStartTime lies between result party start and endtime ){
                        Log.e("---", " already created a party or exist in a party ");
                        flagCheckParty = "Yes";
//                }else{
//                    flagCheckParty = "No";
//                }
                    }
                }
            }


            if (flagCheckParty.equals("Yes")) {
                Log.e("---", " ---already created a party or exist in a party2--- ");
            } else {
                PartyTable party = new PartyTable();
                Log.e("MainActivity---", "Inserting party");
                party.setPartyID("");
                party.setPartyName("");
                party.setStartTime("");
                party.setEndTime("");
                party.setDate("");
                party.setHostFBID("");
                party.setHostQBID("");
                party.setHostName("");
                party.setPartyType("");
                party.setPartyDescription("");
                party.setBYOB("");
                party.setPartyAddress("");
                party.setPartyLatLong("");
                party.setPartyImage("");
                party.setMaskStatus("");
                party.setDialogID(null);
                party.setGateCrashers(null);
                m_config.mapper.save(party);
                Log.e("MainActivity---", "Party inserted");
                addPartiestoUserTable(selUserTable);
            }



        } catch (Exception ex) {
            Log.e("", "Error retrieving data");
            ex.printStackTrace();
            GenerikFunctions.hideDialog(m_config.pDialog);
        }
    }


    /** Adding Parties to userTable at time of creating party*/
    public static void addPartiestoUserTable(UserTable selUserTable) {
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();



        try {

            if (selUserTable.getParties() == null) {
                PartiesClass Parties = new PartiesClass();
                Parties.setPartyId("434543");
                Parties.setPartyStatus("Created");
                Parties.setStartTime("Created");
                Parties.setEndTime("Created");

                List PartiesList = new ArrayList();
                PartiesList.add(Parties);
                selUserTable.setParties(PartiesList);
                m_config.mapper.save(selUserTable);

            } else {
                //add new entry to existing array
                PartiesClass Parties = new PartiesClass();
                Parties.setPartyId("434543");
                Parties.setPartyStatus("Created");
                Parties.setStartTime("Created");
                Parties.setEndTime("Created");

                List PartiesList = new ArrayList();
                PartiesList = selUserTable.getParties();
                Log.e("finalpartyIdstatus size", " " + PartiesList.size());
                PartiesList.add(Parties);
                selUserTable.setParties(PartiesList);
                m_config.mapper.save(selUserTable);

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

            PartiesClass Parties = new PartiesClass();
            Parties = (PartiesClass) finalPartiesList.get(0);
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


    public static void basicValidation(final LoggedInUserInformation loggedInUserInfo)
    {
        if(loggedInUserInfo.getFB_USER_PROFILE_PIC().equals("N/A") || loggedInUserInfo.getFB_USER_PROFILE_PIC().equals(null))
        {
            hasFBImage=false;
            //Check for LI profile Pic
            if(loggedInUserInfo.getLI_USER_PROFILE_PIC().equals("N/A") || loggedInUserInfo.getLI_USER_PROFILE_PIC().equals(null))
            {
                ConstsCore.profilePicAvailable="No";
                hasLIImage=false;
            }
            else
            {
                hasLIImage=true;
                ConstsCore.profilePicAvailable="Yes";
            }
        }
        else
        {
            hasFBImage=true;
            ConstsCore.profilePicAvailable="Yes";
        }

        //Connection Count
        if(Integer.parseInt(loggedInUserInfo.getFB_USER_FRIENDS()) >= ConstsCore.FB_FRIENDS)
        {

            if(Integer.parseInt(loggedInUserInfo.getLI_USER_CONNECTIONS())< ConstsCore.LI_CONNECTIONS)
            {
                //fb valid  li invalid
                ConstsCore.validFriendsCount="Yes";
                ConstsCore.validConnCount="No";
            }
            else
            {
                // fb valid li valid
                ConstsCore.validFriendsCount="Yes";
                ConstsCore.validConnCount="Yes";
            }
        }
        else
        {
            ConstsCore.validFriendsCount="No";
        }


//                    profilePicAvailable="No";
//                    String validAge="No";
//                    String validFriendsCount="No";
//                    String validConnCount="No";

        //     Log.e("Before Conditions check priflefald ageflag friendsflag", ConstsCore.profilePicAvailable +"    " + ConstsCore.ValidAge +"    " + ConstsCore.validFriendsCount);
        if(ConstsCore.profilePicAvailable.equals("No"))
        {
            Handler h = new Handler(cont.getMainLooper());
            h.post(new Runnable()
            {
                @Override
                public void run()
                {
                    GenerikFunctions.showToast(cont,"DOnt have profile picture");
                }
            });

        }
        else
        {
            //Have profile picture
            if(ConstsCore.ValidAge.equals("No"))
            {

                Handler h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        GenerikFunctions.showToast(cont,"Invalid age");
                    }
                });

            }
            else
            {
                //Valid age
                if(ConstsCore.validFriendsCount.equals("No"))
                {
                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            GenerikFunctions.showToast(cont,"Face book friends count is less than " + ConstsCore.FB_FRIENDS);
                        }
                    });
                }
                else
                {
                    if(ConstsCore.validConnCount.equals("No"))
                    {
                        Handler h = new Handler(cont.getMainLooper());
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                GenerikFunctions.showToast(cont,"LI conn count is less than "+ ConstsCore.LI_CONNECTIONS);
                            }
                        });
                    }
                    else
                    {
                        //Start Home Page
                        //Set Validation FLags Here
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(m_config.BasicFBLIValidationsDone,"Yes");
                        editor.apply();

                        //Go for Face Detection
                        //  new Imageverification(context).execute();

                        try
                        {
                            // Log.e("Inside try","yes");
                            String url = loggedInUserInfo.getFB_USER_PROFILE_PIC();

                            if(url.equals(null) || url.equals("") || url.equals("N/A"))
                            {
                                if(loggedInUserInfo.getLI_USER_PROFILE_PIC() == null ||
                                        loggedInUserInfo.equals("") ||
                                        loggedInUserInfo.getLI_USER_PROFILE_PIC().equals("N/A"))
                                {
                                    url = "";
                                }
                                else
                                {
                                    url = loggedInUserInfo.getLI_USER_PROFILE_PIC();
                                }
                            }
                            else
                            {
                                url = loggedInUserInfo.getFB_USER_PROFILE_PIC();
                            }

                            // Log.e("URL",url);

                            if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
                            {

                                //       Log.e("Before Picasso play service","yes");

                                Target mTarget = new Target()
                                {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
                                    {
                                        Log.e("FB bitmap loaded ","sucessfully  " + bitmap.toString() );


                                        int faces = faceOverlayView.setBitmap(bitmap);

                                        if(faces>0)
                                        {
                                            Log.e("There is face in pic",faces+"");
                                            Log.e("GO for OTP","Yes");

                                            //Set  Face detect flag here to true
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(m_config.FaceDetectDone,"Yes");
                                            editor.apply();
                                        }
                                        else
                                        {

                                            //Set  Face detect flag here to false
                                            Log.e("There is no face in pic","");
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(m_config.FaceDetectDone,"No");
                                            editor.apply();

                                            GenerikFunctions.showToast(cont,"There is no face in your profile pic");
                                        }

                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable drawable)
                                    {
                                        Log.e("On FB bitmap failed",drawable.toString());
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable drawable)
                                    {

                                    }

                                };


                                Picasso.with(cont)
                                        .load(url)
                                        .into(mTarget);


                                faceOverlayView.setTag(mTarget);



                            }
                        }

                        catch(Exception e)
                        {
                            e.printStackTrace();
//                            Log.e(TAG,e.getMessage());
                        }


//                        Intent i = new Intent(cont, HomePageActivity.class);
//                        cont.startActivity(i);
                    }
                }
            }
        }
    }

}



