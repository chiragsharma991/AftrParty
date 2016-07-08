package com.aperotechnologies.aftrparties.LocalNotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.TransparentActivity;

/**
 * Created by mpatil on 27/06/16.
 */
public class RatingsAlarmReceiver extends BroadcastReceiver
{
    Configuration_Parameter m_config;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        m_config = Configuration_Parameter.getInstance();

        Bundle bundle = intent.getExtras();

        String from = bundle.getString("from");


        // receiver for PartyRetention
        if(from.equals("PartyRetention"))
        {

            String PartyName = bundle.getString("PartyName");
            String PartyId =  bundle.getString("PartyId");
            String DialogId =  bundle.getString("DialogId");
            Log.e("RatingsAlarmReceiver "," PartyName "+" "+PartyId+" "+DialogId);
            Intent i = new Intent(context, TransparentActivity.class);
            i.putExtra("PartyName", PartyName);
            i.putExtra("PartyId", PartyId);
            i.putExtra("PartyName", "PartyName");
            i.putExtra("from", from);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }




}
