package com.aperotechnologies.aftrparties.HomePage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherSearchActivity;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.LocalNotifications.SetLocalNotifications;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.QBSessionClass;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Settings.SettingsActivity;
import com.aperotechnologies.aftrparties.SplashActivity;
import com.aperotechnologies.aftrparties.TipsActivity;
import com.aperotechnologies.aftrparties.TransparentActivity;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.utils.ResizableButton;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.linkedin.platform.LISessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 06/05/16.
 */
public class HomePageActivity extends Activity
{

    ImageButton btn_logout;
    CircularImageView imguser;
    TextView txt_Header;
    TextView txtuserName, txtuserEmail, txtuserDOB, txtuserGender;
    Context cont;
    SharedPreferences sharedPreferences;
    Configuration_Parameter m_config;
    ResizableButton btnTips, btnSettings, btnChat, btnHistory,  btnHost, btnGateCrasher;
    LoggedInUserInformation loggedInUserInformation;
    ArrayList<String> profilePics, validPics;
    int i=0;
    String url;
    FaceOverlayView faceOverlayView;
    public static ProgressDialog hp_pd = null;
    DBHelper helper;
    SQLiteDatabase sqldb;



    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_homepage);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        btn_logout = (ImageButton) findViewById(R.id.btn_logout);
        txt_Header = (TextView) findViewById(R.id.activity_title);
        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();

        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(m_config.TempEntered_User_Name,"");
        editor.putString(m_config.TempEntered_Email,"");
        editor.putString(m_config.TempEntered_Contact_No,"");
        editor.apply();


        hp_pd = new ProgressDialog(this);
        if (SplashActivity.pd != null)
        {
            if(SplashActivity.pd.isShowing())
            {
                SplashActivity.pd.dismiss();
            }
        }
        SplashActivity.pd = null;
        if(SplashActivity.splash != null)
        {
            SplashActivity.splash.finish();
        }


        if (RegistrationActivity.reg_pd != null)
        {
            if(RegistrationActivity.reg_pd.isShowing())
            {
                RegistrationActivity.reg_pd.dismiss();
            }
        }
        RegistrationActivity.reg_pd = null;

        if (Welcome.wl_pd != null)
        {
            if(Welcome.wl_pd.isShowing())
            {
                Welcome.wl_pd.dismiss();
            }
        }
        Welcome.wl_pd = null;



//        imguser = (CircularImageView) findViewById(R.id.userimage);
//        txtuserName = (TextView) findViewById(R.id.userName);
//        txtuserEmail = (TextView) findViewById(R.id.userEmail);
//        txtuserDOB = (TextView) findViewById(R.id.userDOB);
//        txtuserGender = (TextView) findViewById(R.id.userGender);
        btnTips = (ResizableButton) findViewById(R.id.btnTips);
        btnSettings = (ResizableButton) findViewById(R.id.btnSettings);
        btnChat = (ResizableButton) findViewById(R.id.btnChat);
        btnHistory = (ResizableButton) findViewById(R.id.btnHistory);
        btnHost = (ResizableButton) findViewById(R.id.btnHost);
        btnGateCrasher = (ResizableButton) findViewById(R.id.btnGateCrasher);
        faceOverlayView = (FaceOverlayView)findViewById(R.id.face_overlay);

        loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);

        Log.e("loggedInUserInfo", " "+loggedInUserInformation);

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().getString("from").equals("PartyRetention"))
            {
                String PartyName = getIntent().getExtras().getString("PartyName");
                String PartyId = getIntent().getExtras().getString("PartyId");
                String DialogId = getIntent().getExtras().getString("DialogId");
                Intent i = new Intent(HomePageActivity.this, TransparentActivity.class);
                i.putExtra("DialogId", DialogId);
                i.putExtra("PartyId",PartyId);
                i.putExtra("PartyName", PartyName);
                i.putExtra("from", "PartyRetention");
                startActivity(i);
            }
            else if(getIntent().getExtras().getString("from").equals("privatechatsubs"))
            {
                String loginUserFbId = getIntent().getExtras().getString("loginUserFbId");
                String oppFbId = getIntent().getExtras().getString("oppFbId");
                String dialogId = getIntent().getExtras().getString("dialogId");
                Intent i = new Intent(HomePageActivity.this, TransparentActivity.class);
                i.putExtra("loginUserFbId", loginUserFbId);
                i.putExtra("oppFbId",oppFbId);
                i.putExtra("dialogId", dialogId);
                i.putExtra("from", "privatechatsubs");
                startActivity(i);
            }
        }



//        txtuserName.setText(sharedPreferences.getString(m_config.Entered_User_Name,""));
//        txtuserEmail.setText(sharedPreferences.getString(m_config.Entered_Email,""));
//        txtuserDOB.setText(loggedInUserInformation.getFB_USER_BIRTHDATE());
//        txtuserGender.setText(loggedInUserInformation.getFB_USER_GENDER());

