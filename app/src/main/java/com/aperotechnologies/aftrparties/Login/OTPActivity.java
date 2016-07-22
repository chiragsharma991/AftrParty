package com.aperotechnologies.aftrparties.Login;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.Contact;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ContactTable;
import com.aperotechnologies.aftrparties.HomePage.HomePageActivity;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.aperotechnologies.aftrparties.model.LoggedInUserInformation;
import com.aperotechnologies.aftrparties.model.OtpRequestApi;
import com.aperotechnologies.aftrparties.model.OtpVerifiedResponse;
import com.aperotechnologies.aftrparties.model.ValidOTPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by mpatil on 26/05/16.
 */
public class OTPActivity extends Activity
{

    Button btn_generate, btn_verify, btn_submit;
    EditText edt_otp, edt_mob_no;
    RequestQueue queue;
    Context cont = this;
    String verfication_Id, mobileNumber;
    TextView txt_timer;
    LinearLayout grid_otp;
    // ShredPreferences

    //For OTP
   //Get these from FOneVerify  dashboard
    String customerId = "r3r31h14";
    String appkey = "a8244ed6ddf7ce236fd9a09581877435";
    String appid = "dc53aa537b3eb74694ca3601dd6daea5";

    RequestInterceptor requestInterceptor;
    Gson gson;
    OtpRequestApi otpRequestApi;

    RestAdapter adapter;

    String primaryUrl = "http://apifv.foneverify.com/U2opia_Verify/v1.0/trial";

    String newPrimaryUrl = primaryUrl;

    OtpVerifiedResponse otpVerifiedResponse;

    CountDownTimer countDownTimer;

    HashSet<Contact> lhm;
    ArrayList<Contact> finalContacts;
    LoggedInUserInformation loggedInUserInformation;

    String name = "N/A";
    SharedPreferences sharedpreferences;
    Configuration_Parameter m_config;

    private static final  int MY_PERMISSIONS_REQUEST_READ_CONTATCS = 1;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_config = Configuration_Parameter.getInstance();

        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;

        // btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        edt_otp = (EditText) findViewById(R.id.edi_otp);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        edt_mob_no = (EditText) findViewById(R.id.edi_verify_mobileno);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        grid_otp = (LinearLayout) findViewById(R.id.grid_otp);
        //queue = Volley.newRequestQueue(cont);
        lhm = new HashSet<Contact>();
        countDownTimer=null;
        edt_otp.setRawInputType(Configuration.KEYBOARD_12KEY);

        mobileNumber = sharedpreferences.getString(m_config.Entered_Contact_No,"N/A");

        queue = Volley.newRequestQueue(cont);
        gson = new Gson();

        //Creating a RestAdapter
        adapter = new RestAdapter.Builder()
                .setEndpoint(primaryUrl) //Setting the Root URL
                .build(); //Finally building the adapter


        loggedInUserInformation = LoginValidations.initialiseLoggedInUser(cont);

        finalContacts = new ArrayList<Contact>();

