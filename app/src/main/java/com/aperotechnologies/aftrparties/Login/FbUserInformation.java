package com.aperotechnologies.aftrparties.Login;

/**
 * Created by mpatil on 12/05/16.
 */
//Meghana
    //Class to get JSON object
public class FbUserInformation
{
    String id=null;
    String gender=null;
    String birthday=null;
    String email=null;
    String name=null;

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