//        String url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
//
//        if(url.equals(null) || url.equals("") || url.equals("N/A"))
//        {
//            url = "";
//        }
//        else
//        {
//            url = loggedInUserInformation.getFB_USER_PROFILE_PIC();
//        }
//
//        if(!url.equals("") || !url.equals(null) || !url.equals("N/A"))
//        {
//            Picasso.with(cont).load(url).fit().centerCrop()
//                    .placeholder(R.drawable.placeholder_user)
//                    .error(R.drawable.placeholder_user)
//                    .into(imguser);
//        }


        //if(.equals("")){
//            try {
//                UserTable user = m_config.mapper.load(UserTable.class, loggedInUserInformation.getFB_USER_ID());
//                txt_Header.setText("Welcome " + user.getName());
//            }catch (Exception e){
//
//            }
              txt_Header.setText("Welcome " + sharedPreferences.getString(m_config.Entered_User_Name,""));




//        }else{
//            txt_Header.setText("Welcome " +sharedPreferences.getString(m_config.Entered_User_Name,""));
//        }


        btn_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);
                alertDialogBuilder
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setCancelable(false)
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        LISessionManager.getInstance(getApplicationContext()).clearSession();
                                        LoginManager.getInstance().logOut();
                                        sqldb.delete("UserTable", "fb_user_id='" + loggedInUserInformation.getFB_USER_ID()  + "'", null);


                                        Log.e("----"," "+LoginValidations.getFBAccessToken());
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(m_config.FinalStepDone,"No");
                                        editor.putString(m_config.LoggedInFBUserID,"N/A");
                                        editor.putString(m_config.Entered_User_Name,"");
                                        editor.putString(m_config.Entered_Email,"");
                                        editor.putString(m_config.Entered_Contact_No,"");
                                        editor.putString(m_config.BasicFBLIValidationsDone,"No");
                                        editor.putString(m_config.FaceDetectDone,"No");
                                        editor.putString(m_config.OTPValidationDone,"No");
                                        editor.putString(m_config.FinalStepDone,"No");
                                        editor.putString(m_config.FBLoginDone,"No");
                                        editor.putString(m_config.LILoginDone,"No");
                                        editor.apply();

                                        if(LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken() == null)
                                        {
                                            Log.e("Logged out from " ,"Linked In");
                                        }

                                        Log.e("Logged Out","Yes    "  + "  aa");//.getAccessToken().toString());
                                        finish();
                                        Intent intent = new Intent(cont, Welcome.class);
                                        intent.putExtra("from","homepage");
                                        startActivity(intent);

                                    }
                                });
                alertDialogBuilder.show();
            }
        });

        btnTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

