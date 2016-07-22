package com.aperotechnologies.aftrparties.Host;

/**
 * Created by hasai on 06/05/16.
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.aperotechnologies.aftrparties.Chats.ChatService;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSPartyOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.GateCrasher.GCParceableData;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherActivity;
import com.aperotechnologies.aftrparties.Login.Welcome;
import com.aperotechnologies.aftrparties.QBSessionClass;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.linkedin.platform.LISessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.aperotechnologies.aftrparties.Reusables.Validations.decodeFile;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getDateNo;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getImageUri;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getMonthNo;
import static com.aperotechnologies.aftrparties.Reusables.Validations.getOutputMediaFileUri;

/**
 * Created by hasai on 02/05/16.
 */
public class HostActivity extends Activity//implements AdapterView.OnItemSelectedListener,TimePicker.OnTimeChangedListener {
{

    LinearLayout lLyoutHost, llayoutenterAddress;
    CircularImageView imgParty;
    TextView uploadPartyImage, txtStartDate, txtEndDate;
    TextView edtEndDate;
    EditText edt_PartyName, edt_Description;
    CheckBox cb_byobYes, cb_byobNo;
    CheckBox cb_mask, cb_unmask;

    CheckBox cb_getLocation, cb_EnterAddress;
    EditText edt_address, edt_street,edt_city, edt_pincode;
    public String[] states = new String[]{"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh",
            "Jammu & Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland",
            "Odisha (Orissa)","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal"};


    AutoCompleteTextView edt_state;
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
    LocationManager locationManager;
    private static final int MY_PERMISSIONS_ACCESS_CF_LOCATION = 3;
    Location currentlocation = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        m_config = Configuration_Parameter.getInstance();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        cont = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);
        m_config.pDialog = new ProgressDialog(cont);

        imgParty = (CircularImageView) findViewById(R.id.partyImage);
        uploadPartyImage = (TextView) findViewById(R.id.uploadPartyImage);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        // Spinner for Start Now/Later
//        Spinner spn_startTime = (Spinner) findViewById(R.id.spn_startTime);
//        List<String> startTimelist = new ArrayList<String>();
//        startTimelist.add("Now");
//        startTimelist.add("Later");

        // Spinner for BYOB
//        Spinner spn_byob = (Spinner) findViewById(R.id.spn_byob);
//        List<String> byobList = new ArrayList<String>();
//        byobList.add("Yes");
//        byobList.add("No");



//        final ArrayAdapter<String> startTime = new ArrayAdapter<String>(HostActivity.this, R.layout.spinner_item, startTimelist);
//        startTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn_startTime.setAdapter(startTime);

