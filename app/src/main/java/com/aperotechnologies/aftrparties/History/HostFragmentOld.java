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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
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


public class HostFragmentOld extends Fragment {
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
    private IabHelper mHostChatHelper;

    Configuration_Parameter m_config;

    public HostFragmentOld()
    {
    }

    @SuppressLint("ValidFragment")
    public HostFragmentOld(Context cont, int position, String fbid, String liid, String qbid, String img_url){
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


        Picasso.with(cont).load(img_url).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(image);
        Log.e("in Host Fragment","11");
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
                        Log.e("11","  aa  ");
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + fbUrl);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                    else
                    {
                        Log.e("22","  aa  ");
                        Uri uri = Uri.parse("fb://page/"+fbid);
                        cont.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    Log.e("33","  aa  ");
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
//                QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid), cont);



                mHostChatHelper = new IabHelper(cont, ConstsCore.base64EncodedPublicKey);

                mHostChatHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
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
                                            mHostChatHelper.launchPurchaseFlow(
                                                    (Activity) cont, ConstsCore.ITEM_PRIVATECHAT_SKU, ConstsCore.RequestCode,
                                                    mHostChatPurchaseFinishedListener, "mypurchasetoken");

                                        } catch (IabHelper.IabAsyncInProgressException e) {
                                            e.printStackTrace();
                                        }




                                    }
                                });
                alertDialogBuilder.show();



            }


        });



        return layoutView;
    }


    IabHelper.OnIabPurchaseFinishedListener mHostChatPurchaseFinishedListener
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
            mHostChatHelper.queryInventoryAsync(mHostChatReceivedInventoryListener);
            Log.e("consumeItem ","try");
            Toast.makeText(cont,"consumeitem try", Toast.LENGTH_SHORT).show();
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e("consumeItem ","catch");
            Toast.makeText(cont,"consumeitem catch", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mHostChatReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure()) {
                // Handle failure
                Log.e("QueryInventory ","failed");
                Toast.makeText(cont,"QueryInventory failed", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    mHostChatHelper.consumeAsync(inv.getPurchase(ConstsCore.ITEM_PRIVATECHAT_SKU),
                            mHostChatConsumeFinishedListener);

                    Toast.makeText(cont,"QueryInventory  consumeAsync", Toast.LENGTH_SHORT).show();

                } catch (IabHelper.IabAsyncInProgressException e) {

                    Toast.makeText(cont,"QueryInventory  catch", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }


    };


    IabHelper.OnConsumeFinishedListener mHostChatConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {
                    // if we were disposed of in the meantime, quit.
                    if (mHostChatHelper == null) return;

                    if (result.isSuccess()) {

                        Toast.makeText(cont,"consume success", Toast.LENGTH_SHORT).show();

                        GenerikFunctions.sDialog(cont, "Creating 1-1 Chat...");
                        QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid) ,cont);



                    } else {
                        // handle error

                        Toast.makeText(cont,"consume failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };





}
