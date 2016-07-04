package com.aperotechnologies.aftrparties.HomePage;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherSearchActivity;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.LocalNotifications.LNotificationHelper;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Settings.SettingsActivity;
import com.aperotechnologies.aftrparties.TipsActivity;
import com.aperotechnologies.aftrparties.utils.ResizableButton;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.linkedin.platform.LISessionManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 06/05/16.
 */
public class HomePageActivity extends Activity
{

    ImageButton btn_logout;
    CircularImageView imguser;
    TextView txtuserName, txtuserEmail, txtuserDOB, txtuserGender;
    Context cont;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    ResizableButton btnTips, btnSettings, btnChat, btnHistory,  btnHost, btnGateCrasher;
    ArrayList<String> profilePics, validPics;
    int i=0;
    String url;
    FaceOverlayView faceOverlayView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_homepage);

        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        btn_logout = (ImageButton) findViewById(R.id.btn_logout);

///
        LNotificationHelper.setLNotificationPartyRetention(cont, "PartyHarsha", "123456", "fsdfsdf45345345345","14545345345");
        ///
        imguser = (CircularImageView) findViewById(R.id.userimage);
        txtuserName = (TextView) findViewById(R.id.userName);
        txtuserEmail = (TextView) findViewById(R.id.userEmail);
        txtuserDOB = (TextView) findViewById(R.id.userDOB);
        txtuserGender = (TextView) findViewById(R.id.userGender);
        btnTips = (ResizableButton) findViewById(R.id.btnTips);
        btnSettings = (ResizableButton) findViewById(R.id.btnSettings);
        btnChat = (ResizableButton) findViewById(R.id.btnChat);
        btnHistory = (ResizableButton) findViewById(R.id.btnHistory);
        btnHost = (ResizableButton) findViewById(R.id.btnHost);
        btnGateCrasher = (ResizableButton) findViewById(R.id.btnGateCrasher);

        faceOverlayView = (FaceOverlayView)findViewById(R.id.face_overlay);

        txtuserName.setText(sharedPreferences.getString(m_config.Entered_User_Name,""));
        txtuserEmail.setText(sharedPreferences.getString(m_config.Entered_Email,""));
        txtuserDOB.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE());
        txtuserGender.setText(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_GENDER());

        String url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();

        if(url.equals(null) || url.equals("") || url.equals("N/A"))
        {
            //if(LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC() == null || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("") || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("N/A")) {
                url = "";
//            }else{
//                url = LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC();
//            }
        }
        else
        {
            url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();
        }

        if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
        {
            Picasso.with(cont).load(url).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_user)
                    .error(R.drawable.placeholder_user)
                    .into(imguser);
        }

        btn_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(m_config.FinalStepDone,"No");
                editor.apply();

                if(LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken() == null)
                {
                    Log.e("Logge out from " ,"Linked In");
                }

                Log.e("Logged Out","Yes    "  + "  aa");//.getAccessToken().toString());
                Intent intent = new Intent(cont, Welcome.class);
                startActivity(intent);

//                LoginValidations.QBSessionLogOut();
//                LoginValidations.chatLogout();
            }
        });

        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, TipsActivity.class);
                startActivity(i);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("----"," came here");

                getFBProfilePictures();
//
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, DialogsActivity.class);
                startActivity(i);

            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, HistoryActivity.class);
                startActivity(i);

            }
        });

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, HostActivity.class);
                startActivity(i);

            }
        });

        btnGateCrasher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomePageActivity.this, GateCrasherSearchActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    public void onBackPressed()
    {
        //Harshada
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);
        alertDialogBuilder
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
        alertDialogBuilder.show();
    }

    public void getFBProfilePictures()
    {
        GraphRequest request1 = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        profilePics = new ArrayList<String>();
                        validPics = new ArrayList<String>();
                        try
                        {
                            JSONObject FBAlbum = object;
                            JSONObject Albums = FBAlbum.getJSONObject("albums");
                            JSONArray data = Albums.getJSONArray("data");
                            for (int i = 0 ;i<data.length();i++)
                            {
                                JSONObject AlbumContainer = data.getJSONObject(i);
                                if(AlbumContainer.getString("name").equals("Profile Pictures"))
                                {
                                    JSONObject photos = AlbumContainer.getJSONObject("photos");
                                    JSONArray innerData = photos.getJSONArray("data");
                                    for(int j=0;j<innerData.length();j++)
                                    {
                                        JSONObject innerAlbum = innerData.getJSONObject(j);
                                        profilePics.add(innerAlbum.getString("picture"));
                                    }
                                    break;
                                }
                            }
                        }
                        catch(Exception e)
                        {
                        }
                        for(int i=0;i<profilePics.size();i++)
                        {
                            Log.e("Index PP "+ i,profilePics.get(i));
                        }
                        FaceDetectteo(0);
                        //  FaceDetect();
                    }
                });

        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "albums.fields(name,photos.fields(name,picture,source,created_time))");//,)");
        request1.setParameters(parameters1);
        request1.executeAsync();
    }

    public void FaceDetectteo(int index)
    {
        Log.e("Index ",index +"");
        i = index;

        if(index==0)
        {
            validPics.clear();
        }

        if(index >= profilePics.size())
        {
            Log.e("In outer if","Yes");
            String[] test = new String[validPics.size()];

            for(int  p=0;p<validPics.size();p++)
            {
                test[p] = validPics.get(p);
            }
            for(int  p=0;p<test.length;p++)
            {
                Log.e("Index "+i,test[p]);
            }
            Intent i = new Intent(HomePageActivity.this, SettingsActivity.class);
            Bundle b=new Bundle();
            b.putStringArray("images", test);
            i.putExtras(b);
            startActivity(i);
        }
        else
        {
            do
            {
                url = profilePics.get(index);
                if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
                {
                    Target mTarget = new Target()
                    {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
                        {
                            //  Log.e("FB bitmap loaded  " + k,"sucessfully  " + bitmap.toString() );
                            int faces = faceOverlayView.setBitmap(bitmap);
                         //   Log.e("No of faces from post",faces+"");
                            if(faces>0)
                            {
                           //      Log.e("Index  " + i,"Faces :  "+faces);
                                //Set  Face detect flag here to true
                                validPics.add(url);
                                i++;
                                FaceDetectteo(i);
                                //  Log.e("Valid Pics size in if",validPics.size()+"");
                            }
                            else
                            {
                                //Set  Face detect flag here to false
                                i++;
                                FaceDetectteo(i);

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

            }while(profilePics.size()==index);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }


}
