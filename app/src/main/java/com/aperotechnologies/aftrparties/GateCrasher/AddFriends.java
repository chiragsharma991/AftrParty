package com.aperotechnologies.aftrparties.GateCrasher;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mpatil on 08/06/16.
 */
public  class AddFriends
{

    ArrayList<String> str_id, str_name, str_url,invitable,taggable;
    RequestQueue queue;

    public AddFriends()
    {
        Log.e("Inside Add Friends","Yes");
    }

  //  Bundle args = new Bundle();
  //  args.putInt("limit", 547);

    public  void requestInvitableFriends(Context cont,AccessToken token)
    {

       new GraphRequest(
               token,
                //AccessToken.getCurrentAccessToken(),
                "/me/friends", null, HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        Log.e("friends response",response.toString());
                       // Intent intent = new Intent(StartActivity.this, Success.class);
                        try
                        {
                            JSONArray rawName = response.getJSONObject().getJSONArray("data");
                            Log.e("rawName friends", rawName + "");
                           // intent.putExtra("jsondata", rawName.toString());

                            //startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();



        queue = Volley.newRequestQueue(cont);
        invitable = new ArrayList<String>();
        GraphRequest request2 = new GraphRequest(token,
                "/me/taggable_friends", null, HttpMethod.GET, new GraphRequest.Callback()
        {
            @Override
            public void onCompleted(GraphResponse graphResponse)
            {
                Log.e("Taggable Friends", graphResponse + "");
                try
                {
                    JSONObject graphObject = graphResponse.getJSONObject();
                    JSONArray dataArray = graphObject.getJSONArray("data");
                    Log.e("Data Array length", dataArray.length() + "");
                    JSONObject paging = graphObject.getJSONObject("paging");
                    Log.e("Paging", paging + "");
                    String next = "";
                    try
                    {
                        next = paging.getString("next");
                        Log.e("next", next + "");
                    }
                    catch (Exception e)
                    {
                        next = "";
                    }
                    finally
                    {
                        for (int i = 0; i < dataArray.length(); i++)
                        {
                            try
                            {
                                // here all that you want
                                JSONObject object = dataArray.getJSONObject(i);

                                // get facebook user id,name and picture
                                String str_id = object.getString("id");

                                String str_name = object.getString("name");

                                JSONObject picture_obj = object.getJSONObject("picture");

                                JSONObject data_obj = picture_obj.getJSONObject("data");

                                String str_url = data_obj.getString("url");
                                invitable.add(str_id + "    " + str_name + "     " + "       " + str_url );
                                Log.e(i + " Info", str_name + "        " +  str_id + "     " + "       " + str_url + "     " + picture_obj + "    " + data_obj);







                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    Log.e("Next before check",next +" aa");
                    if(next!=null || !(next.equals(null) || next.length()!=0))
                    {
                        Log.e("Inside if","yess");
                        getNextInvitableFriends(next);
                    }
                    else
                    {
                        Log.e("No Next ","Break");
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Exception=" + e);
                    e.printStackTrace();
                }
            }
        });
        request2.executeAsync();
    }

    private void getNextInvitableFriends(String next)
    {
        Log.e("Inside ","getNext     " + next);

        final StringRequest postRequest = new StringRequest(Request.Method.GET, next,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        String next="";
                        Log.e("Taggable Friends", response + "");
                        try
                        {
                            JSONObject graphObject = new JSONObject(response);
                            JSONArray dataArray = graphObject.getJSONArray("data");
                            Log.e("Data Array length", dataArray.length() + "");
                            JSONObject paging = graphObject.getJSONObject("paging");
                            Log.e("Paging", paging + "");
                            try
                            {
                                next = paging.getString("next");
                                // Log.e("next", next + "");
                            }
                            catch(Exception e)
                            {
                                for (int i = 0; i < dataArray.length(); i++)
                                {
                                    try
                                    {
                                        // here all that you want
                                        JSONObject object = dataArray.getJSONObject(i);

                                        // get facebook user id,name and picture
                                        String str_id = object.getString("id");

                                        String str_name = object.getString("name");


                                        JSONObject picture_obj = object.getJSONObject("picture");

                                        JSONObject data_obj = picture_obj.getJSONObject("data");

                                        String str_url = data_obj.getString("url");
                                        invitable.add( str_name+ "    " + str_id + "    " + "       " + str_url );
                                        //  Log.e(i + " Info", str_id + "    " + str_name + "     " + "       " + str_url + "     " + picture_obj + "    " + data_obj);

                                    }
                                    catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                        for(int p=0;p<invitable.size();p++)
                                        {
                                            Log.e("invitable  " +p,invitable.get(p));
                                        }
                                    }
                                }

                                Log.e("Invitable friends final count",invitable.size()+"");
                                for(int p=0;p<invitable.size();p++)
                                {
                                    Log.e("invitable  " +p,invitable.get(p));
                                }

                            }


                            for (int i = 0; i < dataArray.length(); i++)
                            {
                                try
                                {
                                    // here all that you want
                                    JSONObject object = dataArray.getJSONObject(i);

                                    // get facebook user id,name and picture
                                    String str_id = object.getString("id");

                                    String str_name = object.getString("name");


                                    JSONObject picture_obj = object.getJSONObject("picture");

                                    JSONObject data_obj = picture_obj.getJSONObject("data");

                                    String str_url = data_obj.getString("url");
                                    invitable.add( str_name+ "    " + str_id + "    " + "       " + str_url );
                                    //  Log.e(i + " Info", str_id + "    " + str_name + "     " + "       " + str_url + "     " + picture_obj + "    " + data_obj);

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    for(int p=0;p<invitable.size();p++)
                                    {
                                        Log.e("invitable  " +p,invitable.get(p));
                                    }
                                }
                            }

                            if(next!=null || !(next.equals(null) || next.length()!=0))
                            {
                                getNextInvitableFriends(next);
                            }
                            else
                            {
                                Log.e("Invitable friends final count",invitable.size()+"");
                                for(int p=0;p<invitable.size();p++)
                                {
                                    Log.e("invitable  " +p,invitable.get(p));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            System.out.println("Exception=" + e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                });  queue.add(postRequest);
    }



//    Bundle args2 = new Bundle();
//    args.putInt("limit", 547);
//    new GraphRequest(
//        AccessToken.getCurrentAccessToken(),
//    "/1058409207555652/taggable_friends",
//    args2,
//    HttpMethod.GET,
//        new GraphRequest.Callback()
//    {
//        public void onCompleted(GraphResponse response)
//        {
//            JSONObject res = response.getJSONObject();
//            Log.e("Taggable user friends list", response + "");
//            try
//            {
//                JSONObject graphObject = response.getJSONObject();
//                JSONArray dataArray = graphObject.getJSONArray("data");
//                Log.e("Taggable Array length", dataArray.length() + "");
//                JSONObject paging = graphObject.getJSONObject("paging");
//                Log.e("Taggable Paging", paging + "");
//                String next = paging.getString("next");
//                Log.e("Taggable next", next + "");
//                for (int i = 0; i < dataArray.length(); i++)
//                {
//                    try
//                    {
//                        // here all that you want
//                        JSONObject object = dataArray.getJSONObject(i);
//
//                        // get facebook user id,name and picture
//                        String str_id = object.getString("id");
//
//                        String str_name = object.getString("name");
//
//
//                        JSONObject picture_obj = object.getJSONObject("picture");
//
//                        JSONObject data_obj = picture_obj.getJSONObject("data");
//
//                        String str_url = data_obj.getString("url");
//                        Log.e(i + "Taggable Info", str_id + "    " + str_name + "     " + "       " + str_url + "     " + picture_obj + "    " + data_obj);
//
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            catch (Exception e)
//            {
//                System.out.println("Exception=" + e);
//                e.printStackTrace();
//            }
//        }
//    }
//    ).executeAsync();
}
