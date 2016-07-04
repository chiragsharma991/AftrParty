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
    private String partyid;
    private String partyname;
    private String partystatus;
    private String starttime;
    private String endtime;
    private Date convertedstarttime;
    private Date convertedendtime;


    public PartyConversion()
    {

    }

    public String getPartyid() { return partyid; }
    public void setPartyid(String partyid) { this.partyid = partyid; }

    public String getPartyname() { return partyname; }
    public void setPartyname(String partyname) { this.partyname = partyname; }

    public String getPartystatus() { return partystatus; }
    public void setPartystatus(String partystatus) { this.partystatus = partystatus; }

    public String getStarttime() { return starttime; }
    public void setStarttime(String starttime) { this.starttime = starttime; }

    public String getEndtime() { return endtime; }
    public void setEndtime(String endtime) { this.endtime = endtime; }



    public Date getConvertedendtime()
    {

        long milliSeconds= Long.parseLong(endtime);
        System.out.println(milliSeconds);
        convertedendtime = new Date(milliSeconds);
        System.out.println(convertedendtime);
        return convertedendtime;
    }

    public void setConvertedendtime(Date convertedEndTime) {
        convertedendtime = convertedEndTime;
    }

    public Date getConvertedstarttime() {


        long milliSeconds= Long.parseLong(starttime);
        System.out.println(milliSeconds);
        convertedstarttime = new Date(milliSeconds);
        System.out.println(convertedstarttime);
        return convertedstarttime;
    }

    public void setConvertedstarttime(Date convertedStartTime) {

        convertedstarttime = convertedStartTime;
    }
}

