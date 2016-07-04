package com.aperotechnologies.aftrparties.PNotifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class PlayServicesHelper {

    //Harshada
    private static final String TAG = "PlayServicesHelper";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging googleCloudMessaging;
    private Activity cont;
    private String regId;
    Configuration_Parameter m_config;
    LoggedInUserInformation loggedInUserInfo;
    SharedPreferences sharedPreferences;

    //Meghana
    Bitmap imageBitmap = null;
    boolean hasFBImage = false;
    boolean hasLIImage = false;
    FaceOverlayView faceOverlayView;

    RequestQueue queue;

    public PlayServicesHelper(Activity cont){
        checkPlayService();
    }



    public PlayServicesHelper(Activity cont, LoggedInUserInformation loggedInUserInfo) {
        this.cont = cont;
        this.loggedInUserInfo = loggedInUserInfo;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        faceOverlayView = new FaceOverlayView(cont);
//      faceOverlayView.setTag(1);
        faceOverlayView = (FaceOverlayView) cont.findViewById(R.id.face_overlay);
        queue = Volley.newRequestQueue(cont);

        //checkPlayService();
        Thread t = new Thread(new CheckPlayServicesLooper());
        t.start();
    }

    private void checkPlayService() {
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices())
        {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(cont);
            regId = getRegistrationId();
            Log.e("check regId--- "," "+regId);

            if (regId.isEmpty())
            {
                registerInBackground();
            }
            else
            {


            Handler h = new Handler(cont.getMainLooper());
            h.post(new Runnable()
            {
                @Override
                public void run()
                {
                    LoginValidations.checkPendingLoginFlags(cont);
                }
            });

            }

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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(cont);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, cont, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                cont.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cont);// getGCMPreferences();
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
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(cont);
                    }
                    regId = googleCloudMessaging.register(ConstsCore.PROJECT_NUMBER);

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cont);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(m_config.temp_regId, regId);
                    editor.apply();
                    Log.e("regID"," "+regId);
                    msg = "Device registered, registration ID=" + regId;

                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {


                            if (cont instanceof RegistrationActivity )
                            {
                                RegistrationActivity registration = new RegistrationActivity();
                                registration.getDeviceIdAndroid(regId, cont);
                            }
                            else
                            {
                                Welcome welcome = new Welcome();
                                welcome.getDeviceIdAndroid(regId,cont);
                            }

                        }
                    });


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
        return cont.getSharedPreferences(cont.getPackageName(), Context.MODE_PRIVATE);
    }


    class CheckPlayServicesLooper implements Runnable {

        private Looper myLooper;

        @Override
        public void run() {

            Looper.prepare();
            // code that needed a separated thread
            checkPlayService();
            myLooper = Looper.myLooper();
            Looper.loop();
            myLooper.quit();
        }
    };


}