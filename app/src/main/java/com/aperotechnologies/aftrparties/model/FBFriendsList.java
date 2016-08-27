package com.aperotechnologies.aftrparties.model;

/**
 * Created by hasai on 24/08/16.
 */
public class FBFriendsList {


    private String id = "N/A";
    private String name = "N/A";
    private String gender = "N/A";

    FbProfilePictureData picture = null;



    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getGender()
    {
        return gender;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }


    public FbProfilePictureData getFbProfilePictureData()
    {
        return picture;
    }
    public void setFbProfilePictureData(FbProfilePictureData FbProfilePictureData)
    {
        this.picture = picture;
    }



}
