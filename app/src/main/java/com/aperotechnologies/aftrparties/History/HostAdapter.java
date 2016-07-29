package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;

/**
 * Created by ifattehkhan on 20/07/16.
 */
public class HostAdapter extends FragmentStatePagerAdapter{
    Context cont;
    String fbid;
    String liid;
    String qbid;
    String img_url;




    public HostAdapter(Context cont, FragmentManager fm, String fbid, String liid, String qbid, String img_url) {
        super(fm);
        this.cont=cont;
        this.fbid=fbid;
        this.liid=liid;
        this.qbid=qbid;
        this.img_url=img_url;

        Log.e("in Host Adapter","11");


    }

    @Override
    public Fragment getItem(int position) {
        HostFragment tab1 = new HostFragment(cont,position,fbid,liid,qbid,img_url);


        Log.e("in Host Adapter","22");
        return tab1;
    }


    @Override
    public int getCount() {
        return 1;
    }
}
