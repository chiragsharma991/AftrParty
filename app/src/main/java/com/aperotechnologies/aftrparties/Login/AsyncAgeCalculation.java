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
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        //String dtStart=params[0];
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
            }
          //  Log.e("Valid Age", ConstsCore.ValidAge + "");

            // basicValidation();

            //Check for profile pic first
            if (loggedInUserInformation.getFB_USER_PROFILE_PIC().equals("N/A") || loggedInUserInformation.getFB_USER_PROFILE_PIC().equals(null))
            {
                hasFBImage = false;
                //Check for LI profile Pic
                if (loggedInUserInformation.getLI_USER_PROFILE_PIC().equals("N/A") || loggedInUserInformation.getLI_USER_PROFILE_PIC().equals(null))
                {
                    ConstsCore.profilePicAvailable = "No";
                    hasLIImage = false;
                }
                else
                {
                    hasLIImage = true;
                    ConstsCore.profilePicAvailable = "Yes";
                }
            }
            else
            {
                hasFBImage = true;
                ConstsCore.profilePicAvailable = "Yes";
            }


            //Connection Count
            if (Integer.parseInt(loggedInUserInformation.getFB_USER_FRIENDS()) >= ConstsCore.FB_FRIENDS)
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
            else
            {
                ConstsCore.validFriendsCount = "No";
            }


          //  Log.e("Before Conditions check priflefald ageflag friendsflag", ConstsCore.profilePicAvailable + "    " + ConstsCore.ValidAge + "    " + ConstsCore.validFriendsCount);
            if (ConstsCore.profilePicAvailable.equals("No"))
            {
                Handler h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        GenerikFunctions.showToast(cont, "DOnt have profile picture");
                    }
                });
            }
            else
            {
                //Have profile picture
                if (ConstsCore.ValidAge.equals("No"))
                {

                    Handler h = new Handler(cont.getMainLooper());
                    h.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            GenerikFunctions.showToast(cont, "Invalid age");
                        }
                    });
                }
                else
                {
                    //Valid age
                    if (ConstsCore.validFriendsCount.equals("No"))
                    {
                        Handler h = new Handler(cont.getMainLooper());
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                GenerikFunctions.showToast(cont, "Face book friends count is less than " + ConstsCore.FB_FRIENDS);
                            }
                        });
                    }
                    else
                    {
                        if (ConstsCore.validConnCount.equals("No"))
                        {
                            Handler h = new Handler(cont.getMainLooper());
                            h.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    GenerikFunctions.showToast(cont, "LI conn count is less than " + ConstsCore.LI_CONNECTIONS);
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
        if (result==true)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(m_config.BasicFBLIValidationsDone,"Yes");
            editor.apply();

            // GO for face detect
            // new AsyncFaceDetection(cont).execute();

            try
            {
                // Log.e("Inside try","yes");
                String  url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
                Log.e("URL for FB",url+"");
                if(url.equals(null) || url.equals("") || url.equals("N/A"))
                {
                    Log.e("Users FB Pic not Avail","Yes");
                    url = "";
//                    if( loggedInUserInformation.getLI_USER_PROFILE_PIC() == null || loggedInUserInformation.equals("") || loggedInUserInformation.getLI_USER_PROFILE_PIC().equals("N/A") )
//                    {
//                        url = "";
//                    }
//                    else
//                    {
//                        url = loggedInUserInformation.getLI_USER_PROFILE_PIC();
//                    }
                }
                else
                {
                    url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
                }

                // Log.e("URL",url);
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
                                Log.e("There is face in pic",faces+"");
                                Log.e("GO for OTP","Yes");
                                //Set  Face detect flag here to true
                                SharedPreferences.Editor editorq = sharedPreferences.edit();
                                editorq.putString(m_config.FaceDetectDone,"Yes");
                                editorq.apply();

                                Intent intent = new Intent(cont,OTPActivity.class);
                                cont.startActivity(intent);
                            }
                            else
                            {
                                //Set  Face detect flag here to false
                                Log.e("There is no face in pic","");
                                SharedPreferences.Editor editorq = sharedPreferences.edit();
                                editorq.putString(m_config.FaceDetectDone,"No");
                                editorq.apply();
                                GenerikFunctions.showToast(cont,"There is no face in your profile pic");
                            }
                            GenerikFunctions.hideDialog(m_config.pDialog);
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
                e.printStackTrace();
            }
        }
        else
        {
        }
    }
}

