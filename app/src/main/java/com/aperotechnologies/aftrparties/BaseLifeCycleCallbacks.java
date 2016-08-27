package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.linkedin.platform.LISessionManager;

import java.util.HashMap;

/**
 * Created by hasai on 28/06/16.
 */


//public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
//    // I use two separate variables here. You can, of course, just use one and
//    // increment/decrement it instead of using two and incrementing both.
//    private static int resumed;
//    private static int stopped;
//
//    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//    }
//
//    public void onActivityDestroyed(Activity activity) {
//    }
//
//    public void onActivityResumed(Activity activity) {
//        ++resumed;
//
//    }
//
//    public void onActivityPaused(Activity activity) {
//    }
//
//    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//    }
//
//    public void onActivityStarted(Activity activity) {
//    }
//
//    public void onActivityStopped(Activity activity) {
//        ++stopped;
//
//        //Log.e("resumed value"," "+resumed+"   ---stopped value---"+stopped);
//        android.util.Log.w("test", "application is being backgrounded: " + (resumed == stopped));
//    }
//
//    // If you want a static function you can use to check if your application is
//    // foreground/background, you can use the following:
//    /*
//    // Replace the two variables above with these two
//    private static int resumed;
//    private static int stopped;*/
//
//    // And add this public static function
//    public static boolean isApplicationInForeground() {
//        Log.e("resumed "," "+resumed+"   ---stopped---"+stopped);
//
//        return resumed > stopped;
//    }

//}

public class BaseLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {


    static HashMap<String, Integer> activities;
    static Configuration_Parameter m_config = Configuration_Parameter.getInstance();
    static SharedPreferences sharedpreferences;

    BaseLifeCycleCallbacks() {
        activities = new HashMap<String, Integer>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //Log.e("sharedpre"," "+sharedpreferences);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //map Activity unique class name with 1 on foreground
        activities.put(activity.getLocalClassName(), 1);
        applicationStatus();


    }

    @Override
    public void onActivityResumed(Activity activity) {



    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //map Activity unique class name with 0 on foreground
        activities.put(activity.getLocalClassName(), 0);
        applicationStatus();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * Check if any activity is in the foreground
     */
    private static boolean isBackGround() {
        for (String s : activities.keySet()) {
            if (activities.get(s) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Log application status.
     */
    public static boolean applicationStatus() {


        Log.d("ApplicationStatus", "Is application background " + isBackGround());
        if (isBackGround()) {
            //Do something if the application is in background
            return false;
        }
        else
        {
            //Log.e("sharedpreferences"," "+sharedpreferences+" "+m_config);
            //checks whether user is logged in or not for canceeling notifications from notifical tray
            if(sharedpreferences.getString(m_config.FinalStepDone,"No").equals("Yes")) {
                if (m_config.notificationManager != null) {
                    m_config.notificationManager.cancelAll();
                }
            }

            return true;
        }
    }
}
