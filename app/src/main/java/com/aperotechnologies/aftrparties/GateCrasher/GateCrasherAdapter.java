package com.aperotechnologies.aftrparties.GateCrasher;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by hasai on 02/06/16.
 */
public class GateCrasherAdapter extends BaseAdapter
{

    //private View.OnClickListener onclick;
    private PaginatedScanList<PartyTable> result;
    Context context;
    UserTable currentUser;
    String reqStartTime;
    Configuration_Parameter m_config;


    //    SQLiteDatabase sdb;
    public GateCrasherAdapter(Context context, PaginatedScanList<PartyTable> result, String reqStartTime)
    {
        this.context = context;
        this.result = result;
        this.reqStartTime = reqStartTime;
        m_config = Configuration_Parameter.getInstance();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDeath()
                .build());

        m_config.pc = new ArrayList<PartyConversion>();


//        onclick = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                onItemClick(v, (PartiesClass) v.getTag());
//            }
//        };

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
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return result.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_gatecrasher, parent, false);
            holder.partyName =  (TextView) convertView.findViewById(R.id.partyname);
            holder.btn_Request = (Button) convertView.findViewById(R.id.btn_Request);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        PartyTable party = result.get(position);

        if(position == 0)
        {
            UserTable user = m_config.mapper.load(UserTable.class, LoginValidations.initialiseLoggedInUser(context).getFB_USER_ID());
            List<PartiesClass> p =  user.getParties();

            if(p == null || p.size() == 0)
            {
                Log.e("Inside if","Yes   aa");
            }
            else
            {

                for (int i = p.size() - 1; i >= 0; i--)
                {
                    PartyConversion pconv = new PartyConversion();
                    pconv.setPartyId(p.get(i).getPartyId());
                    pconv.setPartyName(p.get(i).getPartyName());
                    pconv.setPartyStatus(p.get(i).getPartyStatus());
                    pconv.setStartTime(p.get(i).getStartTime());
                    pconv.setEndTime(p.get(i).getEndTime());


                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(pconv.getConvertedStartTime());
                    cal2.setTime(new Date());
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
                    Log.e("sameDay   " + i, " " + sameDay);

                    if (sameDay == true)
                    {
                        m_config.pc.add(pconv);
                        Log.e("pc---- " + " " + pconv.getPartyName() + " " + pconv.getPartyStatus(), "aa");
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        //Log.e("Filtered Array Size",m_config.pc.size() + "   aa");
        holder.btn_Request.setText("Request");
        holder.partyName.setText(party.getPartyName());


        for(int i=0;i<m_config.pc.size();i++)
        {
            if(party.getPartyID().equals(m_config.pc.get(i).getPartyId()))
            {
                holder.btn_Request.setText(m_config.pc.get(i).getPartyStatus());
            }

        }



        final PartyTable finalParty = party;

        final ViewHolder finalHolder = holder;
        holder.btn_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //toDate(Long.parseLong(finalParty.getStartTime()));
                Button b = (Button) v;
                Log.e("Click Of Button",b.getText()  + "   aa");

                if(b.getText().toString().trim().equals("Request"))
                {
                    Log.e("Allow request For  " , finalHolder.partyName.getText() + "Check Conditions here  aa");
                }
                else
                {
                    Log.e("Dont Allow request For  " , finalHolder.partyName.getText() + " aa");
                }




               new GetData(finalParty).execute();

            }
        });


        convertView.setTag(party.getPartyID());
        return convertView;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

//    protected void onItemClick(View v, PartiesClass partyIdstatus) {
//        Log.e("PartyId "," "+partyIdstatus.getPartyId());
//        Log.e("PartyStatus "," "+partyIdstatus.getPartyStatus());
//        Intent i = new Intent(context,RequestantsList.class);
//        i.putExtra("PartyId",partyIdstatus.getPartyId());
//        context.startActivity(i);
//
//    }

    static class ViewHolder
    {

        private TextView partyName;
        private Button btn_Request;


    }



    private void toDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        String currentPartyDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        Log.e("currentPartyDate"," "+currentPartyDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(currentPartyDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public class GetData extends AsyncTask<Void,Void,Void>
    {

        PartyTable finalParty;

        public GetData(PartyTable finalParty) {
            this.finalParty = finalParty;
            Log.e("---"," 1----");
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.e("---"," 2----");
            try {

                 currentUser = m_config.mapper.load(UserTable.class, LoginValidations.initialiseLoggedInUser(context).getFB_USER_ID());


            } catch (Exception ex) {
                Log.e("", "Error retrieving data");
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Log.e("---"," 3----");

            if(currentUser.getParties() == null){
                // if user has no party
                PartiesClass Parties = new PartiesClass();
                Parties.setPartyId(finalParty.getPartyID());
                Parties.setPartyName(finalParty.getPartyName());
                Parties.setPartyStatus("Pending");
                Parties.setStartTime(finalParty.getStartTime());
                Parties.setEndTime(finalParty.getEndTime());

                List finalPartiesList = new ArrayList();
                finalPartiesList.add(Parties);
                currentUser.setParties(finalPartiesList);
                m_config.mapper.save(currentUser);
            }
            else
            {
                Log.e("---"," 4----");

                
                List<PartyConversion> mfinalParty = new ArrayList<PartyConversion>();
                List finalPartiesList = currentUser.getParties();
                Log.e("finalpartyIdstatus size", " " + finalPartiesList.size());

                for(int i = 0;i<finalPartiesList.size();i++)
                {
                    PartiesClass p = (PartiesClass)finalPartiesList.get(i);
                    PartyConversion pc = new PartyConversion();
                    pc.setPartyName(p.getPartyName());
                    pc.setPartyId(p.getPartyId());
                    pc.setStartTime(p.getStartTime());
                    pc.setEndTime(p.getEndTime());
                    pc.setPartyStatus(p.getPartyStatus());

                    mfinalParty.add(pc);
                    Log.e("PC Data",pc.getStartTime() +"   " +pc.getConvertedStartTime() + "   " +pc.getEndTime() +"   " +pc.getConvertedEndTime());
                }


            }



            super.onPostExecute(aVoid);
        }
    }




}
