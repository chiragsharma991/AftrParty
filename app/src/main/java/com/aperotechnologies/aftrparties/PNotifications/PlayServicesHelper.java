package com.aperotechnologies.aftrparties.PNotifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;

import java.io.IOException;
import java.util.ArrayList;

public class PlayServicesHelper {

    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "PlayServicesHelper";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleCloudMessaging googleCloudMessaging;
    private Activity activity;
    private String regId;


    public PlayServicesHelper(Activity activity) {
        this.activity = activity;
        checkPlayService();
    }

    private void checkPlayService() {
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
            regId = getRegistrationId();
            Log.e("regId--- "," "+regId);
            registerInBackground();
            //subscribeToPushNotifications(regId);
            //getDeviceIdAndroid(regId);


        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
                    }
                    regId = googleCloudMessaging.register(ConstsCore.PROJECT_NUMBER);
                    Log.e("regID"," "+regId);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    Handler h = new Handler(activity.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            getDeviceIdAndroid(regId);
                        }
                    });

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Subscribe to Push Notifications
     *
     * @param regId registration ID
     */
    private void subscribeToPushNotifications(String regId, TelephonyManager mTelephony) {

            QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
            subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
            //
            String deviceId;

           if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            } else {
                deviceId = Settings.Secure.getString(activity.getContentResolver(),
                        Settings.Secure.ANDROID_ID); //*** use for tablets
            }
            subscription.setDeviceUdid(deviceId);
            //
            subscription.setRegistrationID(regId);
            //
            QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>() {

                @Override
                public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                    Log.e("subscription","OnSuccess");
                }

                @Override
                public void onError(QBResponseException error) {
                    Log.e("subscription","onError");
                    error.printStackTrace();
                }
            });

    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }



    public void getDeviceIdAndroid(String regId){

        TelephonyManager mTelephony = null;

        if ((int) Build.VERSION.SDK_INT < 23)
        {
            mTelephony = (TelephonyManager) activity.getSystemService(
                    Context.TELEPHONY_SERVICE);
            subscribeToPushNotifications(regId,mTelephony);
        }
        else
        {
            int permissionCheck = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_PHONE_STATE);


            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {

                mTelephony = (TelephonyManager) activity.getSystemService(
                        Context.TELEPHONY_SERVICE);
                subscribeToPushNotifications(regId,mTelephony);


            }else{
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_PHONE_STATE},30);
            }

        }
    }



    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {

        switch (requestCode)
        {
            case 30:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("Permission granted", "");
                }
                else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                {

                    new AlertDialog.Builder(activity)
                            .setTitle("Permission Denied")
                            .setMessage("READ_PHONE_STATE")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    ActivityCompat.requestPermissions(activity,
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            30);
                                }
                            })
                            .setNegativeButton("I'm Sure", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
                break;


        }

    }


}