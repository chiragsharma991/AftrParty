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

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartiesClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.SearchedParties;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by hasai on 02/06/16.
 */
public class GateCrasherAdapter extends BaseAdapter
{

    //private View.OnClickListener onclick;
    private JSONArray result;//PaginatedScanList<PartyTable> result;
    Context cont;
    UserTable currentUser;
    String reqStartTime;
    Configuration_Parameter m_config;
    public List<PartyConversion> pc;

    Intent notificationIntent;
    AlarmManager alarmManager;


    //    SQLiteDatabase sdb;
    public GateCrasherAdapter(Context context, JSONArray result)//PaginatedScanList<PartyTable> result)
    {
        this.cont = context;
        this.result = result;
        //   this.reqStartTime = reqStartTime;
        m_config = Configuration_Parameter.getInstance();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().build());

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
        return result.length();//size();
    }

    @Override
    public Object getItem(int position) {
        try {
            return result.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
        ViewHolder holder;
        View participentView = convertView;

        if (participentView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            participentView = inflater.inflate(R.layout.adapter_gatecrasher, parent, false);
            holder.partyName =  (TextView) participentView.findViewById(R.id.partyname);
            holder.partydesc =  (TextView) participentView.findViewById(R.id.desc);
            holder.psTime =  (TextView) participentView.findViewById(R.id.sTime);
            holder.peTime =  (TextView) participentView.findViewById(R.id.eTime);
            holder.pByob =  (TextView) participentView.findViewById(R.id.byob);
            holder.btn_Request = (Button) participentView.findViewById(R.id.btn_Request);

            participentView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) participentView.getTag();
        }

        SearchedParties party = null;
        Gson gson = new Gson();

        try {

            party = gson.fromJson(result.get(position).toString(), SearchedParties.class);
            Log.e(""," "+party.getPartyName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        String GCFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        if(position == 0)
        {
            UserTable user = m_config.mapper.load(UserTable.class, GCFBID);
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
                    Calendar currentDay = Calendar.getInstance();
                    cal1.setTime(pconv.getConvertedstarttime());
                    currentDay.setTime(new Date());


                    boolean sameDay = cal1.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.MONTH) == currentDay.get(Calendar.MONTH);

                    Log.e("sameDay   " + i, " " + sameDay);
                    Log.e("currrr"," "+currentDay.getTimeInMillis()+" "+pconv.getEndtime() +" "+(currentDay.getTimeInMillis() <= Long.parseLong(pconv.getEndtime()))+" "+currentDay.getTime());
                    //stores parties data of user in PartyConversion array for current date
                    //if (sameDay == true)

                    if(currentDay.getTimeInMillis() <= Long.parseLong(pconv.getEndtime()))
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

        String StartTime = party.getStartTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(StartTime));
        String partystrDate = Validations.getMonthNo(calendar.get(Calendar.MONTH)) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + Validations.showTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        String EndDate = party.getEndTime();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(Long.parseLong(EndDate));
        String partyendDate = Validations.getMonthNo(calendar1.get(Calendar.MONTH)) + "/" + calendar1.get(Calendar.DAY_OF_MONTH) + "/" + calendar1.get(Calendar.YEAR) + " " + Validations.showTime(calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));


        //Log.e("Filtered Array Size",m_config.pc.size() + "   aa");
        holder.btn_Request.setText("Request");
        holder.partyName.setText(party.getPartyName());
        holder.partydesc.setText(party.getPartyDescription().trim());
        holder.psTime.setText("ST : "+partystrDate);
        holder.peTime.setText("ET : "+partyendDate);
        holder.pByob.setText("BYOB : "+party.getBYOB());


        //pc is array for todays party
        for(int i=0;i < pc.size();i++)
        {
            //check whether response party(party) and todays party(pc) are same
            if(party.getPartyID().equals(pc.get(i).getPartyid()))
            {
                //set status of party based on PartyConversion array
                holder.btn_Request.setText(pc.get(i).getPartystatus());
            }
        }


        final ViewHolder finalHolder = holder;
        final SearchedParties finalParty = party;
        holder.btn_Request.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                Button b = (Button) v;
                //Log.e("Click Of Button",b.getText()  + "   aa");

                if(b.getText().toString().trim().equals("Request"))
                {
                    GenerikFunctions.sDialog(cont, "Sending Request for Party");
                    Log.e("PartyName  " , finalHolder.partyName.getText() + " Check Conditions here  aa"+"  "+ finalParty.getPartyName());
                    Long currentReqTime = System.currentTimeMillis();
                    String PartyId = finalParty.getPartyID();


                    try {

                        PartyTable partytable = m_config.mapper.load(PartyTable.class, PartyId);
                        //Log.e("party ID "," "+partytable.getPartyID());
                        sendGCReqtoHost(currentReqTime, partytable, pc, b, cont);
                    }catch (Exception e){
                        e.printStackTrace();
                        GenerikFunctions.showToast(cont, "Unable to send request, Please try again after some time.");
                        GenerikFunctions.hDialog();
                    }

                }
                else
                {
                    String stateparty = b.getText().toString().trim();
                    if(stateparty.equals("Created")){
                        GenerikFunctions.showToast(cont, "You cannot send request to this party.");
                    }else  if(stateparty.equals("Pending")){
                        GenerikFunctions.showToast(cont, "You have already send request to this party.");
                    }else  if(stateparty.equals("Declined")){
                        GenerikFunctions.showToast(cont, "Your request for this party have been declined.");
                    }else  if(stateparty.equals("Approved")){
                        GenerikFunctions.showToast(cont, "Your request for this party have been approved.");
                    }else  if(stateparty.equals("Cancelled")){
                        GenerikFunctions.showToast(cont, "Your request for this party have been cancelled.");
                    }

                    Log.e("Dont Allow request For  " , finalHolder.partyName.getText() + " aa");
                    GenerikFunctions.hDialog();
                }

            }


        });


        //convertView.setTag(party.getPartyID());
        return participentView;
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

        private TextView partyName, partydesc, psTime, peTime, pByob;
        private Button btn_Request;

    }

    public  void setLocalNotification(int position)
    {
        Long currentTimeMillis = System.currentTimeMillis();
        Log.e("Current Time Millis",currentTimeMillis + "");
        PartyTable party = null;
        try {
            party = (PartyTable) result.get(position);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        String FBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        UserTable user = m_config.mapper.load(UserTable.class, FBID);
        List<PaidGCClass> PaidGC = new ArrayList<>();
        PaidGC = user.getPaidgc();

        //Log.e("user.getActiveParty()", " " + user.getActiveparty()+" "+(user.getActiveparty() == null));

        //check whether any party of user have been approved or not for the day
        if (user.getActiveparty() == null || user.getActiveparty().size() == 0)
        {
            // if there is no active party
            Log.e("if there is no active party", "");
            new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();
        }
        else
        {
            /**/
            String Endblocktime = user.getActiveparty().get(0).getEndblocktime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(Long.parseLong(Endblocktime));
            String PartyEndBlockTime = Validations.getMonthNo(calendar1.get(Calendar.MONTH)) + "/" + calendar1.get(Calendar.DAY_OF_MONTH) + "/" + calendar1.get(Calendar.YEAR) + " " + Validations.showTime(calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));
            /**/
            //if there is an active party
            Log.e("if there is an active party", "");
            //check for Paid status
            if (PaidGC == null)
            {
                //UnPaidUser
                Log.e("UnPaid User","");
                // if there is any active party
                if (currentReqTime > Long.parseLong(Endblocktime)) {
                    //if currentTime is greater than approved party BlockEndTime
                    Log.e("if there is any active party ---- ", "if currentTime is greater than approved party BlockEndTime");
                    new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();

                } else {
                    //if currentTime is less than approved party BlockEndTime
                    Log.e("if there is any active party ---- ", "if currentTime is less than approved party BlockEndTime");
                    GenerikFunctions.showToast(cont, "You are already approved for another party. You have been blocked till "+PartyEndBlockTime);
                    GenerikFunctions.hDialog();
                }


            } else {//Paid User

                Log.e("Paid User","");
                String SubscriptionTime = PaidGC.get(0).getSubscriptiondate();
                if (currentReqTime > Long.parseLong(SubscriptionTime)) {
                    GenerikFunctions.showToast(cont, "Your subscription has been expired");
                    GenerikFunctions.hDialog();

                } else {
                    //Within subscription Time
                    // if there is any active party
                    if (currentReqTime > Long.parseLong(Endblocktime)) {
                        //if currentTime is greater than approved party BlockEndTime
                        Log.e("if there is any active party ---- ", "if currentTime is greater than approved party BlockEndTime");
                        new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending", "GateCrasherReq", pc, b).execute();

                    } else {
                        //if currentTime is less than approved party BlockEndTime
                        Log.e("if there is any active party ---- ", "if currentTime is less than approved party BlockEndTime");
                        GenerikFunctions.showToast(cont, "You are already approved for another party. You have been blocked till "+PartyEndBlockTime);
                        GenerikFunctions.hDialog();
                    }

                }


            }


        }
    }














}

