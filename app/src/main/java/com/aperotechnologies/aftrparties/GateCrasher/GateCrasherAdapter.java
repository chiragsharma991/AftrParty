package com.aperotechnologies.aftrparties.GateCrasher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hasai on 02/06/16.
 */
public class GateCrasherAdapter extends BaseAdapter
{

    //private View.OnClickListener onclick;
    private PaginatedScanList<PartyTable> result;
    Context cont;
    UserTable currentUser;
    String reqStartTime;
    Configuration_Parameter m_config;
    public List<PartyConversion> pc;

    Intent notificationIntent;
    AlarmManager alarmManager;


    //    SQLiteDatabase sdb;
    public GateCrasherAdapter(Context context, PaginatedScanList<PartyTable> result)
    {
        this.cont = context;
        this.result = result;
        //   this.reqStartTime = reqStartTime;
        m_config = Configuration_Parameter.getInstance();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDeath()
                .build());

        pc = new ArrayList<PartyConversion>();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

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
    public View getView(int position, View convertView, final ViewGroup parent)
    {
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

        final PartyTable party = result.get(position);

        if(position == 0)
        {
            UserTable user = m_config.mapper.load(UserTable.class, LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID());
            List<PartiesClass> p =  user.getParties();

            if(p == null || p.size() == 0)
            {
                Log.e("Inside if","Yes   aa");
            }
            else
            {
                Log.e("Inside else","Yes  " +p.size() +"   aa");

                //loop for all parties data of user(from Usertable)
                for (int i = p.size() - 1; i >= 0; i--)
                {
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
                    if (sameDay == true)
                    {
                        pc.add(pconv);
                        Log.e("pc---- " + " " + pconv.getPartyname() + " " + pconv.getPartystatus(), "aa");
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


        for(int i=0;i < pc.size();i++)
        {
            if(party.getPartyID().equals(pc.get(i).getPartyid()))
            {
                //set status of party based on PartyConversion array
                holder.btn_Request.setText(pc.get(i).getPartystatus());
            }
        }


        final ViewHolder finalHolder = holder;
        holder.btn_Request.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Button b = (Button) v;
                //Log.e("Click Of Button",b.getText()  + "   aa");

                if(b.getText().toString().trim().equals("Request"))
                {

                    Log.e("PartyName  " , finalHolder.partyName.getText() + " Check Conditions here  aa"+"  "+party.getPartyName());
                    Long currentReqTime = System.currentTimeMillis();
                    sendGCReqtoHost(currentReqTime, party, pc, b, cont);


                }
                else
                {
                    Log.e("Dont Allow request For  " , finalHolder.partyName.getText() + " aa");

                }

            }


        });


        convertView.setTag(party.getPartyID());
        return convertView;
    }




    @Override
    public boolean isEmpty()
    {
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

    public  void setLocalNotification(int position)
    {
        Long currentTimeMillis = System.currentTimeMillis();
        Log.e("Current Time Millis",currentTimeMillis + "");
        PartyTable party = result.get(position);
        Log.e("=====","======");
        Log.e("Party Name",party.getPartyName());
        Log.e("Party Host",party.getHostName());
        Log.e("Party end time",party.getEndTime() +"");

        long timeDifference= Long.parseLong(party.getEndTime().trim())  - currentTimeMillis;

        notificationIntent.putExtra("PartyName",party.getPartyName());
        notificationIntent.putExtra("HostName",party.getHostName());

        PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + (5000))

                     /*Long.parseLong(party.getEndTime())*/, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + (5000)) /*Long.parseLong(party.getEndTime())*/, broadcast);

    }







    /*public String getStatus(PartyTable party)
    {
        String allowStatus = "Yes";

        //Fetchnig New status ????

        for(int i=0;i<pc.size();i++)
        {
            if((Long.parseLong(party.getStartTime()) >= Long.parseLong(pc.get(i).getEndTime()))  ||
                    (Long.parseLong(party.getEndTime()) <=  Long.parseLong(pc.get(i).getStartTime())))
            {
                //allow request
                allowStatus = "Yes";
            }
            else
            {
                //Check status
                if(pc.get(i).getPartyStatus().equals("Approved") || pc.get(i).getPartyStatus().equals("Created") ||  pc.get(i).getPartyStatus().equals("Pending"))
                    {
                        //dont allow request
                        allowStatus = "No";
                        break;
                    }
                    else
                    {
                        //allow request
                        allowStatus = "Yes";

                    }
            }
        }

        Log.e("allowStatus"," "+allowStatus);

        return allowStatus;
    }*/

    public void sendGCReqtoHost(Long currentReqTime, PartyTable party, List<PartyConversion> pc, Button b, Context cont) {

        UserTable user = m_config.mapper.load(UserTable.class, LoginValidations.initialiseLoggedInUser(AWSPartyOperations.cont).getFB_USER_ID());
        List<PaidGCClass> PaidGC = new ArrayList<>();
        PaidGC = user.getPaidgc();
        Log.e("user.getActiveParty()", " " + user.getActiveparty());

        //check whether any party of user have been approved or not for the day
        if (user.getActiveparty() == null) {
            // if there is no active party
            Log.e("if there is no active party", "");
            new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();
        } else {
            //if there is an active party
            Log.e("if there is an active party", "");
            //check for Paid statuss
            if (PaidGC == null) {
                //UnPaidUser
                Log.e("UnPaid User","");
                // if there is any active party
                if (currentReqTime > Long.parseLong(user.getActiveparty().get(0).getEndblocktime())) {
                    //if currentTime is greater than approved party BlockEndTime
                    Log.e("if there is any active party ---- ", "if currentTime is greater than approved party BlockEndTime");
                    new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();

                } else {
                    //if currentTime is less than approved party BlockEndTime
                    Log.e("if there is any active party ---- ", "if currentTime is less than approved party BlockEndTime");
                    GenerikFunctions.showToast(cont, "You are already approved for another party");
                }


            } else {//Paid User

                Log.e("Paid User","");
                String SubscriptionTime = PaidGC.get(0).getSubscriptiondate();
                if (currentReqTime > Long.parseLong(SubscriptionTime)) {
                    GenerikFunctions.showToast(cont, "Your subscription has been expired");

                } else {
                    //Within subscription Time
                    // if there is any active party
                    if (currentReqTime > Long.parseLong(user.getActiveparty().get(0).getEndblocktime())) {
                        //if currentTime is greater than approved party BlockEndTime
                        Log.e("if there is any active party ---- ", "if currentTime is greater than approved party BlockEndTime");
                        new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();

                    } else {
                        //if currentTime is less than approved party BlockEndTime
                        Log.e("if there is any active party ---- ", "if currentTime is less than approved party BlockEndTime");
                        GenerikFunctions.showToast(cont, "You are already approved for another party");
                    }

                }


            }


        }
    }














}

