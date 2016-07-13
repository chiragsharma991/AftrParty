package com.aperotechnologies.aftrparties.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DBOperations.DBHelper;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.Login.LoginTableColumns;
import com.aperotechnologies.aftrparties.Login.RegistrationActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.Age;
import com.aperotechnologies.aftrparties.Reusables.AgeCalculator;
import com.aperotechnologies.aftrparties.Reusables.EditTextPopUpFixed;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.utils.SeekbarWithIntervals;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.aperotechnologies.aftrparties.Reusables.Validations.decodeFile;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getImageUri;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getOutputMediaFileUri;


/**
 * Created by hasai on 19/05/16.
 */
public class SettingsActivity extends Activity
{
    Context cont;
    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;
    CircularImageView imguser;
    EditTextPopUpFixed edt_usermsgStatus, edt_Age, edt_userName;
    TextView txtrangeseekbarval, txtseekbarval;
    //    RangeSeekBar rangeSeekBar;
    SeekBar SeekBar;
    SeekbarWithIntervals seekbarWithIntervals = null;
    String seekbarVal = "";
    Spinner spn_Gender, spn_mask;
    Button img_editSettings;
    String selected_Gender, selected_maskStatus = "Mask";
    String FacebookID;
    int selectedDistVal = 1;
    Uri fileUri;
    String picturePath = "";


    FaceOverlayView faceOverlayView;
    List<String> valid;
    public static ArrayList<String> validPics;
   // GridView images;
    LinearLayout lineimg;
    String user_name,user_gender,user_status,user_mask_unmask,user_profilepic,user_age;
    String[] imagesArray;
    Handler h;
    //ProgressDialog progressDialog;

    DBHelper helper;
    SQLiteDatabase sqldb;
    UserTable userTable;

    String ImageFlag;
    RecyclerView recyclerView;
    Activity act = this;

    public  static ProgressDialog sett_pd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont  = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
       // m_config.pDialog = new ProgressDialog(cont);

