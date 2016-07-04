package com.aperotechnologies.aftrparties.Login;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by hasai on 13/06/16.
 */
public class EmailVerification {
    Context cont;
    String EmailId;

    public EmailVerification(Context cont,
            String EmailId){
        this.cont = cont;
        this.EmailId = EmailId;


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue((Activity)cont);
        String url ="http://api.verify-email.org/api.php?usr=harshadaasai&pwd=harshada26&check="+EmailId;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }


}
