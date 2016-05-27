package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.model.OtpVerifiedResponse;
import com.aperotechnologies.aftrparties.model.ValidOTPResponse;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by mpatil on 26/05/16.
 */
public class OTPActivity extends Activity
{
    Button btn_send,btn_verify;
    EditText edt_otp;
    RequestQueue queue;
    Context cont = this;
    String verfication_Id,mobileNumber;
    TextView txt_timer;

    //For OTP
    String customerId = "kr0lv39n";
    String appkey = "b22ab60eda70072cd34c507c4870ab3c";
    String appid = "c31396423a00078697c490cb13a6d99b";
    RequestInterceptor requestInterceptor;


    StringRequest postRequest;
    OtpRequestApi otpRequestApi;
    OtpVerificationApi otpVerificatioApi;

    RestAdapter adapter;

    String primaryUrl = "http://apifv.foneverify.com/U2opia_Verify/v1.0/trial";

    String newPrimaryUrl = primaryUrl;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btn_send=(Button)findViewById(R.id.btn_generate);
        btn_verify=(Button)findViewById(R.id.btn_verify);
        edt_otp=(EditText)findViewById(R.id.edi_otp);
        txt_timer = (TextView) findViewById(R.id.txt_timer);

        //queue = Volley.newRequestQueue(cont);

        mobileNumber = "8087752523";

        //Creating a RestAdapter
        adapter = new RestAdapter.Builder()
                .setEndpoint(primaryUrl) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating an object of our api interface
        otpRequestApi = adapter.create(OtpRequestApi.class);

        requestInterceptor = new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request)
            {
                Log.e("Interceptor intercept",request.toString());

            }
        };


//        //Defining the method
//        api.getOTP(appkey,customerId,"IN",mobileNumber/*,"application/x-www-form-urlencoded"*/,new Callback<ValidOTPResponse>()
//        {
//            @Override
//            public void success(ValidOTPResponse validOTPResponse, retrofit.client.Response response)
//            {
//
//                Log.e("validOTP Response",validOTPResponse.getMobileNumber() + "   " + validOTPResponse.getVerificationId() + "   "
//                + validOTPResponse.getResponseCode() + "   " + validOTPResponse.getVerificationStatus());
//
//            }
//
//            @Override
//            public void failure(RetrofitError error)
//            {
//                Log.e("Retrofit error",error.toString());
//                error.printStackTrace();
//            }
//        });

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Btn send clicked","Yes");

                //Defining the method
                otpRequestApi.getOTP(appkey,customerId,"IN",mobileNumber,"application/x-www-form-urlencoded",new Callback<ValidOTPResponse>()
                {
                    @Override
                    public void success(ValidOTPResponse validOTPResponse, retrofit.client.Response response)
                    {

                        Log.e("validOTP Response",validOTPResponse.getMobileNumber() + "   " + validOTPResponse.getVerificationId() + "   "
                                + validOTPResponse.getResponseCode() + "   " + validOTPResponse.getSmsCLI() +"   " +validOTPResponse.getTimeout());

                        verfication_Id = validOTPResponse.getVerificationId().trim();

                        CountDownTimer countDownTimer = new CountDownTimer(90000, 1000)
                        {
                            public void onTick(long millisUntilFinished)
                            {
                                txt_timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                            }
                            public void onFinish()
                            {
                                txt_timer.setText("Time out ... ! Regenerate OTP");
                            }
                        }.start();
                    }

                    @Override
                    public void failure(RetrofitError error)
                    {
                        Log.e("Retrofit error",error.toString());
                        error.printStackTrace();
                    }
                });
               // requestOTP();
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String otp=edt_otp.getText().toString().trim();

                newPrimaryUrl =  primaryUrl+"/update?appKey=" + appkey + "&customerId=" +  customerId +   "&verificationId="+verfication_Id+"&code="+otp;
                Log.e("New URL",newPrimaryUrl);

              //  Creating a RestAdapter
                adapter = new RestAdapter.Builder()
                        .setEndpoint(newPrimaryUrl) //Setting the Root URL
                        .build(); //Finally building the adapter

                //Creating an object of our api interface
                otpVerificatioApi = adapter.create(OtpVerificationApi.class);

                otpVerificatioApi.getBooks(new Callback<OtpVerifiedResponse>()
                {
                    @Override
                    public void success(OtpVerifiedResponse otpVerifiedResponse, retrofit.client.Response response)
                    {
                        Log.e("OtpVerifiedResponse", otpVerifiedResponse.getMobileNumber() +"   "
                                + otpVerifiedResponse.getVerificationStatus() + "      " +otpVerifiedResponse.getResponseCode() +  "    "
                                + otpVerifiedResponse.getVerificationId());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("OTP Verification Error",error.toString());
                        error.printStackTrace();
                    }
                });

            }
        });
    }

   // - See more at: http://www.theappguruz.com/blog/android-count-timer#sthash.PfD5YcqI.dpuf

    public RestAdapter getHostAdapter(String baseHost)
    {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseHost).build();
        return restAdapter;
    }


    public void requestOTP()
    {
        String url="http://apifv.foneverify.com/U2opia_Verify/v1.0/trial/sms";
        Log.e("URL 1",url);





        StringRequest stringRequest =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //  Log.d(TAG, response);

                Log.i("OTP Generated Response", response);
                //Parse JSON Data
                try
                {
                    JSONObject imageObject = new JSONObject(response);
                    verfication_Id=imageObject.getString("verificationId")+"";
                    mobileNumber=imageObject.getString("mobileNumber")+"";
                    String responseCode=imageObject.getInt("responseCode")+"";
                    String timeout=imageObject.getString("timeout")+"";
                    String smsCLI=imageObject.getString("smsCLI")+"";
                    Log.e("Verification code",""+verfication_Id);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("Volley Error",error.toString());
                error.printStackTrace();

                //  Log.e(TAG, error.getLocalizedMessage());
            }
        })
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("appKey",appkey);
                params.put("customerId",customerId);
                params.put("isoCountryCode","IN");
                //params.put("msisdn","9987936766                                                                                                                                                                 ");
                params.put("msisdn",mobileNumber);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response)
            {
                if (response.headers == null)
                {
                    // cant just set a new empty map because the member is final.
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }
                return super.parseNetworkResponse(response);
            }
        };
        //     int socketTimeout = 5000;//5 seconds
        //     RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //     postRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
