        helper= DBHelper.getInstance(cont);
        sqldb=helper.getWritableDatabase();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imguser = (CircularImageView) findViewById(R.id.userimage);
        edt_usermsgStatus = (EditTextPopUpFixed) findViewById(R.id.edt_usermsgStatus);
        edt_Age = (EditTextPopUpFixed) findViewById(R.id.edt_Age);
        edt_userName = (EditTextPopUpFixed) findViewById(R.id.edt_userName);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SettingsActivity.this, LinearLayoutManager.HORIZONTAL, false));


        txtseekbarval = (TextView) findViewById(R.id.txtseekbarval);

        SeekBar = (SeekBar) findViewById(R.id.seekbar);

        img_editSettings = (Button) findViewById(R.id.img_editSettings);
        faceOverlayView =(FaceOverlayView) findViewById(R.id.face_overlay);

        sett_pd = new ProgressDialog(this);

        // Spinner for Mask/UnMask
        spn_mask = (Spinner) findViewById(R.id.spn_maskStatus);
        List<String> maskList = new ArrayList<String>();
        //maskList.add("Select Mask Status");
        maskList.add("Mask");
        maskList.add("Unmask");

        // Spinner for Gender
        spn_Gender = (Spinner) findViewById(R.id.spn_Gender);
        List<String> genderList = new ArrayList<String>();
        genderList.add("Select Gender");
        genderList.add("Male");
        genderList.add("Female");

        final ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, genderList);
        genderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Gender.setAdapter(genderadapter);

        final ArrayAdapter<String> maskStatus = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, maskList);
        maskStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_mask.setAdapter(maskStatus);

        FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        Bundle b=this.getIntent().getExtras();
        imagesArray = b.getStringArray("images");
        validPics = new ArrayList<String>();

        for(int i=0;i<imagesArray.length;i++)
        {
            validPics.add(imagesArray[i]);
            Log.e("Received images",validPics.get(i));
        }


        if(imagesArray.length<5)
        {
            Log.e("Get LocalImages","Yes");
        }

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    setUserInfo();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        /*1111*/

    final int stepSize = 10;
    SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            progress = ((int)Math.round(progress/stepSize))*stepSize;
            seekBar.setProgress(progress);
            txtseekbarval.setText(progress + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    });



    imguser.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            int listCount = recyclerView.getAdapter().getItemCount();
            if(listCount == 5)
            {
                Toast.makeText(cont,"Only 5 images are allowed. Please remove atleast one image first to upload new",Toast.LENGTH_LONG).show();
            }
            else
            {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SettingsActivity.this);

                builderSingle.setTitle("Add Photo");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        SettingsActivity.this, android.R.layout.select_dialog_item);
                arrayAdapter.add("Camera");
                arrayAdapter.add("Gallery");

                builderSingle.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which==0)
                                {
                                    if ((int) Build.VERSION.SDK_INT < 23)
                                    {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        fileUri = getOutputMediaFileUri(ConstsCore.MEDIA_TYPE_IMAGE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                        intent.putExtra("return-data", true);
                                        // start the image capture Intent
                                        startActivityForResult(intent, ConstsCore.CAMERA_REQUEST);
                                    }
                                    else
                                    {

                                        int permissionCheck = ContextCompat.checkSelfPermission(SettingsActivity.this,
                                                android.Manifest.permission.CAMERA);

                                        if(permissionCheck== PackageManager.PERMISSION_GRANTED)
                                        {
                                            permissionCheck = ContextCompat.checkSelfPermission(SettingsActivity.this,
                                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                            //here
                                            int permissioncheckRead= ContextCompat.checkSelfPermission(SettingsActivity.this,
                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE);

                                            if(permissionCheck == PackageManager.PERMISSION_GRANTED && permissioncheckRead == PackageManager.PERMISSION_GRANTED)
                                            {
                                                //Open Camera Here
                                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                fileUri = getOutputMediaFileUri(ConstsCore.MEDIA_TYPE_IMAGE);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                intent.putExtra("return-data", true);
                                                // start the image capture Intent
                                                startActivityForResult(intent, ConstsCore.CAMERA_REQUEST);
                                            }
                                            else
                                            {
                                                //Get Permission for read and write
                                                Log.i("Have camera but ","Not RW for camera permision");
                                                Log.i("Ask for camera RW perm","Yes");
                                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                            }
                                        }
                                        else
                                        {
                                            // Log.i("Dont have camera permission", "Else bliock");
                                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                                    new String[]{android.Manifest.permission.CAMERA},
                                                    ConstsCore.MY_PERMISSIONS_REQUEST_CAMERA);
                                        }
                                    }
                                }
                                else
                                {
                                    if ((int) Build.VERSION.SDK_INT < 23)
                                    {
                                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        i.setType("image/*");
                                        startActivityForResult(i, ConstsCore.GALLERY_REQUEST);

                                    }
                                    else
                                    {
                                        Log.i("Ask for Read permission","Yes Ask ");
                                        Log.i("Check for Read permission", "Yes check");

                                        int permissionCheck = ContextCompat.checkSelfPermission(SettingsActivity.this,
                                                android.Manifest.permission.READ_EXTERNAL_STORAGE);

                                        Log.i("permission Check",permissionCheck+"   aaa");

                                        if(permissionCheck==PackageManager.PERMISSION_GRANTED)
                                        {
                                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            i.setType("image/*");
                                            startActivityForResult(i, ConstsCore.GALLERY_REQUEST);
                                        }
                                        else
                                        {
                                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    ConstsCore.MY_PERMISSIONS_REQUEST_R);
                                        }
                                    }
                                }
                            }
                        });

                builderSingle.show();
            }
        }
    });


    // spinner selection for Gender
    spn_Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selected_Gender = parent.getSelectedItem().toString().trim();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
        }
    });

    img_editSettings.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edt_usermsgStatus.getWindowToken(), 0);
