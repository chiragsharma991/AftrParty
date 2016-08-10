package com.aperotechnologies.aftrparties.GateCrasher;

/**
 * Created by hasai on 07/06/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PaidGCClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyMaskStatusClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.util.IabHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.aperotechnologies.aftrparties.Reusables.Validations.getDateNo;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getMonthNo;


/**
 * Created by hasai on 02/05/16.
 */
public class GateCrasherSearchActivity extends Activity implements BillingProcessor.IBillingHandler {


    TextView txtStartDate;
    Button btn_getCurrentLocation, btn_SearchGCParty;
    CheckBox cb_byobYes, cb_byobNo;

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
    Location location = null;

    private String loginUserFBID;
    private UserTable user;
    private BillingProcessor bpGCParties;
    private boolean readyToPurchaseGCParties = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        cont = this;

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // Spinner for Start Now/Later
//        Spinner spn_startTime = (Spinner) findViewById(R.id.spn_startTime);
//        List<String> startTimelist = new ArrayList<String>();
//        startTimelist.add("Now");
//        startTimelist.add("Later");
//
//        // Spinner for BYOB
//        Spinner spn_byob = (Spinner) findViewById(R.id.spn_byob);
//        List<String> byobList = new ArrayList<String>();
//        byobList.add("Yes");
//        byobList.add("No");


//        final ArrayAdapter<String> startTime = new ArrayAdapter<String>(GateCrasherSearchActivity.this, R.layout.spinner_item, startTimelist);
//        startTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn_startTime.setAdapter(startTime);
//
//        final ArrayAdapter<String> byob = new ArrayAdapter<String>(GateCrasherSearchActivity.this, R.layout.spinner_item, byobList);
//        byob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn_byob.setAdapter(byob);


        Button btninapppurchase = (Button) findViewById(R.id.inapppurchase);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        btn_getCurrentLocation = (Button) findViewById(R.id.btn_getlocation);
        btn_getCurrentLocation.setVisibility(View.GONE);
        cb_byobYes = (CheckBox) findViewById(R.id.byobyes);
        cb_byobNo = (CheckBox) findViewById(R.id.byobno);

        btn_SearchGCParty = (Button) findViewById(R.id.btn_SearchGCParty);

        loginUserFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

        //Calendar date,time value for StartDateTime
        final Calendar calendar = getCalendar();
        startDate = calendar.get(Calendar.DAY_OF_MONTH);
        startMon = calendar.get(Calendar.MONTH);
        startYear = calendar.get(Calendar.YEAR);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);

        txtStartDate.setText(getDateNo(startDate) + "-" + getMonthNo(startMon) + "-" + startYear + ", " + showTime(startHour, startMin));
        //current date time in milliseconds
        selected_startTimeVal = getTimeinMs(startDate, startMon, startYear, startHour, startMin);


        //set startdateTime to temporary variables
        tempstartTimeVal[0] = selected_startTimeVal;

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateDialog("show");
            }
        });


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            GenerikFunctions.showToast(cont, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bpGCParties = new BillingProcessor(this, ConstsCore.base64EncodedPublicKey, this);


        btninapppurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check mask status of user for party request
                try {
                    user = m_config.mapper.load(UserTable.class, loginUserFBID);
                    Log.e("----", " " + user.getPaidgc());
                    List<PaidGCClass> paidgclist = user.getPaidgc();
                    if (paidgclist == null || paidgclist.size() == 0) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GateCrasherSearchActivity.this);
                        alertDialogBuilder
                                .setTitle("Pay for Unmasking Party.")
                                .setMessage("Are you sure you want to pay for Party?")
                                .setCancelable(false)
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (!readyToPurchaseGCParties) {
                                                    GenerikFunctions.showToast(cont, "Billing not initialized.");
                                                    return;
                                                } else {
                                                    bpGCParties.purchase((Activity) cont, ConstsCore.ITEM_PARTYPURCHASE_SKU);

                                                }
                                            }

                                        });
                        alertDialogBuilder.show();

                    } else {


                        if (paidgclist.get(0).getPaidstatus().equals("Yes")) {

                            Long currTime = Validations.getCurrentTime();//System.currentTimeMillis();
                            if (currTime < Long.parseLong(paidgclist.get(0).getSubscriptiondate())) {
                                GenerikFunctions.showToast(cont, "Your subscription is upto date.");

                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GateCrasherSearchActivity.this);
                                alertDialogBuilder
                                        .setTitle("Pay for Multiple Party Request.")
                                        .setMessage("Are you sure you want to pay for Party?")
                                        .setCancelable(false)
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        })
                                        .setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        if (!readyToPurchaseGCParties) {
                                                            GenerikFunctions.showToast(cont, "Billing not initialized.");
                                                            return;
                                                        } else {
                                                            bpGCParties.purchase((Activity) cont, ConstsCore.ITEM_PARTYPURCHASE_SKU);
                                                        }
                                                    }

                                                });
                                alertDialogBuilder.show();
                            }

                        } else {

                            ///10001 - is requestCode
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GateCrasherSearchActivity.this);
                            alertDialogBuilder
                                    .setTitle("Pay for Unmasking Party.")
                                    .setMessage("Are you sure you want to pay for Party?")
                                    .setCancelable(false)
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    })
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (!readyToPurchaseGCParties) {
                                                        GenerikFunctions.showToast(cont, "Billing not initialized.");
                                                        return;
                                                    } else {
                                                        bpGCParties.purchase((Activity) cont, ConstsCore.ITEM_PARTYPURCHASE_SKU);
                                                    }
                                                }

                                            });
                            alertDialogBuilder.show();
                        }

                    }
                } catch (Exception e) {

                }


            }
        });


        // selection of Spinner Now/Later and start DatePicker
