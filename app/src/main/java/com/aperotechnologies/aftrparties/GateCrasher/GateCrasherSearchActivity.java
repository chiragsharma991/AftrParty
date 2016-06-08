package com.aperotechnologies.aftrparties.GateCrasher;

/**
 * Created by hasai on 07/06/16.
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import com.aperotechnologies.aftrparties.Host.GeocodingLocation;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);
        cont = this;

        m_config = Configuration_Parameter.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);


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
        btn_SearchGCParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("selected_startTimeVal"," "+selected_startTimeVal);
                GenerikFunctions.showDialog(m_config.pDialog,"Creating Party...");

                Intent i = new Intent(GateCrasherSearchActivity.this,GateCrasherActivity.class);
                i.putExtra("StartTime",String.valueOf(selected_startTimeVal));
                startActivity(i);

                GenerikFunctions.hideDialog(m_config.pDialog);



            }

        });

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
    public void onBackPressed() {
        finish();
    }

}

