package com.aperotechnologies.aftrparties.Login;

import org.json.JSONObject;

/**
 * Created by mpatil on 12/05/16.
 */
public class FbProfilePictureData
{

    FbPictureInformation data=null;

    public FbPictureInformation getFbPictureInformation()
    {
        return data;
    }

    public void setFbPictureInformation(FbPictureInformation data)
    {
        this.data = data;
    }
}
