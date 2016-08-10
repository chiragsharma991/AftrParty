package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyMaskStatusClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 03/05/16.
 */
public class HistoryAdapter extends BaseAdapter {

    private List<PartiesClass> PartiesList;
    Context cont;
    Configuration_Parameter m_config;

    public HistoryAdapter(Context cont, List<PartiesClass> PartiesList)
    {
        this.cont = cont;
        this.PartiesList = PartiesList;
        m_config= Configuration_Parameter.getInstance();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);



    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return PartiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return PartiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = new ViewHolder();

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_history, parent, false);
            holder.partyName =  (TextView) convertView.findViewById(R.id.partyname);
            holder.partyStatus =  (TextView) convertView.findViewById(R.id.partystatus);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        PartiesClass Parties = new PartiesClass();
        Parties = (PartiesClass) PartiesList.get(position);
//        Log.e("", " " + Parties.getPartyName());
//        Log.e("", " " + Parties.getPartyStatus());

      //  Log.e("Party Adapter  " , position + "   " + Parties.getPartyName());




        holder.partyName.setText(Parties.getPartyname());
        holder.partyStatus.setText(Parties.getPartystatus());

        final PartiesClass finalParties = Parties;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("PartyName"," "+ finalParties.getPartyName());
//                Log.e("Status"," "+ finalParties.getPartyStatus());

                if(finalParties.getPartystatus().equals("Created") || finalParties.getPartystatus().equals("Approved"))
                {
                    Intent i = new Intent(cont,PartyDetails.class);
                    PartyParceableData party = new PartyParceableData();
                    party.setPartyId(finalParties.getPartyid());
                    party.setPartyName(finalParties.getPartyname());
                    party.setStartTime(finalParties.getStarttime());
                    party.setEndTime(finalParties.getEndtime());
                    party.setPartyStatus(finalParties.getPartystatus());
                    Bundle mBundles = new Bundle();
                    mBundles.putSerializable(ConstsCore.SER_KEY, party);
                    i.putExtras(mBundles);
                    cont.startActivity(i);

                }
                else
                {

                    GenerikFunctions.sDialog(cont, "Fetching Party Mask Status");
                    try
                    {
                        Log.e("here "," ");
                        String PartyId = finalParties.getPartyid();
                        PartyTable partytable = m_config.mapper.load(PartyTable.class, PartyId);

                        if(finalParties.getPartystatus().equals("Cancelled") && LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID().equals(partytable.getHostFBID()))
                        {
                            //check whether host is current user or not if party is cancelled
                            Intent i = new Intent(cont,PartyDetails.class);
                            PartyParceableData party = new PartyParceableData();
                            party.setPartyId(finalParties.getPartyid());
                            party.setPartyName(finalParties.getPartyname());
                            party.setStartTime(finalParties.getStarttime());
                            party.setEndTime(finalParties.getEndtime());
                            party.setPartyStatus(finalParties.getPartystatus());
                            Bundle mBundles = new Bundle();
                            mBundles.putSerializable(ConstsCore.SER_KEY, party);
                            i.putExtras(mBundles);
                            cont.startActivity(i);
                            GenerikFunctions.hDialog();
                        }
                        else
                        {
                            // check whether host had unmask the party for status declined, pending, cancelled
                            List<PartyMaskStatusClass> partymaskstatus = partytable.getPartymaskstatus();
                            Log.e("partymaskstatus",""+partymaskstatus);
                            if(partymaskstatus != null && partymaskstatus.get(0).getMaskstatus().equals("Unmask"))
                            {
                                Long currTime = Validations.getCurrentTime();//System.currentTimeMillis();
                                if(currTime < Long.parseLong(partymaskstatus.get(0).getMasksubscriptiondate()))
                                {
                                    Log.e("Party status is Unmask"," ");

                                    // check whether party is Unmasked
                                    Intent i = new Intent(cont,PartyDetails.class);
                                    PartyParceableData party = new PartyParceableData();
                                    party.setPartyId(finalParties.getPartyid());
                                    party.setPartyName(finalParties.getPartyname());
                                    party.setStartTime(finalParties.getStarttime());
                                    party.setEndTime(finalParties.getEndtime());
                                    party.setPartyStatus(finalParties.getPartystatus());
                                    Bundle mBundles = new Bundle();
                                    mBundles.putSerializable(ConstsCore.SER_KEY, party);
                                    i.putExtras(mBundles);
                                    cont.startActivity(i);
                                    GenerikFunctions.hDialog();

                                }else
                                {
                                    Log.e("Party status is Unmask"," subscription is expired");
                                    GenerikFunctions.hDialog();
                                }

                            }
                            else
                            {
                                Log.e("Party status is Mask","");
                                GenerikFunctions.hDialog();
                            }
                        }


                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        GenerikFunctions.hDialog();

                    }


                }


            }
        });

        return convertView;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }


    static class ViewHolder
    {

        private TextView partyName;
        private TextView partyStatus;


    }
}
