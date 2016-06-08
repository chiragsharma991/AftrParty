package com.aperotechnologies.aftrparties.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mpatil on 24/05/16.
 */
public class LIPictureData
{
    String _total = "0";
    List<String> values =null;

    public LIPictureData()
    {
        ArrayList<String> array = new ArrayList<>();
        array.add("N/A");
        values = array;
    }

    public String get_total()
    {
        return _total;
    }

    public void set_total(String _total)
    {
        this._total = _total;
    }

    public List<String> getvalues()
    {
        return values;
    }

    public void setvalues(ArrayList<String> values)
    {
        this.values = values;
    }


    @Override
    public int hashCode()
    {
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        return i1*values.size();

    }

    @Override
    public boolean equals(Object o)
    {
        LIPictureData liPictureData = (LIPictureData) o;
        if(this._total.equals(liPictureData._total) && compareLists(this.values,liPictureData.values))
        {
            return  true;
        }
        else
        {
            return false;
        }
        //return super.equals(o);
    }

    private  boolean compareLists(List<String> local,List<String> received)
    {
        if(local.size() == received.size())
        {
            String diff = "No";
            for(int i=0;i<local.size();i++)
            {
                if(local.get(i).trim().equals(received.get(i).trim()))
                {
                    diff = "No";
                }
                else
                {
                    diff = "Yes";
                    break;
                }
            }

            if(diff.equals("No"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
