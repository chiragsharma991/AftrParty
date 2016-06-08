package com.aperotechnologies.aftrparties.Host;

/**
 * Created by hasai on 06/05/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSDBOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.aperotechnologies.aftrparties.Reusables.Validations.decodeFile;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getImageUri;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getOutputMediaFileUri;

/**
 * Created by hasai on 02/05/16.
 */
public class HostActivity extends Activity{//implements AdapterView.OnItemSelectedListener,TimePicker.OnTimeChangedListener {


    LinearLayout lLyoutHost;
    CircularImageView imgParty;
    TextView uploadPartyImage, txtStartDate, txtEndDate;
    EditText edt_PartyName, edt_Description, edt_Address;
    Button btn_createParty;
    String timeSelection;

    //final variables for StartTime and EndTime in milliseconds
    long selected_startTimeVal;
    long selected_endTimeVal;

    String selected_byob;



    //variables for date, mon, year, hour and min
    private int startDate, startMon, startYear, startHour, startMin;
    private int endDate, endMon, endYear, endHour, endMin;

    //temp variables for storing StartTime and EndTime in milliseconds
    final long[] tempstartTimeVal = new long[1];
    final long[] tempendTimeVal = new long[1];

    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;
    Context cont;


    Uri fileUri;
    String picturePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        cont = this;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);

        imgParty = (CircularImageView) findViewById(R.id.partyImage);
        uploadPartyImage = (TextView) findViewById(R.id.uploadPartyImage);

        // Spinner for Start Now/Later
        Spinner spn_startTime = (Spinner) findViewById(R.id.spn_startTime);
        List<String> startTimelist = new ArrayList<String>();
        startTimelist.add("Now");
        startTimelist.add("Later");

        // Spinner for BYOB
        Spinner spn_byob = (Spinner) findViewById(R.id.spn_byob);
        List<String> byobList = new ArrayList<String>();
        byobList.add("Yes");
        byobList.add("No");



        final ArrayAdapter<String> startTime = new ArrayAdapter<String>(HostActivity.this, R.layout.spinner_item, startTimelist);
        startTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_startTime.setAdapter(startTime);

        final ArrayAdapter<String> byob = new ArrayAdapter<String>(HostActivity.this, R.layout.spinner_item, byobList);
        byob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_byob.setAdapter(byob);


        lLyoutHost = (LinearLayout) findViewById(R.id.lLyoutHost);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        edt_PartyName = (EditText) findViewById(R.id.edt_PartyName);
        edt_Description = (EditText) findViewById(R.id.edt_Description);
        edt_Address = (EditText) findViewById(R.id.edt_Partyaddress);
        btn_createParty = (Button) findViewById(R.id.btn_CreateParty);

        //Code to clear Focus from editText
        edt_PartyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_PartyName.clearFocus();
                    edt_Description.performClick();

                }
                return false;
            }
        });


