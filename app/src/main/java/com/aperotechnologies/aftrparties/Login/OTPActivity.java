package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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



    //For OTP
    String customerId = "kr0lv39n";
    String appkey = "b22ab60eda70072cd34c507c4870ab3c";
    String appid = "c31396423a00078697c490cb13a6d99b";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btn_send=(Button)findViewById(R.id.btn_generate);
        btn_verify=(Button)findViewById(R.id.btn_verify);
        edt_otp=(EditText)findViewById(R.id.edi_otp);

        queue = Volley.newRequestQueue(cont);

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestOTP();
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String otp=edt_otp.getText().toString().trim();
                requestOTPStrp2(otp);
            }
        });
    }

    public void requestOTP()
    {
        String url="http://apifv.foneverify.com/U2opia_Verify/v1.0/trial/sms";
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
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
                params.put("appKey",appkey);
                params.put("customerId",customerId);
                params.put("isoCountryCode","IN");
                //params.put("msisdn","9987936766                                                                                                                                                                 ");
                params.put("msisdn",mobileNumber);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        int socketTimeout = 5000;//5 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void requestOTPStrp2(final String otpText)
    {
        String url="http://apifv.foneverify.com/U2opia_Verify/v1.0/trial/update?appKey=" +
                appkey +
                "&customerId=" +
                customerId +
                "&verificationId="+verfication_Id+"&code="+otpText;

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
