package com.aperotechnologies.aftrparties.model;

import java.util.Random;

/**
 * Created by mpatil on 13/05/16.
 */
//Class to parse FB Home Location information
public class FbHomelocationInformation
{
    String id = "N/A";
    String name = "N/A";

    public FbHomelocationInformation()
    {
    }

    public String getLocationId()
    {
        return id;
    }
    public void setLocationId(String id)
    {
        this.id = id;
    }

    public String getLocationName()
    {
        return name;
    }
    public void setLocationName(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        return i1*name.length();
    }

    @Override
    public boolean equals(Object o)
    {
        FbHomelocationInformation fbHomelocationInformation = (FbHomelocationInformation) o;
        if(this.id.equals(fbHomelocationInformation.id) && this.name.equals(fbHomelocationInformation.name))
        {
            return  true;
        }
        else
        {
            return false;
        }
        //return super.equals(o);
    }
}
