package com.aperotechnologies.aftrparties.Login;

/**
 * Created by mpatil on 13/05/16.
 */
public class LIUserInformation
{
   String id=null;
   String pictureUrl=null;
   String emailAddress=null;
   String numConnections=null;

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
}
