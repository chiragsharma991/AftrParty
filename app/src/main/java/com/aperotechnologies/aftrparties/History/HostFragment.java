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

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.util.IabHelper;
import com.aperotechnologies.aftrparties.util.IabResult;
import com.aperotechnologies.aftrparties.util.Inventory;
import com.aperotechnologies.aftrparties.util.Purchase;
import com.facebook.AccessToken;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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


        });



        return layoutView;
    }



}
