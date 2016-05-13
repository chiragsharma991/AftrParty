package com.aperotechnologies.aftrparties.Login;

/**
 * Created by mpatil on 12/05/16.
 */
//Meghana
    //Class to get JSON object
public class FbUserInformation
{
    /* {"picture":
    {"data":
    {"url":"https:\/\/fbcdn-profile-a.akamaihd.net\/hprofile-ak-xft1\/v\/t1.0-1\/p200x200\/13133150_145693209167310_7419746219172680731_n.jpg?oh=1c1b1d986aa4644f9a0c175a797b753e&oe=57AD5435&__gda__=1474315388_aee3f970f392960a884640e086c46e3c","is_silhouette":false}},
    "id":"155325838204047","gender":"female",
    "birthday":"02\/26\/1991","email":"hasai@aperotechnologies.com",
    "link":"https:\/\/www.facebook.com\/app_scoped_user_id\/155325838204047\/",
    "name":"Harsha Apero"}
    */

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
