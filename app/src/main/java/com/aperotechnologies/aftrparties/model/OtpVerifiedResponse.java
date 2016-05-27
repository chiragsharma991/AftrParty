package com.aperotechnologies.aftrparties.model;

/**
 * Created by mpatil on 26/05/16.
 */
public class OtpVerifiedResponse
{
    String verificationId = "N/A";
    String mobileNumber = "N/A";
    int responseCode = 0;
    String verificationStatus = "N/A";


    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    public String getVerificationId()
    {
        return  verificationId;
    }

    public void setVerificationId(String verificationId)
    {
        this.verificationId = verificationId;
    }

    public String getVerificationStatus()
    {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus)
    {
        this.verificationStatus = verificationStatus;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }

}
