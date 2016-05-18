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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.Login.LoggedInUserInformation;
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

public class PlayServicesHelper1 {

    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "PlayServicesHelper";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final  int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 30;

    private GoogleCloudMessaging googleCloudMessaging;
    private Activity context;
    private String regId;
    Configuration_Parameter m_config;
    LoggedInUserInformation loggedInUserInfo;
    SharedPreferences sharedPreferences;
    TelephonyManager mTelephony = null;


    public PlayServicesHelper1(Activity context, LoggedInUserInformation loggedInUserInfo) {
        this.context = context;
        this.loggedInUserInfo = loggedInUserInfo;;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        checkPlayService();
    }

    private void checkPlayService() {
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices())
        {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
            regId = getRegistrationId();
            Log.e("check regId--- "," "+regId);

//            if (regId.isEmpty())
//            {
                registerInBackground();
//            }
//            else
//            {
//                if(sharedPreferences.getString(m_config.AWSUserDataDone,"No").equals("Yes"))
//                {
//                    GenerikFunctions.hideDialog(m_config.pDialog);
//                    Log.e("call to next activity","");
//                }
//                else
//                {
//                    AWSDBOperations.createUser(context, loggedInUserInfo);
//                }
//
//            }

        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                context.finish();
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
    private String getRegistrationId()
    {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(m_config.REG_ID, "");
        if (registrationId.isEmpty())
        {
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
                    if (googleCloudMessaging == null)
                    {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = googleCloudMessaging.register(ConstsCore.PROJECT_NUMBER);
                    Log.e("regID"," "+regId);
                    msg = "Device registered, registration ID=" + regId;

                    Handler h = new Handler(context.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            getDeviceIdAndroid(regId);
                        }
                    });

                    // Persist the regID - no need to register again.
                    //storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

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
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Subscribe to Push Notifications
     *
     * @param regId registration ID
     */
    private void subscribeToPushNotifications(final String regId, TelephonyManager mTelephony) {

            QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
            subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
            //
            String deviceId;

           if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId(); //*** use for mobiles
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(),
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
                    // Persist the regID - no need to register again.
                    storeRegistrationId(regId);
                    AWSDBOperations.createUser(context, loggedInUserInfo);
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
        editor.putString(m_config.REG_ID, regId);
        editor.commit();
    }



    public void getDeviceIdAndroid(String regId){



        if ((int) Build.VERSION.SDK_INT < 23)
        {
            mTelephony = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            subscribeToPushNotifications(regId,mTelephony);
        }
        else
        {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                Log.e("requestforPermission","");


            }else{
                mTelephony = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                subscribeToPushNotifications(regId,mTelephony);
                Log.e("Permission is granted","");
            }

        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mTelephony = (TelephonyManager) context.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    subscribeToPushNotifications(regId,mTelephony);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    new AlertDialog.Builder(context)
                            .setTitle("Permission Denied")
                            .setMessage("READ_PHONE_STATE")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    ActivityCompat.requestPermissions(context,
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }








}