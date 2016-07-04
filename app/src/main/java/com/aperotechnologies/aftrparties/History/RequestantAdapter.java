package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hasai on 14/06/16.
 */
public class RequestantAdapter extends BaseAdapter {

    private List<GateCrashersClass> GateCrasherList;
    Context cont;
    Configuration_Parameter m_config;
    PartyParceableData partyy;
    String message = " ";

    public RequestantAdapter(Context cont, List<GateCrashersClass> GateCrasherList, PartyParceableData partyy)
    {
        this.cont = cont;
        this.GateCrasherList = GateCrasherList;
        this.partyy = partyy;
//        this.PartyID = PartyID;
//        this.PartyName = PartyName;
//        this.PartyStartTime = PartyStartTime;
//        this.PartyEndTime = PartyEndTime;
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
        return GateCrasherList.size();
    }

    @Override
    public Object getItem(int position) {
        return GateCrasherList.get(position);
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
            convertView = inflater.inflate(R.layout.adapter_requestant, parent, false);
            holder.txtGCNameId =  (TextView) convertView.findViewById(R.id.gcnameid);
            holder.txtGCRequestStatus =  (TextView) convertView.findViewById(R.id.gcreqstatus);
            holder.btnfbProfile =  (Button) convertView.findViewById(R.id.fbprofile);
            holder.btnliProfile =  (Button) convertView.findViewById(R.id.liprofile);
            holder.btn1to1Chat =  (Button) convertView.findViewById(R.id.chat);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final GateCrashersClass GateCrashers =  (GateCrashersClass) GateCrasherList.get(position);
        Log.e("", " " + GateCrashers.getGatecrasherid());
        Log.e("", " " + GateCrashers.getGcrequeststatus());

        holder.txtGCNameId.setText(GateCrashers.getGatecrasherid());
        holder.txtGCRequestStatus.setText(GateCrashers.getGcrequeststatus());



        final String GateCrasherID = GateCrashers.getGatecrasherid();

        holder.txtGCRequestStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView t = (TextView) v;

                if(t.getText().toString().trim().equals("Pending") || t.getText().toString().trim().equals("Declined"))
                {
                    Long currentApprovalTime = System.currentTimeMillis();
                    Boolean allowStatus  = getActivePartyStatus(GateCrasherID, currentApprovalTime);
                    Log.e("allowStatus"," "+allowStatus);

                    //check for Active Party of GC
                    if(allowStatus.booleanValue() == true){
                        Boolean paidStatus = getPaidUser(GateCrasherID, currentApprovalTime);
                        Log.e("paidStatus"," "+paidStatus);
                        //check for Paid Status of GC
                        if(paidStatus.booleanValue() == true){
                            //paid user
                            setPaidUserApproval(currentApprovalTime, GateCrasherID, partyy, "Approved", cont, t);
                        }else{
                            //unpaid user
                            setUnPaidUserApproval(currentApprovalTime, GateCrasherID, partyy, "Approved", cont, t);
                        }
                    }else{
                        Boolean paidStatus = getPaidUser(GateCrasherID, currentApprovalTime);
                        Log.e("paidStatus"," "+paidStatus);
                        //check for Paid Status of GC
                        if(paidStatus.booleanValue() == true){
                            //paid user
                            GenerikFunctions.showToast(cont, "You cannot approved the request as " +message);
                        }else{
                            //unpaid user
                            GenerikFunctions.showToast(cont, "You cannot approved the request as " +message);                        }



                    }



                }

            }
        });

        holder.btnfbProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTable user = getUser(GateCrasherID);
                String FBID = GateCrasherID;
                String fbUrl = "https://www.facebook.com/"+FBID;
                Log.e("fbUrl"," "+fbUrl);
