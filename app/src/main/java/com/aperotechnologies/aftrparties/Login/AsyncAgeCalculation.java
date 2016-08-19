package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.Age;
import com.aperotechnologies.aftrparties.Reusables.AgeCalculator;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mpatil on 25/05/16.
 */

public class AsyncAgeCalculation extends AsyncTask<Void, Void, Boolean>
{

    LoggedInUserInformation loggedInUserInformation;
    Context cont;
    boolean hasFBImage = false;
    boolean hasLIImage = false;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    int faces=0;
    FaceOverlayView faceOverlayView;
    Target mTarget;

    public AsyncAgeCalculation(Context cont)
    {
        this.cont=cont;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();
        faceOverlayView = (FaceOverlayView) ((Activity)cont).findViewById(R.id.face_overlay);

        Log.e("Shrd Pref in AsyncAgeCalc",sharedPreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                sharedPreferences.getString(m_config.Entered_Email,"N/A") + "   "
                + sharedPreferences.getString(m_config.Entered_Contact_No,"N/A"));

        Log.e("frnds count  li conn count",loggedInUserInformation.getFB_USER_FRIENDS() + "    " +loggedInUserInformation.getLI_USER_CONNECTIONS());

    }

    @Override
    protected Boolean doInBackground(Void... params)
    {

        /*harshada
        String  dtStart = loggedInUserInformation.getFB_USER_BIRTHDATE();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try
        {
            Date date = format.parse(dtStart);
         //   Log.e("Parsed Date", date + "");
            Age age = AgeCalculator.calculateAge(date);
         //   Log.e("Calculated AGe", age.toString() + "    " + ConstsCore.FB_AGE);
            if (age.getYears() < ConstsCore.FB_AGE)
            {
                ConstsCore.ValidAge = "No";
            }
            else
            {
                ConstsCore.ValidAge = "Yes";
            }*/

        //harshada
        String  dtage = loggedInUserInformation.getFB_USER_AGE();
        Log.e("dtage ", " "+dtage);
        try
        {

            if (dtage.equals("21") || dtage.equals("18"))
            {
                ConstsCore.ValidAge = "Yes";
            }
            else
            {
                ConstsCore.ValidAge = "No";
            }


            //Check for profile pic first
            if (loggedInUserInformation.getFB_USER_PROFILE_PIC().equals("N/A") || loggedInUserInformation.getFB_USER_PROFILE_PIC().equals(null))
            {
                hasFBImage = false;
                ConstsCore.profilePicAvailable = "No";

            }
            else
            {
                hasFBImage = true;
                ConstsCore.profilePicAvailable = "Yes";
            }


            //Connection Count

            if (!loggedInUserInformation.getFB_USER_FRIENDS().equals("N/A") && Integer.parseInt(loggedInUserInformation.getFB_USER_FRIENDS()) >= ConstsCore.FB_FRIENDS)
            {
                Log.e("--- "," "+loggedInUserInformation.getLI_USER_CONNECTIONS());

                if(loggedInUserInformation.getLI_USER_CONNECTIONS().equals("N/A"))
                {
                    ConstsCore.validFriendsCount = "Yes";
                    ConstsCore.validConnCount = "No";

                }
                else
                {
                    if (Integer.parseInt(loggedInUserInformation.getLI_USER_CONNECTIONS()) < ConstsCore.LI_CONNECTIONS)
                    {
                        //fb valid  li invalid
                        ConstsCore.validFriendsCount = "Yes";
                        ConstsCore.validConnCount = "No";
                    }
                    else
                    {
                        // fb valid li valid
                        ConstsCore.validFriendsCount = "Yes";
                        ConstsCore.validConnCount = "Yes";
                    }
                }

            }
            else
            {
                ConstsCore.validFriendsCount = "No";
            }

//            //harshada 8Aug Temporary data
//            ConstsCore.validFriendsCount = "Yes";
//            ConstsCore.validConnCount = "Yes";
//            //harshada


            //final check for all basic validations
            //check for profile pic
            if (ConstsCore.profilePicAvailable.equals("No"))
            {
                //does not Have profile picture
                Handler h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(RegistrationActivity.reg_pd.isShowing())
                        {
                            RegistrationActivity.reg_pd.dismiss();
                        }
                        GenerikFunctions.showToast(cont, "Don't have profile picture");
                    }
                });
            }
            else
            {
                //Have profile picture
                //check for valid age
                if (ConstsCore.ValidAge.equals("No"))
                {
                    //Invalid age
                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            if(RegistrationActivity.reg_pd.isShowing())
                            {
                                RegistrationActivity.reg_pd.dismiss();
                            }
                            GenerikFunctions.showToast(cont, "Invalid age");
                        }
                    });
                }
                else
                {
                    //Valid age
                    //check for fb count
                    if (ConstsCore.validFriendsCount.equals("No"))
                    {
                        Handler h = new Handler(cont.getMainLooper());
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(RegistrationActivity.reg_pd.isShowing())
                                {
                                    RegistrationActivity.reg_pd.dismiss();
                                }
                                GenerikFunctions.showToast(cont, "Face book friends count is less than " + ConstsCore.FB_FRIENDS);
                            }
                        });
                    }
                    else
                    {
                        //check for linkedin count
                        if (ConstsCore.validConnCount.equals("No"))
                        {
                            Handler h = new Handler(cont.getMainLooper());
                            h.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(RegistrationActivity.reg_pd.isShowing())
                                    {
                                        RegistrationActivity.reg_pd.dismiss();
                                    }
                                    GenerikFunctions.showToast(cont, "Linked In connection count is less than " + ConstsCore.LI_CONNECTIONS);
                                }
                            });
                        }
                        else
                        {
                            //Initialise shared prefs
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (result == true)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(m_config.BasicFBLIValidationsDone,"Yes");
            editor.apply();


            try
            {

                String  url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
                Log.e("URL for FB",url+"" + url.equals(null));
                if(url.equals(null) || url.equals("") || url.equals("N/A"))
                {
                    Log.e("Users FB Pic not Avail","Yes");
                    url = "";

                }
                else
                {
                    url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
                }

                 Log.e("URL in async",url);
                if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
                {
                    // Log.e("Before Picasso play service","yes");
                    mTarget = new Target()
                    {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
                        {
                            Log.e("FB bitmap loaded ","sucessfully  " + bitmap.toString() );
                            faces = faceOverlayView.setBitmap(bitmap);
                            Log.e("No of faces from post",faces+"");

                            if(faces>0)
                            {
                              //  Log.e("There is face in pic",faces+"");
                                Log.e("GO for OTP","Yes");
                                //Set  Face detect flag here to true
                                SharedPreferences.Editor editorq = sharedPreferences.edit();
                                editorq.putString(m_config.FaceDetectDone,"Yes");
                                editorq.apply();

                                Log.e("Here at OTP Activity from Async","Yes");

                                if(RegistrationActivity.reg_pd.isShowing())
                                {
                                    RegistrationActivity.reg_pd.dismiss();
                                }
                                faces = 0;

                                //Harshada 22 jul//
//                                Intent intent = new Intent(cont,OTPActivity.class);
//                                cont.startActivity(intent);
                                //

                                Intent intent = new Intent(cont, HomePageActivity.class);
                                cont.startActivity(intent);
                            }
                            else
                            {
                                faces = 0;
                                if(RegistrationActivity.reg_pd.isShowing())
                                {
                                    RegistrationActivity.reg_pd.dismiss();
                                }
                                //Set  Face detect flag here to false
                                Log.e("There is no face in pic","");
                                SharedPreferences.Editor editorq = sharedPreferences.edit();
                                editorq.putString(m_config.FaceDetectDone,"No");
                                editorq.apply();
                                GenerikFunctions.showToast(cont,"There is no face in your profile pic");
                            }

                          }

                        @Override
                        public void onBitmapFailed(Drawable drawable)
                        {
                            Log.e("On FB bitmap failed",drawable.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable drawable)
                        {
                        }
                    };

                    Picasso.with(cont)
                            .load(url)
                            .into(mTarget);
                    faceOverlayView.setTag(mTarget);
                }
            }
            catch(Exception e)
            {
                if(RegistrationActivity.reg_pd.isShowing())
                {
                    RegistrationActivity.reg_pd.dismiss();
                }
                e.printStackTrace();
            }
        }
        else
        {
        }
    }
}


