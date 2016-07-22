package com.aperotechnologies.aftrparties.History;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.ActivePartyClass;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.UserTable;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hasai on 12/07/16.
 */
public class HostCancelPartyAPI {

    Context cont;
    String partyid;
    Configuration_Parameter m_config;
    TextView btnReqCancel;

    public HostCancelPartyAPI(final Context cont,
                              String partyid, final TextView btnReqCancel) {
        this.cont = cont;
        this.partyid = partyid;
        m_config = Configuration_Parameter.getInstance();
        this.btnReqCancel = btnReqCancel;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity) cont);
        String url = "https://j4zoihu1pl.execute-api.us-east-1.amazonaws.com/Development/ChangePartyStatus";


        JSONObject obj = new JSONObject();
        try {
            obj.put("partyid", partyid);
            obj.put("partystatus", "Cancelled");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("-object--"," "+obj);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            if(response.getInt("confirm_status") == 1)
                            {
                                GenerikFunctions.showToast(cont, "Party request has been cancelled");
                                GenerikFunctions.hDialog();
                                Intent i = new Intent(cont, HistoryActivity.class);
                                cont.startActivity(i);
                            }
                            else
                            {
                                GenerikFunctions.showToast(cont, response.getString("message"));
                                GenerikFunctions.hDialog();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            GenerikFunctions.showToast(cont, "Unable to Cancel Party, Please try again after some time");
                            GenerikFunctions.hDialog();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GenerikFunctions.showToast(cont, "Unable to Cancel Party, Please try again after some time");
                        GenerikFunctions.hDialog();
                    }
                }
        );

        // add it to the RequestQueue
        int socketTimeout = 10000;//60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
        queue.add(getRequest);


    }
}
