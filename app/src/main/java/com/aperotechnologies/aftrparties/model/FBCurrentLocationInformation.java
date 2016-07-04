package com.aperotechnologies.aftrparties.model;

import java.util.Random;

/**
 * Created by mpatil on 12/05/16.
 */
//Meghana
//FB USer Location Information class
public class FBCurrentLocationInformation
{
    String id= "N/A";
    String name= "N/A";

    public FBCurrentLocationInformation()
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
        FBCurrentLocationInformation fbCurrentLocationInformation = (FBCurrentLocationInformation) o;
        if(this.id.equals(fbCurrentLocationInformation.id) && this.name.equals(fbCurrentLocationInformation.name))
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
