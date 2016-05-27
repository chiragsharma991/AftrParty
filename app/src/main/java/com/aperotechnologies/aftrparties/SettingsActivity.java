package com.aperotechnologies.aftrparties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.Reusables.EditTextPopUpFixed;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.utils.SeekbarWithIntervals;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by hasai on 19/05/16.
 */
public class SettingsActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;

    Context cont;
    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;
    CircularImageView imguser;
    EditTextPopUpFixed edt_usermsgStatus, edt_Age;
    TextView txtrangeseekbarval, txtseekbarval;
//    RangeSeekBar rangeSeekBar;
    SeekBar SeekBar;
    SeekbarWithIntervals seekbarWithIntervals = null;
    String seekbarVal = "1";
    Spinner spn_Gender;
    Button img_editSettings;
    String selected_Gender;
    String FacebookID;
    int selectedDistVal = 1;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA=15;
    public static final int MY_PERMISSIONS_REQUEST_R=30;
    public static final int MY_PERMISSIONS_REQUEST_RWFRMCAM=60;
    Uri fileUri;
    String picturePath = "";
    private static final String IMAGE_DIRECTORY_NAME = "AftrParties";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cont  = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);


        imguser = (CircularImageView) findViewById(R.id.userimage);
        edt_usermsgStatus = (EditTextPopUpFixed) findViewById(R.id.edt_usermsgStatus);
        edt_Age = (EditTextPopUpFixed) findViewById(R.id.edt_Age);
        edt_Age.setEnabled(false);
        txtrangeseekbarval = (TextView) findViewById(R.id.txtrangeseekbarval);
        txtseekbarval = (TextView) findViewById(R.id.txtseekbarval);
        //rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeseekbar);
        SeekBar = (SeekBar) findViewById(R.id.seekbar);

        List<String> seekbarIntervals = getIntervals();
        getSeekbarWithIntervals().setIntervals(seekbarIntervals);
        img_editSettings = (Button) findViewById(R.id.img_editSettings);

        String PrfStatus = sharedPreferences.getString("ProfileStatus","");
        String Gender = sharedPreferences.getString("Gender","");
        seekbarVal = sharedPreferences.getString("Distance","1");
        String Age = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE();


