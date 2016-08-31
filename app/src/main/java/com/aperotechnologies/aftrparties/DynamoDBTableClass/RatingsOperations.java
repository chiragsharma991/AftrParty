package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.History.RequestantActivity;
import com.aperotechnologies.aftrparties.History.RequestantFragment;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBPushNotifications;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.RatePartyActivity;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;

import java.util.List;

/**
 * Created by hasai on 23/08/16.
 */
public class RatingsOperations
{

    static Configuration_Parameter m_config;
    static SharedPreferences sharedPreferences;


    // function for ratings by host to Gc in partytable
    public static class RatingsByHostinPartyTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String GCFBID;
        String partyID;
        boolean value = false;
        PartyTable party;
        List<GateCrashersClass> GCList;
        String hostRatings;

        public RatingsByHostinPartyTable(String GCFBID, String partyID, Context cont, String hostRatings)
        {

            this.cont = cont;
            this.GCFBID = GCFBID;
            this.partyID = partyID;
            this.hostRatings = hostRatings.replace(".0","");
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try{
                party = m_config.mapper.load(PartyTable.class, partyID);
                GCList = party.getGatecrashers();


                if (GCList != null) {

                    //Update Item at particular Position
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
            Log.e("--onPostEx- RatingsByHostpartytable "," "+v);

            if(v == true) {


                for (int i = GCList.size() - 1; i >= 0; i--) {

                    if (GCList.get(i).getGatecrasherid().equals(GCFBID)) {

                        GateCrashersClass GateCrashers = GCList.get(i);
                        GateCrashers.setGatecrasherid(GCList.get(i).getGatecrasherid());
                        GateCrashers.setGcrequeststatus(GCList.get(i).getGcrequeststatus());
                        GateCrashers.setgcfbprofilepic(GCList.get(i).getgcfbprofilepic());
                        GateCrashers.setgclkid(GCList.get(i).getgclkid());
                        GateCrashers.setgcqbid(GCList.get(i).getgcqbid());
                        GateCrashers.setGcattendancestatus(GCList.get(i).getGcattendancestatus());
                        GateCrashers.setRatingsbyhost(hostRatings);
                        GateCrashers.setRatingsbygc(GCList.get(i).getRatingsbygc());
                        GCList.set(i, GateCrashers);
                        party.setGatecrashers(GCList);
                        m_config.mapper.save(party);
                        break;
                    }

                }

                new RatingsByHostinUserTable(GCFBID, partyID, cont, hostRatings).execute();

            }
            else
            {

                GenerikFunctions.hDialog();
                GenerikFunctions.sDialog(cont, "Ratings failed");

            }

        }
    }


    // function for ratings by host to Gc in usertable
    public static class RatingsByHostinUserTable extends AsyncTask<String, Void, Boolean> {
        Context cont;
        String GCFBID;
        String partyID;
        boolean value = false;
        List<PartiesClass> PartiesList;
        UserTable user;
        String hostRatings;


        public RatingsByHostinUserTable(String GCFBID, String partyID, Context cont, String hostRatings) {
            this.cont = cont;
            this.GCFBID = GCFBID;
            this.partyID = partyID;
            this.hostRatings = hostRatings;

            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.cont);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                user = m_config.mapper.load(UserTable.class, GCFBID);
                PartiesList = user.getParties();


                if (PartiesList != null) {

                    //Update Item at particular Position
                    value = true;


                }
            } catch (Exception e) {
                value = false;
            } finally {
                return value;
            }


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v) {
            Log.e("--onPostEx- RatingsByHostinUserTable", " " + v + " ");

            if (v == true) {

                for (int i = PartiesList.size() - 1; i >= 0; i--)
                {
                    if (PartiesList.get(i).getPartyid().equals(partyID))
                    {
                        PartiesClass Parties = PartiesList.get(i);
                        Parties.setPartyid(PartiesList.get(i).getPartyid());
                        Parties.setStarttime(PartiesList.get(i).getStarttime());
                        Parties.setEndtime(PartiesList.get(i).getEndtime());
                        Parties.setPartyname(PartiesList.get(i).getPartyname());
                        Parties.setPartystatus(PartiesList.get(i).getPartystatus());
                        Parties.setRatingsbyhost(hostRatings);
                        Parties.setRatingsbygc(PartiesList.get(i).getRatingsbygc());
                        PartiesList.set(i, Parties);
                        user.setParties(PartiesList);
                        m_config.mapper.save(user);

                        GenerikFunctions.hDialog();
                        GenerikFunctions.showToast(cont, "Ratings Succeeded");


                        RatePartyActivity.rateparty.finish();
                        break;
                    }
                }

            }
            else
            {
                GenerikFunctions.hDialog();
                GenerikFunctions.sDialog(cont, "Ratings failed");
            }
        }
    }


    // function for ratings by GC to host in partytable
    public static class RatingsByGCinPartyTable extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String GCFBID;
        String partyID;
        boolean value = false;
        PartyTable party;
        List<GateCrashersClass> GCList;
        String GCRatings;

        public RatingsByGCinPartyTable(String GCFBID, String partyID, Context cont, String GCRatings)
        {

            this.cont = cont;
            this.partyID = partyID;
            this.GCRatings = GCRatings.replace(".0","");
            this.GCFBID = GCFBID;
            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try{
                party = m_config.mapper.load(PartyTable.class, partyID);
                GCList = party.getGatecrashers();


                if (GCList != null) {

                    //Update Item at particular Position
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
            Log.e("--onPostEx- RatingsByGCinPartyTable "," "+v);

            if(v == true) {


                for (int i = GCList.size() - 1; i >= 0; i--) {

                    if (GCList.get(i).getGatecrasherid().equals(GCFBID)) {

                        GateCrashersClass GateCrashers = GCList.get(i);
                        GateCrashers.setGatecrasherid(GCList.get(i).getGatecrasherid());
                        GateCrashers.setGcrequeststatus(GCList.get(i).getGcrequeststatus());
                        GateCrashers.setgcfbprofilepic(GCList.get(i).getgcfbprofilepic());
                        GateCrashers.setgclkid(GCList.get(i).getgclkid());
                        GateCrashers.setgcqbid(GCList.get(i).getgcqbid());
                        GateCrashers.setGcattendancestatus(GCList.get(i).getGcattendancestatus());
                        GateCrashers.setRatingsbyhost(GCList.get(i).getRatingsbyhost());
                        GateCrashers.setRatingsbygc(GCRatings);
                        GCList.set(i, GateCrashers);
                        party.setGatecrashers(GCList);
                        m_config.mapper.save(party);

                        new RatingsByGCinUserTable(GCFBID, partyID, cont, GCRatings).execute();
                        break;
                    }

                }

            }
            else
            {

                GenerikFunctions.hDialog();
                GenerikFunctions.sDialog(cont, "Ratings failed");
            }

        }
    }


    // function for ratings by GC to host in usertable
    public static class RatingsByGCinUserTable extends AsyncTask<String, Void, Boolean> {
        Context cont;
        String partyID;
        boolean value = false;
        List<PartiesClass> PartiesList;
        UserTable user;
        String GCRatings;
        String GCFBID;


        public RatingsByGCinUserTable(String GCFBID, String partyID, Context cont, String GCRatings) {
            this.cont = cont;
            this.partyID = partyID;
            this.GCRatings = GCRatings;
            this.GCFBID = GCFBID;

            m_config = Configuration_Parameter.getInstance();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.cont);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try
            {
                user = m_config.mapper.load(UserTable.class, GCFBID);
                PartiesList = user.getParties();

                if (PartiesList != null) {

                    //Update Item at particular Position
                    value = true;
                }
            }
            catch (Exception e) {
                value = false;
            }
            finally {
                return value;
            }


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v) {

            if (v == true) {

                for (int i = PartiesList.size() - 1; i >= 0; i--)
                {
                    Log.e("--onPostEx- ", " RatingsByGCinUserTable" + v + " "+ PartiesList.get(i).getPartyid().equals(partyID));

                    if (PartiesList.get(i).getPartyid().equals(partyID))
                    {


                        PartiesClass Parties = PartiesList.get(i);
                        Parties.setPartyid(PartiesList.get(i).getPartyid());
                        Parties.setStarttime(PartiesList.get(i).getStarttime());
                        Parties.setEndtime(PartiesList.get(i).getEndtime());
                        Parties.setPartyname(PartiesList.get(i).getPartyname());
                        Parties.setPartystatus(PartiesList.get(i).getPartystatus());
                        Parties.setRatingsbyhost(PartiesList.get(i).getRatingsbyhost());
                        Parties.setRatingsbygc(GCRatings);
                        PartiesList.set(i, Parties);
                        user.setParties(PartiesList);
                        m_config.mapper.save(user);


                        AWSPartyOperations.updateRatingsInUserTable(cont, GCFBID, partyID, user);

                        break;
                    }

                }

            }
            else
            {
                GenerikFunctions.hDialog();
                GenerikFunctions.sDialog(cont, "Ratings failed");
            }
        }
    }





}
