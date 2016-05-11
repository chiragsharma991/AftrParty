package com.aperotechnologies.aftrparties.Reusables;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by mpatil on 11/05/16.
 */
public class GenerikFunctions
{
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
}