//        final ArrayAdapter<String> byob = new ArrayAdapter<String>(HostActivity.this, R.layout.spinner_item, byobList);
//        byob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spn_byob.setAdapter(byob);


        lLyoutHost = (LinearLayout) findViewById(R.id.lLyoutHost);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        edtEndDate = (TextView) findViewById(R.id.edtEndDate);
        //txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        edt_PartyName = (EditText) findViewById(R.id.edt_PartyName);
        edt_Description = (EditText) findViewById(R.id.edt_Description);

        // for BYOB
        cb_byobYes = (CheckBox) findViewById(R.id.byobyes);
        cb_byobNo = (CheckBox) findViewById(R.id.byobno);

        // for mask/unmask
        cb_mask = (CheckBox) findViewById(R.id.mask);
        cb_unmask = (CheckBox) findViewById(R.id.unmask);

        //For Location
        cb_getLocation = (CheckBox) findViewById(R.id.cbgetLocation);
        cb_EnterAddress = (CheckBox) findViewById(R.id.cbEnterAddress);

        // UI for Entering Address
        llayoutenterAddress = (LinearLayout) findViewById(R.id.llayoutenterAddress);
        edt_street =  (EditText) findViewById(R.id.edt_street);
        edt_city =  (EditText) findViewById(R.id.edt_city);
        edt_state =  (AutoCompleteTextView) findViewById(R.id.edt_state);
        edt_pincode =  (EditText) findViewById(R.id.edt_pincode);
        edt_address = (EditText) findViewById(R.id.edt_Partyaddress);
        btn_createParty = (Button) findViewById(R.id.btn_CreateParty);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,states);
        edt_state.setAdapter(adapter);

        lLyoutHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_PartyName.clearFocus();
                edt_Description.clearFocus();
                edt_address.clearFocus();
                edt_street.clearFocus();
                edt_city.clearFocus();
                edt_state.clearFocus();
                edt_pincode.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }


            }
        });

        //Code to clear Focus from editText
        edt_PartyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_PartyName.clearFocus();
                    edt_Description.requestFocus();
                    handled = true;

                }
                return handled;
            }
        });


        //Code to clear Focus from editText
        edt_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_address.clearFocus();
                    edt_street.requestFocus();

                    handled = true;

                }
                return handled;
            }
        });



        edt_street.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_street.clearFocus();
                    edt_city.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        edt_city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_city.clearFocus();
                    edt_state.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        edt_state.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_state.clearFocus();
                    edt_pincode.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        edt_pincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_NONE)) {
                    edt_pincode.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    handled = true;
                }
                return handled;
            }
        });


        //Code to show Focus on first letter
        edt_PartyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_PartyName.setText(edt_PartyName.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });


        //Code to show Focus on first letter
        edt_Description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_Description.setText(edt_Description.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });



        //Code to show Focus on first letter
        edt_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_address.setText(edt_address.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });

        //Code to show Focus on first letter
        edt_street.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_street.setText(edt_street.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });

        //Code to show Focus on first letter
        edt_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_city.setText(edt_city.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });

        //Code to show Focus on first letter
        edt_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_state.setText(edt_state.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });

        //Code to show Focus on first letter
        edt_pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edt_pincode.setText(edt_pincode.getText().toString().replaceAll("\\s+", " ").trim());
            }
        });


        cb_byobYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_byobYes.setChecked(true);
                cb_byobNo.setChecked(false);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });



        cb_byobNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_byobNo.setChecked(true);
                cb_byobYes.setChecked(false);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });



        cb_mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_mask.setChecked(true);
                cb_unmask.setChecked(false);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });


        cb_unmask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HostActivity.this);
                alertDialogBuilder
                        .setTitle("Pay for Unmasking Party.")
                        .setMessage("Are you sure you want to pay for Party?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                cb_unmask.setChecked(false);
                                cb_mask.setChecked(true);
                            }
                        })
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        cb_unmask.setChecked(true);
                                        cb_mask.setChecked(false);
                                        cb_unmask.setEnabled(false);
                                        cb_mask.setEnabled(false);

                                    }
                                });
                alertDialogBuilder.show();


                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


        cb_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_getLocation.setChecked(true);
                cb_EnterAddress.setChecked(false);
                llayoutenterAddress.setVisibility(View.GONE);
                edt_address.setText("");
                edt_street.setText("");
                edt_city.setText("");
                edt_state.setText("");
                edt_pincode.setText("");
                edt_address.setError(null);
                edt_street.setError(null);
                edt_city.setError(null);
                edt_state.setError(null);
                edt_pincode.setError(null);
                
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                if ((int) Build.VERSION.SDK_INT < 23)
                {
                    getSelfLocation();

                }
                else {
                    if (ActivityCompat.checkSelfPermission(HostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(HostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) cont,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_CF_LOCATION);
                    } else {
                        getSelfLocation();

                    }
                }

            }
        });


        cb_EnterAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_EnterAddress.setChecked(true);
                cb_getLocation.setChecked(false);
                edt_address.requestFocus();
                llayoutenterAddress.setVisibility(View.VISIBLE);
                edt_address.setText("");
                edt_street.setText("");
                edt_city.setText("");
                edt_state.setText("");
                edt_pincode.setText("");
                edt_address.setError(null);
                edt_street.setError(null);
                edt_city.setError(null);
                edt_state.setError(null);
                edt_pincode.setError(null);


            }
        });

        //Calendar date,time value for StartDateTime
        final Calendar calendar = Validations.getCalendar();
        startDate = calendar.get(Calendar.DAY_OF_MONTH);
        startMon = calendar.get(Calendar.MONTH);
        startYear = calendar.get(Calendar.YEAR);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMin = calendar.get(Calendar.MINUTE);



        txtStartDate.setText(getDateNo(startDate) + "-" + getMonthNo(startMon) + "-" + startYear + ", " + Validations.showTime(startHour, startMin));
        //current date time in milliseconds
        selected_startTimeVal = getTimeinMs(startDate, startMon, startYear, startHour, startMin);

        //date,time value for EndDateTime
        endDate = calendar.get(Calendar.DAY_OF_MONTH);
        endMon = calendar.get(Calendar.MONTH);
        endYear = calendar.get(Calendar.YEAR);
        endHour = calendar.get(Calendar.HOUR_OF_DAY);
        endMin = calendar.get(Calendar.MINUTE);

        //txtEndDate.setText(getDateNo(endDate) + "-" + getMonthNo(endMon) + "-" + endYear + ", " + Validations.showTime(endHour, endMin));
        //current date time in milliseconds
        //selected_endTimeVal = getTimeinMs(endDate, endMon, endYear, endHour, endMin);

        //set startdateTime and endDateTime to temporary variables
        tempstartTimeVal[0] = selected_startTimeVal;
        //tempendTimeVal[0] = selected_endTimeVal;

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                startDateDialog("show");
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
//                    txtStartDate.setText(getDateNo(mDay) + "-" + getMonthNo(mMonth) + "-" + mYear + ", " + Validations.showTime(hour, min));
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

        // selection of end date edittext
        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                endDateDialog("show");
            }
        });

        // selection of end date textfield