//        spn_startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                timeSelection = parent.getSelectedItem().toString().trim();
//                if (timeSelection.equals("Later")) {
//                    startDateDialog("show");
//
//                } else {
//                    //spinner click for Now Selection
//                    //current date time
//                    Calendar calendar = Calendar.getInstance();
//                    int mYear = calendar.get(Calendar.YEAR);
//                    int mMonth = calendar.get(Calendar.MONTH);
//                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                    int min = calendar.get(Calendar.MINUTE);
//                    startDate = mDay;
//                    startMon = mMonth;
//                    startYear = mYear;
//                    startHour = hour;
//                    startMin = min;
//                    txtStartDate.setText(getDateNo(mDay) + "-" + getMonthNo(mMonth) + "-" + mYear + ", " + showTime(hour, min));
//                    selected_startTimeVal = getTimeinMs(mDay, mMonth, mYear, hour, min);
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//
//        // spinner selection for BYOB
//        spn_byob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selected_byob = parent.getSelectedItem().toString().trim();
//                //Log.e("selected_byob "," ---- "+selected_byob);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        cb_byobYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                cb_byobYes.setChecked(true);
                cb_byobNo.setChecked(false);

            }
        });


        cb_byobNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                cb_byobNo.setChecked(true);
                cb_byobYes.setChecked(false);
            }
        });

        btn_getCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Find Self Location Here

                //this is a check for build version below 23

                if ((int) Build.VERSION.SDK_INT < 23) {
                    getSelfLocation();
                } else {
                    if (ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //Permission model implementation

//                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                return locationNet;

                        ActivityCompat.requestPermissions((Activity) cont,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_CF_LOCATION);
                    } else {
                        getSelfLocation();
                    }
                }
            }
        });


        // create party button click
        btn_SearchGCParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cb_byobYes.isChecked()) {
                    selected_byob = "Yes";
                } else {
                    selected_byob = "No";
                }

                if (compareWithCurrentTime(startHour, startMin) == false) {
                    GenerikFunctions.showToast(cont, "PartyStartTime should be greater than current time.");

                } else {


                    if ((int) Build.VERSION.SDK_INT < 23) {
                        getSelfLocation();
                    } else {
                        if (ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                &&
                                ActivityCompat.checkSelfPermission(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //Permission model implementation

//                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                return locationNet;

                            ActivityCompat.requestPermissions((Activity) cont,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_ACCESS_CF_LOCATION);
                        } else {
                            getSelfLocation();
                        }
                    }
                }


                //Log.e("location"," ----- "+location+" ---- "+selected_byob);

//                if(location == null){
//                    Toast.makeText(cont,"Current Location not available",Toast.LENGTH_SHORT).show();
//
//                }else{
//                    Intent i = new Intent(GateCrasherSearchActivity.this, GateCrasherActivity.class);
//                    GCParceableData data = new GCParceableData();
//                    data.setlatitude(String.valueOf(location.getLatitude()));
//                    data.setlongitude(String.valueOf(location.getLongitude()));
//                    data.setdistance(sharedPreferences.getString(m_config.Distance,"3"));
//                    data.setatdatetime(String.valueOf(selected_startTimeVal));
//                    data.setbyob(selected_byob);
//                    data.setpreference(sharedPreferences.getString(m_config.GenderPreference,""));
//                    Bundle mBundles = new Bundle();
//                    mBundles.putSerializable(ConstsCore.SER_KEY, data);
//                    i.putExtras(mBundles);
//                    cont.startActivity(i);
//
//
//                }


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


            location = getLastBestLocation();


            if (location == null) {
                Toast.makeText(cont, "Current Location not available", Toast.LENGTH_SHORT).show();

            } else {
                Intent i = new Intent(GateCrasherSearchActivity.this, GateCrasherActivity.class);
                GCParceableData data = new GCParceableData();
                data.setlatitude(String.valueOf(location.getLatitude()));
                data.setlongitude(String.valueOf(location.getLongitude()));
                data.setdistance(sharedPreferences.getString(m_config.Distance, "3"));
                data.setatdatetime(String.valueOf(selected_startTimeVal));
                data.setbyob(selected_byob);
                data.setgenderpreference(sharedPreferences.getString(m_config.GenderPreference, "N/A"));
                Bundle mBundles = new Bundle();
                mBundles.putSerializable(ConstsCore.SER_KEY, data);
                i.putExtras(mBundles);
                cont.startActivity(i);


            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_CF_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSelfLocation();
                } else {
                    // permission was not granted
                    if (cont == null) {
                        return;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GateCrasherSearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(GateCrasherSearchActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_cf_location_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
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

                    } else {

                    }
                    break;

                }
            }

        }
    }

    private Location getLastBestLocation() {
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
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
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
        String hours, mins;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if (hour < 10) {
            hours = "0" + hour;
        } else {
            hours = String.valueOf(hour);
        }

        if (min < 10) {
            mins = "0" + min;
        } else {
            mins = String.valueOf(min);
        }

        time = new StringBuilder().append(hours).append(" : ").append(mins)
                .append(" ").append(format);
        return time;
    }


    //function for converting values in milliseconds
    public long getTimeinMs(int mDay, int mMonth, int mYear, int hour, int minute) {

        //Log.e("hour "," "+hour+" minute "+minute);

        Calendar calendar = Calendar.getInstance();
//        calendar.set(mYear, mMonth, mDay,
//                hour, minute, 0);

        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long TimeinMs = calendar.getTimeInMillis();

        Log.e("TimeinMs ", "" + TimeinMs);
        return TimeinMs;


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        String inputString = hour +":"+minute+":00";
//        String date = mYear+"-"+mMonth+"-"+mDay;
//        Log.e("date "," "+date +" --- "+inputString);
//        Date d = null;
//        try {
//            d = sdf.parse(date+" " + inputString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Log.e("getTime in GC ",""+d.getTime());
//        return d.getTime();
    }


    // function for StartTime DatePicker
    private DatePicker startDateDialog(String check) {

        final int tempstartdate;
        final int tempstartmonth;
        final int tempstartyear;

        tempstartdate = startDate;
        tempstartmonth = startMon;
        tempstartyear = startYear;

        DatePickerDialog dp = new DatePickerDialog(GateCrasherSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }

        }, startYear, startMon, startDate);


        dp.getDatePicker().init(dp.getDatePicker().getYear(), dp.getDatePicker().getMonth(), dp.getDatePicker().getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //DatePicker change listener

                Log.e("-------", "change " + startDate);


                startDate = dayOfMonth;
                startMon = month;
                startYear = year;
                tempstartTimeVal[0] = getTimeinMs(startDate, startMon, startYear, startHour, startMin);

            }
        });

        dp.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //checks whether selected date and current date is same or not
                        if (getCurrentDate(startDate, startMon, startYear) == false) {
                            Toast.makeText(getApplicationContext(), "Party can be searched for current day only", Toast.LENGTH_SHORT).show();
                        } else {
                            //call to Start TimePicker
                            startTimeDialog("show");
                        }
                    }
                }
        );

        dp.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("-------", "cancel " + tempstartdate);
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
        if (check == "show") {
            dp.show();
        }
        return dp.getDatePicker();

    }


    // function for StartTime TimePicker
    private void startTimeDialog(String check) {

        final int tempstarthour;
        final int tempstartmin;
        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(startHour);
        timePicker.setCurrentMinute(startMin);

        tempstarthour = startHour;
        tempstartmin = startMin;

        new AlertDialog.Builder(this)
                .setTitle("Set Time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startHour = timePicker.getCurrentHour();
                        startMin = timePicker.getCurrentMinute();
                        tempstartTimeVal[0] = getTimeinMs(startDate, startMon, startYear, startHour, startMin);
                        Log.e("diff ", "  " + (tempstartTimeVal[0] - selected_startTimeVal));

                        if (compareWithCurrentTime(startHour, startMin) == false) {
                            startHour = tempstarthour;
                            startMin = tempstartmin;
                            Toast.makeText(getApplication(), "StartTime Should be greater than or equal to currentTime", Toast.LENGTH_SHORT).show();
                        } else {
                            txtStartDate.setText(getDateNo(startDate) + "-" + getMonthNo(startMon) + "-" + startYear + ", " + showTime(startHour, startMin));
                            selected_startTimeVal = tempstartTimeVal[0];
                            Log.e("selected_startTimeVal", " " + selected_startTimeVal + " " + new Date(selected_startTimeVal));

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

    //compare starttime with currenttime
    private boolean compareWithCurrentTime(int startHour, int startMin) {
        Boolean value = false;
        Log.e("startHour" + startHour + "     " + startMin, "");


        Calendar calendar = Validations.getCalendar();
        int currHour, currMin;
        currHour = calendar.get(Calendar.HOUR_OF_DAY);
        currMin = calendar.get(Calendar.MINUTE);
        Log.e("currHour" + currHour + "     " + currMin, "");
        if (startHour < currHour) {
            value = false;
        } else if (startHour == currHour) {
            if (startMin < currMin) {
                value = false;
            } else {
                value = true;
            }
        } else {
            value = true;
        }
        return value;

    }


    //function for comparing dates
    public boolean getCurrentDate(int startDate, int startMon, int startYear) {
        Boolean flag = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = getCalendar();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int mon = c.get(Calendar.MONTH);//mMonth;
        int year = c.get(Calendar.YEAR);//mYear;
        String date = year + "-" + getMonthNo(mon) + "-" + day;
        String startdate = startYear + "-" + getMonthNo(startMon) + "-" + startDate;
        try {
            Date date1 = sdf.parse(date);
            Date date2 = sdf.parse(startdate);
            if (date1.compareTo(date2) == 0) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;

    }

    private class setPartyPurchaseinAWS extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params)
        {

            Long subVal = Validations.getCurrentTime() + ConstsCore.FifteenDayVal;
            List<PaidGCClass> paidgclist = user.getPaidgc();

            try {
                //check whether is paid or unpaid user
                if (paidgclist == null || paidgclist.size() == 0)
                {
                    // here user is unpaid user

                    PaidGCClass paidGCClass = new PaidGCClass();
                    paidGCClass.setPaidstatus("Yes");
                    paidGCClass.setSubscriptiondate(String.valueOf(subVal));
                    paidgclist = new ArrayList<>();
                    paidgclist.add(paidGCClass);

                    List<ActivePartyClass> ActivePartyList = user.getActiveparty();
                    if (ActivePartyList != null || ActivePartyList.size() != 0)
                    {
                        //if there is an active party update endblocktime of active party
                        ActivePartyClass ActiveParty = ActivePartyList.get(0);

                        String EndBlockTime = "";
                        if ((Long.parseLong(ActiveParty.getStartblocktime() + ConstsCore.hourVal)) > Long.parseLong(ActiveParty.getEndtime())) {
                            EndBlockTime = ActiveParty.getEndtime();
                        } else {
                            EndBlockTime = String.valueOf(Long.parseLong(ActiveParty.getStarttime()) + ConstsCore.hourVal);
                        }


                        ActiveParty.setPartyid(ActiveParty.getPartyid());
                        ActiveParty.setPartyname(ActiveParty.getPartyname());
                        ActiveParty.setStarttime(ActiveParty.getStarttime());
                        ActiveParty.setEndtime(ActiveParty.getEndtime());
                        ActiveParty.setPartystatus(ActiveParty.getPartystatus());
                        ActiveParty.setStartblocktime(ActiveParty.getStartblocktime());
                        ActiveParty.setEndblocktime(EndBlockTime);
                        ActivePartyList.set(0, ActiveParty);
                        user.setActiveparty(ActivePartyList);
                        user.setPaidgc(paidgclist);
                        m_config.mapper.save(user);
                        Toast.makeText(getApplicationContext(), "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                        GenerikFunctions.hDialog();



                    }
                    else
                    {
                        user.setPaidgc(paidgclist);
                        m_config.mapper.save(user);

                        Toast.makeText(getApplicationContext(), "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                        GenerikFunctions.hDialog();

                    }


                } else {

                    // here user is paid user
                    PaidGCClass paidGCClass = paidgclist.get(0);
                    paidGCClass.setPaidstatus("Yes");
                    paidGCClass.setSubscriptiondate(String.valueOf(subVal));
                    paidgclist.add(0, paidGCClass);

                    List<ActivePartyClass> ActivePartyList = user.getActiveparty();
                    if (ActivePartyList != null || ActivePartyList.size() != 0)
                    {
                        //if there is an active party update endblocktime of active party
                        ActivePartyClass ActiveParty = ActivePartyList.get(0);

                        String EndBlockTime = "";
                        if ((Long.parseLong(ActiveParty.getStartblocktime() + ConstsCore.hourVal)) > Long.parseLong(ActiveParty.getEndtime())) {
                            EndBlockTime = ActiveParty.getEndtime();
                        } else {
                            EndBlockTime = String.valueOf(Long.parseLong(ActiveParty.getStarttime()) + ConstsCore.hourVal);
                        }


                        ActiveParty.setPartyid(ActiveParty.getPartyid());
                        ActiveParty.setPartyname(ActiveParty.getPartyname());
                        ActiveParty.setStarttime(ActiveParty.getStarttime());
                        ActiveParty.setEndtime(ActiveParty.getEndtime());
                        ActiveParty.setPartystatus(ActiveParty.getPartystatus());
                        ActiveParty.setStartblocktime(ActiveParty.getStartblocktime());
                        ActiveParty.setEndblocktime(EndBlockTime);
                        ActivePartyList.set(0, ActiveParty);
                        user.setActiveparty(ActivePartyList);
                        user.setPaidgc(paidgclist);
                        m_config.mapper.save(user);


                        Toast.makeText(getApplicationContext(), "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                        GenerikFunctions.hDialog();


                    }
                    else
                    {
                        user.setPaidgc(paidgclist);
                        m_config.mapper.save(user);

                        Toast.makeText(getApplicationContext(), "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                        GenerikFunctions.hDialog();

                    }

                }
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();

            }
            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void v)
        {


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bpGCParties.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        if (bpGCParties != null)
            bpGCParties.release();

        super.onDestroy();
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

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        //GenerikFunctions.showToast(cont, "Purchase Successful");
        GenerikFunctions.sDialog(cont, "Saving Data");
        new setPartyPurchaseinAWS().execute();
        Boolean consumed = bpGCParties.consumePurchase(ConstsCore.ITEM_PARTYPURCHASE_SKU);

        if (consumed) {
            GenerikFunctions.showToast(cont,"Successfully consumed");
        } else {
            GenerikFunctions.hDialog();
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
        //GenerikFunctions.showToast(cont,"onPurchaseHistoryRestored");
        for (String sku : bpGCParties.listOwnedProducts())
            Log.d("", "Owned Managed Product: " + sku);
        for (String sku : bpGCParties.listOwnedSubscriptions())
            Log.d("", "Owned Subscription: " + sku);

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        //GenerikFunctions.showToast(cont,"onBillingError: " + Integer.toString(errorCode));
    }

    @Override
    public void onBillingInitialized() {
        //GenerikFunctions.showToast(cont,"onBillingInitialized");
        Boolean consumed = bpGCParties.consumePurchase(ConstsCore.ITEM_PARTYPURCHASE_SKU);
        readyToPurchaseGCParties = true;

    }


}

