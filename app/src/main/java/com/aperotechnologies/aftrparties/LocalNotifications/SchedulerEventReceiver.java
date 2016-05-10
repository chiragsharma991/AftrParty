package com.aperotechnologies.aftrparties.LocalNotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import com.aperotechnologies.linelist_new.SchedulerEventService.LocalBinder;

public class SchedulerEventReceiver extends BroadcastReceiver {
    private static final String APP_TAG = "com.aperotechnologies.linelist_new";
    Context c;

    @Override
    public void onReceive(final Context ctx, Intent intent) {

        Log.e("SchedulerEventRr.onReceive()", "");
        c = ctx;
        Intent eventService = new Intent(ctx, SchedulerEventService.class);
        ctx.startService(eventService);

//        MainActivity mainact = new MainActivity();
//        mainact.cancelAlarm(ctx);

    }



}