//


        // Spinner for Gender
        spn_Gender = (Spinner) findViewById(R.id.spn_Gender);
        List<String> genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");


        final ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, genderList);
        genderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Gender.setAdapter(genderadapter);

        FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();


        String url = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_PROFILE_PIC();

        if(url.equals(null) || url.equals("") || url.equals("N/A"))
        {
//            if(LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC() == null || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("") || LoginValidations.initialiseLoggedInUser(cont).getLI_USER_PROFILE_PIC().equals("N/A"))
//            {
                url = "";
//            }
//            else
//            {
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



        //check whether profilestatus is stored in shared preference
        if(PrfStatus.equals(null) || PrfStatus.equals(""))
        {
            UserTable userTable = m_config.mapper.load(UserTable.class, FacebookID);
            Log.e("userTable "," "+userTable);
            if(userTable.getProfileStatus() == null || userTable.getProfileStatus().equals(null) || userTable.getProfileStatus().equals("N/A"))
            {
                edt_usermsgStatus.setText("");

            }
            else{

                edt_usermsgStatus.setText(userTable.getProfileStatus());
            }
        }
        else
        {
            edt_usermsgStatus.setText(sharedPreferences.getString("ProfileStatus",""));

        }

        if(Gender.equals("Female"))
        {
            spn_Gender.setSelection(1);
        }else
        {
            spn_Gender.setSelection(0);
        }

        edt_Age.setText(Age);




        if(seekbarVal.equals("")){
            String maxText = String.valueOf(2);
            txtrangeseekbarval.setText(maxText);
            txtseekbarval.setText(maxText);
            //rangeSeekBar.setSelectedMaxValue(2);
            SeekBar.setProgress(2);

            getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
            selectedDistVal = Integer.parseInt(seekbarVal);
        }else{

            Log.e("came here"," ");
            txtrangeseekbarval.setText(seekbarVal);
            txtseekbarval.setText(seekbarVal);
            int value = Integer.parseInt(seekbarVal);
            //rangeSeekBar.setSelectedMaxValue(value);
            SeekBar.setProgress(value);

            getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
            selectedDistVal = Integer.parseInt(seekbarVal);
        }

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





        final int stepSize = 1;
        SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/stepSize))*stepSize;
                seekBar.setProgress(progress);
                txtseekbarval.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imguser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        SettingsActivity.this);

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
                                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                        intent.putExtra("return-data", true);
                                        // start the image capture Intent
                                        startActivityForResult(intent, CAMERA_REQUEST);
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
                                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                intent.putExtra("return-data", true);
                                                // start the image capture Intent
                                                startActivityForResult(intent, CAMERA_REQUEST);
                                            }
                                            else
                                            {
                                                //Get Permission for read and write
                                                Log.i("Have camera but ","Not RW for camera permision");
                                                Log.i("Ask for camera RW perm","Yes");
                                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                            }
                                        }
                                        else
                                        {
                                            // Log.i("Dont have camera permission", "Else bliock");
                                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                                    new String[]{android.Manifest.permission.CAMERA},
                                                    MY_PERMISSIONS_REQUEST_CAMERA);
                                        }
                                    }
                                }
                                else
                                {
                                    if ((int) Build.VERSION.SDK_INT < 23)
                                    {
                                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        i.setType("image/*");
                                        startActivityForResult(i, GALLERY_REQUEST);

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
                                            startActivityForResult(i, GALLERY_REQUEST);

                                        }
                                        else
                                        {
                                            ActivityCompat.requestPermissions(SettingsActivity.this,
                                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    MY_PERMISSIONS_REQUEST_R);

                                        }
                                    }
                                }
                            }
                        });

                builderSingle.show();
            }
        });

        // spinner selection for Gender
        spn_Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_Gender = parent.getSelectedItem().toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        img_editSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_usermsgStatus.getWindowToken(), 0);
                editSettings();
            }
        });

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

    private SeekbarWithIntervals getSeekbarWithIntervals()
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
    }

    private void editSettings()
    {
        Log.e("picturePath "," "+picturePath);

        AWSDBOperations.updateUserSettings(picturePath, edt_usermsgStatus, cont);

        String profileStatus = edt_usermsgStatus.getText().toString().replaceAll("\\s+", " ").trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ProfileStatus",profileStatus);
        editor.putString("Gender",selected_Gender);
        editor.putString("Distance", txtseekbarval.getText().toString());
       // editor.putString("Distance", String.valueOf(selectedDistVal));
        editor.apply();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                // Camera click
                try {

                    Bitmap myTest = decodeFile(fileUri.getPath());
                    Uri tempUri = getImageUri(getApplicationContext(), myTest);
                    picturePath = getpath(tempUri);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath,bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                    imguser.setImageBitmap(bitmap);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else if (requestCode == GALLERY_REQUEST) {
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
                bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                imguser.setImageBitmap(bitmap);

            }
        }
    }

    /* Creating file uri to store image */
    public Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /* returning image */

    public File getOutputMediaFile(int type)
    {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        }

        else
        {
            return null;
        }
        Log.i("mediaFile",mediaFile.getPath().toString());
        return mediaFile;
    }

    public Bitmap decodeFile(String path)
    {
        int orientation;
        try
        {
            if (path == null)
            {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 4;
            while (true)
            {
                if (width_tmp / 2 < REQUIRED_SIZE  || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();

            if ((orientation == 3))
            {
                m.postRotate(180);
                m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            }
            else if (orientation == 6)
            {
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);



                return bitmap;
            }
            else if (orientation == 8)
            {
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            }
            return bitmap;
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,0, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}



