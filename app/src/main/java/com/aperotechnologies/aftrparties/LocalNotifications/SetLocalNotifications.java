package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.aperotechnologies.aftrparties.PNotifications.GcmBroadcastReceiver;
import com.aperotechnologies.aftrparties.Reusables.Validations;


/**
 * Created by hasai on 04/07/16.
 */
public class SetLocalNotifications {

    // set Local Notification for party retention
    public static void setLNotificationPartyRetention(Context cont, String PartyName, String PartyId, String DialogId, String PartyEndTime)
    {
        AlarmManager alarmManager = (AlarmManager) cont.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(cont, RatingsAlarmReceiver.class);
        notificationIntent.putExtra("PartyName",PartyName);
        notificationIntent.putExtra("PartyId",PartyId);
        notificationIntent.putExtra("DialogId",DialogId);

        long notificationTime = System.currentTimeMillis() + 50000;//Long.parseLong(PartyEndTime) + 10000;
        Log.e("notificationTime "," "+(notificationTime + 50000));

        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(cont, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 19)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        else if (Build.VERSION.SDK_INT >= 15)
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }
}