//        //Code to clear Focus from editText
//        edt_Description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
//                    edt_Description.clearFocus();
//                    edt_Address.performClick();
//
//                }
//                return false;
//            }
//        });
//
//
//        //Code to clear Focus from editText
//        edt_Address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
//                    edt_Address.clearFocus();
//                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//
//                }
//                return false;
//            }
//        });

        uploadPartyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        HostActivity.this);

                builderSingle.setTitle("Add Photo");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        HostActivity.this, android.R.layout.select_dialog_item);
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

                                        int permissionCheck = ContextCompat.checkSelfPermission(HostActivity.this,
                                                android.Manifest.permission.CAMERA);


                                        if(permissionCheck== PackageManager.PERMISSION_GRANTED)
                                        {

                                            permissionCheck = ContextCompat.checkSelfPermission(HostActivity.this,
                                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                            //here
                                            int permissioncheckRead= ContextCompat.checkSelfPermission(HostActivity.this,
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
                                                ActivityCompat.requestPermissions(HostActivity.this,
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                                            }
                                        }
                                        else
                                        {
                                            // Log.i("Dont have camera permission", "Else bliock");
                                            ActivityCompat.requestPermissions(HostActivity.this,
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

                                        int permissionCheck = ContextCompat.checkSelfPermission(HostActivity.this,
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
                                            ActivityCompat.requestPermissions(HostActivity.this,
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


        //Calendar date,time value for StartDateTime
        final Calendar calendar = Validations.getCalendar();
        startDate = calendar.get(Calendar.DAY_OF_MONTH);
        startMon = calendar.get(Calendar.MONTH);
        startYear = calendar.get(Calendar.YEAR);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);

        txtStartDate.setText(startDate + "-" + Validations.getMonthNo(startMon) + "-" + startYear + ", " + Validations.showTime(startHour, startMin));
        //current date time in milliseconds
        selected_startTimeVal = getTimeinMs(startDate, startMon, startYear, startHour, startMin);

        //date,time value for EndDateTime
        endDate = calendar.get(Calendar.DAY_OF_MONTH);
        endMon = calendar.get(Calendar.MONTH);
        endYear = calendar.get(Calendar.YEAR);
        endHour = calendar.get(Calendar.HOUR_OF_DAY);
        endMin = calendar.get(Calendar.MINUTE);

        txtEndDate.setText(endDate + "-" + Validations.getMonthNo(endMon) + "-" + endYear + ", " + Validations.showTime(endHour, endMin));
        //current date time in milliseconds
        selected_endTimeVal = getTimeinMs(endDate, endMon, endYear, endHour, endMin);

        //set startdateTime and endDateTime to temporary variables
        tempstartTimeVal[0] = selected_startTimeVal;
        tempendTimeVal[0] = selected_endTimeVal;

        // selection of Spinner Now/Later and start DatePicker
        spn_startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeSelection = parent.getSelectedItem().toString().trim();
                if (timeSelection.equals("Later")) {
                    startDateDialog("show");

                } else {
                    //spinner click for Now Selection
                    //current date time
                    Calendar calendar = Calendar.getInstance();
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);
                    startDate = mDay;
                    startMon = mMonth;
                    startYear = mYear;
                    startHour = hour;
                    startMin = min;
                    txtStartDate.setText(mDay + "-" + Validations.getMonthNo(mMonth) + "-" + mYear + ", " + Validations.showTime(hour, min));
                    selected_startTimeVal = getTimeinMs(mDay, mMonth, mYear, hour, min);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // selection of end date textfield
        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDateDialog("show");
            }
        });

        // spinner selection for BYOB
        spn_byob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_byob = parent.getSelectedItem().toString().trim();
                //Log.e("selected_byob "," ---- "+selected_byob);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // create party button click
        btn_createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_PartyName.clearFocus();
                edt_Description.clearFocus();
                edt_Address.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                GenerikFunctions.showDialog(m_config.pDialog,"Creating Party...");


                String val = Validations.checkWordsCount(edt_Description.getText().toString());
                String msg = "";
                int msglength=0;
                if(picturePath.equals("")){
                    msg += "Please upload Image" + "\n";
                    msglength++;
                }

                 if (selected_endTimeVal - selected_startTimeVal == 0)
                {
                    msg += "StartTime & EndTime cannot be same. It should be greater than one hour" + "\n";
                    msglength++;
                    //Toast.makeText(getApplicationContext(), "StartTime & EndTime cannot be same. It should be greater than one hour", Toast.LENGTH_SHORT).show();

                }
                else if (selected_endTimeVal < selected_startTimeVal)
                {
                    msg += "EndTime should be greater than StartTime" + "\n";
                    msglength++;
                    //Toast.makeText(getApplicationContext(), " EndTime should be greater than StartTime", Toast.LENGTH_SHORT).show();
                }
                else if (selected_endTimeVal - selected_startTimeVal < 3600000)
                {
                    msg += "EndTime should be greater than 1 hour" + "\n";
                    msglength++;
                    //Toast.makeText(getApplicationContext(), " EndTime should be greater than 1 hour ", Toast.LENGTH_SHORT).show();
                }
                else if (selected_endTimeVal - selected_startTimeVal > 43200000)
                {
                    msg += "EndTime cannot be greater than 12 hours" + "\n";
                    msglength++;
                    // Toast.makeText(getApplicationContext(), " EndTime cannot be greater than 12 hours ", Toast.LENGTH_SHORT).show();
                }

                if(edt_PartyName.getText().toString().replaceAll("\\s+", " ").trim().equals("") || edt_PartyName.getText().toString().replaceAll("\\s+", " ").trim().equals(" "))
                {
                    //Toast.makeText(getApplicationContext(), "Please enter proper address", Toast.LENGTH_SHORT).show();
                    //edt_PartyName.setError("Please fill Party Name");
                    msg += "Please fill Party Name"+ "\n";
                    msglength++;
                }

                if(!val.equals("true")) {
                    //edt_Description.setError(val);
                    msg += val + "\n";
                    msglength++;
                    //Toast.makeText(getApplicationContext(), " "+val, Toast.LENGTH_SHORT).show();
                }

                if(edt_Address.getText().toString().replaceAll("\\s+", " ").trim().equals("") || edt_Address.getText().toString().replaceAll("\\s+", " ").trim().equals(" "))
                {
                    //Toast.makeText(getApplicationContext(), "Please enter proper address", Toast.LENGTH_SHORT).show();
                    //edt_Address.setError("Please fill the Address");
                    msg += "Please fill the Address"+ "\n";
                    msglength++;
                }



                if(msglength == 0){
                    getLatLong();

                }else{
                    if(msg.equals("")){

                    }else {
                        Toast.makeText(getApplicationContext(), " " + msg, Toast.LENGTH_LONG).show();
                    }

                    GenerikFunctions.hideDialog(m_config.pDialog);
                }
                //Log.e(""," "+timeSelection);
//                    Log.e("selected_byob"," "+selected_byob);
//                    Log.e("selected_startTimeVal"," "+selected_startTimeVal);
//                    Log.e("selected_endTimeVal"," "+selected_endTimeVal);



            }

        });

    }

