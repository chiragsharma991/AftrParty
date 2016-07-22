package com.aperotechnologies.aftrparties.model;

import java.util.Random;

/**
 * Created by mpatil on 12/05/16.
 */
//Class to parse FB profilepic data
public class FbProfilePictureData
{

    FbPictureInformation data = null;

    public  FbProfilePictureData()
    {

    }
    public FbPictureInformation getFbPictureInformation()
    {
        return data;
    }

    public void setFbPictureInformation(FbPictureInformation data)
    {
        this.data = data;
    }

//    @Override
//    public int hashCode()
//    {
//        Random r = new Random();
//        int i1 = r.nextInt(80 - 65) + 65;
//        return i1;
//    }
//
//    @Override
//    public boolean equals(Object o)
//    {
//        FbProfilePictureData fbProfilePictureData = (FbProfilePictureData)o;
//        if(fbProfilePictureData.equals(this.data))
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }




//        FbPictureInformation fbPictureInformation = (FbPictureInformation) o;
//        if(fbPictureInformation.url.equals(this.url)  && fbPictureInformation.is_silhouette == this.is_silhouette)
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
    //}

}