//                Intent i = new Intent(cont,FBProfileWebView.class);
//                i.putExtra("fbUrl",fbUrl);
//                cont.startActivity(i);

                try
                {

                    String accessToken= AccessToken.getCurrentAccessToken().toString();
                    Log.e("Token","  aa  "+accessToken);


                    int versionCode = cont.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850)
                    {
                        Log.e("11","  aa  ");
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + fbUrl);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    else
                    {
                        Log.e("22","  aa  ");
                        Uri uri = Uri.parse("fb://page/"+FBID);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
                 catch (PackageManager.NameNotFoundException e) {
                     Log.e("33","  aa  ");
                     e.printStackTrace();
                     cont.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
                }




            }
        });


        holder.btnliProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("finalGateCrashers.getGateCrasherID()"," "+GateCrasherID);




                UserTable user = getUser(GateCrasherID);

                String LIID = user.getLinkedInID();//"gWXSo4n1ei";//
                Log.e("targetID"," "+LIID);

                if(LIID.equals(null) || LIID.equals("") || LIID.equals("N/A")){
                    return;
                }

                DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
                // Open the target LinkedIn member's profile
                deepLinkHelper.openOtherProfile((Activity)cont, LIID, new DeepLinkListener()
                {
                    @Override
                    public void onDeepLinkSuccess()
                    {
                        // Successfully sent user to LinkedIn app
                        Log.e("Success","Yess");
                    }

                    @Override
                    public void onDeepLinkError(LIDeepLinkError error)
                    {
                        // Error sending user to LinkedIn app

                        Log.e("error","Yess ");
                        Log.e("---- ",""+ error.toString());
                    }
                });
            }
        });


        holder.btn1to1Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTable user = getUser(GateCrasherID);
                String QBID = user.getQuickBloxID();

                QBChatDialogCreation.createPrivateChat(Integer.valueOf(QBID), cont);
            }
        });

        return convertView;
    }

    private void setPaidUserApproval(Long currentApprovalTime, String gateCrasherID, PartyParceableData partyy, String status, Context cont, TextView t)
    {

        String StartBlockTime = String.valueOf(currentApprovalTime);
        String EndBlockTime = "";
        if((Long.parseLong(StartBlockTime) + ConstsCore.hourVal) > Long.parseLong(partyy.getEndTime()))
        {
            EndBlockTime = partyy.getEndTime();
        }
        else
        {
            EndBlockTime = String.valueOf(Long.parseLong(partyy.getStartTime()) +  ConstsCore.hourVal);
        }

        Log.e("EndblockTime"," "+StartBlockTime+" "+EndBlockTime);

        new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t).execute();


        //
    }

    private void setUnPaidUserApproval(Long currentApprovalTime, String gateCrasherID, PartyParceableData partyy, String status, Context cont, TextView t)
    {
        String StartBlockTime = String.valueOf(currentApprovalTime);
        String EndBlockTime = String.valueOf(Long.parseLong(partyy.getStartTime()) +  ConstsCore.weekVal);


        Log.e("StartBlockTime"," "+StartBlockTime +" "+EndBlockTime);


        new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t).execute();
    }


    @Override
    public boolean isEmpty() {
        return false;
    }


    static class ViewHolder
    {

        private TextView txtGCNameId;
        private TextView txtGCRequestStatus;
        private Button btnfbProfile;
        private Button btnliProfile;
        private Button btn1to1Chat;


    }


    private UserTable getUser(String GCNameId){
        UserTable user = m_config.mapper.load(UserTable.class, GCNameId);
        Log.e("user"," "+user);
        return user;
    }

    private boolean getPaidUser(String GCID, Long currentApprovalTime)
    {

        Boolean val = false;

        try
        {
            UserTable user = m_config.mapper.load(UserTable.class, GCID);
            List<PaidGCClass> PaidGC = new ArrayList<>();
            PaidGC = user.getPaidgc();

            if(PaidGC == null)
            {
                //UnPaidUser
                message = user.getName() +" is unpaid user";
                val = false;

            }
            else {
                //PaidUser
                if (PaidGC.get(0).getPaidstatus().equals("Yes")) {

                    String SubscriptionTime = PaidGC.get(0).getSubscriptiondate();
                    if (currentApprovalTime > Long.parseLong(SubscriptionTime)) {
                        //GenerikFunctions.showToast(cont, "Your subscription has been expired");
                        message = user.getName() +" Your subscription has been expired";
                        //Subscription time expired
                        val = false;


                    } else {
                        //Within subscription Time

                        val = true;
                    }
                } else {
                    message = user.getName() +" is unpaid user";
                    val = false;
                }
            }
        }
        catch(Exception e)
        {
            val = false;

        }


        finally {
            return val;
        }

    }

    private Boolean getActivePartyStatus(String GCID, Long currentApprovalTime)
    {

        Boolean allowStatus = false;
        try
        {
            UserTable user = m_config.mapper.load(UserTable.class, GCID);
            List<ActivePartyClass> ActiveParty = new ArrayList<>();
            ActiveParty = user.getActiveparty();
            Log.e("ActiveParty "," "+ActiveParty);

            if(ActiveParty == null)
            {
                allowStatus = true;
            }
            else
            {
                Log.e("currentApprovalTime"," "+currentApprovalTime+" EndBlockTime "+ActiveParty.get(0).getEndblocktime());
                Log.e("currentApprovalDay"," "+new Date(currentApprovalTime)+" EndBlockTime "+new Date(Long.parseLong(ActiveParty.get(0).getEndblocktime())));

                if(currentApprovalTime > Long.parseLong(ActiveParty.get(0).getEndblocktime())){
                    //if currentTime is greater than approved party BlockEndTime
                    allowStatus = true;
                }else{
                    //if currentTime is less than approved party BlockEndTime
                    allowStatus = false;
                    message = user.getName()+" is blocked for another party";
                }

            }
        }
        catch(Exception e){
            allowStatus = false;
            message = "user is blocked for another party";
        }
        finally {
            return allowStatus;
        }

    }



}
