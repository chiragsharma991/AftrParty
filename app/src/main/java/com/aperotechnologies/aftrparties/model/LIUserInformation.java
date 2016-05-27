package com.aperotechnologies.aftrparties.model;

/**
 * Created by mpatil on 13/05/16.
 */
public class LIUserInformation
{
    String id=null;
    String pictureUrl=null;
    String emailAddress=null;
    String numConnections="0";
    String headline=null;
    String firstName=null;
    String lastName=null;

    LIPictureData pictureUrls=null;

    public LIUserInformation()
    {

    }

   public String getId()
   {
       return id;
   }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getNumConnections()
    {
        return numConnections;
    }

    public void setNumConnections(String numConnections)
    {
        this.numConnections = numConnections;
    }

    public String getPictureUrl()
    {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl)
    {
        this.pictureUrl = pictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }



    public LIPictureData getLIPictureData()
    {
        return pictureUrls;
    }
    public void setLIPictureData(LIPictureData pictureUrls)
    {
        this.pictureUrls = pictureUrls;
    }
}
