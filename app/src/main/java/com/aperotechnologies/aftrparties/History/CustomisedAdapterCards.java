package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mpatil on 29/04/16.
 */
public class CustomisedAdapterCards extends CardStackAdapter
{
    Context context;
    Button accept,decline;
    Configuration_Parameter m_config;
    String message = " ";
    String requestStatus;
    TextView txt_status;

    public CustomisedAdapterCards(Context mContext)
    {
        super(mContext);
        this.context=mContext;
        m_config = Configuration_Parameter.getInstance();
    }

    @Override
    public View getCardView(final int position, CardModel model, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.requestant_swipe_card, parent, false);
            assert convertView != null;
        }

        String imageString = model.getDescription()+"";

        ImageView img = (ImageView) convertView.findViewById(R.id.image);
        Picasso.with(context).load(imageString).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(img);
        String ids = model.getTitle();
        Log.e("IDs",ids);
        String[] id = ids.split("\\*");
        final String fbid ,liid,qbid;
        fbid = id[0];
        liid = id[1];
        qbid = id[2];
        requestStatus = id[3];
        Log.e("fbid  liid  qbid  requestStatus",fbid + "    " + liid  +"    " + qbid + "      " + requestStatus);


        decline = (Button) convertView.findViewById(R.id.decline);
        accept = (Button) convertView.findViewById(R.id.accept);
        ((TextView) convertView.findViewById(R.id.title)).setText(fbid);
        txt_status = (TextView) convertView.findViewById(R.id.status);
        txt_status.setText(requestStatus);
        final ImageButton imageButton,imageButton2,imageButton3;
        imageButton = (ImageButton) convertView.findViewById(R.id.image_fb);
        imageButton2 = (ImageButton) convertView.findViewById(R.id.image_li);
        imageButton3 = (ImageButton) convertView.findViewById(R.id.image_oto);

        if(requestStatus.equals("Approved") || requestStatus.equals("Declined"))
        {
            accept.setEnabled(false);
            decline.setEnabled(false);
        }
        else
        {
            accept.setEnabled(true);
            decline.setEnabled(true);
        }

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              //  Log.e("Clicked On FB Profile  " , position + "    fbid  " +fbid);


                String fbUrl = "https://www.facebook.com/"+fbid;
                Log.e("fbUrl"," "+fbUrl);

                try
                {
                    String accessToken= AccessToken.getCurrentAccessToken().toString();
                    Log.e("Token","  aa  "+accessToken);

                    int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850)
                    {
                        Log.e("11","  aa  ");
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + fbUrl);
                        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    else
                    {
                        Log.e("22","  aa  ");
                        Uri uri = Uri.parse("fb://page/"+fbid);
                        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    Log.e("33","  aa  ");
                    e.printStackTrace();
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
                }

            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                if(liid.equals(null) || liid.equals("") || liid.equals("N/A"))
//                {
//                    return;
//                }

                DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
                // Open the target LinkedIn member's profile
                deepLinkHelper.openOtherProfile((Activity)context, liid, new DeepLinkListener()
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

        imageButton3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GenerikFunctions.sDialog(context, "Creating 1-1 Chat...");
                QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid), context);
            }
        });


        accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                GenerikFunctions.sDialog(context,"Approving Request");


                accept.setEnabled(false);
                decline.setEnabled(false);

                Long currentApprovalTime = System.currentTimeMillis();
                Boolean allowStatus = getActivePartyStatus(fbid, currentApprovalTime);
                Log.e("allowStatus", " " + allowStatus);

                //check for Active Party of GC
                if (allowStatus.booleanValue() == true)
                {
                    Boolean paidStatus = getPaidUser(fbid, currentApprovalTime);
                    Log.e("paidStatus", " " + paidStatus);
                    //check for Paid Status of GC
                    if (paidStatus.booleanValue() == true)
                    {
                        //paid user
                        setPaidUserApproval(currentApprovalTime, fbid, RequestantCardsActivity.partyy, "Approved", context, txt_status);
                    } else
                    {
                        //unpaid user
                        setUnPaidUserApproval(currentApprovalTime, fbid, RequestantCardsActivity.partyy, "Approved", context, txt_status);
                    }
                }
                else
                {
                    Boolean paidStatus = getPaidUser(fbid, currentApprovalTime);
                    Log.e("paidStatus", " " + paidStatus);
                    //check for Paid Status of GC
                    if (paidStatus.booleanValue() == true)
                    {
                        //paid user
                        GenerikFunctions.showToast(context, "You cannot approve the request as " + message);
                        GenerikFunctions.hDialog();

                    }
                    else
                    {
                        //unpaid user
                        GenerikFunctions.showToast(context, "You cannot approve the request as " + message);
                        GenerikFunctions.hDialog();
                    }
                }
            }
        });


        decline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GenerikFunctions.sDialog(context,"Declining Request");

                accept.setEnabled(false);
                decline.setEnabled(false);


                new AWSPartyOperations.updateGCinPartyTable(fbid, RequestantCardsActivity.partyy.getPartyId(),  "Declined", context, txt_status).execute();
            }
        });


        model.setOnCardDimissedListener(new CardModel.OnCardDimissedListener()
        {
            @Override
            public void onLike()
            {
               // Toast.makeText(context,"I liked it  " + position,Toast.LENGTH_LONG).show();
                Log.i("Swipeable Card", "I liked it");
                if(position==0)
                {
                    Thread timer = new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                sleep(2000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    };
                    timer.start();


                    RequestantCardsActivity.pd.setVisibility(View.VISIBLE);
                    RequestantCardsActivity.refillAdapter();
                }
                 // notifyDataSetChanged();
            }

            @Override
            public void onDislike()
            {
                //Toast.makeText(context,"I did not liked it   " +position,Toast.LENGTH_LONG).show();
                Log.i("Swipeable Card", "I did not liked it");
                if(position==0)
                {

                    Thread timer = new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                sleep(2000);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    };
                    timer.start();

                    RequestantCardsActivity.pd.setVisibility(View.VISIBLE);
                    RequestantCardsActivity.refillAdapter();
                }
                //notifyDataSetChanged();
            }
        });


        return convertView;
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


}



/* public final class SimpleCardStackAdapter extends CardStackAdapter {

    public SimpleCardStackAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
            assert convertView != null;
        }

        ((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
        ((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
        ((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());

        return convertView;
    }
}*/