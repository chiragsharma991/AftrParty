package com.aperotechnologies.aftrparties.GateCrasher;

/**
 * Created by hasai on 07/06/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;


/**
 * Created by hasai on 02/05/16.
 */
public class GateCrasherSearchActivity extends Activity {


    TextView txtStartDate;
    Button btn_SearchGCParty;
    String timeSelection;

    //final variables for StartTime in milliseconds
    long selected_startTimeVal;
    String selected_byob;
    private String format = "";

    //variables for date, mon, year, hour and min
    private int startDate, startMon, startYear, startHour, startMin;

    //temp variables for storing StartTime and EndTime in milliseconds
    final long[] tempstartTimeVal = new long[1];

    Configuration_Parameter m_config;
    SharedPreferences sharedPreferences;
    Context cont;
    LocationManager locationManager;
    private static final int MY_PERMISSIONS_ACCESS_CF_LOCATION = 3;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);
        cont = this;

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


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



        final ArrayAdapter<String> startTime = new ArrayAdapter<String>(GateCrasherSearchActivity.this, R.layout.spinner_item, startTimelist);
        startTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_startTime.setAdapter(startTime);

        final ArrayAdapter<String> byob = new ArrayAdapter<String>(GateCrasherSearchActivity.this, R.layout.spinner_item, byobList);
        byob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_byob.setAdapter(byob);


        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        btn_SearchGCParty = (Button) findViewById(R.id.btn_SearchGCParty);



        //Calendar date,time value for StartDateTime
        final Calendar calendar = getCalendar();
        startDate = calendar.get(Calendar.DAY_OF_MONTH);
        startMon = calendar.get(Calendar.MONTH);
        startYear = calendar.get(Calendar.YEAR);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);

        txtStartDate.setText(startDate + "-" + getMonthNo(startMon) + "-" + startYear + ", " + showTime(startHour, startMin));
        //current date time in milliseconds
        selected_startTimeVal = getTimeinMs(startDate, startMon, startYear, startHour, startMin);


        //set startdateTime to temporary variables
        tempstartTimeVal[0] = selected_startTimeVal;


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
                    txtStartDate.setText(mDay + "-" + getMonthNo(mMonth) + "-" + mYear + ", " + showTime(hour, min));
                    selected_startTimeVal = getTimeinMs(mDay, mMonth, mYear, hour, min);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        btn_SearchGCParty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Find Self Location Here

                //this is a check for build version below 23

                if ((int) Build.VERSION.SDK_INT < 23)
                {
                    getSelfLocation();
                }
                else
                {
                    if (ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //Permission model implementation

//                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                return locationNet;

                        ActivityCompat.requestPermissions((Activity) cont,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_CF_LOCATION);
                    }
                    else
                    {
                        getSelfLocation();
                    }
                }


//                Log.e("selected_startTimeVal"," "+selected_startTimeVal);
//                GenerikFunctions.showDialog(m_config.pDialog,"Creating Party...");
//                Intent i = new Intent(GateCrasherSearchActivity.this,GateCrasherActivity.class);
//                startActivity(i);
//                GenerikFunctions.hideDialog(m_config.pDialog);

            }
        });
    }

    public void getSelfLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(GateCrasherSearchActivity.this, "GPS not Available", Toast.LENGTH_LONG).show();
            Log.e("GPS DEisables", "Disabled");
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.e("GPS DEisables 111 ", "Disabled");
                    } else {
                        Location location = getLastBestLocation();
                        Log.e("Func  ", location.getLatitude() + "     " + location.getLongitude());
                    }
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            Location location = getLastBestLocation();
            Log.e("Func  ", location.getLatitude() + "     " + location.getLongitude());

            Log.e("selected_startTimeVal", " " + selected_startTimeVal);


            m_config.pDialog.setMessage("Creating Party.");
            m_config.pDialog.setCancelable(false);
            m_config.pDialog.show();

            Intent i = new Intent(GateCrasherSearchActivity.this, GateCrasherActivity.class);
            startActivity(i);
            m_config.pDialog.dismiss();
            m_config.pDialog.cancel();


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_ACCESS_CF_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getSelfLocation();
                }
                else
                {
                    // permission was not granted
                    if (cont == null)
                    {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(GateCrasherSearchActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_cf_location_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(GateCrasherSearchActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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

        }
    }

    private Location getLastBestLocation()
    {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //  return TODO;
        }
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }




    // calendar function
    private Calendar getCalendar() {
        /** Current Day data using calendar*/
        final Calendar calendar = Calendar.getInstance();
        return calendar;
    }

    // function for displaying time in AM/PM
    public StringBuilder showTime(int hour, int min) {
        StringBuilder time;
        String hours,mins;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if(hour < 10){
            hours = "0" + hour;
        }else{
            hours = String.valueOf(hour);
        }

        if(min < 10){
            mins = "0" + min;
        }else{
            mins = String.valueOf(min);
        }

        time = new StringBuilder().append(hours).append(" : ").append(mins)
                .append(" ").append(format);
        return time;
    }


    //function for displaying month
    public static String getMonthNo(int val) {
        String month = null;

        switch (val) {
            case 0:
                month = "01";
                break;
            case 1:
                month = "02";
                break;
            case 2:
                month = "03";
                break;
            case 3:
                month = "04";
                break;
            case 4:
                month = "05";
                break;
            case 5:
                month = "06";
                break;
            case 6:
                month = "07";
                break;
            case 7:
                month = "08";
                break;
            case 8:
                month = "09";
                break;
            case 9:
                month = "10";
                break;
            case 10:
                month = "11";
                break;
            case 11:
                month = "12";
                break;
            default:
                break;

        }

        return month;
    }

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

        DatePickerDialog dp = new DatePickerDialog(GateCrasherSearchActivity.this,new DatePickerDialog.OnDateSetListener() {
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
                        if(getCurrentDate(startDate, startMon, startYear) == false){
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
                            txtStartDate.setText(startDate + "-" + getMonthNo(startMon) + "-" + startYear + ", " + showTime(startHour, startMin));
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


    //function for comparing dates
    public boolean getCurrentDate(int startDate, int startMon, int startYear){
        Boolean flag = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = getCalendar();
        int day  = c.get(Calendar.DAY_OF_MONTH);
        int mon = c.get(Calendar.MONTH);//mMonth;
        int year = c.get(Calendar.YEAR);//mYear;
        String date = year+"-"+getMonthNo(mon)+"-"+day;
        String startdate = startYear+"-"+getMonthNo(startMon)+"-"+startDate;
        try {
            Date date1 = sdf.parse(date);
            Date date2 = sdf.parse(startdate);
            if(date1.compareTo(date2)==0){
                flag = true;
            }else{
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}
