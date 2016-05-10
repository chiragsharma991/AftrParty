package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class SchedulerEventService extends Service {
    private static final String APP_TAG = "com.aperotechnologies.linelist_new";


    @Override
    public IBinder onBind(final Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {

        Log.e(APP_TAG,"service call");
        //MainActivity mainact = new MainActivity();
        //mainact.CreateNotification(this);
        return Service.START_NOT_STICKY;
    }


}




