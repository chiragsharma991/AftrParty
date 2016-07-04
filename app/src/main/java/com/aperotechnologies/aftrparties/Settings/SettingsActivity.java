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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.Login.FaceOverlayView;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.Age;
import com.aperotechnologies.aftrparties.Reusables.AgeCalculator;
import com.aperotechnologies.aftrparties.Reusables.EditTextPopUpFixed;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.utils.SeekbarWithIntervals;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    String seekbarVal = "1";
    Spinner spn_Gender, spn_mask;
    Button img_editSettings;
    String selected_Gender, selected_maskStatus = "mask";
    String FacebookID;
    int selectedDistVal = 1;
    Uri fileUri;
    String picturePath = "";


    FaceOverlayView faceOverlayView;
    List<String> valid;
    ArrayList<String> validPics;
   // GridView images;
    LinearLayout lineimg;
    String user_name,user_gender,user_status,user_mask_unmask,user_profilepic,user_age;
    String[] imagesArray;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cont  = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imguser = (CircularImageView) findViewById(R.id.userimage);
        edt_usermsgStatus = (EditTextPopUpFixed) findViewById(R.id.edt_usermsgStatus);
        edt_Age = (EditTextPopUpFixed) findViewById(R.id.edt_Age);
        edt_userName = (EditTextPopUpFixed) findViewById(R.id.edt_userName);

        //txtrangeseekbarval = (TextView) findViewById(R.id.txtrangeseekbarval);
        txtseekbarval = (TextView) findViewById(R.id.txtseekbarval);
        //rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeseekbar);
        SeekBar = (SeekBar) findViewById(R.id.seekbar);
        //images = (GridView)findViewById(R.id.imggrid);
        lineimg = (LinearLayout)findViewById(R.id.lineimages);

        //List<String> seekbarIntervals = getIntervals();
        //getSeekbarWithIntervals().setIntervals(seekbarIntervals);
        img_editSettings = (Button) findViewById(R.id.img_editSettings);
        faceOverlayView =(FaceOverlayView) findViewById(R.id.face_overlay);

        // Spinner for Mask/UnMask
        spn_mask = (Spinner) findViewById(R.id.spn_maskStatus);
        List<String> maskList = new ArrayList<String>();
        maskList.add("Select Mask Status");
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
        valid =  Arrays.asList(imagesArray);
        validPics = (ArrayList<String>) valid;

        if(imagesArray.length<5)
        {
            Log.e("Get LocalImages","Yes");
        }

        setUserInfo();
        /*1111*/

    final int stepSize = 1;
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
            m_config.pDialog.setMessage("Updating User");
            m_config.pDialog.setCancelable(false);
            m_config.pDialog.show();
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
        for(int i=0;i<validPics.size();i++)
        {
            ImageView img = new CircularImageView(SettingsActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(10,10,10,10);
            params.gravity = Gravity.RIGHT;
            img.setLayoutParams(params);
            Picasso.with(SettingsActivity.this)
                    .load((String) validPics.get(i))
                    .into(img);

            lineimg.addView(img);
            final int  p =i;
            img.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(SettingsActivity.this,"Image at position "+ p +"  clicked",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void setUserInfo()
    {
        UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);
        if(userTable == null)
        {
        }
        else
        {
            //user_name,user_gender,user_status,user_mask_unmask
            user_name = sharedPreferences.getString(m_config.Entered_User_Name,"");
            user_status = userTable.getProfileStatus();
            user_mask_unmask = userTable.getCurrentmaskstatus()+"";
            user_profilepic = userTable.getProfilePicUrl().get(0);

            user_gender = sharedPreferences.getString("Gender","");
            seekbarVal = sharedPreferences.getString("Distance","1");
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

            edt_userName.setText(user_name);
            edt_Age.setText(user_age);
            edt_Age.setEnabled(false);

            if(user_status.equals("N/A"))
            {
                edt_usermsgStatus.setHint("Enter  your status message");
            }
            else
            {
                edt_usermsgStatus.setText(user_status);
            }

            if(user_mask_unmask.equals("Yes"))
            {
                spn_mask.setSelection(1);
            }
            else  if(user_mask_unmask.equals("No"))
            {
                spn_mask.setSelection(2);
            }
            else
            {
                spn_mask.setSelection(0);
            }

            if(user_profilepic.equals("N/A"))
            {
                Picasso.with(cont).load(R.drawable.placeholder_user).fit().centerCrop()
                        .placeholder(R.drawable.placeholder_user)
                        .error(R.drawable.placeholder_user)
                        .into(imguser);
            }
            else
            {
                Picasso.with(cont).load(user_profilepic).fit().centerCrop()
                        .placeholder(R.drawable.placeholder_user)
                        .error(R.drawable.placeholder_user)
                        .into(imguser);
            }

            if(user_gender.equals("Female"))
            {
                spn_Gender.setSelection(2);
            }
            else if(user_gender.equals("Male"))
            {
                spn_Gender.setSelection(1);
            }
            else
            {
                spn_Gender.setSelection(0);
            }

            if(seekbarVal.equals(""))
            {
                String maxText = String.valueOf(2);
                //txtrangeseekbarval.setText(maxText);
                txtseekbarval.setText(maxText);
                //rangeSeekBar.setSelectedMaxValue(2);
                SeekBar.setProgress(2);
                //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
                selectedDistVal = Integer.parseInt(seekbarVal);
            }
            else
            {
                Log.e("came here"," ");
                //txtrangeseekbarval.setText(seekbarVal);
                txtseekbarval.setText(seekbarVal);
                int value = Integer.parseInt(seekbarVal);
                //rangeSeekBar.setSelectedMaxValue(value);
                SeekBar.setProgress(value);
                //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
                selectedDistVal = Integer.parseInt(seekbarVal);
            }

            getFBProfilePictures();
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

    /*private SeekbarWithIntervals getSeekbarWithIntervals()

    {
        if (seekbarWithIntervals == null)
        {


            Log.e("---------------","");
            seekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);

            seekbarWithIntervals.setProgress(Integer.parseInt(seekbarVal));
            selectedDistVal = Integer.parseInt(seekbarVal);

        }


        seekbarWithIntervals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                selectedDistVal = progress;
                progress = progress + 1;
                Log.e("progress"," "+progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        return seekbarWithIntervals;
    }*/

    private void editSettings()
    {
        Log.e("picturePath "," "+picturePath);
        String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProfileStatus",profileStatus);
        editor.putString("Gender",selected_Gender);
        editor.putString("Distance", txtseekbarval.getText().toString());
        // editor.putString("Distance", String.valueOf(selectedDistVal));
        editor.apply();
        AWSLoginOperations.updateUserSettings(picturePath, edt_usermsgStatus, cont);
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
                    }
                    else
                    {

                    }

                    for(int i=0;i<validPics.size();i++)
                    {
                        Log.e("Valid Pics   " + i,(String)validPics.get(i));
                    }

                    imguser.setImageBitmap(finalbitmap);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else if (requestCode == ConstsCore.GALLERY_REQUEST)
            {
                // Gallery Selection

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                Log.e("PicturePath",picturePath);
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
                Log.e("Faces",faces+"  " + picturePath);
                if(faces>0)
                {
                    validPics.add(picturePath+"");
                    getFBProfilePictures();
                }
                else
                {

                }

                for(int i=0;i<validPics.size();i++)
                {
                    Log.e("Valid Pics   " + i,(String)validPics.get(i));
                }

                imguser.setImageBitmap(finalbitmap);

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
    public void onBackPressed() {
        super.onBackPressed();
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




