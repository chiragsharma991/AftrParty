package com.aperotechnologies.aftrparties.GateCrasher;

import android.content.Context;
import android.database.DataSetObserver;
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
public class GateCrasherAdapterOverlapTime extends BaseAdapter
{

    //private View.OnClickListener onclick;
    private PaginatedScanList<PartyTable> result;
    Context cont;
    UserTable currentUser;
    String reqStartTime;
    Configuration_Parameter m_config;
    public List<PartyConversion> pc;


    //    SQLiteDatabase sdb;
    public GateCrasherAdapterOverlapTime(Context context, PaginatedScanList<PartyTable> result)
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
                    //  Log.e("pconv aa" ,pconv.getConvertedStartTime().toString() +"  aa");
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


        for(int i=0;i<pc.size();i++)
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
                    //Again Request For updated History Party Status???
                    Log.e("PartyName  " , finalHolder.partyName.getText() + " Check Conditions here  aa"+"  "+party.getPartyName());
                    String partyReqallowStatus = getStatus(party);
                    Log.e("partyReqallowSrarus"," " +partyReqallowStatus);
                    if(partyReqallowStatus.equals("Yes"))
                    {

                        UserTable user = m_config.mapper.load(UserTable.class, LoginValidations.initialiseLoggedInUser(AWSPartyOperations.cont).getFB_USER_ID());
                        //Allow for party request
                        new AWSPartyOperations.addPartiestoUserTable(user, party, cont, "Pending","GateCrasherReq", pc, b).execute();

                    }
                    else
                    {
                        //Dont Allow for party request
                        //Log.e("Don't allow req","");

                        GenerikFunctions.showToast(cont, "Cannot send Request , already created party or has send req to party");
                    }
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



    public String getStatus(PartyTable party)
    {
        String allowStatus = "Yes";

        //Fetchnig New status ????

        for(int i=0;i<pc.size();i++)
        {
            if((Long.parseLong(party.getStartTime()) >= Long.parseLong(pc.get(i).getEndtime()))  ||
                    (Long.parseLong(party.getEndTime()) <=  Long.parseLong(pc.get(i).getStarttime())))
            {
                //allow request
                allowStatus = "Yes";
            }
            else
            {
                //Check status
                if(pc.get(i).getPartystatus().equals("Approved") || pc.get(i).getPartystatus().equals("Created") ||  pc.get(i).getPartystatus().equals("Pending"))
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
    }

}

