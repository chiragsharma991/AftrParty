package com.aperotechnologies.aftrparties.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aperotechnologies.aftrparties.Chats.ChatActivity;

import java.util.ArrayList;

/**
 * Created by mpatil on 19/07/16.
 */
//Adapter for requestant fragm,ents
public class RequestantPagerAdapter extends FragmentStatePagerAdapter
{

    //integer to count number of tabs
    ArrayList<String> status;
    ArrayList<String> facebookId;ArrayList<String> liId;
    ArrayList<String> QbId;ArrayList<String> imageArray;
    Context cont;

    //Constructor to the class
    public RequestantPagerAdapter(Context cont, FragmentManager fm, ArrayList<String> facebookId, ArrayList<String> liId,
                                  ArrayList<String> QbId, ArrayList<String> imageArray, ArrayList<String> status)
    {
        super(fm);
        //Initializing tab count
        this.status = status;
        this.facebookId = facebookId;
        this.liId = liId;
        this.QbId = QbId;
        this.imageArray = imageArray;
        this.cont = cont;
    }

    public RequestantPagerAdapter(FragmentManager supportFragmentManager)
    {
        super(supportFragmentManager);
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position)
    {
        //Returning the current tabs
        RequestantFragment tab1 = new RequestantFragment(cont,position,status,facebookId,liId,QbId,imageArray);
        return tab1;
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount()
    {
        return status.size();
    }
}