//        txtEndDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                endDateDialog("show");
//            }
//        });


        // create party button click
        btn_createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_PartyName.clearFocus();
                edt_Description.clearFocus();
                edt_address.clearFocus();
                edt_street.clearFocus();
                edt_city.clearFocus();
                edt_state.clearFocus();
                edt_pincode.clearFocus();
                //edt_address.clearFocus();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                GenerikFunctions.sDialog(cont,"Creating party...");


                if(cb_byobYes.isChecked()){
                    selected_byob = "Yes";
                }else{
                    selected_byob = "No";
                }


                String val = Validations.checkWordsCount(edt_Description.getText().toString());
                String msg = "";
                int msglength = 0;

//                if(picturePath.equals(""))
//                {
//                    msg += "Please upload Image." + "\n";
//                    msglength++;
//                }

                if (compareWithCurrentTime(startHour, startMin) == false) {
                    msg += "PartyStartTime should be greater than current time." + "\n";
                    msglength++;

                }

                else if(edtEndDate.getText().toString().equals("Select End Time")){
                    msg += "Please select PartyEndTime." + "\n";
                    msglength++;

                }
                else if (selected_endTimeVal - selected_startTimeVal == 0 || selected_endTimeVal - selected_startTimeVal < ConstsCore.hourVal)
                {
                    msg += "PartyEndTime should be greater than one hour." + "\n";
                    msglength++;

                }
//
                else if (selected_endTimeVal - selected_startTimeVal > ConstsCore.TwelveHrVal)
                {
                    msg += "PartyEndTime cannot be greater than 12 hours." + "\n";
                    msglength++;
                }

                if(edt_PartyName.getText().toString().replaceAll("\\s+", " ").trim().equals("") || edt_PartyName.getText().toString().replaceAll("\\s+", " ").trim().equals(" "))
                {
                    //msg += "Please fill Party Name."+ "\n";
                    edt_PartyName.setError("Please fill Party Name");
                    msglength++;
                }
                else
                {
                    edt_PartyName.setError(null);
                }

                if(!val.equals("true")) {
                    edt_Description.setError(val);
                    //msg += val + "\n";
                    msglength++;
                }else{
                    edt_Description.setError(null);
                }


                if(!cb_getLocation.isChecked() && (!cb_EnterAddress.isChecked())){
                    msg += "Please Select Address";
                    msglength++;
                }


