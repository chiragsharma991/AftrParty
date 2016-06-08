package com.aperotechnologies.aftrparties.model;

import java.util.Random;

/**
 * Created by mpatil on 12/05/16.
 */
public class FbPictureInformation
{

    String url= "N/A";
    boolean is_silhouette=false;

    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public boolean getis_silhouette()
    {
        return is_silhouette;
    }

    public void setIs_silhouette(boolean is_silhouette)
    {
        this.is_silhouette = is_silhouette;
    }

    @Override
    public int hashCode()
    {
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        return i1*url.length();
    }

    @Override
    public boolean equals(Object o)
    {
        FbPictureInformation fbPictureInformation = (FbPictureInformation) o;
        if(fbPictureInformation.url.equals(this.url)  && fbPictureInformation.is_silhouette == this.is_silhouette)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