        Log.e("Shrd Pref inOTPActivity",sharedpreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                sharedpreferences.getString(m_config.Entered_Email,"N/A") + "   "
                + sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));

        //Creating an object of our api interface
        otpRequestApi = adapter.create(OtpRequestApi.class);

        requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                Log.e("Interceptor intercept", request.toString());

            }
        };

        grid_otp.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                dismissCursor();
                return true;
            }
        });

        Log.e("Shrd Pref in OTP Activity",sharedpreferences.getString(m_config.Entered_User_Name,"N/A") + "   " +
                sharedpreferences.getString(m_config.Entered_Email,"N/A") + "   "
                + sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));


        //Function to generate OTP
        GenerateOTP();


        edt_otp.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                edt_otp.setCursorVisible(true);
                return false;
            }
        });

        edt_mob_no.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                edt_mob_no.setCursorVisible(true);
                return false;
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e(" btn_submit clicked", "Yes");
                mobileNumber = edt_mob_no.getText().toString().trim();
                Log.e("Mobile No", mobileNumber);

                if(mobileNumber.trim().equals(sharedpreferences.getString(m_config.Entered_Contact_No,"N/A")))
                {
                    Log.e("Same As previous mobile","request for fallback");
                    edt_mob_no.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.GONE);
                    String otp = "";
                    newPrimaryUrl = primaryUrl + "/update?appKey=" + appkey + "&customerId=" + customerId + "&verificationId=" + verfication_Id + "&code=" + otp;
                    Log.e("New fallback URL", newPrimaryUrl);
                    verifyEnteredOTP(newPrimaryUrl);
                }
                else
                {
                    //  Log.e("Not Same As previous mobile no","New OTP Cycle");
                    edt_mob_no.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.GONE);
                    Log.e("New Mobile No", mobileNumber);
                    //Defining the method for regenerating OTP on new mobile no
                    otpRequestApi.getOTP(appkey, customerId, "IN", mobileNumber, "application/x-www-form-urlencoded", new Callback<ValidOTPResponse>() {
                        @Override
                        public void success(ValidOTPResponse validOTPResponse, retrofit.client.Response response)
                        {
//                            Log.e("validOTP Response for fallback", validOTPResponse.getMobileNumber() + "   " + validOTPResponse.getVerificationId() + "   "
//                                    + validOTPResponse.getResponseCode() + "   " + validOTPResponse.getSmsCLI() + "   " + validOTPResponse.getTimeout());
                            verfication_Id = validOTPResponse.getVerificationId().trim();
                            if(countDownTimer!=null)
                            {
                                countDownTimer.cancel();
                                countDownTimer = null;
                            }
                            if(countDownTimer==null)
                            {
                                countDownTimer = new CountDownTimer(90001, 1000)
                                {
                                    public void onTick(long millisUntilFinished)
                                    {
                                        txt_timer.setText(""+millisUntilFinished / 1000);
                                    }

                                    public void onFinish()
                                    {
                                        edt_mob_no.setVisibility(View.VISIBLE);
                                        edt_mob_no.setText(sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
                                        btn_submit.setVisibility(View.VISIBLE);
                                        txt_timer.setText("Time out ... ! Regenerate OTP");
                                    }
                                }.start();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error)
                        {
                            Log.e("Retrofit error", error.toString());
                            error.printStackTrace();
                        }
                    });
                }
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String otp = edt_otp.getText().toString().trim();
                newPrimaryUrl = primaryUrl + "/update?appKey=" + appkey + "&customerId=" + customerId + "&verificationId=" + verfication_Id + "&code=" + otp;
                Log.e("New URL", newPrimaryUrl);
                verifyEnteredOTP(newPrimaryUrl);
            }
        });
    }

    // - See more at: http://www.theappguruz.com/blog/android-count-timer#sthash.PfD5YcqI.dpuf

    public void dismissCursor()
    {
        //Meghana
        //Clear Focus from all edit texts
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_otp.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_mob_no.getWindowToken(), 0);
        edt_otp.clearFocus();
        edt_mob_no.clearFocus();
        edt_otp.setCursorVisible(false);
        edt_mob_no.setCursorVisible(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Crouton.cancelAllCroutons();
        m_config.foregroundCont = this;
    }

    //Generate OTP with simple retrofit callback request to registered mobile no
    public void GenerateOTP()
    {
        //Defining the method
        otpRequestApi.getOTP(appkey, customerId, "IN", mobileNumber, "application/x-www-form-urlencoded", new Callback<ValidOTPResponse>()
        {
            @Override
            public void success(ValidOTPResponse validOTPResponse, retrofit.client.Response response)
            {
                Log.e("validOTP Response", validOTPResponse.getMobileNumber() + "   " + validOTPResponse.getVerificationId() + "   "
                        + validOTPResponse.getResponseCode() + "   " + validOTPResponse.getSmsCLI() + "   " + validOTPResponse.getTimeout());

                if(validOTPResponse.getResponseCode().equals("508"))
                {
                    Toast.makeText(OTPActivity.this,"Limits finished",Toast.LENGTH_LONG).show();
                }
                else
                {
                    verfication_Id = validOTPResponse.getVerificationId().trim();
                    if(countDownTimer==null)
                    {
                        countDownTimer = new CountDownTimer(90000, 1000)
                        {
                            public void onTick(long millisUntilFinished)
                            {
                                txt_timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                            }

                            public void onFinish()
                            {
                                edt_mob_no.setVisibility(View.VISIBLE);
                                edt_mob_no.setText(sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
                                btn_submit.setVisibility(View.VISIBLE);
                                txt_timer.setText("Time out ... ! Regenerate OTP");
                            }
                        }.start();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error)
            {

                countDownTimer.cancel();
                countDownTimer=null;
                Log.e("Retrofit error", error.toString());
                error.printStackTrace();
            }
        });
    }

    
    //Navigate to registration activity
    @Override
    public void onBackPressed()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_otp.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_mob_no.getWindowToken(), 0);
        edt_otp.clearFocus();
        edt_mob_no.clearFocus();
        edt_otp.setCursorVisible(false);
        edt_mob_no.setCursorVisible(false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OTPActivity.this);
        alertDialogBuilder
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();

                                Intent intent = new Intent(OTPActivity.this,RegistrationActivity.class);
                                startActivity(intent);
                            }
                        });
        alertDialogBuilder.show();
    }
    
    //Verify entered OTP
    public void verifyEnteredOTP(final String url)
    {
        final StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("OTP Receive response", response);
                        //Parse JSON Data
                        try
                        {
                            otpVerifiedResponse = gson.fromJson(response.toString(), OtpVerifiedResponse.class);
                            Log.e("", otpVerifiedResponse.getVerificationId() + "");
                            Log.e("", otpVerifiedResponse.getVerificationStatus() + "");
                            Log.e("", otpVerifiedResponse.getResponseCode() + "");
                            //For wrong OTP
                            if (otpVerifiedResponse.getVerificationStatus().equals("WRONG_OTP_PROVIDED"))
                            {
                                GenerikFunctions.showToast(cont, "Enter Correct OTP");
                                edt_otp.setText("");
                            }
                            //Upon completion of OTP verification cycle
                            else if (otpVerifiedResponse.getVerificationStatus().equals("VERIFICATION_COMPLETED"))
                            {
                                countDownTimer.cancel();
                                countDownTimer = null;
                                GenerikFunctions.showToast(cont, "Verification Done");
                                txt_timer.setText("");

                                //Fetch Contacts Here
                                getDeviceContatcs();

                            }
                            //Fallbak mode of OTP
                            else if(otpVerifiedResponse.getVerificationStatus().equals("TRYING_FALLBACK_SMS_NOT_DELIVERED"))
                            {
                                Log.e("Restart timer","Yes");
                                countDownTimer.cancel();
                                countDownTimer=null;

                                countDownTimer = new CountDownTimer(90001, 1000)
                                {
                                    public void onTick(long millisUntilFinished)
                                    {
                                        txt_timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                                    }

                                    public void onFinish()
                                    {
                                        edt_mob_no.setVisibility(View.VISIBLE);
                                        edt_mob_no.setText(sharedpreferences.getString(m_config.Entered_Contact_No,"N/A"));
                                        btn_submit.setVisibility(View.VISIBLE);
                                        txt_timer.setText("Time out ... ! Regenerate OTP");
                                    }
                                }.start();
                            }
                            //Upon failure of cycle
                            else if(otpVerifiedResponse.getVerificationStatus().equals("VERIFICATION_FAILED"))
                            {
                                Toast.makeText(OTPActivity.this,"Verification failed. Please check your mobile no",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(OTPActivity.this,RegistrationActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();
                        Log.e("Error", error + "");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        int socketTimeout = 5000;//5 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    //Function to retrieve Contacts in Contacts Arraylist
    private void displayContacts()
    {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY
                + " ASC");
        int index = 0;
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> contatcs = new ArrayList<String>();

        ArrayList<String> NP = new ArrayList<>();
        if (cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                names.add(name);

                Contact contact = new Contact();

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    //contatcs.clear();
                    String email = "N/A";
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (emailCur.getCount() > 0)
                    {
                        emailCur.moveToFirst();
                        email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }

                    emailCur.close();

                    if (pCur.getCount() > 0)
                    {
                        pCur.moveToFirst();
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //  Log.e("Name: " + name, "Phone No: " + phoneNo+"    Email : " + email);

                        int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                // Log.e(name + ": TYPE_MOBILE", " " + phoneNo);
                                contact.setConactNo(phoneNo);
                                contact.setEmail(email);
                                contact.setName(name);
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                //  Log.e(name + ": TYPE_HOME", " " + phoneNo);
                                contact.setConactNo(phoneNo);
                                contact.setEmail(email);
                                contact.setName(name);
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                contact.setConactNo(phoneNo);
                                contact.setEmail(email);
                                contact.setName(name);
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                contact.setConactNo(phoneNo);
                                contact.setEmail(email);
                                contact.setName(name);
                                break;

                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                contact.setConactNo(phoneNo);
                                contact.setEmail(email);
                                contact.setName(name);
                                break;

                            default:
                                break;
                        }
                        pCur.close();
                    }
                }
                else
                {
                    //  Log.i("Name: "+ name, "Id: "+id  );//+" Email: " +email);
                    index = index + 1;
                    String phoneNo = "N/A";
                    String email = "N/A";

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    //  Log.e("emailCur.length",name + "    " + emailCur.getCount() +"   aa");

                    if (emailCur.getCount() > 0)
                    {
                        emailCur.moveToFirst();
                        email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emailCur.close();
                    contact.setConactNo(phoneNo);
                    contact.setEmail(email);
                    contact.setName(name);
                    //  Log.e("Name: " + name, "Phone No: " + phoneNo + "    Email : " + email);

                }

                lhm.add(contact);
            }
            for (Contact ct : lhm)
            {
                if (Validations.isValidEmailId(ct.name))
                {
                    //  Log.e("Dont Add ", ct.name);
                }
                else
                {
                    // Log.e("All ", ct.name);
                    NP.add(ct.toString());
                    finalContacts.add(ct);
                }
                //    System.out.println(pr);
            }

//            for(int i=0;i<NP.size();i++)
//            {
//                Log.e("Contatct  " + i,NP.get(i));
//            }

            new SaveContactsAsync().execute();

        }
    }


    //Save Contatcs to AWS

    class SaveContactsAsync extends AsyncTask<Void, Void, Void>
    {
        protected void onPostExecute(Void abc)
        {
            // TODO: check this.exception
            // TODO: do something with the feed
            SharedPreferences.Editor editor= sharedpreferences.edit();
            editor.putString(m_config.OTPValidationDone,"Yes");
            editor.apply();
            Log.e("--- "," loggedInUserInformation    "+loggedInUserInformation.getFB_USER_ID());
            new AWSLoginOperations.addUserRegStatus(cont, loggedInUserInformation).execute();


        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                ContactTable selContactData = m_config.mapper.load(ContactTable.class,loggedInUserInformation.getFB_USER_ID());
                if(selContactData == null)
                {
                    ContactTable contactTable = new ContactTable();
                    contactTable.setFBID(loggedInUserInformation.getFB_USER_ID());
                    contactTable.setContact(finalContacts);

                    Log.e("AWS Contact insert ", " " + contactTable.toString());
                    m_config.mapper.save(contactTable);
                }
                else if(selContactData.getFBID().equals(loggedInUserInformation.getFB_USER_ID()))
                {
                    selContactData.setContact(finalContacts);
                    Log.e("AWS Contact update ", " " + selContactData.toString());
                    m_config.mapper.save(selContactData);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Log.e("Error in AWS"," " +e.toString());
            }
            return null;
        }
    }

  //function for enabling TelephonyManager for fetching device contatcs
    public void getDeviceContatcs()
    {
        if ((int) Build.VERSION.SDK_INT < 23)
        {
            //this is a check for build version below 23
            displayContacts();
        }
        else
        {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(cont, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) cont,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTATCS);

            }
            else
            {
                displayContacts();
            }
        }
    }

    //Callback function for Android M Permission
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        Log.e("grantResults.length"," "+grantResults.length+" "+grantResults[0]);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_CONTATCS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    displayContacts();

                }
                else
                {
                    // permission denied
                    Log.e("Permission request is denied","");
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

                    boolean should = ActivityCompat.shouldShowRequestPermissionRationale((Activity) cont, Manifest.permission.READ_CONTACTS);
                    if(should)
                    {
                        //user denied without Never ask again, just show rationale explanation
                        new android.app.AlertDialog.Builder(OTPActivity.this)
                                .setTitle("Permission Denied")
                                .setMessage("Without this permission the app will be unable to read contatcs.Are you sure you want to deny this permission?")
                                .setPositiveButton("RE-TRY", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with delete
                                        Log.e("Click of Retry, If permission request is denied",",ask request for permission again");
                                        ActivityCompat.requestPermissions((Activity) cont,
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                MY_PERMISSIONS_REQUEST_READ_CONTATCS);
                                    }
                                })
                                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Log.e("Click of I m sure",", permission request is denied");

                                        SharedPreferences.Editor editor= sharedpreferences.edit();
                                        editor.putString(m_config.OTPValidationDone,"Yes");
                                        editor.apply();
                                        Log.e("--- "," loggedInUserInformation    "+loggedInUserInformation.getFB_USER_ID());
                                        new AWSLoginOperations.addUserRegStatus(cont, loggedInUserInformation).execute();
//
                                    }
                                }).show();
                    }
                    else
                    {
                        //user has denied with `Never Ask Again`
                        Log.e("Click of Never ask again",", permission request is denied");

                        SharedPreferences.Editor editor= sharedpreferences.edit();
                        editor.putString(m_config.OTPValidationDone,"Yes");
                        editor.apply();
                        Log.e("--- "," loggedInUserInformation    "+loggedInUserInformation.getFB_USER_ID());
                        new AWSLoginOperations.addUserRegStatus(cont, loggedInUserInformation).execute();
                    }
                }
                break;
            }
        }
    }
}
