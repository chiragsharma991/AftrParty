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

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.GateCrashersClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;

import java.util.List;

/**
 * Created by hasai on 14/06/16.
 */
public class RequestantAdapterforOverlapping extends BaseAdapter {

    private List<GateCrashersClass> GateCrasherList;
    Context cont;
    Configuration_Parameter m_config;
    String PartyID;

    public RequestantAdapterforOverlapping(Context cont, List<GateCrashersClass> GateCrasherList, String PartyID)
    {
        this.cont = cont;
        this.GateCrasherList = GateCrasherList;
        this.PartyID = PartyID;
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
                    new AWSPartyOperations.updateGCinPartyTable(GateCrasherID, PartyID, "Approved", cont, t).execute();
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



}
