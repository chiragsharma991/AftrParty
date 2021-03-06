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
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPaymentOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaymentTable;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.squareup.picasso.Picasso;


public class HostFragment extends Fragment
{
    ImageButton fbProfile,liProfile, oto;
    TextView txt_fbid;
    ImageView image;
    String message = " ";
    Context cont;
    int position;
    String fbid;
    String liid;
    String qbid;
    String img_url;
    Configuration_Parameter m_config;



    public HostFragment()
    {
    }

    @SuppressLint("ValidFragment")
    public HostFragment(Context cont, int position, String fbid, String liid, String qbid, String img_url)
    {
        this.position=position;
        this.cont=cont;
        this.fbid=fbid;
        this.liid=liid;
        this.qbid=qbid;
        this.img_url=img_url;
        m_config = Configuration_Parameter.getInstance();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        final View  layoutView = inflater.inflate(R.layout.fragment_host, container, false);

        txt_fbid = (TextView) layoutView.findViewById(R.id.title);


        fbProfile = (ImageButton) layoutView.findViewById(R.id.image_fb);
        liProfile = (ImageButton) layoutView.findViewById(R.id.image_li);
        oto = (ImageButton) layoutView.findViewById(R.id.image_oto);

        image = (ImageView) layoutView.findViewById(R.id.image);


        txt_fbid.setText(fbid);

//


        Picasso.with(cont).load(img_url).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(image);



        //For FB Profile Navigation
        fbProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                String fbUrl = "https://www.facebook.com/"+fbid;
                Log.e("fbUrl"," "+fbUrl);

                try
                {
                    String accessToken= AccessToken.getCurrentAccessToken().toString();
                    Log.e("Token","  aa  "+accessToken);

                    int versionCode = cont.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850)
                    {

                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + fbUrl);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    else
                    {

                        Uri uri = Uri.parse("fb://page/"+fbid);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
                catch (PackageManager.NameNotFoundException e)
                {

                    e.printStackTrace();
                    cont.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)));
                }

                //    layoutView.invalidate();
            }
        });


        //LI Deep Linking
        liProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }


                DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
                // Open the target LinkedIn member's profile
                deepLinkHelper.openOtherProfile((Activity)cont, liid, new DeepLinkListener()
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
                        Log.e("error ",""+ error.toString());
                    }
                });
            }
        });




        oto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                GenerikFunctions.sDialog(cont, "Loading...");
                checkforPrivateChat(cont, fbid);

