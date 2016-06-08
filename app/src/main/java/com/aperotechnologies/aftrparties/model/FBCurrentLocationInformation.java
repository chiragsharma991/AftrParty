package com.aperotechnologies.aftrparties.model;

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
}