//            m_config.pDialog.setMessage("Updating User");
//            m_config.pDialog.setCancelable(false);
//            m_config.pDialog.show();
            editSettings();
        }
    });

    // spinner selection for Mask
    spn_mask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Log.e("selected_maskStatus"," "+selected_maskStatus);
            if(parent.getSelectedItem().toString().trim().equals("Unmask"))
            {
                //ask for in-app purchase
                Log.e("ask for in-app purchase"," ");
                selected_maskStatus = "Unmask";
            }
            else
            {
                selected_maskStatus = "Mask";
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
        }
     });

    }

    public void getFBProfilePictures()
    {
        Log.e("Valid Pics ",validPics.size()+"");

//        for(int i=0;i<validPics.size();i++)
//        {
//            Log.e("Valid Pics getFBProfilePictures",validPics.get(i)+"");
//        }
     //   lineimg.removeAllViews();

        Log.e("Removed all  objects","Yes");

        RecyclerViewAdapter  adapter = new RecyclerViewAdapter(SettingsActivity.this, validPics,imguser);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        for(int i=0;i<validPics.size();i++)
        {
          //  Log.e("Inside for",i + "   " + validPics.get(i));
            ImageView img = new CircularImageView(SettingsActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(10,10,10,10);
            params.gravity = Gravity.RIGHT;
            img.setLayoutParams(params);

            if(validPics.get(i).toString().contains("http") || validPics.get(i).toString().contains("https"))
            {
                Picasso.with(SettingsActivity.this)
                        .load((String) validPics.get(i))
                        .into(img);
            }
            else
            {
                Uri uri = Uri.fromFile(new File(validPics.get(i)));
                Picasso.with(SettingsActivity.this).load(uri)
                        .into(img);
            }
        }

//        if(progressDialog.isShowing())
//        {
//            progressDialog.dismiss();
//            progressDialog.cancel();
//        }
    }


    public void setUserInfo()
    {

        Log.e("Inside setUserInfo","setUserInfo");
//        progressDialog.setMessage("Loading");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        try
        {
            userTable = m_config.mapper.load(UserTable.class, FacebookID);

        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
        Log.e("Inside setUserInfo","setUserInfo");
        if(userTable == null)
        {
        }
        else
        {
            //user_name,user_gender,user_status,user_mask_unmask
            user_name = sharedPreferences.getString(m_config.Entered_User_Name,"");
            user_status = "N/A";
            Log.e("Status ",userTable.getProfileStatus() +"   aa");
            user_status = userTable.getProfileStatus();
            user_mask_unmask = userTable.getcurrentmaskstatus()+"";


            user_profilepic = userTable.getProfilePicUrl().get(0);
            Log.e("User_profilepicurl",user_profilepic);
            user_gender = sharedPreferences.getString(m_config.GenderPreference,"");
            seekbarVal = sharedPreferences.getString(m_config.Distance,"");
            String DOB = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE();

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

            try
            {
                Date date = format.parse(DOB);
                Log.e("Parsed Date", date + "");
                Age  age = AgeCalculator.calculateAge(date);
                user_age = age.toString();
                Log.e("Calculated AGe", user_age.toString() + "    " + ConstsCore.FB_AGE);
            }
            catch(Exception e)
            {
                Log.e("Date COnversion error",e.toString());
            }

             h = new Handler(cont.getMainLooper());
             h.post(new Runnable()
             {
                @Override
                public void run()
                {
                    Log.e("Inside setUserInfo","setUserInfo  11");

                    edt_userName.setText(user_name);
                    edt_Age.setText(user_age);
                    edt_userName.setEnabled(false);
                    edt_Age.setEnabled(false);
                }
             });

            if(user_status.equals("N/A") || user_status.equals(null) || user_status.length() == 0 || user_status.equals("") || user_status == null)
            {
                h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("Inside setUserInfo","setUserInfo  22");
                        edt_usermsgStatus.setHint("Enter  your status message");
                    }
                });
            }
            else
            {
                h = new Handler(cont.getMainLooper());
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("Inside setUserInfo","setUserInfo  22");
                        edt_usermsgStatus.setText(user_status);
                    }
                });
            }

            if(user_mask_unmask.equals("Mask"))
            {
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("Inside setUserInfo","setUserInfo  33");
                        spn_mask.setSelection(0);

                    }
                });
            }
            else  if(user_mask_unmask.equals("Unmask"))
            {
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("Inside setUserInfo","setUserInfo  44");
                        spn_mask.setSelection(1);
                        spn_mask.setEnabled(false);
                    }
                });
            }
