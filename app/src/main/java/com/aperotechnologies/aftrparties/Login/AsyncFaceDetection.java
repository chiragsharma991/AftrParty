package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by mpatil on 25/05/16.
 */
public class AsyncFaceDetection extends AsyncTask<Void, Void, Integer>
{
    LoggedInUserInformation loggedInUserInformation;
    Context cont;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    FaceOverlayView faceOverlayView;
    Target mTarget;
    int faces = 0;
    String url;
    public AsyncFaceDetection(Context cont)
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

    }
    @Override
    protected Integer doInBackground(Void... params)
    {
        try
        {
            // Log.e("Inside try","yes");
            url = loggedInUserInformation.getFB_USER_PROFILE_PIC();

            if(url.equals(null) || url.equals("") || url.equals("N/A"))
            {
                if(loggedInUserInformation.getLI_USER_PROFILE_PIC() == null ||
                        loggedInUserInformation.equals("") ||
                        loggedInUserInformation.getLI_USER_PROFILE_PIC().equals("N/A"))
                {
                    url = "";
                }
                else
                {
                    url = loggedInUserInformation.getLI_USER_PROFILE_PIC();
                }
            }
            else
            {
                url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
            }

            // Log.e("URL",url);

            if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
            {

//                Log.e("Before Picasso play service","yes");

                mTarget = new Target()
                {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
                    {
                        Log.e("FB bitmap loaded ","sucessfully  " + bitmap.toString() );


                        faces = faceOverlayView.setBitmap(bitmap);

                        Log.e("No of faces from doinbg",faces+"");

                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable)
                    {
                        Log.e("On FB bitmap failed",drawable.toString());
                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable) {

                    }

                };



                Handler h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Picasso.with(cont)
                                .load(url)
                                .into(mTarget);
                          faceOverlayView.setTag(mTarget);

                    }
                });

            }
        }

        catch(Exception e)
        {
            e.printStackTrace();

//                            Log.e(TAG,e.getMessage());
        }

        Log.e("Faces in background 2",faces+"");
        return faces;
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        faces = result;

        //Log.e("No of faces from onPostExecute",faces+"");

        if(faces>0)
        {
            Log.e("There is face in pic",faces+"");
            Log.e("GO for OTP","Yes");

            //Set  Face detect flag here to true
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(m_config.FaceDetectDone,"Yes");
            editor.apply();
        }
        else
        {

            //Set  Face detect flag here to false
            Log.e("There is no face in pic","");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(m_config.FaceDetectDone,"No");
            editor.apply();

            GenerikFunctions.showToast(cont,"There is no face in your profile pic");
        }

        GenerikFunctions.hideDialog(m_config.pDialog);


    }
}
