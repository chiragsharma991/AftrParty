package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.GateCrasher.PartyConversion;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.PartyDetails;
import com.aperotechnologies.aftrparties.History.PartyParceableData;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.AsyncAgeCalculation;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBPushNotifications;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;

import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.aperotechnologies.aftrparties.Reusables.Validations.getUrlfromCloudinary;

/**
 * Created by hasai on 16/05/16.
 */
public class AWSPartyOperations {



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
    /*public static void createUser(Context context, LoggedInUserInformation loggedInUserInfo) {

        cont = context;
        m_config = Configuration_Parameter.getInstance();
        m_config.pDialog = new ProgressDialog(cont);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Activity act=(Activity)context;
        faceOverlayView = (FaceOverlayView) ((Activity) context).findViewById(R.id.face_overlay);

        try {

            String FacebookID = loggedInUserInfo.getFB_USER_ID();
            String QuickBloxID = sharedPreferences.getString(m_config.QuickBloxID,"");//String.valueOf(m_config.chatService.getUser().getId());
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
    }*/


    /** create party in PartyTable in hostActivity **/
    public static class createParty extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        PartyTable partyTable;
        UserTable user;
        boolean value = false;


        public createParty(Context cont, PartyTable partyTable)
        {
            this.cont = cont;
            this.partyTable = partyTable;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
                user = m_config.mapper.load(UserTable.class, HostFBID);
                value = true;

            }catch (Exception ex) {
                Log.e("", "Error retrieving data");
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
            Log.e("--onPostEx- createParty"," "+v);

            if(v == true) {

                //new getPrevPartyStatus(cont, partyTable, user).execute();
                String url = getUrlfromCloudinary(cont, partyTable.getPartyImage());
                //Log.e("url", " " + url);
                partyTable.setPartyImage(url);

                m_config.mapper.save(partyTable);
                Log.e("MainActivity---", "Party inserted");
                new addPartiestoUserTable(user, partyTable, cont, "Created", "CreateParty", null, null).execute();

            }else{
                Log.e("", "Error retrieving data");
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont, "Party creation failed, Please try again after some time");
            }

        }
    }

    public static void updateDialogId(Context cont, String DialogID, String GCFBID, String GCQBID, String PartyID, String PartyName){
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        try {
            PartyTable selPartyTable = m_config.mapper.load(PartyTable.class, PartyID);
            selPartyTable.setDialogID(DialogID);
            m_config.mapper.save(selPartyTable);
            QBPushNotifications.sendApprovedPN(GCFBID, GCQBID, PartyID, PartyName, cont);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Adding Parties to userTable at time of creating party*/
    public static class addPartiestoUserTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        UserTable user;
        PartyTable partytable;
        String Status;
        String fromWhere;
        List<PartyConversion> pc;
        Button b;
        boolean value = false;


        public addPartiestoUserTable(UserTable user, PartyTable partytable, Context cont, String Status, String fromWhere, List<PartyConversion> pc, Button b)
        {
            this.cont = cont;
            this.user = user;
            this.partytable = partytable;
            this.Status = Status;
            this.fromWhere = fromWhere;
            this.pc = pc;
            this.b = b;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {


            try {

                if (user.getParties() == null) {
                    PartiesClass Parties = new PartiesClass();
                    Parties.setPartyid(partytable.getPartyID());
                    Parties.setPartyname(partytable.getPartyName());
                    Parties.setPartystatus(Status);
                    Parties.setStarttime(partytable.getStartTime());
                    Parties.setEndtime(partytable.getEndTime());

                    List PartiesList = new ArrayList();
                    PartiesList.add(Parties);
                    user.setParties(PartiesList);
                    m_config.mapper.save(user);
                    value = true;

                } else {
                    //add new entry to existing array
                    PartiesClass Parties = new PartiesClass();
                    Parties.setPartyid(partytable.getPartyID());
                    Parties.setPartyname(partytable.getPartyName());
                    Parties.setPartystatus(Status);
                    Parties.setStarttime(partytable.getStartTime());
                    Parties.setEndtime(partytable.getEndTime());

                    List PartiesList = new ArrayList();
                    PartiesList = user.getParties();
                    Log.e("finalpartyIdstatus size", " " + PartiesList.size());
                    PartiesList.add(Parties);
                    user.setParties(PartiesList);
                    m_config.mapper.save(user);
                    value = true;
                }


            } catch (Exception ex) {
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
            Log.e("--onPostEx- addPartiestoUserTable"," "+v);

            if(v == true)
            {

                if(fromWhere == "CreateParty")
                {
                    GenerikFunctions.hDialog();
                    GenerikFunctions.showToast(cont,"Party created successfully");
                    cont.startActivity(new Intent(cont, HistoryActivity.class));
                    ((Activity) cont).finish();

                }
                else
                {
                    new addGCtoPartyTable(cont, partytable, "Pending", pc, b).execute();
                }


            }
            else
            {
                if(fromWhere == "CreateParty")
                {
                    GenerikFunctions.hDialog();
                    GenerikFunctions.showToast(cont,"Party creation failed, Please try again after some time.");
                }
                else
                {
                    GenerikFunctions.hDialog();
                    GenerikFunctions.showToast(cont,"Party Request Failed, Please try again after some time.");
                }
            }

        }
    }


    /** Adding GateCrashers to partyTable at time of requesting party*/
    public static class addGCtoPartyTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        PartyTable partytable;
        String Status;
        List<PartyConversion> pc;
        Button b;
        boolean value = false;


        public addGCtoPartyTable(Context cont, PartyTable partytable, String Status, List<PartyConversion> pc, Button b)
        {
            this.cont = cont;
            this.partytable = partytable;
            this.Status = Status;
            this.pc = pc;
            this.b = b;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                PartyTable selPartyTable = m_config.mapper.load(PartyTable.class, partytable.getPartyID());
                LoggedInUserInformation loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
                String GCFBID = loggedInUserInformation.getFB_USER_ID();

                String GCFBProfilePic = loggedInUserInformation.getFB_USER_PROFILE_PIC();
                String GCLKID = loggedInUserInformation.getLI_USER_ID();

                if (selPartyTable.getGatecrashers() == null) {
                    GateCrashersClass GateCrashers = new GateCrashersClass();
                    GateCrashers.setGatecrasherid(GCFBID);
                    GateCrashers.setGcrequeststatus(Status);
                    //
                    GateCrashers.setgcfbprofilepic(GCFBProfilePic);
                    GateCrashers.setgclkid(GCLKID);
                    GateCrashers.setgcqbid(sharedPreferences.getString(m_config.QuickBloxID,""));
                    //
                    GateCrashers.setGcattendancestatus("No");

                    List GateCrasherList = new ArrayList();
                    GateCrasherList.add(GateCrashers);
                    selPartyTable.setGatecrashers(GateCrasherList);
                    m_config.mapper.save(selPartyTable);
                    value = true;

                } else {
                    //add new entry to existing array
                    GateCrashersClass GateCrashers = new GateCrashersClass();
                    GateCrashers.setGatecrasherid(GCFBID);
                    GateCrashers.setGcrequeststatus(Status);
                    //
                    GateCrashers.setgcfbprofilepic(GCFBProfilePic);
                    GateCrashers.setgclkid(GCLKID);
                    GateCrashers.setgcqbid(sharedPreferences.getString(m_config.QuickBloxID,""));
                    //
                    GateCrashers.setGcattendancestatus("No");

                    List GateCrasherList = new ArrayList();
                    GateCrasherList = selPartyTable.getGatecrashers();
                    Log.e("finalpartyIdstatus size", " " + GateCrasherList.size());
                    GateCrasherList.add(GateCrashers);
                    selPartyTable.setGatecrashers(GateCrasherList);
                    m_config.mapper.save(selPartyTable);
                    value = true;
                }


            } catch (Exception ex) {
                //Log.e("", "Error Update retrieving data");
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
            Log.e("--onPostEx- addGCtoPartyTable"," "+v);

            if(v == true) {


                b.setText(Status);
                PartyConversion pconv = new PartyConversion();
                pconv.setPartyid(partytable.getPartyID());
                pconv.setPartyname(partytable.getPartyName());
                pconv.setPartystatus(Status);
                pconv.setStarttime(partytable.getStartTime());
                pconv.setEndtime(partytable.getEndTime());
                pc.add(pconv);
                Log.e("pc"," "+pc.size()+" ");
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont,"Request has been send to Party");
                QBPushNotifications.sendRequestPN(partytable.getHostQBID(), partytable.getHostFBID(), partytable.getPartyName(), partytable.getPartyID(), cont);


            }else{
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont,"Party Request Failed, Please try after some time.");
                //addGCtoPartyTable(cont, partytable, "Pending", pc);
            }

        }
    }


    public static class addupdActiveParty extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String gateCrasherID;
        PartyParceableData party;
        String startBlocktime;
        String endBlockTime;
        String status;
        TextView t;
        boolean value = false;
        UserTable user;
        List<ActivePartyClass> ActivePartyList;


        public addupdActiveParty(String gateCrasherID, PartyParceableData party, String startBlockTime, String endBlockTime, String status, Context cont, TextView t)
        {
            this.cont = cont;
            this.gateCrasherID = gateCrasherID;
            this.party = party;
            this.startBlocktime = startBlockTime;
            this.endBlockTime = endBlockTime;
            this.status = status;
            this.t = t;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try{
                user = m_config.mapper.load(UserTable.class, gateCrasherID);
                ActivePartyList = user.getActiveparty();
                Log.e("ActiveParty "," "+ActivePartyList);
                value = true;

            }
            catch(Exception e)
            {
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
            Log.e("--onPostEx- addupdActiveParty "," "+v);

            if(v == true) {


                if (ActivePartyList == null) {

                    List<ActivePartyClass> ActivityPartylist = new ArrayList<>();
                    ActivePartyClass ActiveParty = new ActivePartyClass();
                    ActiveParty.setPartyid(party.getPartyId());
                    ActiveParty.setPartyname(party.getPartyName());
                    ActiveParty.setStarttime(party.getStartTime());
                    ActiveParty.setEndtime(party.getEndTime());
                    ActiveParty.setPartystatus(status);
                    ActiveParty.setStartblocktime(startBlocktime);
                    ActiveParty.setEndblocktime(endBlockTime);
                    ActivityPartylist.add(ActiveParty);
                    user.setActiveparty(ActivityPartylist);
                    m_config.mapper.save(user);
                    new AWSPartyOperations.updateGCinPartyTable(gateCrasherID, party.getPartyId(),  status, cont, t).execute();



                }else{

                    ActivePartyClass ActiveParty = ActivePartyList.get(0);
                    //ActivePartyList.remove(0);
                    ActiveParty.setPartyid(party.getPartyId());
                    ActiveParty.setPartyname(party.getPartyName());
                    ActiveParty.setStarttime(party.getStartTime());
                    ActiveParty.setEndtime(party.getEndTime());
                    ActiveParty.setPartystatus(status);
                    ActiveParty.setStartblocktime(startBlocktime);
                    ActiveParty.setEndblocktime(endBlockTime);
                    ActivePartyList.set(0, ActiveParty);
                    user.setActiveparty(ActivePartyList);
                    m_config.mapper.save(user);
                    new AWSPartyOperations.updateGCinPartyTable(gateCrasherID, party.getPartyId(),  status, cont, t).execute();


                }



            }else{
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont, "Approval failed, Please try again after some time");
            }

        }
    }


    /** Updating PartyTable at time of Status Updation**/

    public static class updateGCinPartyTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String gateCrasherID;
        String partyID;
        String Status;
        TextView t;
        String DialogID;
        String PartyImage;
        boolean value = false;
        PartyTable party;
        List<GateCrashersClass> GCList;


        public updateGCinPartyTable(String gateCrasherID, String partyID, String Status, Context cont, TextView t)
        {
            this.cont = cont;
            this.gateCrasherID = gateCrasherID;
            this.partyID = partyID;
            this.Status = Status;
            this.t = t;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try{
                party = m_config.mapper.load(PartyTable.class, partyID);
                GCList = party.getGatecrashers();
                Log.e("PartyImage "," "+PartyImage);
                Log.e("GCLIST", "---- "+GCList+" "+gateCrasherID);

                DialogID = party.getDialogID();
                PartyImage = party.getPartyImage();

                if (GCList != null) {

                    //Update Item at particular Position
                    Log.e("GCList size", " " + GCList.size());
                    value = true;
                }

            }
            catch(Exception e)
            {
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
            Log.e("--onPostEx- updateGCinPartyTable "," "+v);

            if(v == true) {


                for (int i = GCList.size() - 1; i >= 0; i--) {

                    if (GCList.get(i).getGatecrasherid().equals(gateCrasherID)) {

                        Log.e("-----","came here");
                        GateCrashersClass GateCrashers = GCList.get(i);
                        GateCrashers.setGatecrasherid(GCList.get(i).getGatecrasherid());
                        GateCrashers.setGcrequeststatus(Status);
                        GateCrashers.setgcfbprofilepic(GCList.get(i).getgcfbprofilepic());
                        GateCrashers.setgclkid(GCList.get(i).getgclkid());
                        GateCrashers.setgcqbid(GCList.get(i).getgcqbid());
                        GateCrashers.setGcattendancestatus("No");
                        GCList.set(i, GateCrashers);
                        party.setGatecrashers(GCList);
                        m_config.mapper.save(party);
                        break;
                    }

                }

                new updatePartiesinUserTable(gateCrasherID, partyID, Status, DialogID, PartyImage, cont, t).execute();

            }else{

                GenerikFunctions.hDialog();

            }

        }
    }


    /** Updating UserTable at time of Status Updation**/


    public static class updatePartiesinUserTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String gateCrasherFBID;
        String partyID;
        String DialogID;
        String PartyImage;
        String Status;
        TextView t;

        String PartyName;
        String PartyEndTime;
        String gateCrasherQBID;
        boolean value = false;
        List<PartiesClass> PartiesList;
        UserTable user;


        public updatePartiesinUserTable(String gateCrasherFBID, String partyID, String Status, String DialogID, String PartyImage, Context cont, TextView t)
        {
            this.cont = cont;
            this.gateCrasherFBID = gateCrasherFBID;
            this.partyID = partyID;
            this.DialogID = DialogID;
            this.PartyImage = PartyImage;
            this.Status = Status;
            this.t = t;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                user = m_config.mapper.load(UserTable.class, gateCrasherFBID);
                PartiesList = user.getParties();


                gateCrasherQBID = user.getQuickBloxID();

                if (PartiesList != null) {

                    //Update Item at particular Position
                    Log.e("PartiesList size", " " + PartiesList.size());
                    value = true;




                }
            }
            catch (Exception e){
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
            Log.e("--onPostEx- updatePartiesinUserTable"," "+v);

            if(v == true) {

                for (int i = PartiesList.size() - 1; i >= 0; i--) {
                    if (PartiesList.get(i).getPartyid().equals(partyID)) {
                        PartiesClass Parties = PartiesList.get(i);
                        Parties.setPartyid(PartiesList.get(i).getPartyid());
                        Parties.setStarttime(PartiesList.get(i).getStarttime());
                        Parties.setEndtime(PartiesList.get(i).getEndtime());
                        Parties.setPartyname(PartiesList.get(i).getPartyname());
                        //PartiesList.remove(i);
                        Parties.setPartystatus(Status);
                        PartiesList.set(i, Parties);
                        user.setParties(PartiesList);
                        PartyName = PartiesList.get(i).getPartyname();
                        PartyEndTime = PartiesList.get(i).getEndtime();
                        m_config.mapper.save(user);

                        value = true;
                        break;
                    }
                }


                if (Status.equals("Cancelled"))
                {  //when GC Cancel Party Request after Approval

                    if(t.getText().toString().equals("Cancel Request"))
                    {

                        try
                        {
                            List<ActivePartyClass> ActivePartyList = user.getActiveparty();
                            List<PaidGCClass> PaidGC = user.getPaidgc();

                            if (PaidGC == null)
                            {
                                //Unpaid user
                                t.setVisibility(View.GONE);
                                Toast.makeText(cont,"Party request has been cancelled",Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                //Paid User
                                // remove party from ActiveParty list
                                if (ActivePartyList != null) {
                                    ActivePartyClass ActiveParty = ActivePartyList.get(0);
                                    ActivePartyList.remove(ActiveParty);
                                    //ActivePartyList.remove(0);
                                    user.setActiveparty(ActivePartyList);
                                    m_config.mapper.save(user);
                                }
                                t.setVisibility(View.GONE);
                                Toast.makeText(cont,"Party request has been cancelled",Toast.LENGTH_SHORT).show();


                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            GenerikFunctions.showToast(cont, "Unable to Cancel Request, Please try again after some time");
                        }
                    }

                }
                else if(Status.equals("Approved"))
                {
                    //Chat Dialog Creation after Party Approval

                    t.setText(Status);
                    Toast.makeText(cont, "Request has been approved",Toast.LENGTH_SHORT).show();
                    GenerikFunctions.hDialog();

                    if (DialogID == null || DialogID.equals(null) || DialogID.equals("") || DialogID.equals("N/A"))
                    {
                        QBChatDialogCreation.createGroupChat(PartyName, PartyImage, gateCrasherQBID, gateCrasherFBID, partyID, cont, PartyEndTime);
                    }
                    else
                    {
                        QBChatDialogCreation.updateGroupChat(PartyName, DialogID, gateCrasherQBID, gateCrasherFBID, partyID, cont);
                    }
                }
                else if(Status.equals("Declined"))
                {
                    t.setText(Status);
                    Toast.makeText(cont, "Request has been declined",Toast.LENGTH_SHORT).show();
                    GenerikFunctions.hDialog();
                }


            }else{
                GenerikFunctions.hDialog();
            }

        }
    }



    private static class getPrevPartyStatus extends AsyncTask<String, Void, String> {
        List<PartiesClass> p;
        List<PartyConversion> pc;
        String allowStatus = "Yes";
        Context cont;
        PartyTable partyTable;
        UserTable user;
        Configuration_Parameter m_config;

        public getPrevPartyStatus(Context cont, PartyTable partyTable, UserTable user) {
            this.cont = cont;
            this.partyTable = partyTable;
            this.user = user;
            pc = new ArrayList<PartyConversion>();
            p =  user.getParties();
            m_config = Configuration_Parameter.getInstance();
        }

        @Override
        protected String doInBackground(String... params) {


            if(p == null || p.size() == 0)
            {

            }
            else
            {
                //loop for all parties data of user(from Usertable)
                for (int i = p.size() - 1; i >= 0; i--)
                {

                    Log.e("p.size() "," --- "+p.size());
                    // Log.e("In loop " +i,p.get(i).getPartyName() +  "  aa  " + p.get(i).getStartTime().toString());

                    PartyConversion pconv = new PartyConversion();
                    pconv.setPartyid(p.get(i).getPartyid());
                    pconv.setPartyname(p.get(i).getPartyname());
                    pconv.setPartystatus(p.get(i).getPartystatus());
                    pconv.setStarttime(p.get(i).getStarttime());
                    pconv.setEndtime(p.get(i).getEndtime());

                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(pconv.getConvertedstarttime());
                    cal2.setTime(new Date());
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
                    Log.e("sameDay   " + i, " " + sameDay);

                    //stores parties data of user in PartyConversion array for current date
                    if (sameDay == true) {
                        pc.add(pconv);
                        Log.e("pc---- " + " " + pconv.getPartyname() + " " + pconv.getPartystatus(), "aa");
                    }
                    else
                    {
                        break;
                    }

                }


                Log.e("pc.size() "," --- "+pc.size());

                if(pc.size() != 0) {
                    for (int j = 0; j < pc.size(); j++) {
                        if ((Long.parseLong(partyTable.getStartTime()) >= Long.parseLong(pc.get(j).getEndtime())) ||
                                (Long.parseLong(partyTable.getEndTime()) <= Long.parseLong(pc.get(j).getStarttime()))) {
                            //allow request
                            allowStatus = "Yes";
                        } else {
                            //Check status
                            if (pc.get(j).getPartystatus().equals("Approved") || pc.get(j).getPartystatus().equals("Created") || pc.get(j).getPartystatus().equals("Pending")) {
                                //dont allow request
                                allowStatus = "No";
                                break;
                            } else {
                                //allow request
                                allowStatus = "Yes";

                            }
                        }


                    }
                }else{
                    allowStatus = "Yes";
                }
            }

            Log.e("allowStatus"," "+allowStatus);
            return allowStatus;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String pCreationAllowStatus)
        {
                if (pCreationAllowStatus.equals("Yes")) {
                    //Allow for party request
                    String url = getUrlfromCloudinary(cont, partyTable.getPartyImage());
                    Log.e("url", " " + url);
                    partyTable.setPartyImage(url);

                    m_config.mapper.save(partyTable);
                    Log.e("MainActivity---", "Party inserted");

                    new addPartiestoUserTable(user, partyTable, cont, "Created", "CreateParty", null, null).execute();

                } else {
                    //Dont Allow for party request

                    GenerikFunctions.showToast(cont,"Party creation failed, already created a party or exist in a party");
                    GenerikFunctions.hideDialog(m_config.pDialog);
                }


        }
    }







}



