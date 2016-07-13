package com.aperotechnologies.aftrparties.GateCrasher;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.DynamoDBTableClass.PartyTable;
import com.aperotechnologies.aftrparties.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by hasai on 06/05/16.
 */
public class GateCrasherActivity extends Activity
{

    ListView listGateCrasher;
    ProgressBar pBar;
    TextView txtNoParties;
    GateCrasherAdapter adapter;
    Configuration_Parameter m_config;
    Context cont;
    RequestQueue queue;



        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gatecrasher);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);

            cont = this;
            m_config = Configuration_Parameter.getInstance();
            Crouton.cancelAllCroutons();
            m_config.foregroundCont = this;
            listGateCrasher = (ListView) findViewById(R.id.listgatecrasher);
            pBar = (ProgressBar) findViewById(R.id.progressBar);
            pBar.setVisibility(View.VISIBLE);
            txtNoParties = (TextView) findViewById(R.id.noParties);
            new GetData().execute();



        }


    public class GetData extends AsyncTask<Void, Void, PartyTable>
    {

        String url = "https://j4zoihu1pl.execute-api.us-east-1.amazonaws.com/Development/GetPartiesAroundMe";

        GCParceableData parcedata = (GCParceableData)getIntent().getSerializableExtra(ConstsCore.SER_KEY);



        @Override
        protected PartyTable doInBackground(final Void... params) {

            JSONObject obj = null;

            Log.e("parcedata preference "," "+parcedata.getgenderpreference());

            try {
                obj = new JSONObject();
                obj.put("latitude", parcedata.getlatitude());
                obj.put("longitude", parcedata.getlongitude());
                obj.put("distance",  parcedata.getdistance());
                obj.put("atdatetime",  Long.parseLong(parcedata.getatdatetime()));
                obj.put("byob", parcedata.getbyob());
                obj.put("preference", parcedata.getgenderpreference());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("obj"," "+obj.toString());

            queue = Volley.newRequestQueue(GateCrasherActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                if(response.getString("noofparties").equals("0")){
                                    pBar.setVisibility(View.GONE);
                                    txtNoParties.setVisibility(View.VISIBLE);
                                }else{

                                    txtNoParties.setVisibility(View.GONE);
                                    Log.e("response"," "+response.get("data"));
                                    JSONArray result = (JSONArray) response.get("data");
                                    adapter = new GateCrasherAdapter(GateCrasherActivity.this, result) {
                                    };
                                    listGateCrasher.setAdapter(adapter);
                                    pBar.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pBar.setVisibility(View.GONE);
                            txtNoParties.setVisibility(View.GONE);
                            Toast.makeText(cont, "There was an error connecting to network, Please try after some time",Toast.LENGTH_SHORT).show();
                        }
                    });
            //add request to queue
            int socketTimeout = 5000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            queue.add(jsonObjectRequest);


            return null;
        }

            @Override
        protected void onPostExecute(PartyTable partyTable) {


        }
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


//    JsonArrayRequest jr = new JsonArrayRequest(url, obj.toString(), new Response.Listener<NetworkResponse>() {
//
//                        @Override
//                        public void onResponse(NetworkResponse networkResponse) {
//
//                            try {
//                                String json = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
//                                Log.e("json=",""+json);
//
////                if(callBackStringDataListener!=null)
////                {
////                    callBackStringDataListener.callbackStringData(json);
////                }
//                                Response<PartyTable> party =  Response.success(gson.fromJson(json, PartyTable.class),
//                                        HttpHeaderParser.parseCacheHeaders(networkResponse));
//
//                                String parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
//                                Log.e("parsed"," "+" ----- "+ parsed.toString());
//
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            } catch (JsonSyntaxException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    }, new Response.ErrorListener(){
//
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            volleyError.printStackTrace();
//                            Log.e("volleyError"," ");
//                        }
//                    });
//
//            int socketTimeout = 5000;//30 seconds - change to what you want
//            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            jr.setRetryPolicy(policy);
//            queue.add(jr);



//    public class JsonArrayRequest extends Request<NetworkResponse> {
//
//
//        Response.Listener mListener;
//        Response.ErrorListener merrorListener;
//
//        Gson gson;
//
//        public JsonArrayRequest(String url, String jsonRequest,
//                                Response.Listener listener, Response.ErrorListener errorListener) {
//            super(Method.POST, url, errorListener);
//            mListener = listener;
//            merrorListener = errorListener;
//            gson = new Gson();
//        }
//
//
//        @Override
//        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse networkResponse) {
//            return Response.success(networkResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
//
//        }
//
//        @Override
//        protected void deliverResponse(NetworkResponse networkResponse) {
//            mListener.onResponse(networkResponse);
//        }
//
//        @Override
//        public void deliverError(VolleyError error) {
//
//            merrorListener.onErrorResponse(error);
//        }
//
//
//    }
}



