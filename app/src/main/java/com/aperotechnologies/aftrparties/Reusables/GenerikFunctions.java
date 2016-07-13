package com.aperotechnologies.aftrparties.Reusables;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mpatil on 11/05/16.
 */
public class GenerikFunctions
{

    static ProgressDialog progressDialog = null;
    //Check Network availability
    public static boolean chkStatus(Context cont)
    {
        final ConnectivityManager connMgr = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting();
    }

    public static void  showToast(Context cont,String message)
    {
        Toast.makeText(cont,message,Toast.LENGTH_LONG).show();
    }


    public static void showDialog(ProgressDialog pDialog, String message){

        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    public static void hideDialog(ProgressDialog pDialog){
        if(pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.cancel();
            }
        }
    }



    public static void sDialog(Context cont, String message){
        Log.e("progressDialog in sDialog "+progressDialog," ");
        if(progressDialog == null){
            progressDialog = new ProgressDialog(cont);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }



    }

    public static void hDialog(){
        Log.e("progressDialog in hDialog "+progressDialog," ");
        if(progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
                progressDialog = null;
                Log.e("progressDialog  "+progressDialog," ");
            }
        }
    }
}
