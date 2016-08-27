package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.aperotechnologies.aftrparties.Reusables.Validations;


/**
 * Created by hasai on 04/07/16.
 */
public class SetLocalNotifications {

    // set Local Notification for party retention
    public static void setLNotificationPartyRetention(Context cont, String PartyName, String PartyId, String DialogId, String PartyEndTime)
    {
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(cont, LNotificationsAlarmReceiver.class);
        notificationIntent.putExtra("PartyName",PartyName);
        notificationIntent.putExtra("PartyId",PartyId);
        notificationIntent.putExtra("DialogId",DialogId);
        notificationIntent.putExtra("from","PartyRetention");

        long notificationTime = Validations.getCurrentTime() + 600000;//Long.parseLong(PartyEndTime) + 10000;
        Log.e("notificationTime "," "+notificationTime);

        String notid = PartyId.split("_")[1];
        int notId =  Integer.parseInt(notid.substring(0, 8));
        Log.e("notid "," "+notId);


        PendingIntent broadcast = PendingIntent.getBroadcast(cont, notId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }

    // set Local Notification for private chat for subscription
    public static void setLNotificationforPrivateChat(Context cont, Long subscriptiondate, String dialogId, String oppFbId, String loginUserFbId)
    {

        Log.e("set LocalNotification for Private Chat","");
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(cont, LNotificationsAlarmReceiver.class);
        notificationIntent.putExtra("dialogId",dialogId);
        notificationIntent.putExtra("oppFbId",oppFbId);
        notificationIntent.putExtra("loginUserFbId",loginUserFbId);
        notificationIntent.putExtra("from","privatechatsubs");

        long notificationTime = Validations.getCurrentTime() + 50000;//subscriptiondate;

        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }


    // set Local Notification for party mask status  for subscription
    public static void setLNotificationforPartyMaskStatus(Context cont, Long subscriptiondate, String loginUserFbId)
    {

        Log.e("set LocalNotification for Party Mask","");
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(cont, LNotificationsAlarmReceiver.class);
        notificationIntent.putExtra("loginUserFbId",loginUserFbId);
        notificationIntent.putExtra("from","partymaskstatus");

        long notificationTime = Validations.getCurrentTime() + 50000;//subscriptiondate;

        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }


    // set Local Notification for multiple parties subscription  for subscription
    public static void setLNotificationforGcMultipleParties(Context cont, long subscriptiondate, String loginUserFbId)
    {

        Log.e("set LocalNotification for Gc Multiple party","");
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(cont, LNotificationsAlarmReceiver.class);

        notificationIntent.putExtra("loginUserFbId",loginUserFbId);
        notificationIntent.putExtra("from","gcmultipleparty");

        long notificationTime = Validations.getCurrentTime() + 50000;// subscriptiondate;

        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }



}
