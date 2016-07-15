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

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
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
 * Created by mpatil on 13/07/16.
 */
public class HostCardAdapter extends CardStackAdapter
{

    Context context;
    Configuration_Parameter m_config;

    public HostCardAdapter(Context mContext)
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
            convertView = inflater.inflate(R.layout.host_swipe_card, parent, false);
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


       ((TextView) convertView.findViewById(R.id.host_name)).setText(fbid);
        final ImageButton imageButton,imageButton2,imageButton3;
        imageButton = (ImageButton) convertView.findViewById(R.id.host_fb);
        imageButton2 = (ImageButton) convertView.findViewById(R.id.host_li);
        imageButton3 = (ImageButton) convertView.findViewById(R.id.host_oto);

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
                QBChatDialogCreation.createPrivateChat(Integer.valueOf(qbid), context);
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


                    HostDetailsCardsActivity.refillAdapter();
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

                    HostDetailsCardsActivity.refillAdapter();

                }
                //notifyDataSetChanged();
            }
        });

        return convertView;
    }

}
