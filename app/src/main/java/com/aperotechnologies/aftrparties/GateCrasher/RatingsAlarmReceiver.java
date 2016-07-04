package com.aperotechnologies.aftrparties.GateCrasher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.MyLifecycleHandler;
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
        String PartyName = bundle.getString("PartyName");
        String PartyId =  bundle.getString("PartyId");
        String DialogId =  bundle.getString("DialogId");


        Log.e("RatingsAlarmReceiver "," PartyName "+" "+PartyId+" "+DialogId);

        Intent notificationIntent = new Intent(context, RatePartyActivity.class);
        notificationIntent.putExtra("PartyName",PartyName);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(RatePartyActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        Notification notification = builder.setContentTitle("Aftrparties")
//                .setContentText("Rate the Party... " + PartyName + " By " +HostName)
//                .setTicker("New Rate Alert!")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent).build();
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);

            Intent i = new Intent(context, TransparentActivity.class);
            i.putExtra("PartyName", PartyName);
            i.putExtra("PartyId", PartyId);
            i.putExtra("PartyName", "PartyName");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }







    /*public void setLocalNotification(int position)
     {
       Long currentTimeMillis = System.currentTimeMillis();
       Log.e("Current Time Millis",currentTimeMillis + "");
       PartyTable party = result.get(position);
       Log.e("=====","======");
       Log.e("Party Name",party.getPartyName());
       Log.e("Party Host",party.getHostName());
       Log.e("Party end time",party.getEndTime() +"");

       long timeDifference= Long.parseLong(party.getEndTime().trim())  - currentTimeMillis;

       notificationIntent.putExtra("PartyName",party.getPartyName());
       notificationIntent.putExtra("HostName",party.getHostName());


       PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

       Calendar cal = Calendar.getInstance();
       cal.add(Calendar.SECOND, 15);

         if (Build.VERSION.SDK_INT >= 19)
             alarmManager.setExact(AlarmManager.RTC_WAKEUP,  Long.parseLong(party.getEndTime()), broadcast);
         else if (Build.VERSION.SDK_INT >= 15)
             alarmManager.set(AlarmManager.RTC_WAKEUP,  Long.parseLong(party.getEndTime()), broadcast);

     }*/
}
