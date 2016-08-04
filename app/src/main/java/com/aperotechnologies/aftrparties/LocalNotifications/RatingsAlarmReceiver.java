package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.BaseLifeCycleCallbacks;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.PNotifications.BackgroundFunctionCall;
import com.aperotechnologies.aftrparties.PNotifications.ForegroundFunctionCall;
import com.aperotechnologies.aftrparties.PNotifications.GCMIntentService;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.linkedin.platform.LISessionManager;

import java.util.Random;

/**
 * Created by mpatil on 27/06/16.
 */
public class RatingsAlarmReceiver extends BroadcastReceiver{
    Configuration_Parameter m_config;
    Context cont;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        m_config = Configuration_Parameter.getInstance();
        cont = context;

        Bundle extras = intent.getExtras();
        String from = extras.getString("from");


        Boolean isApplicationForeGround = BaseLifeCycleCallbacks.applicationStatus();

        String PartyID = extras.getString("PartyId");
        String PartyName = extras.getString("PartyName");
        String DialogId = extras.getString("DialogId");
        Log.e("PartyName", " " + PartyName + " PartyID" + " " + PartyID );

        //Notification for PartyRetention
        if (isApplicationForeGround == true)
        {

            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(context).getSession() == null || LISessionManager.getInstance(context).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(cont, Welcome.class);
                i.putExtra("DialogId", DialogId);
                i.putExtra("PartyId",PartyID);
                i.putExtra("PartyName", PartyName);
                i.putExtra("from", "PartyRetention");
                CreateNotification(i, "hello", cont);
            }
            else
            {
                Intent i = new Intent(context, TransparentActivity.class);
                i.putExtra("DialogId", DialogId);
                i.putExtra("PartyId",PartyID);
                i.putExtra("PartyName", PartyName);
                i.putExtra("from", "PartyRetention");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }


        }
        else
        {
            if (LoginValidations.getFBAccessToken() == null || LoginValidations.getFBAccessToken().getToken() == null || LISessionManager.getInstance(context).getSession() == null || LISessionManager.getInstance(context).getSession().getAccessToken() == null)
            {
                Intent i = new Intent(cont, Welcome.class);
                i.putExtra("DialogId", DialogId);
                i.putExtra("PartyId",PartyID);
                i.putExtra("PartyName", PartyName);
                i.putExtra("from", "PartyRetention");
                CreateNotification(i, "hello", cont);
            }
            else
            {

                Intent i = new Intent(cont, HomePageActivity.class);
                i.putExtra("DialogId", DialogId);
                i.putExtra("PartyId",PartyID);
                i.putExtra("PartyName", PartyName);
                i.putExtra("from", "PartyRetention");
                CreateNotification(i, "hello", cont);

            }

        }


    }

    public static void CreateNotification(Intent intent, String message, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random myRandom = new Random();
        int notId = myRandom.nextInt();

        NotificationCompat.Builder mBuilder = null;
        // application is in background or close
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                .setContentTitle("AftrParties")
                .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        notificationManager.notify(notId, mBuilder.build());


    }


}