//                Intent i = new Intent(HomePageActivity.this, TipsActivity.class);
//                startActivity(i);

                SetLocalNotifications.setLNotificationforPrivateChat(cont, Long.parseLong("1471519800000"), "87878" , "57985d4aa28f9adb3b00006a", "777777777777");


            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                hp_pd.setMessage("Loading");
                hp_pd.setCancelable(false);
                hp_pd.show();

                Log.e("ChatService.getInstance().getCurrentUser()", " " + ChatService.getInstance().getCurrentUser());
                if (ChatService.getInstance().getCurrentUser() == null)
                {
                    String accessToken = LoginValidations.getFBAccessToken().getToken();

                    QBSessionClass.getInstance().getQBSession(new QBEntityCallback()
                    {

                        @Override
                        public void onSuccess(Object o, Bundle bundle)
                        {
                            Handler h = new Handler(cont.getMainLooper());
                            h.post(new Runnable() {
                                @Override
                                public void run() {


                                    new AsyncFBFaces().execute();
                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e)
                        {

                            GenerikFunctions.hDialog();
                            GenerikFunctions.showToast(cont, "There was an error connecting to network, Please try after some time.");

                        }

                    }, accessToken, null, cont);


                }
                else
                {
                    Log.e("here ","iiinnn");
                    new AsyncFBFaces().execute();
                }




            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                //Log.e("--current user -- "," "+ChatService.getInstance().getCurrentUser());
                Intent i = new Intent(HomePageActivity.this, DialogsActivity.class);
                startActivity(i);

            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                Intent i = new Intent(HomePageActivity.this, HistoryActivity.class);
                startActivity(i);

            }
        });

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

                Intent i = new Intent(HomePageActivity.this, HostActivity.class);
                startActivity(i);


            }
        });

        btnGateCrasher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!GenerikFunctions.chkStatus(cont)) {
                    GenerikFunctions.showToast(cont, "Check Your Network Connectivity");
                    return;
                }

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
                                //finish();
                                Intent i = new Intent(Intent.ACTION_MAIN);
                                i.addCategory(Intent.CATEGORY_HOME);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);



                            }
                        });

                    alertDialogBuilder.show();


    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }


    public class AsyncFBFaces extends AsyncTask<Void,Void,Void>
    {
        UserTable selUserData = null;

        @Override
        protected void onPreExecute()
        {
//            hp_pd.setMessage("Loading");
//            hp_pd.setCancelable(false);
//            hp_pd.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            Log.e("doInBackground", "");
            try
            {
                selUserData = m_config.mapper.load(UserTable.class, loggedInUserInformation.getFB_USER_ID());
                Log.e("selUserData", " " + selUserData);
            }
            catch (Exception e)
            {

                Log.e("doInBackground", "----e----");
                e.printStackTrace();
                if(hp_pd != null){
                    hp_pd.dismiss();
                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if(selUserData == null)
            {
                if(hp_pd != null){
                    hp_pd.dismiss();
                }
            }
            else
            {
                if(selUserData.getImageflag().equals("Yes"))
                {
                    List<String> image = selUserData.getProfilePicUrl();

                    String[] images = new String[image.size()];
                    for(int q=0;q<image.size();q++)
                    {

                        images[q]=image.get(q);
                        Log.e("Images",images[q]);
                    }

                    Intent i = new Intent(cont, SettingsActivity.class);
                    Bundle b=new Bundle();
                    b.putStringArray("images", images);
                    //i.putStringArrayListExtra("img",validPics);
                    i.putExtras(b);
                    cont.startActivity(i);
                }
                else
                {
                    getFBProfilePictures(selUserData.getProfilePicUrl());
                }
            }

        }
    }

    public void getFBProfilePictures(final List<String> profilePic)
    {

        File f = new File(profilePic.get(0));
        String profilepicname = f.getName();
        profilepicname = profilepicname.split("\\?")[0];
        Log.e("profilepicname"," "+profilepicname);

        final String finalProfilepicname = profilepicname;
        GraphRequest request1 = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        profilePics = new ArrayList<String>();
                        validPics = new ArrayList<String>();
                        ArrayList imgsplit = new ArrayList();
                        try
                        {

                            JSONObject FBAlbum = object;
                            JSONObject Albums = FBAlbum.getJSONObject("albums");
                            //Log.e(" in "," getFBProfilePictures Albums"+Albums);
                            JSONArray data = Albums.getJSONArray("data");

                            for (int i = 0 ; i<data.length(); i++)
                            {
                                Log.e(" i "," "+data.getJSONObject(i).getString("name"));

                                JSONObject AlbumContainer = data.getJSONObject(i);
                                if(AlbumContainer.getString("name").equals("Profile Pictures"))
                                {
                                    JSONObject photos = AlbumContainer.getJSONObject("photos");
                                    JSONArray innerData = photos.getJSONArray("data");

                                    for(int j=0;j<innerData.length();j++)
                                    {
                                        JSONObject innerAlbum = innerData.getJSONObject(j);

                                        File f = new File(innerAlbum.getString("picture"));
                                        String albimagename = f.getName();
                                        albimagename = albimagename.split("\\?")[0];
                                        Log.e("imagename"," "+albimagename);

                                        profilePics.add(innerAlbum.getString("picture"));
                                        imgsplit.add(albimagename);

                                    }

                                    if(imgsplit.contains(finalProfilepicname))
                                    {
                                        String imgpos0 = profilePics.get(0);
                                        int posprPic = imgsplit.indexOf(finalProfilepicname);
                                        String imgposprPic = profilePics.get(imgsplit.indexOf(finalProfilepicname));

                                        profilePics.set(0,imgposprPic);
                                        profilePics.set(posprPic, imgpos0);

                                    }
                                    else{
                                        profilePics.set(0,profilePic.get(0));
                                    }



                                    break;
                                }
                            }


                            for(int i=0;i<profilePics.size();i++)
                            {
                                Log.e("Index PP "+ i,profilePics.get(i));
                            }
                            FaceDetectteo(0);
                        }
                        catch(Exception e)
                        {
                            if(hp_pd != null){
                                hp_pd.dismiss();
                            }
                        }

                    }
                });

        Bundle parameters1 = new Bundle();
        //parameters1.putString("fields", "albums.fields(name,photos.fields(name,picture,source,created_time))");//,)");
        parameters1.putString("fields", "albums.fields(name,photos.fields(name,picture,source,created_time))");
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

          //  m_config.pDialog.dismiss();

            Intent i = new Intent(HomePageActivity.this, SettingsActivity.class);
            Bundle b=new Bundle();
            b.putStringArray("images", test);
            //i.putStringArrayListExtra("img",validPics);
            i.putExtras(b);
            startActivity(i);
        }
        else
        {

            if(validPics.size() == 5)
            {
                Log.e("In outer if","Yes");
                String[] test = new String[validPics.size()];

                for(int  p=0;p<validPics.size();p++)
                {
                    test[p] = validPics.get(p);
                }

               // m_config.pDialog.dismiss();
                Intent i = new Intent(HomePageActivity.this, SettingsActivity.class);
                Bundle b=new Bundle();
                b.putStringArray("images", test);
                //i.putStringArrayListExtra("img",validPics);
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
                                    //Log.e("Index  " + i,"Faces :  "+faces);
                                    //Set  Face detect flag here to true
                                    if(validPics.size()==5)
                                    {
                                        i++;
                                        FaceDetectteo(i);
                                    }
                                    else
                                    {
                                        validPics.add(url);
                                        i++;
                                        FaceDetectteo(i);
                                    }
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
                               // m_config.pDialog.dismiss();

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
    }




}