//            else
//            {
//                h.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Log.e("Inside setUserInfo","setUserInfo  44");
//                        spn_mask.setSelection(0);
//
//                    }
//                });
//
//            }
            Log.e("Inside setUserInfo","setUserInfo  55");

            if(user_profilepic.equals("N/A"))
            {
                h.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        Picasso.with(cont).load(R.drawable.placeholder_user).fit().centerCrop()
                                .placeholder(R.drawable.placeholder_user)
                                .error(R.drawable.placeholder_user)
                                .into(imguser);

                    }

                });
            }
            else
            {
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        Picasso.with(cont).load(user_profilepic).fit().centerCrop()
                                .placeholder(R.drawable.placeholder_user)
                                .error(R.drawable.placeholder_user)
                                .into(imguser);
                        m_config.PrimaryUrl = user_profilepic;
                        imguser.setTag("0");
                    }
                });

            }

            if(user_gender.equals("Female"))
            {

                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        spn_Gender.setSelection(2);

                    }
                });
            }
            else if(user_gender.equals("Male"))
            {

                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        spn_Gender.setSelection(1);

                    }
                });
            }
            else
            {
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        spn_Gender.setSelection(0);

                    }
                });
            }

            if(seekbarVal.equals(""))
            {

                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String maxText = String.valueOf(10);
                        //txtrangeseekbarval.setText(maxText);
                        txtseekbarval.setText(maxText);
                        //rangeSeekBar.setSelectedMaxValue(2);
                        SeekBar.setProgress(10);
                        //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
                        //selectedDistVal = Integer.parseInt(seekbarVal);
                    }
                });
            }
            else
            {
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e("came here"," ");
                        //txtrangeseekbarval.setText(seekbarVal);
                        txtseekbarval.setText(seekbarVal);
                        int value = Integer.parseInt(seekbarVal);
                        //rangeSeekBar.setSelectedMaxValue(value);
                        SeekBar.setProgress(value);
                        //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
                        //selectedDistVal = Integer.parseInt(seekbarVal);
                    }
                });
            }
            h.post(new Runnable()
            {
                @Override
                public void run()
                {
                    getFBProfilePictures();
                }
            });

//            progressDialog.dismiss();

//            progressDialog.cancel();
//            m_config.pDialog.dismiss();
//            m_config.pDialog.cancel();

            if(HomePageActivity.hp_pd!=null)
            {
                if(HomePageActivity.hp_pd.isShowing())
                {
                    HomePageActivity.hp_pd.dismiss();
                }
            }

        }
    }

    private List<String> getIntervals()
    {
        return new ArrayList<String>() {{
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");

        }};
    }

