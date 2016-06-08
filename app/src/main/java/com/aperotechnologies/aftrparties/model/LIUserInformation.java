package com.aperotechnologies.aftrparties.model;

/**
 * Created by mpatil on 13/05/16.
 */
public class LIUserInformation
{
    String id= "N/A";
    String pictureUrl= "N/A";
    String emailAddress="N/A";
    String numConnections="0";
    String headline="N/A";
    String firstName="N/A";
    String lastName="N/A";

    LIPictureData pictureUrls;

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