//    // calendar function
//    private Calendar getCalendar() {
//        /** Current Day data using calendar*/
//        final Calendar calendar = Calendar.getInstance();
//        return calendar;
//    }
//
//    // function for displaying time in AM/PM
//    public StringBuilder showTime(int hour, int min) {
//        StringBuilder time;
//        String hours,mins;
//        if (hour == 0) {
//            hour += 12;
//            format = "AM";
//        }
//        else if (hour == 12) {
//            format = "PM";
//        } else if (hour > 12) {
//            hour -= 12;
//            format = "PM";
//        } else {
//            format = "AM";
//        }
//
//        if(hour < 10){
//            hours = "0" + hour;
//        }else{
//            hours = String.valueOf(hour);
//        }
//
//        if(min < 10){
//            mins = "0" + min;
//        }else{
//            mins = String.valueOf(min);
//        }
//
//
////        Log.e("time",""+new StringBuilder().append(hour).append(" : ").append(min)
////                .append(" ").append(format));
//        time = new StringBuilder().append(hours).append(" : ").append(mins)
//                .append(" ").append(format);
//        return time;
//    }
//
//
//    //function for displaying month
//    public static String getMonthNo(int val) {
//        String month = null;
//
//        switch (val) {
//            case 0:
//                month = "01";
//                break;
//            case 1:
//                month = "02";
//                break;
//            case 2:
//                month = "03";
//                break;
//            case 3:
//                month = "04";
//                break;
//            case 4:
//                month = "05";
//                break;
//            case 5:
//                month = "06";
//                break;
//            case 6:
//                month = "07";
//                break;
//            case 7:
//                month = "08";
//                break;
//            case 8:
//                month = "09";
//                break;
//            case 9:
//                month = "10";
//                break;
//            case 10:
//                month = "11";
//                break;
//            case 11:
//                month = "12";
//                break;
//            default:
//                break;
//
//        }
//
//        return month;
//    }

    //function for converting values in milliseconds
    public long getTimeinMs(int mDay, int mMonth, int mYear, int hour, int minute){

        //Log.e("hour "," "+hour+" minute "+minute);

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay,
                hour, minute, 0);
        long TimeinMs = calendar.getTimeInMillis();

        //Log.e("--- ",""+calendar.getTime()+" "+calendar.getTimeInMillis());
        //Log.e("DateTime in milliseconds : ","" + TimeinMs.getTime());
        //return TimeinMs.getTime();
        return TimeinMs;
    }



    // function for StartTime DatePicker
    private DatePicker startDateDialog(String check) {

        final int tempstartdate;
        final int tempstartmonth;
        final int tempstartyear;

        tempstartdate = startDate;
        tempstartmonth = startMon;
        tempstartyear = startYear;

        DatePickerDialog dp = new DatePickerDialog(HostActivity.this,new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }

        }, startYear, startMon, startDate);


        dp.getDatePicker().init(dp.getDatePicker().getYear(), dp.getDatePicker().getMonth(), dp.getDatePicker().getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //DatePicker change listener

                Log.e("-------","change "+startDate);


                startDate = dayOfMonth;
                startMon = month;
                startYear = year;
                tempstartTimeVal[0] = getTimeinMs(startDate, startMon, startYear, startHour, startMin);

            }
        });

        dp.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //checks whether selected date and current date is same or not
                        if(Validations.getCurrentDate(startDate, startMon, startYear) == false){
                            Toast.makeText(getApplicationContext(),"Party should be created for current day",Toast.LENGTH_SHORT).show();
                        }else{
                            //call to Start TimePicker
                            startTimeDialog("show");
                        }
                    }
                }
        );

        dp.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("-------","cancel "+ tempstartdate);
                        startDate = tempstartdate;
                        startMon = tempstartmonth;
                        startYear = tempstartyear;


                    }
                }
        );
        //dp.getDatePicker().setMinDate(calendar.getTimeInMillis());
        //dp.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        dp.getDatePicker().setCalendarViewShown(true);
        dp.setTitle("Set Date");
        if(check == "show"){
            dp.show();
        }
        return dp.getDatePicker();

    }


    // function for StartTime TimePicker
    private void startTimeDialog(String check) {

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(startHour);
        timePicker.setCurrentMinute(startMin);

        new AlertDialog.Builder(this)
                .setTitle("Set Time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startHour = timePicker.getCurrentHour();
                        startMin = timePicker.getCurrentMinute();
                        tempstartTimeVal[0] = getTimeinMs(startDate, startMon, startYear, startHour,startMin);
                        Log.e("diff ", "  " + (tempstartTimeVal[0] - selected_startTimeVal));

                        if (tempstartTimeVal[0] - selected_startTimeVal < 0) {
                            Toast.makeText(getApplication(), "StartTime Should be greater than or equal to currentTime", Toast.LENGTH_SHORT).show();
                        } else {
                            txtStartDate.setText(startDate + "-" + Validations.getMonthNo(startMon) + "-" + startYear + ", " + Validations.showTime(startHour, startMin));
                            selected_startTimeVal = tempstartTimeVal[0];
                            Log.e("selected_startTimeVal", " " + selected_startTimeVal+" "+new Date(selected_startTimeVal));

                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.d("Picker", "Cancelled!");
                            }
                        }).setView(timePicker).show();

    }


    // function for EndTime DatePicker
    private DatePicker endDateDialog(String check) {
        final Calendar calendar = Validations.getCalendar();
        final int tempenddate;
        final int tempendmonth;
        final int tempendyear;

        tempenddate = endDate;
        tempendmonth = endMon;
        tempendyear = endYear;

        DatePickerDialog dp = new DatePickerDialog(HostActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }

        }, endYear, endMon, endDate);

        dp.getDatePicker().init(dp.getDatePicker().getYear(), dp.getDatePicker().getMonth(), dp.getDatePicker().getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {


                Log.e("-------","change "+endDate);
                endDate = dayOfMonth;
                endMon = month;
                endYear = year;
                tempendTimeVal[0] = getTimeinMs(endDate, endMon, endYear, endHour, endMin);

            }
        });

        dp.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        endTimeDialog("show");
                    }
                }
        );
        dp.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("-------","cancel "+tempenddate);
                        endDate = tempenddate;
                        endMon = tempendmonth;
                        endYear = tempendyear;
                    }
                }
        );
        //dp.getDatePicker().setMinDate(calendar.getTimeInMillis());
        dp.getDatePicker().setCalendarViewShown(true);
        dp.setTitle("Set Date");
        if(check == "show"){
            dp.show();
        }

        return dp.getDatePicker();

    }


    // function for EndTime TimePicker
    private void endTimeDialog(String check) {

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(endHour);
        timePicker.setCurrentMinute(endMin);

        new AlertDialog.Builder(this)
                .setTitle("Set Time")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endHour = timePicker.getCurrentHour();
                        endMin = timePicker.getCurrentMinute();
                        tempendTimeVal[0] = getTimeinMs(endDate, endMon, endYear, endHour, endMin);

                        Log.e("selected_startTimeVal", "   " + selected_startTimeVal+" "+new Date(selected_startTimeVal));
                        Log.e("tempendTimeVal", "   " + tempendTimeVal[0]+" "+new Date(tempendTimeVal[0]));
                        Log.e("difference", "   " + (tempendTimeVal[0] - selected_startTimeVal));
                        long finalEndTimeVal = new Date(tempendTimeVal[0]).getTime();
                        long finalStartTimeVal = new Date(selected_startTimeVal).getTime();

                        Log.e("diff by date", "    " + (finalEndTimeVal - finalStartTimeVal));

                        if (tempendTimeVal[0] - selected_startTimeVal == 0) {
                            Toast.makeText(getApplicationContext(), "StartTime & EndTime cannot be same", Toast.LENGTH_SHORT).show();
                        } else if (tempendTimeVal[0] < selected_startTimeVal) {
                            Toast.makeText(getApplicationContext(), " EndTime should be greater than StartTime", Toast.LENGTH_SHORT).show();
                        } else if (tempendTimeVal[0] - selected_startTimeVal < 3600000) {
                            Toast.makeText(getApplicationContext(), " EndTime should be greater than 1 hour ", Toast.LENGTH_SHORT).show();
                        } else if (tempendTimeVal[0] - selected_startTimeVal > 43200000) {
                            Toast.makeText(getApplicationContext(), " EndTime cannot be greater than 12 hours ", Toast.LENGTH_SHORT).show();
                        } else {

                            selected_endTimeVal = tempendTimeVal[0];
                            txtEndDate.setText(endDate + "-" + Validations.getMonthNo(endMon) + "-" + endYear + ", " + Validations.showTime(endHour, endMin));

                        }
                    }

                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.d("Picker", "Cancelled!");
                            }
                        }).setView(timePicker).show();





    }