//    public void basicValidation()
//    {
//        if(loggedInUserInformation.getFB_USER_PROFILE_PIC().equals("N/A") || loggedInUserInformation.getFB_USER_PROFILE_PIC().equals(null))
//        {
//            hasFBImage=false;
//            //Check for LI profile Pic
//            if(loggedInUserInformation.getLI_USER_PROFILE_PIC().equals("N/A") || loggedInUserInformation.getLI_USER_PROFILE_PIC().equals(null))
//            {
//                ConstsCore.profilePicAvailable="No";
//                hasLIImage=false;
//            }
//            else
//            {
//                hasLIImage=true;
//                ConstsCore.profilePicAvailable="Yes";
//            }
//        }
//        else
//        {
//            hasFBImage=true;
//            ConstsCore.profilePicAvailable="Yes";
//        }
//
//        //Connection Count
//        if(Integer.parseInt(loggedInUserInformation.getFB_USER_FRIENDS()) >= ConstsCore.FB_FRIENDS)
//        {
//
//            if(Integer.parseInt(loggedInUserInformation.getLI_USER_CONNECTIONS())<ConstsCore.LI_CONNECTIONS)
//            {
//                //fb valid  li invalid
//                ConstsCore.validFriendsCount="Yes";
//                ConstsCore.validConnCount="No";
//            }
//            else
//            {
//                // fb valid li valid
//                ConstsCore.validFriendsCount="Yes";
//                ConstsCore.validConnCount="Yes";
//            }
//        }
//        else
//        {
//            ConstsCore.validFriendsCount="No";
//        }
//
//        GenerikFunctions.hideDialog(m_config.pDialog);
//
////                    profilePicAvailable="No";
////                    String validAge="No";
////                    String validFriendsCount="No";
////                    String validConnCount="No";
//
//        Log.e("Before Conditions check priflefald ageflag friendsflag",ConstsCore.profilePicAvailable +"    " +ConstsCore.ValidAge +"    " + ConstsCore.validFriendsCount);
//        if(ConstsCore.profilePicAvailable.equals("No"))
//        {
//            Handler h = new Handler(context.getMainLooper());
//            h.post(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    GenerikFunctions.showToast(context,"DOnt have profile picture");
//                }
//            });
//
//        }
//        else
//        {
//            //Have profile picture
//            if(ConstsCore.ValidAge.equals("No"))
//            {
//
//                Handler h = new Handler(context.getMainLooper());
//                h.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        GenerikFunctions.showToast(context,"Invalid age");
//                    }
//                });
//
//            }
//            else
//            {
//                //Valid age
//                if(ConstsCore.validFriendsCount.equals("No"))
//                {
//                    Handler h = new Handler(context.getMainLooper());
//                    h.post(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            GenerikFunctions.showToast(context,"Face book friends count is less than " +ConstsCore.FB_FRIENDS);
//                        }
//                    });
//                }
//                else
//                {
//                    if(ConstsCore.validConnCount.equals("No"))
//                    {
//                        Handler h = new Handler(context.getMainLooper());
//                        h.post(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                GenerikFunctions.showToast(context,"LI conn count is less than "+ConstsCore.LI_CONNECTIONS);
//                            }
//                        });
//                    }
//                    else
//                    {
//                        //Start Face Detection Page
//                        //Set Validation FLags Here
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString(m_config.BasicFBLIValidationsDone,"Yes");
//                        editor.apply();
//
//                        // Log.e("Go for Face Detect","Yes");
//
//                        //Go for Face Detection
//                        //  new Imageverification(context).execute();
//
//                        try
//                        {
//                            // Log.e("Inside try","yes");
//                            String url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
//
//                            if(url.equals(null) || url.equals("") || url.equals("N/A"))
//                            {
//                                if(loggedInUserInformation.getLI_USER_PROFILE_PIC() == null ||
//                                        loggedInUserInformation.equals("") ||
//                                        loggedInUserInformation.getLI_USER_PROFILE_PIC().equals("N/A"))
//                                {
//                                    url = "";
//                                }
//                                else
//                                {
//                                    url = loggedInUserInformation.getLI_USER_PROFILE_PIC();
//                                }
//                            }
//                            else
//                            {
//                                url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
//                            }
//
//                            // Log.e("URL",url);
//
//                            if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
//                            {
//
//                                Log.e("Before Picasso play service","yes");
//
//                                Target mTarget = new Target()
//                                {
//                                    @Override
//                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
//                                    {
//                                        Log.e("FB bitmap loaded ","sucessfully  " + bitmap.toString() );
//
//
//                                        int faces = faceOverlayView.setBitmap(bitmap);
//
//                                        GenerikFunctions.showToast(context,"No of faces  "+faces);
//
//                                        if(faces>0)
//                                        {
//                                            Log.e("There is face in pic",faces+"");
//                                            Log.e("GO for OTP","Yes");
//
//                                            //Set  Face detect flag here to true
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString(m_config.FaceDetectDone,"Yes");
//                                            editor.apply();
//                                        }
//                                        else
//                                        {
//
//                                            //Set  Face detect flag here to false
//                                            Log.e("There is no face in pic","");
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString(m_config.FaceDetectDone,"No");
//                                            editor.apply();
//
//                                            GenerikFunctions.showToast(context,"There is no face in your profile pic");
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onBitmapFailed(Drawable drawable)
//                                    {
//                                        Log.e("On FB bitmap failed",drawable.toString());
//                                    }
//
//                                    @Override
//                                    public void onPrepareLoad(Drawable drawable) {
//
//                                    }
//
//                                };
//
//
//                                Picasso.with(context)
//                                        .load(url)
//                                        .into(mTarget);
//
//
//                                faceOverlayView.setTag(mTarget);
//
//
//
//                            }
//                        }
//
//                        catch(Exception e)
//                        {
//                            e.printStackTrace();
////                            Log.e(TAG,e.getMessage());
//                        }
//                    }
//                }
//            }
//        }
//
//    }
