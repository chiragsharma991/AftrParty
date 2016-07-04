package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hasai on 28/06/16.
 */
public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // I use two separate variables here. You can, of course, just use one and
    // increment/decrement it instead of using two and incrementing both.
    private static int resumed;
    private static int stopped;

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        ++resumed;

    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        ++stopped;

        //Log.e("resumed value"," "+resumed+"   ---stopped value---"+stopped);
        android.util.Log.w("test", "application is being backgrounded: " + (resumed == stopped));
    }

    // If you want a static function you can use to check if your application is
    // foreground/background, you can use the following:
    /*
    // Replace the two variables above with these two
    private static int resumed;
    private static int stopped;*/

    // And add this public static function
    public static boolean isApplicationInForeground() {
        //Log.e("resumed "," "+resumed+"   ---stopped---"+stopped);

        return resumed > stopped;
    }



}