//    //function for comparing dates
//    public boolean getCurrentDate(int startDate, int startMon, int startYear){
//        Boolean flag = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Validations.getCalendar();
//        int day  = c.get(Calendar.DAY_OF_MONTH);
//        int mon = c.get(Calendar.MONTH);//mMonth;
//        int year = c.get(Calendar.YEAR);//mYear;
//        String date = year+"-"+Validations.getMonthNo(mon)+"-"+day;
//        String startdate = startYear+"-"+Validations.getMonthNo(startMon)+"-"+startDate;
//        try {
//            Date date1 = sdf.parse(date);
//            Date date2 = sdf.parse(startdate);
//            if(date1.compareTo(date2)==0){
//                flag = true;
//            }else{
//                flag = false;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return flag;
//
//    }

    public void getLatLong(){
        String Address = edt_Address.getText().toString().trim();
        //Log.i("Entered Address ", Address);
        Address = Address.replace(" ", "+");
        //Log.i("Processed Address ", Address);

        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(Address,
                getApplicationContext(), new GeocoderHandler());
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {//
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            Log.e("Location Address",""+locationAddress );

            if(locationAddress == null){
                Toast.makeText(getApplicationContext(), "Unable to get Latitude and Longitude for this address location.",Toast.LENGTH_SHORT).show();
                //edt_Address.setError("Unable to get Latitude and Longitude for this address location.");
                GenerikFunctions.hideDialog(m_config.pDialog);
            }else{
                AWSDBOperations.createParty(cont, initialiseParty(cont, locationAddress));
            }




            //lblLatLang.setText("GeoLocation  " + locationAddress);

        }
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
                    imgParty.setImageBitmap(bitmap);


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
                imgParty.setImageBitmap(bitmap);

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
                    ActivityCompat.requestPermissions(HostActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            ConstsCore.MY_PERMISSIONS_REQUEST_RWFRMCAM);
                }
                else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    // permission was not granted
                    if (cont == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HostActivity.this, Manifest.permission.CAMERA))
                    {
                        new AlertDialog.Builder(HostActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_camera_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(HostActivity.this,
                                                new String[]{Manifest.permission.CAMERA},
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(HostActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_rw_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(HostActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        //showStoragePermissionRationale();
                        new AlertDialog.Builder(HostActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_rw_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(HostActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
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

    public PartyTable initialiseParty(Context cont, String locationAddress)
    {
        List latlong = new ArrayList();
        latlong.add(locationAddress.split(" ")[0]);
        latlong.add(locationAddress.split(" ")[1]);

        PartyTable party = new PartyTable();
        party.setPartyID(Validations.getUniqueId(cont));
        party.setPartyName(edt_PartyName.getText().toString());
        party.setStartTime(String.valueOf(selected_startTimeVal));
        party.setEndTime(String.valueOf(selected_endTimeVal));
        //party.setDate("");
        party.setHostFBID(LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID());
        party.setHostQBID(String.valueOf(m_config.chatService.getUser().getId()));
        party.setHostName(sharedPreferences.getString(m_config.Entered_User_Name, ""));
        //party.setPartyType("");
        party.setPartyDescription(edt_Description.getText().toString().trim());
        party.setBYOB(selected_byob);
        party.setPartyAddress(edt_Address.getText().toString().trim());
        party.setPartyLatLong(latlong);
        party.setPartyImage(picturePath);
        //party.setPartyImage("");
        //party.setMaskStatus("");
        party.setDialogID("N/A");

        return party;

    }


    @Override
    public void onBackPressed() {
        finish();
    }

}
