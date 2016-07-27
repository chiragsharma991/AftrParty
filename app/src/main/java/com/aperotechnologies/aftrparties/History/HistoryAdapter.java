package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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


    //    SQLiteDatabase sdb;
    public HistoryAdapter(Context cont, List<PartiesClass> PartiesList)
    {
        this.cont = cont;
        this.PartiesList = PartiesList;
        m_config= Configuration_Parameter.getInstance();



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

                if(finalParties.getPartystatus().equals("Created") || finalParties.getPartystatus().equals("Approved")){
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
                    try
                    {
                        String PartyId = finalParties.getPartyid();
                        PartyTable partytable = m_config.mapper.load(PartyTable.class, PartyId);
                        List<PartyMaskStatusClass> partymaskstatus = partytable.getPartymaskstatus();
                        if(partymaskstatus.get(0).getMaskstatus().equals("Unmask"))
                        {
                            Long currTime = Validations.getCurrentTime();//System.currentTimeMillis();
                            if(currTime < Long.parseLong(partymaskstatus.get(0).getMasksubscriptiondate()))
                            {
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

                            }

                        }

                    }
                    catch (Exception e)
                    {

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