//        postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response)
//                    {
//                        Log.i("OTP Generated Response", response);
//                        //Parse JSON Data
//                        try
//                        {
//                            JSONObject imageObject = new JSONObject(response);
//                            verfication_Id=imageObject.getString("verificationId")+"";
//                            mobileNumber=imageObject.getString("mobileNumber")+"";
//                            String responseCode=imageObject.getInt("responseCode")+"";
//                            String timeout=imageObject.getString("timeout")+"";
//                            String smsCLI=imageObject.getString("smsCLI")+"";
//                            Log.e("Verification code",""+verfication_Id);
//                        }
//                        catch(Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        error.printStackTrace();
//                        Log.e("Error",error+"");
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String,String> getParams()
//            {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("appKey",appkey);
//                params.put("customerId",customerId);
//                params.put("isoCountryCode","IN");
//                //params.put("msisdn","9987936766                                                                                                                                                                 ");
//                params.put("msisdn",mobileNumber);
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }
//        };
////        int socketTimeout = 5000;//5 seconds
////        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
////        postRequest.setRetryPolicy(policy);
//        queue.add(postRequest);
    }

    public void requestOTPStrp2(final String otpText)
    {
        String url="http://apifv.foneverify.com/U2opia_Verify/v1.0/trial/update?appKey=" + appkey + "&customerId=" +  customerId +   "&verificationId="+verfication_Id+"&code="+otpText;

        final StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i( "OTP Receive response", response);
                        //Parse JSON Data
                        try
                        {
                            JSONObject imageObject = new JSONObject(response);
                            verfication_Id=imageObject.getString("verificationId")+"";
                            //If wrong OTP then verification message
                            //WRONG_OTP_PROVIDED
                            if(response.contains("WRONG_OTP_PROVIDED"))
                            {
                                Log.e("Wrong OTP ERROR Status","Resend OTP");
                                //  requestOTP();
                            }
                        }
                        catch(Exception e)
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
                        Log.e("Error",error+"");
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();

                return params;
            }

        };
        int socketTimeout = 5000;//5 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }
}
