package com.aperotechnologies.aftrparties.History;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.QBSessionClass;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.util.IabHelper;
import com.aperotechnologies.aftrparties.util.IabResult;
import com.aperotechnologies.aftrparties.util.Inventory;
import com.aperotechnologies.aftrparties.util.Purchase;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mpatil on 19/07/16.
 */
public class RequestantFragmentOld extends Fragment
{
    //Overriden method onCreateView
    int position;
    ImageButton fbProfile,liProfile, oto;
    Button accept, deny;
    TextView txt_fbid, txt_status;
    ArrayList<String> status,facebookId,liId,QbId,imageArray;
    ImageView image;
    Context cont;
    String message = " ";

    Configuration_Parameter m_config;
    private IabHelper mReqChatHelper;

    public RequestantFragmentOld()
    {
    }

    @SuppressLint("ValidFragment")
    public RequestantFragmentOld(Context cont, int position, ArrayList<String> status, ArrayList<String> facebookId, ArrayList<String> liId, ArrayList<String> QbId, ArrayList<String> imageArray)
    {
        this.status = status;
        this.facebookId = facebookId;
        this.liId = liId;
        this.QbId = QbId;
        this.imageArray = imageArray;
        this.position = position;
        this.cont = cont;
        m_config = Configuration_Parameter.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        final View  layoutView = inflater.inflate(R.layout.requestant_fragment, container, false);

        txt_fbid = (TextView) layoutView.findViewById(R.id.title);
        txt_status = (TextView) layoutView.findViewById(R.id.status);

        fbProfile = (ImageButton) layoutView.findViewById(R.id.image_fb);
        liProfile = (ImageButton) layoutView.findViewById(R.id.image_li);
        oto = (ImageButton) layoutView.findViewById(R.id.image_oto);

        image = (ImageView) layoutView.findViewById(R.id.image);

        accept = (Button) layoutView.findViewById(R.id.accept);
        deny = (Button) layoutView.findViewById(R.id.decline);



        System.out.println(status);
        if(status.get(position).equals("Pending"))
        {
            accept.setVisibility(View.VISIBLE);
            deny.setVisibility(View.VISIBLE);
        }
        else
        {
            accept.setVisibility(View.GONE);
            deny.setVisibility(View.GONE);
        }

        txt_fbid.setText(facebookId.get(position));
        txt_status.setText(status.get(position));

        Picasso.with(cont).load(imageArray.get(position)).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(image);

        //For FB Profile Navigation
        fbProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String fbUrl = "https://www.facebook.com/"+facebookId.get(position);
                Log.e("fbUrl"," "+fbUrl);

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
                        Uri uri = Uri.parse("fb://page/"+facebookId.get(position));
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    Log.e("33","  aa  ");
                    e.printStackTrace();
                    cont.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
                }

            }
        });


        //LI Deep Linking
        liProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
                // Open the target LinkedIn member's profile
                deepLinkHelper.openOtherProfile((Activity)cont, liId.get(position), new DeepLinkListener()
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

        oto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                GenerikFunctions.sDialog(cont, "Creating 1-1 Chat...");
//                QBChatDialogCreation.createPrivateChat(Integer.valueOf(QbId.get(position)), cont);


/****/
                mReqChatHelper = new IabHelper(cont, ConstsCore.base64EncodedPublicKey);

                mReqChatHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {
                        if (!result.isSuccess()) {
                            Log.e("HomePageActivity", "In-app Billing setup failed: " +
                                    result);
                        } else {
                            Log.e("HomePageActivity", "In-app Billing is set up OK");
                        }
                    }
                });
                /****/



                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
                alertDialogBuilder
                        .setTitle("Pay for 1-1 chat")
                        .setMessage("Are you sure you want to pay for Party?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        })
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {


                                        try {

                                            ///10001 - is requestCode
                                            mReqChatHelper.launchPurchaseFlow(
                                                    (Activity) cont, ConstsCore.ITEM_PRIVATECHAT_SKU, ConstsCore.RequestCode,
                                                    mReqChatPurchaseFinishedListener, "mypurchasetoken");

                                        } catch (IabHelper.IabAsyncInProgressException e) {
                                            e.printStackTrace();
                                        }




                                    }
                                });
                alertDialogBuilder.show();



            }
        });

        accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                GenerikFunctions.sDialog(cont,"Approving Request");


                accept.setEnabled(false);
                deny.setEnabled(false);

                Long currentApprovalTime = Validations.getCurrentTime();//System.currentTimeMillis();
                Boolean allowStatus = getActivePartyStatus(facebookId.get(position), currentApprovalTime);
                Log.e("allowStatus", " " + allowStatus);

                //check for Active Party of GC
                if (allowStatus.booleanValue() == true)
                {
                    Boolean paidStatus = getPaidUser(facebookId.get(position), currentApprovalTime);
                    Log.e("paidStatus", " " + paidStatus);
                    //check for Paid Status of GC
                    if (paidStatus.booleanValue() == true)
                    {
                        //paid user
                        setPaidUserApproval(currentApprovalTime, facebookId.get(position), RequestantActivity.partyy, "Approved", cont, txt_status, accept, deny);
                    } else
                    {
                        //unpaid user
                        setUnPaidUserApproval(currentApprovalTime, facebookId.get(position), RequestantActivity.partyy, "Approved", cont, txt_status, accept, deny);
                    }
                }
                else
                {
                    Boolean paidStatus = getPaidUser(facebookId.get(position), currentApprovalTime);
                    Log.e("paidStatus", " " + paidStatus);
                    //check for Paid Status of GC
                    if (paidStatus.booleanValue() == true)
                    {
                        //paid user
                        GenerikFunctions.showToast(cont, "You cannot approve the request as " + message);
                        GenerikFunctions.hDialog();

                    }
                    else
                    {
                        //unpaid user
                        GenerikFunctions.showToast(cont, "You cannot approve the request as " + message);
                        GenerikFunctions.hDialog();
                    }
                }
            }
        });


        deny.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GenerikFunctions.sDialog(cont,"Declining Request");

                accept.setEnabled(false);
                deny.setEnabled(false);


                new AWSPartyOperations.updateGCinPartyTable(facebookId.get(position), RequestantActivity.partyy.getPartyId(),
                        "Declined", cont, txt_status, accept, deny).execute();
            }
        });

        return layoutView;
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

    private void setPaidUserApproval(Long currentApprovalTime, final String gateCrasherID, final PartyParceableData partyy, final String status, final Context cont, final TextView t, final Button accept, final Button deny)
    {
        final String StartBlockTime = String.valueOf(currentApprovalTime);
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

        Log.e("gateCrasherID"," "+gateCrasherID);

        Log.e("ChatService.getInstance().getCurrentUser()"," "+ ChatService.getInstance().getCurrentUser());

        //check for QBSession
        if (ChatService.getInstance().getCurrentUser() == null)
        {
            String accessToken = LoginValidations.getFBAccessToken().getToken();

            final String finalEndBlockTime = EndBlockTime;
            QBSessionClass.getInstance().getQBSession(new QBEntityCallback() {

                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("inside handler","---");
                            new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, finalEndBlockTime, status, cont, t, accept, deny).execute();

                        }
                    });
                }

                @Override
                public void onError(QBResponseException e) {

                    GenerikFunctions.hDialog();
                    GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");

                }

            }, accessToken, null, cont);

        }
        else
        {
            new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t, accept, deny).execute();

        }


    }



    private void setUnPaidUserApproval(Long currentApprovalTime, final String gateCrasherID, final PartyParceableData partyy, final String status, final Context cont, final TextView t, final Button accept, final Button deny)
    {
        final String StartBlockTime = String.valueOf(currentApprovalTime);
        final String EndBlockTime = String.valueOf(Long.parseLong(partyy.getStartTime()) +  ConstsCore.weekVal);

        Log.e("StartBlockTime"," "+StartBlockTime +" "+EndBlockTime);

        Log.e("gateCrasherID"," "+gateCrasherID);
        //new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t).execute();

        Log.e("ChatService.getInstance().getCurrentUser()"," "+ChatService.getInstance().getCurrentUser());
        //check for QBSession
        if (ChatService.getInstance().getCurrentUser() == null)
        {
            String accessToken = LoginValidations.getFBAccessToken().getToken();

            QBSessionClass.getInstance().getQBSession(new QBEntityCallback() {

                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("inside handler","---");
                            new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t, accept, deny).execute();
                        }
                    });
                }

                @Override
                public void onError(QBResponseException e) {

                    GenerikFunctions.hDialog();
                    GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");

                }

            }, accessToken, null, cont);


        }
        else
        {
            new AWSPartyOperations.addupdActiveParty(gateCrasherID, partyy, StartBlockTime, EndBlockTime, status, cont, t, accept, deny).execute();
        }
    }




    IabHelper.OnIabPurchaseFinishedListener mReqChatPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {

            if (result.isFailure())
            {
                // Handle error
                Log.e("Error purchasing:"," ---- " + result+" ");
                Toast.makeText(cont,""+result, Toast.LENGTH_SHORT).show();
                return;
            }
            else if (purchase.getSku().equals(ConstsCore.ITEM_PRIVATECHAT_SKU))
            {


                Log.e("Purchase Success"," "+purchase.getToken());

                Toast.makeText(cont,"Purchase success", Toast.LENGTH_SHORT).show();
                consumeItem();

            }

        }
    };


    private void consumeItem() {
        try {
            mReqChatHelper.queryInventoryAsync(mReqChatReceivedInventoryListener);
            Log.e("consumeItem ","try");
            Toast.makeText(cont,"consumeitem try", Toast.LENGTH_SHORT).show();
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e("consumeItem ","catch");
            Toast.makeText(cont,"consumeitem catch", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReqChatReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure()) {
                // Handle failure
                Log.e("QueryInventory ","failed");
                Toast.makeText(cont,"QueryInventory failed", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    mReqChatHelper.consumeAsync(inv.getPurchase(ConstsCore.ITEM_PRIVATECHAT_SKU),
                            mReqChatConsumeFinishedListener);

                    Toast.makeText(cont,"QueryInventory  consumeAsync", Toast.LENGTH_SHORT).show();

                } catch (IabHelper.IabAsyncInProgressException e) {

                    Toast.makeText(cont,"QueryInventory  catch", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }


    };


    IabHelper.OnConsumeFinishedListener mReqChatConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {
                    // if we were disposed of in the meantime, quit.
                    if (mReqChatHelper == null) return;

                    if (result.isSuccess()) {

                        Toast.makeText(cont,"consume success", Toast.LENGTH_SHORT).show();

                        GenerikFunctions.sDialog(cont, "Creating 1-1 Chat...");
                        QBChatDialogCreation.createPrivateChat(Integer.valueOf(QbId.get(position)), cont);
                        


                    } else {
                        // handle error

                        Toast.makeText(cont,"consume failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };



}