//                if(cb_getLocation.isChecked())
//                {
//                    Log.e("edt_locationAddress.getText().toString().length()"," "+edt_locationAddress.getText().toString().length());
//                    if(edt_locationAddress.getText().toString().length() == 0)
//                    {
//                        msg += "Unable to get current location.";
//                        msglength++;
//                        edt_locationAddress.setError("Unable to get current location.");
//                    }else{
//                        edt_locationAddress.setError(null);
//                    }
//                }

                if(cb_EnterAddress.isChecked() || cb_getLocation.isChecked()) {
                    if (edt_address.getText().toString().length() == 0) {
                        //msg += "Please fill the Address."+ "\n";
                        msglength++;
                        edt_address.setError("Please Enter Building No./Name");
                    }else{
                        edt_address.setError(null);
                    }
                    if (edt_street.getText().toString().length() == 0) {
                        //msg += "Please fill the Address"+ "\n";
                        msglength++;
                        edt_street.setError("Please Enter Street Name.");
                    }else{
                        edt_street.setError(null);
                    }
                    if (edt_city.getText().toString().length() == 0) {
                        //msg += "Please fill the Address"+ "\n";
                        msglength++;
                        edt_city.setError("Please Enter City Name");
                    }else{
                        edt_city.setError(null);
                    }
                    if (edt_state.getText().toString().length() == 0) {
                        //msg += "Please fill the Address"+ "\n";
                        msglength++;
                        edt_state.setError("Please Enter State Name");
                    }else{
                        edt_state.setError(null);
                    }
                    if (edt_pincode.getText().toString().length() == 0) {
                        //msg += "Please fill the Address"+ "\n";
                        msglength++;
                        edt_pincode.setError("Please Enter PIN code");
                    }else{
                        edt_pincode.setError(null);
                    }
                }
                Log.e("msglength"," "+msglength);

                if(msglength == 0)
                {

                    String Address = edt_address.getText().toString().trim();
                    Log.e("address",Address);
                    String street = edt_street.getText().toString().trim();
                    String city  = edt_city.getText().toString().trim();
                    String state = edt_state.getText().toString().trim();
                    String pin = edt_pincode.getText().toString().trim();
                    final String newAddress = Address +", " + street +", " + city+ ", " + state + ", " + pin;
                    Log.i("Entered Address ", newAddress);

                    if(cb_EnterAddress.isChecked())
                    {
                        getLatLong(newAddress,0);
                    }
                    else if(cb_getLocation.isChecked()) {

                        final String latitude = String.valueOf(currentlocation.getLatitude());
                        final String longitude = String.valueOf(currentlocation.getLongitude());


                        Log.e("ChatService.getInstance().getCurrentUser()", " " + ChatService.getInstance().getCurrentUser());
                        if (ChatService.getInstance().getCurrentUser() == null)
                        {
                            String accessToken = LoginValidations.getFBAccessToken().getToken();

                            QBSessionClass.getInstance().getQBSession(new QBEntityCallback()
                            {

                                @Override
                                public void onSuccess(Object o, Bundle bundle) {
                                    Handler h = new Handler(cont.getMainLooper());
                                    h.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            new AWSPartyOperations.createParty(cont, initialiseParty(cont, latitude, longitude, newAddress)).execute();
                                        }
                                    });
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                    GenerikFunctions.hDialog();
                                    GenerikFunctions.showToast(cont, "Party creation failed, Please try again after some time");
                                }

                            }, accessToken, null, cont);


                        }
                        else
                        {
                            new AWSPartyOperations.createParty(cont, initialiseParty(cont, latitude, longitude, newAddress)).execute();
                        }

                    }

                }
                else
                {
                    Log.e("msg"," "+msg.equals(""));

                    if(msg.equals(""))
                    {
                        GenerikFunctions.hDialog();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), " " + msg, Toast.LENGTH_LONG).show();
                        GenerikFunctions.hDialog();
                    }


                }


            }


        });

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

    }





    //function for converting values in milliseconds
    public long getTimeinMs(int mDay, int mMonth, int mYear, int hour, int minute){


        Log.e("----"," "+mDay+"--- "+mMonth+"--"+mYear+"---"+hour+"----"+minute);

        Calendar calendar = Calendar.getInstance();
//        calendar.set(mYear, mMonth, mDay,
//                hour, minute, 00);

        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long TimeinMs = calendar.getTimeInMillis();
        Log.e("getTime "+TimeinMs,"");
        return TimeinMs;


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        String inputString = hour +":"+minute+":00";
//        String date = mYear+"-"+mMonth+"-"+mDay;
//        Log.e("date "," "+date +" --- "+inputString);
//        Date d = null;
//        try {
//             d = sdf.parse(date+" " + inputString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("getTime "," "+d.getTime());
//        return d.getTime();

    }

    //compare starttime with currenttime
    private boolean compareWithCurrentTime(int startHour, int startMin) {
        Boolean value = false;
        Log.e("startHour"+startHour+"     "+startMin,"");

        int currHour, currMin;
        Calendar calendar = Validations.getCalendar();
        currHour = calendar.get(Calendar.HOUR_OF_DAY);
        currMin = calendar.get(Calendar.MINUTE);        Log.e("currHour"+currHour+"     "+currMin,"");
        if(startHour < currHour){
            value = false;
        }
        else if(startHour == currHour){
            if(startMin < currMin) {
                value = false;
            }
            else{
                value = true;
            }
        }else{
            value = true;
        }
        return value;

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

        final int tempstartHour;
        final int tempstartMin;

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(startHour);
        timePicker.setCurrentMinute(startMin);
        tempstartHour = startHour;
        tempstartMin = startMin;

        new AlertDialog.Builder(this)
                .setTitle("Set Time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startHour = timePicker.getCurrentHour();
                        startMin = timePicker.getCurrentMinute();
                        tempstartTimeVal[0] = getTimeinMs(startDate, startMon, startYear, startHour,startMin);
                        Log.e("values... ", "  " + tempstartTimeVal[0]+"----"+selected_endTimeVal+" "+(selected_endTimeVal == 0)+" --- "+compareWithCurrentTime(startHour, startMin));

                       if(selected_endTimeVal == 0)
                       {

                           Boolean val = compareWithCurrentTime(startHour, startMin);
                           Log.e("val"," "+val);
                           if(val == false)
                           {
                               startHour = tempstartHour;
                               startMin = tempstartMin;
                               Toast.makeText(getApplicationContext(), "StartTime should be greater than current time. ", Toast.LENGTH_SHORT).show();
                           }
                           else
                           {
                               txtStartDate.setText(getDateNo(startDate) + "-" + getMonthNo(startMon) + "-" + startYear + ", " + Validations.showTime(startHour, startMin));
                               selected_startTimeVal = tempstartTimeVal[0];
                               Log.e("selected_startTimeVal", " " + selected_startTimeVal + " " + new Date(selected_startTimeVal));
                           }


                        }
                       else
                       {
                           Boolean val = compareWithCurrentTime(startHour, startMin);
                           if(val == false)
                           {
                               startHour = tempstartHour;
                               startMin = tempstartMin;
                               Toast.makeText(getApplicationContext(), "StartTime should be greater than current time.", Toast.LENGTH_SHORT).show();
                           }
                           else if (selected_endTimeVal - tempstartTimeVal[0] == 0 || selected_endTimeVal - tempstartTimeVal[0] < 0 || selected_endTimeVal - tempstartTimeVal[0] < ConstsCore.hourVal)
                           {
                               startHour = tempstartHour;
                               startMin = tempstartMin;
                               Toast.makeText(getApplicationContext(), "Party minimum duraton is of 1 hour.", Toast.LENGTH_SHORT).show();
                           }
                           else if (selected_endTimeVal - tempstartTimeVal[0] > ConstsCore.TwelveHrVal)
                           {
                               startHour = tempstartHour;
                               startMin = tempstartMin;
                               Toast.makeText(getApplicationContext(), "Party maximum duration is of 12 hours.", Toast.LENGTH_SHORT).show();

                           }
                           else
                           {
                               txtStartDate.setText(getDateNo(startDate) + "-" + getMonthNo(startMon) + "-" + startYear + ", " + Validations.showTime(startHour, startMin));
                               selected_startTimeVal = tempstartTimeVal[0];
                               Log.e("selected_startTimeVal", " " + selected_startTimeVal+" "+new Date(selected_startTimeVal));

                           }

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
                        //date,time value for EndDateTime

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

        final int tempendHour;
        final int tempendMin;

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(endHour);
        timePicker.setCurrentMinute(endMin);
        tempendHour = endHour;
        tempendMin = endMin;

        new AlertDialog.Builder(this)
                .setTitle("Set Time")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endHour = timePicker.getCurrentHour();
                        endMin = timePicker.getCurrentMinute();
                        tempendTimeVal[0] = getTimeinMs(endDate, endMon, endYear, endHour, endMin);


                        long finalEndTimeVal = new Date(tempendTimeVal[0]).getTime();
                        long finalStartTimeVal = new Date(selected_startTimeVal).getTime();
                        long diff = finalEndTimeVal - finalStartTimeVal - 1000;
//                      long diffHours = diff / (60 * 60 * 1000);
                        Log.e("tempendTimeVal format----", "    " + tempendTimeVal[0]+"---"+selected_startTimeVal+ " ---"+( tempendTimeVal[0] - selected_startTimeVal));
                        Log.e("date format----", "    " + finalEndTimeVal+"---"+finalStartTimeVal+ " ---"+diff);
//                        Log.e("diffHours "," "+diffHours);
//                        Log.e("compare "," "+(tempendTimeVal[0] - selected_startTimeVal));
//                        Log.e("compare111--- "," "+(tempendTimeVal[0] < selected_startTimeVal));
//                        Log.e("compare2222==== "," "+(tempendTimeVal[0] - selected_startTimeVal < ConstsCore.hourVal));



                        if (tempendTimeVal[0] - selected_startTimeVal == 0 || tempendTimeVal[0] < selected_startTimeVal || tempendTimeVal[0] - selected_startTimeVal < ConstsCore.hourVal)
                        {
                            endHour = tempendHour;
                            endMin = tempendMin;
                            Toast.makeText(getApplicationContext(), " EndTime should be greater than 1 hour ", Toast.LENGTH_SHORT).show();
                        }
                        else if (tempendTimeVal[0] - selected_startTimeVal > ConstsCore.TwelveHrVal)
                        {   endHour = tempendHour;
                            endMin = tempendMin;
                            Toast.makeText(getApplicationContext(), " EndTime cannot be greater than 12 hours ", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            selected_endTimeVal = tempendTimeVal[0];
                            edtEndDate.setText(getDateNo(endDate) + "-" + getMonthNo(endMon) + "-" + endYear + ", " + Validations.showTime(endHour, endMin));

                        }
                    }

                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("Picker", "Cancelled!");
                            }
                        }).setView(timePicker).show();





    }


    //*// function providing latitude longitude from address
    public void getLatLong(String newAddress,int iteration)
    {
        try
        {
            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(newAddress,getApplicationContext(), new GeocoderHandler(iteration));

        }
        catch(Exception e)
        {
            Log.e("Excep 11",e.toString());
            e.printStackTrace();
        }
    }

    private class GeocoderHandler extends Handler
    {
        int iteration;
        public GeocoderHandler(int iteration)
        {
            this.iteration = iteration;
        }
        @Override
        public void handleMessage(Message message)
        {
            final String locationAddress;
            switch (message.what)
            {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            Log.e("Location Address",""+locationAddress );

            if(locationAddress == null)
            {
                switch(iteration)
                {
                    case 0 : {
                        String street = edt_street.getText().toString().trim();
                        String city  = edt_city.getText().toString().trim();
                        String state = edt_state.getText().toString().trim();
                        String pin = edt_pincode.getText().toString().trim();

                        String newAddress = street +", " + city+ ", " + state + ", " + pin;
                        Log.i("Entered Address with " + iteration , newAddress);
                        newAddress = newAddress.replace(" ", "+");
                        Log.i("Processed Address ", newAddress);
                        iteration++;
                        getLatLong(newAddress,iteration);
                    }
                    break;

                    case 1 : {
                        String city  = edt_city.getText().toString().trim();
                        String state = edt_state.getText().toString().trim();
                        String pin = edt_pincode.getText().toString().trim();

                        String newAddress =  city+ ", " + state + ", " + pin;
                        Log.i("Entered Address with " + iteration, newAddress);
                        newAddress = newAddress.replace(" ", "+");
                        Log.i("Processed Address ", newAddress);
                        iteration++;
                        getLatLong(newAddress,iteration);
                    }
                    break;

                    case 2 : {
                        Toast.makeText(getApplicationContext(), "Unable to get Latitude and Longitude for this address location.",Toast.LENGTH_SHORT).show();
                    }
                    break;

                }
                //edt_address.setError("Unable to get Latitude and Longitude for this address location.");
            }
            else
            {

                String Address = edt_address.getText().toString().trim();
                String street = edt_street.getText().toString().trim();
                String city  = edt_city.getText().toString().trim();
                String state = edt_state.getText().toString().trim();
                String pin = edt_pincode.getText().toString().trim();
                final String fullAddress = Address +", " + street +", " + city+ ", " + state + ", " + pin;

                Log.e("Store Party to AWS","Yes");
                Toast.makeText(HostActivity.this,"Store Party to AWS",Toast.LENGTH_LONG);
                Log.e("ChatService.getInstance().getCurrentUser()", " " + ChatService.getInstance().getCurrentUser());
                if (ChatService.getInstance().getCurrentUser() == null)
                {
                    String accessToken = LoginValidations.getFBAccessToken().getToken();

                    QBSessionClass.getInstance().getQBSession(new QBEntityCallback()
                    {

                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            Handler h = new Handler(cont.getMainLooper());
                            h.post(new Runnable() {
                                @Override
                                public void run() {

                                    new AWSPartyOperations.createParty(cont, initialiseParty(cont, locationAddress.split(" ")[0],locationAddress.split(" ")[1], fullAddress)).execute();
                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                            GenerikFunctions.hDialog();
                            GenerikFunctions.showToast(cont, "Party creation failed, Please try again after some time");
                        }

                    }, accessToken, null, cont);


                }
                else
                {
                    new AWSPartyOperations.createParty(cont, initialiseParty(cont, locationAddress.split(" ")[0],locationAddress.split(" ")[1], fullAddress)).execute();
                }

            }

        }
    }

   //*//



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
                    int value = 0;
                    if (bitmap.getHeight() <= bitmap.getWidth()) {
                        value = bitmap.getHeight();
                    } else {
                        value = bitmap.getWidth();
                    }
                    Bitmap finalbitmap = null;
                    finalbitmap = Bitmap.createScaledBitmap(bitmap,value,value,true);
                    imgParty.setImageBitmap(finalbitmap);


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
                //Log.e("--- "," "+bitmap.getWidth()+" "+bitmap.getHeight());
                int value = 0;
                if (bitmap.getHeight() <= bitmap.getWidth()) {
                    value = bitmap.getHeight();
                } else {
                    value = bitmap.getWidth();
                }

                Bitmap finalbitmap = null;
                finalbitmap = Bitmap.createScaledBitmap(bitmap,value,value,true);
                imgParty.setImageBitmap(finalbitmap);

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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        //showStoragePermissionRationale();

                        new AlertDialog.Builder(HostActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage(getResources().getString(R.string.message_cf_location_permission))
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        ActivityCompat.requestPermissions(HostActivity.this,
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


    //**//Function for storing data of Party while creating party
    public PartyTable initialiseParty(Context cont, String latitude, String longitude, String fullAddress)
    {

        String HostFBID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        List latlong = new ArrayList();
        latlong.add(latitude);
        latlong.add(longitude);

        PartyTable party = new PartyTable();
        party.setPartyID(Validations.getUniqueId(cont));
        party.setPartyName(edt_PartyName.getText().toString());
        party.setStartTime(String.valueOf(selected_startTimeVal));
        party.setEndTime(String.valueOf(selected_endTimeVal));
        //party.setDate("");
        party.setHostFBID(HostFBID);
        party.setHostQBID(sharedPreferences.getString(m_config.QuickBloxID,""));//String.valueOf(m_config.chatService.getUser().getId()));
        party.setHostName(sharedPreferences.getString(m_config.Entered_User_Name, ""));
        //party.setPartyType("");
        party.setPartyDescription(edt_Description.getText().toString().trim());
        if(selected_byob.equals("Yes")){
            party.setBYOB("Yes");
        }else{
            party.setBYOB("No");
        }

        party.setPartyAddress(fullAddress);
        party.setPartylatlong(latlong);
        party.setPartyImage(picturePath);
        //party.setPartyImage("");
        //party.setMaskStatus("");
        party.setDialogID("N/A");
        return party;

    }
    //**//

    //***// Function for getting Current Location
    public void getSelfLocation() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(HostActivity.this, "GPS not Available", Toast.LENGTH_LONG).show();
            Log.e("GPS Disabled", "Disabled---");
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.e("GPS Disabled ", "Disabled");
                        
                    } else {
                        currentlocation = getLastBestLocation();
                        //Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
                        if (currentlocation != null) {
                            double latitude = currentlocation.getLatitude();
                            double longitude = currentlocation.getLongitude();
                            Geocoder geocoder = new Geocoder(HostActivity.this, Locale.getDefault());
                            try {


                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                Log.e("here", " " + addresses);

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                                //edt_locationAddress.setText(address + " ,  " + city + "  ,  " + state + "  ,  " + country + "  ,  " + postalCode);
                                //llayoutGetLocation.setVisibility(View.VISIBLE);
                                edt_street.setText(address);
                                edt_city.setText(city);
                                edt_state.setText(state);
                                edt_pincode.setText(postalCode);
                                edt_address.requestFocus();
                                llayoutenterAddress.setVisibility(View.VISIBLE);


                            }
                            catch (Exception e)
                            {

                                e.printStackTrace();
                                Toast.makeText(cont, "Unable to get current location.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {

                            Toast.makeText(cont, "Unable to get current location.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {

            currentlocation = getLastBestLocation();
            //Log.e("Func  ", currentlocation.getLatitude() + "     " + currentlocation.getLongitude());
            if (currentlocation != null) {

                double latitude = currentlocation.getLatitude();
                double longitude = currentlocation.getLongitude();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try
                {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Log.e("here", " " + addresses);

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
             //       String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

//                    edt_locationAddress.setText(address + " ,  " + city + "  ,  " + state + "  ,  " /*+ country + "  ,  "*/ + postalCode);
//                    llayoutGetLocation.setVisibility(View.VISIBLE);
                    edt_street.setText(address);
                    edt_city.setText(city);
                    edt_state.setText(state);
                    edt_pincode.setText(postalCode);
                    edt_address.requestFocus();
                    llayoutenterAddress.setVisibility(View.VISIBLE);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(cont, "Unable to get current location.",Toast.LENGTH_SHORT).show();

                }
            }
            else
            {
                Toast.makeText(cont, "Unable to get current location.",Toast.LENGTH_SHORT).show();
            }




        }

    }
    //***//

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

    @Override
    protected void onResume() {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
        if(cb_getLocation.isChecked()){
            getSelfLocation();
        }

    }

    @Override
    public void onBackPressed() {

        edt_PartyName.clearFocus();
        edt_Description.clearFocus();
        edt_address.clearFocus();
        edt_street.clearFocus();
        edt_city.clearFocus();
        edt_state.clearFocus();
        edt_pincode.clearFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null){
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        finish();
    }

}
