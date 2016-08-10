package com.aperotechnologies.aftrparties.model;

import java.util.List;

/**
 * Created by mpatil on 12/05/16.
 */
//Meghana
    //Class to get JSON object from FB response
public class FbUserInformation
{
    String id = "N/A";
    String gender = "N/A";
    String birthday = "N/A";
    String email = "N/A";
    String name = "N/A";
    Object age_range = "N/A";
    String age = "N/A";

    FBCurrentLocationInformation location=null;
    FbProfilePictureData picture=null;

    FbHomelocationInformation hometown=null;

    public String getFbId()
    {
        return id;
    }
    public void setFbId(String fbId)
    {
        this.id = fbId;
    }

    public String getGender()
    {
        return gender;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getBirthday()
    {
        return birthday;
    }
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }


    public String getAge()
    {
        return age;
    }
    public void setAge(String age)
    {
        this.age = age;
    }


    public Object getAgerange()
    {
        return age_range;
    }
    public void setAgerange(Object age_range)
    {
        this.age_range = age_range;
    }

    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFbUserName()
    {
        return name;
    }
    public void setFbUserName(String name)
    {
        this.name = name;
    }


    public FBCurrentLocationInformation getFBLocationInformation()
    {
        return location;
    }
    public void setFBLocationInformation(FBCurrentLocationInformation location)
    {
        this.location = location;
    }


    public FbHomelocationInformation getFbHomelocationInformation()
    {
        return hometown;
    }
    public void setFbHomelocationInformation(FbHomelocationInformation hometown)
    {
        this.hometown = hometown;
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