//    private SeekbarWithIntervals getSeekbarWithIntervals()
//
//    {
//        if (seekbarWithIntervals == null)
//        {
//            Log.e("---------------","");
//            seekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);//
//            seekbarWithIntervals.setProgress(Integer.parseInt(seekbarVal));
//            selectedDistVal = Integer.parseInt(seekbarVal);//
//        }
//
//        seekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
//        {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//            {
//                selectedDistVal = progress;
//                progress = progress + 1;
//                Log.e("progress"," "+progress);//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        return seekbarWithIntervals;
//    }

    private void editSettings()
    {
        sett_pd.setMessage("Updating User Information");
        sett_pd.setCancelable(false);
        sett_pd.show();
        Log.e("picturePath ", " " + picturePath);
        String profileStatus = "N/A";
        if (edt_usermsgStatus.getText().toString().trim().length() == 0 || edt_usermsgStatus.getText().toString().trim().equals("")) {
            profileStatus = "N/A";
        }
        else
        {
            profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();

        }

        Log.e("Profile Statis", profileStatus);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProfileStatus", profileStatus);
        if(selected_Gender.equals("Select Gender")){
            editor.putString(m_config.GenderPreference, "N/A");
        }else{
            editor.putString(m_config.GenderPreference, selected_Gender);
        }

        editor.putString(m_config.Distance, txtseekbarval.getText().toString());
        // editor.putString("Distance", String.valueOf(selectedDistVal));
        editor.apply();

        int index = 0;
        Log.e("M_config.primaryurl", m_config.PrimaryUrl + "  aa");
        Log.e("validPics.get(0)", validPics.get(0));
        if (m_config.PrimaryUrl.equals(validPics.get(0))) {

        } else {
            for (int i = 0; i < validPics.size(); i++) {
                if (m_config.PrimaryUrl.equals(validPics.get(i))) {
                    index = i;
                    break;
                }
            }
            String temp = validPics.get(0);
            validPics.set(0, m_config.PrimaryUrl);
            validPics.set(index, temp);
        }

        for (int i = 0; i < validPics.size(); i++) {
            Log.e("Before upload   " + i, validPics.get(i));
        }

        ImageFlag = "Yes";

        updateUserSettings(m_config.PrimaryUrl, profileStatus, cont, validPics, ImageFlag);

    }



    public void updateQBProfilePic(String fbId,final String profileStatus, final String pic)
    {
        QBUsers.getUserByFacebookId(fbId, new QBEntityCallback<QBUser>()
        {
            @Override
            public void onSuccess(QBUser user, Bundle args)
            {
                user.setCustomData(pic);

                QBUsers.updateUser(user, new QBEntityCallback<QBUser>()
                {
                    @Override
                    public void onSuccess(QBUser user, Bundle args)
                    {
                        Log.e("Before restart","Yes");
                        GenerikFunctions.showToast(cont, "Profile updated successfully");

                        Log.e("Updating view","yes");
                        Log.e("Valid Pics size","Yes "+validPics.size());

                        recyclerView.setAdapter(null);
                        RecyclerViewAdapter  adapter = new RecyclerViewAdapter(SettingsActivity.this, validPics,imguser);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        edt_usermsgStatus.setText(profileStatus);
                        if(sett_pd.isShowing())
                        {
                            sett_pd.dismiss();
                        }
                    }

                    @Override
                    public void onError(QBResponseException errors)
                    {
                        if(sett_pd.isShowing())
                        {
                            sett_pd.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors)
            {
                if(sett_pd.isShowing())
                {
                    sett_pd.dismiss();
                }
            }
        });
    }



    /** update user in UserTable in settings page **/
    public  void updateUserSettings(String picturePath, String profileStatus, Context cont, ArrayList<String> validPics, String ImagesFlag)
    {

        String[] images = new String[validPics.size()];
        for(int i=0;i<validPics.size();i++)
        {
            Log.e("updateUserSettings    "+i,validPics.get(i));
            images[i] =validPics.get(i);
        }

        Configuration_Parameter m_config = Configuration_Parameter.getInstance();
        String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        try
        {
            //Update SQLIte table profile pic

            String updateUser = "Update " + LoginTableColumns.USERTABLE + " set " +

                    LoginTableColumns.FB_USER_PROFILE_PIC + " = '" + validPics.get(0) + "'  where "
                    + LoginTableColumns.FB_USER_ID + " = '" + FacebookID + "'";

            Log.i("update User  "+ LoginTableColumns.FB_USER_ID , updateUser);
            sqldb.execSQL(updateUser);

            //Update AWS user entry



            UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);

            userTable.setProfileStatus(profileStatus);
            userTable.setProfilePicUrl(validPics);
            userTable.setcurrentmaskstatus(selected_maskStatus);
            userTable.setImageflag("Yes");
            m_config.mapper.save(userTable);

            updateQBProfilePic(FacebookID,profileStatus,validPics.get(0));
        }
        catch(Exception e)
        {
            if(sett_pd.isShowing())
            {
                sett_pd.dismiss();
            }

            e.printStackTrace();
            GenerikFunctions.showToast(cont, "Profile updation failed, Please try again after some time");
            GenerikFunctions.hideDialog(m_config.pDialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == ConstsCore.CAMERA_REQUEST)
            {
                // Camera click
                try
                {
                    Bitmap myTest = decodeFile(fileUri.getPath());
                    Uri tempUri = getImageUri(getApplicationContext(), myTest);
                    picturePath = getpath(tempUri);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath,bmOptions);
                    int value = 0;
                    if (bitmap.getHeight() <= bitmap.getWidth())
                    {
                        value = bitmap.getHeight();
                    }
                    else
                    {
                        value = bitmap.getWidth();
                    }
                    Bitmap finalbitmap = null;
                    finalbitmap = Bitmap.createScaledBitmap(bitmap,value,value,true);

                    int faces = faceOverlayView.setBitmap(finalbitmap);
                    if(faces>0)
                    {
                        validPics.add(picturePath);
                        getFBProfilePictures();

                        picturePath = Validations.getUrlfromCloudinary(cont,picturePath);
                        m_config.PrimaryUrl = picturePath;
                        Log.e("New URL",picturePath+"");
                        validPics.set((validPics.size()-1),picturePath);

                        imguser.setImageBitmap(finalbitmap);
                        imguser.setTag(""+(validPics.size()-1));
                    }
                    else
                    {
                        picturePath = "N/A";
                    }
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (requestCode == ConstsCore.GALLERY_REQUEST)
            {
                // Gallery Selection
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath,bmOptions);
                int value = 0;
                if (bitmap.getHeight() <= bitmap.getWidth())
                {
                    value = bitmap.getHeight();
                }
                else
                {
                    value = bitmap.getWidth();
                }
                Bitmap finalbitmap = null;
                finalbitmap = Bitmap.createScaledBitmap(bitmap,value,value,true);

                int faces = faceOverlayView.setBitmap(finalbitmap);

                if(faces>0)
                {
                    validPics.add(picturePath+"");

                    picturePath = Validations.getUrlfromCloudinary(cont,picturePath);
                    validPics.set((validPics.size()-1),picturePath);
                    m_config.PrimaryUrl = picturePath;

                    imguser.setImageBitmap(finalbitmap);
                    imguser.setTag(""+(validPics.size()-1));
                    getFBProfilePictures();
                }
                else
                {
                    picturePath = "N/A";
                }
            }
        }
    }

    public String getpath(Uri imageUri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null,null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        Log.i("column_index", cursor.getString(column_index));

        return cursor.getString(column_index);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case ConstsCore.MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted
                    //  Toast.makeText(StyleDetailsActivity.this, "Has Camera Permission And now ask for read write permission",Toast.LENGTH_LONG).show();
                    Log.i("Ask for read write ", "From nested ask");
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                }
                else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    // permission was not granted
                    if (cont == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, android.Manifest.permission.CAMERA))
                    {
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_camera_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                                new String[]{android.Manifest.permission.CAMERA},
                                                ConstsCore.MY_PERMISSIONS_REQUEST_CAMERA);
                                    }
                                })
                                .setNegativeButton("I'm Sure", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // do nothing
                                    }
                                })
                                .show();
                        break;
                    }
                    else {


                    }
                }
                break;
            }
            case ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM :
            {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(ConstsCore.MEDIA_TYPE_IMAGE);
                    in.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    in.putExtra("return-data", true);
                    startActivityForResult(in, ConstsCore.CAMERA_REQUEST);
                    break;
                }
                else
                {
                    // permission was not granted
                    if (cont == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_rw_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                    }
                                })
                                .setNegativeButton("I'm Sure", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                        break;

                    }
                    else
                    {

                    }
                    break;
                }
            }
            case ConstsCore.MY_PERMISSIONS_REQUEST_R :
            {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, ConstsCore.GALLERY_REQUEST);
                    break;
                }
                else
                {
                    // permission was not granted
                    if (cont == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        //showStoragePermissionRationale();
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_rw_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                    }
                                })
                                .setNegativeButton("I'm Sure", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // do nothing
                                    }
                                })
                                .show();
                        break;
                    }
                    else
                    {

                    }

                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(cont, HomePageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }
}

/*1111*/

//        final RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(this);
//        // Set the range
//        rangeSeekBar.setRangeValues(0, 20);
//        rangeSeekBar.setSelectedMinValue(0);
//        rangeSeekBar.setSelectedMaxValue(10);
//
//
//        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
//            @Override
//            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
//                Log.e("value "," "+rangeSeekBar.getAbsoluteMinValue());
//                Log.e("value "," "+rangeSeekBar.getSelectedMinValue());
//            }

//        });
//
//
//



//
//        // Add to layout
//        LinearLayout layout = (LinearLayout) findViewById(R.id.seekbar_placeholder);
//        layout.addView(rangeSeekBar);

//        rangeSeekBar.setNotifyWhileDragging(true);
//        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
//            @Override
//            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
//
//                txtrangeseekbarval.setText(String.valueOf(maxValue));
//            }
//        });




