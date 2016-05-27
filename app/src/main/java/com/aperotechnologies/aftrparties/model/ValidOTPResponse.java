package com.aperotechnologies.aftrparties.model;

/**
 * Created by mpatil on 26/05/16.
 */
public class ValidOTPResponse
{
    String verificationId = "N/A";
    String mobileNumber = "N/A";
    String responseCode = "N/A";
    int timeout = 0;
    String smsCLI = "N/A";

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

    public String getSmsCLI()
    {
        return smsCLI;
    }

    public void setSmsCLI(String smsCLI)
    {
        this.smsCLI = smsCLI;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public String getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(String responseCode)
    {
        this.responseCode = responseCode;
    }
}