//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
//                alertDialogBuilder
//                        .setTitle("Pay for 1-1 Chat.")
//                        .setMessage("Are you sure you want to pay for 1-1 Chat ?")
//                        .setCancelable(false)
//                        .setNegativeButton("No", new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int id)
//                            {
//
//                            }
//                        })
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id)
//                                    {
//
//
//                                        Log.e("HostProfileActivity.readyToPurchaseHChat"," "+HostProfileActivity.readyToPurchaseHChat+" ");
//                                        if (HostProfileActivity.readyToPurchaseHChat == true)
//                                        {
//                                            HostProfileActivity.bpHostChat.purchase((Activity) cont,ConstsCore.ITEM_PRIVATECHAT_SKU);
//                                        }
//                                        else
//                                        {
//                                            GenerikFunctions.showToast(cont,"Billing not initialized.");
//                                            return;
//                                        }
//                                    }
//
//                                });
//                alertDialogBuilder.show();

            }


        });

        return layoutView;
    }


    private void  checkforPrivateChat(Context cont, String oppFbId)
    {
        String facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();

        try
        {
            PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class,facebookid);

            //check for current user
            if(paymentTable == null)
            {
                // if current user does not exist in payment table check for opponent user in PaymentTable
                checkOppUserinPChat(cont, oppFbId, facebookid);
            }
            else
            {

                //GenerikFunctions.showToast(cont, String.valueOf(paymentTable.getPrivatechat()));
                //if current user exist in payment table check for its privatechat array
                if(paymentTable.getPrivatechat() == null || paymentTable.getPrivatechat().size() == 0)
                {
                    // check for opponent user
                    checkOppUserinPChat(cont, oppFbId, facebookid);

                }
                else
                {

                    for(int i = 0; i< paymentTable.getPrivatechat().size(); i++)
                    {

                            //if current user exist in payment table check for oppIdFbid in privatechat array
                            if(paymentTable.getPrivatechat().get(i).getFbid().equals(oppFbId))
                            {
                                //if oppIdFbid exist in  privatechat array check for subscription date
                                if(Validations.getCurrentTime() > Long.parseLong(paymentTable.getPrivatechat().get(i).getSubscriptiondate()))
                                {
                                    //check whether currentuserfbid and private chat array fbid are similar
                                    //a. if it is similar he cannot go for subscription
                                    //b. if it is non similar he can go for subscription
                                    if(paymentTable.getPrivatechat().get(i).getFbid().equals(facebookid))
                                    {
                                        //here user is not admin and subscription is expired
                                        GenerikFunctions.showToast(cont,"Your chat with this user already exist.");
                                        GenerikFunctions.hDialog();

                                        Intent intent = new Intent(cont, DialogsActivity.class);
                                        startActivity(intent);

                                        break;
                                    }
                                    else
                                    {
                                        GenerikFunctions.showToast(cont,"Your subscription for this user is expired.");
                                        //delete previous chat dialog
                                        //call for in app purchase
                                        GenerikFunctions.hDialog();
                                        inAppPurchaseHChat(cont,paymentTable.getPrivatechat().get(i).getDialogId());
                                        break;
                                    }



                                }
                                else
                                {
                                    GenerikFunctions.showToast(cont,"Your chat with this user already exist.");
                                    GenerikFunctions.hDialog();
                                    break;
                                }
                            }
                            else {
                                // if oppIdFbid does not exist in privatechat array check for opponent user in payment table
                                checkOppUserinPChat(cont, oppFbId, facebookid);
                            }

                    }



                }

            }



        }
        catch (Exception e)
        {
            GenerikFunctions.showToast(cont,"");
            GenerikFunctions.hDialog();
        }
    }

    private void checkOppUserinPChat(Context cont, String oppFbId, String facebookid)
    {
        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        try
        {
            PaymentTable opppaymentTable = m_config.mapper.load(PaymentTable.class,oppFbId);

            // check for opponent user in PaymentTable
            if(opppaymentTable == null)
            {
                //call for in app purchase
                Log.i("oppfbuser does not exist in paymentatble","");

                inAppPurchaseHChat(cont, "");
                GenerikFunctions.hDialog();


            }
            else
            {


                //if opponent user exist in payment table check for its privatechat array
                if(opppaymentTable.getPrivatechat() == null || opppaymentTable.getPrivatechat().size() == 0)
                {

                    Log.i("private chat array is blank for opp user","");
                    GenerikFunctions.hDialog();
                    inAppPurchaseHChat(cont, "");
                    //call for in app purchase

                }
                else
                {

                    for(int i = 0; i< opppaymentTable.getPrivatechat().size(); i++)
                    {
                        //if opponent user exist in payment table check for currentuserfbid in privatechat array
                        if(opppaymentTable.getPrivatechat().get(i).getFbid().equals(facebookid))
                        {
                            //if currentuserfbid exist in  privatechat array check for subscription date
                            if(Validations.getCurrentTime() > Long.parseLong(opppaymentTable.getPrivatechat().get(i).getSubscriptiondate()))
                            {
                                //check whether currentuserfbid and private chat array fbid are similar
                                //a. if it is similar he cannot go for subscription
                                //b. if it is non similar he can go for subscription
                                if(opppaymentTable.getPrivatechat().get(i).getFbid().equals(facebookid))
                                {
                                    //here user is not admin and subscription is expired
                                    GenerikFunctions.showToast(cont,"Your chat with this user already exist.");
                                    GenerikFunctions.hDialog();

                                    Intent intent = new Intent(cont, DialogsActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                                else
                                {
                                    GenerikFunctions.showToast(cont,"Your subscription for this user is expired.");
                                    //delete previous chat dialog
                                    //call for in app purchase
                                    GenerikFunctions.hDialog();
                                    inAppPurchaseHChat(cont,opppaymentTable.getPrivatechat().get(i).getDialogId());
                                    break;
                                }
                            }
                            else
                            {
                                Log.i("current user does not exist in opponent user data ","");
                                GenerikFunctions.showToast(cont,"Your chat with this user already exist.");
                                GenerikFunctions.hDialog();
                                break;
                            }
                        }
                        else{
                            //call for in app purchase
                            Log.i("Call for in app Purchase.","");
                            GenerikFunctions.hDialog();
                            inAppPurchaseHChat(cont, "");
                        }
                    }

                }

            }
        }
        catch (Exception e){

        }
    }

    private void inAppPurchaseHChat(final Context cont, final String dialogId)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont);
                alertDialogBuilder
                        .setTitle("Pay for 1-1 Chat.")
                        .setMessage("Are you sure you want to pay for 1-1 Chat ?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        })
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        Log.e("HostProfileActivity.readyToPurchaseHChat"," "+HostProfileActivity.readyToPurchaseHChat+" ");
                                        if (HostProfileActivity.readyToPurchaseHChat == true)
                                        {
                                            m_config.DialogidforHostInappPChat = dialogId;
                                            HostProfileActivity.bpHostChat.purchase((Activity) cont,ConstsCore.ITEM_PRIVATECHAT_SKU);

                                        }
                                        else
                                        {
                                            GenerikFunctions.showToast(cont,"Billing not initialized.");
                                            return;
                                        }
                                    }

                                });
                alertDialogBuilder.show();
    }


}
