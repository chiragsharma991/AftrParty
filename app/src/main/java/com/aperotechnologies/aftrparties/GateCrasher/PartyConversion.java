package com.aperotechnologies.aftrparties.GateCrasher;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hasai on 07/06/16.
 */
public class PartyConversion
{
    private String PartyId;
    private String PartyName;
    private String PartyStatus;
    private String PartyStartTime;
    private String PartyEndTime;
    private Date ConvertedStartTime;
    private Date ConvertedEndTime;

    PartyConversion()
    {

    }

    public String getPartyId() { return PartyId; }
    public void setPartyId(String PartyId) { this.PartyId = PartyId; }

    public String getPartyName() { return PartyName; }
    public void setPartyName(String PartyName) { this.PartyName = PartyName; }

    public String getPartyStatus() { return PartyStatus; }
    public void setPartyStatus(String PartyStatus) { this.PartyStatus = PartyStatus; }

    public String getStartTime() { return PartyStartTime; }
    public void setStartTime(String PartyStartTime) { this.PartyStartTime = PartyStartTime; }

    public String getEndTime() { return PartyEndTime; }
    public void setEndTime(String PartyEndTime) { this.PartyEndTime = PartyEndTime; }

    public Date getConvertedEndTime()
    {

        long milliSeconds= Long.parseLong(PartyEndTime);
        System.out.println(milliSeconds);
        ConvertedEndTime = new Date(milliSeconds);
        System.out.println(ConvertedEndTime);
        return ConvertedEndTime;
    }

    public void setConvertedEndTime(Date convertedEndTime) {
        ConvertedEndTime = convertedEndTime;
    }

    public Date getConvertedStartTime() {


        long milliSeconds= Long.parseLong(PartyStartTime);
        System.out.println(milliSeconds);
        ConvertedStartTime = new Date(milliSeconds);
        System.out.println(ConvertedStartTime);
        return ConvertedStartTime;
    }

    public void setConvertedStartTime(Date convertedStartTime) {

        ConvertedStartTime = convertedStartTime;
    }
}

