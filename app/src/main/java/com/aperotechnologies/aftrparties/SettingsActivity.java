package com.aperotechnologies.aftrparties;

import android.*;
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
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.Login.AsyncAgeCalculation;
import com.aperotechnologies.aftrparties.Login.LoginActivity;
import com.aperotechnologies.aftrparties.Reusables.Age;
import com.aperotechnologies.aftrparties.Reusables.AgeCalculator;
import com.aperotechnologies.aftrparties.Reusables.EditTextPopUpFixed;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
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

import static com.aperotechnologies.aftrparties.Reusables.Validations.decodeFile;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getImageUri;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getOutputMediaFileUri;


/**
 * Created by hasai on 19/05/16.
 */
public class SettingsActivity extends Activity {


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
    Spinner spn_Gender, spn_mask;
    Button img_editSettings;
    String selected_Gender, selected_maskStatus = "mask";
    String FacebookID;
    int selectedDistVal = 1;
    Uri fileUri;
    String picturePath = "";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cont  = this;
        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);

        imguser = (CircularImageView) findViewById(R.id.userimage);
        edt_usermsgStatus = (EditTextPopUpFixed) findViewById(R.id.edt_usermsgStatus);
        edt_Age = (EditTextPopUpFixed) findViewById(R.id.edt_Age);
        edt_Age.setEnabled(false);
        //txtrangeseekbarval = (TextView) findViewById(R.id.txtrangeseekbarval);
        txtseekbarval = (TextView) findViewById(R.id.txtseekbarval);
        //rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeseekbar);
        SeekBar = (SeekBar) findViewById(R.id.seekbar);

        //List<String> seekbarIntervals = getIntervals();
        //getSeekbarWithIntervals().setIntervals(seekbarIntervals);
        img_editSettings = (Button) findViewById(R.id.img_editSettings);

        String PrfStatus = sharedPreferences.getString("ProfileStatus","");
        String Gender = sharedPreferences.getString("Gender","");
        seekbarVal = sharedPreferences.getString("Distance","1");
        String DOB = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_BIRTHDATE();


        // Spinner for Mask/UnMask
        spn_mask = (Spinner) findViewById(R.id.spn_maskStatus);
        List<String> maskList = new ArrayList<String>();
        maskList.add("Mask");
        maskList.add("Unmask");


        // Spinner for Gender
        spn_Gender = (Spinner) findViewById(R.id.spn_Gender);
        List<String> genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");


        final ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, genderList);
        genderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Gender.setAdapter(genderadapter);


        final ArrayAdapter<String> maskStatus = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, maskList);
        maskStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_mask.setAdapter(maskStatus);

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




        Picasso.with(cont).load(url).fit().centerCrop()
                .placeholder(R.drawable.placeholder_user)
                .error(R.drawable.placeholder_user)
                .into(imguser);




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

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    try {
        Date date = format.parse(DOB);
        Log.e("Parsed Date", date + "");
        Age age = AgeCalculator.calculateAge(date);
        Log.e("Calculated AGe", age.toString() + "    " + ConstsCore.FB_AGE);
        edt_Age.setText(age.toString());
    }catch(Exception e){

    }




    if(seekbarVal.equals("")){
        String maxText = String.valueOf(2);
        //txtrangeseekbarval.setText(maxText);
        txtseekbarval.setText(maxText);
        //rangeSeekBar.setSelectedMaxValue(2);
        SeekBar.setProgress(2);

        //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
        selectedDistVal = Integer.parseInt(seekbarVal);
    }else{

        Log.e("came here"," ");
        //txtrangeseekbarval.setText(seekbarVal);
        txtseekbarval.setText(seekbarVal);
        int value = Integer.parseInt(seekbarVal);
        //rangeSeekBar.setSelectedMaxValue(value);
        SeekBar.setProgress(value);

        //getSeekbarWithIntervals().setProgress(Integer.parseInt(seekbarVal));
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

            GenerikFunctions.showDialog(m_config.pDialog,"Updating User...");

            editSettings();
        }
    });


    // spinner selection for Mask
    spn_mask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e("selected_maskStatus"," "+selected_maskStatus);

            if(parent.getSelectedItem().toString().trim().equals("Unmask")){
                //ask for in-app purchase
                Log.e("ask for in-app purchase"," ");

                selected_maskStatus = "Unmask";
            }else{

            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

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


        AWSDBOperations.updateUserSettings(picturePath, edt_usermsgStatus, cont);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == ConstsCore.CAMERA_REQUEST) {
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


            } else if (requestCode == ConstsCore.GALLERY_REQUEST) {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults)
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



