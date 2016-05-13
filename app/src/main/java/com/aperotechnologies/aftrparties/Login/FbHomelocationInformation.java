package com.aperotechnologies.aftrparties.Login;

/**
 * Created by mpatil on 13/05/16.
 */
public class FbHomelocationInformation
{
    String id=null;
    String name=null;

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
}
