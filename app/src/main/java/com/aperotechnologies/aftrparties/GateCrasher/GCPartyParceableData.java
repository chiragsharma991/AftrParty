package com.aperotechnologies.aftrparties.GateCrasher;

/**
 * Created by hasai on 19/08/16.
 */

import android.util.EventLogTags;

import java.io.Serializable;

/**
 * Created by hasai on 22/06/16.
 */
public class GCPartyParceableData implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String PartyId;
    private String PartyName;
    private String PartyStatus;
    private String PartyStartTime;
    private String PartyEndTime;
    private String HostName;
    private String BYOB;
    private String Address;
    private String Description;



    public String getPartyId() { return PartyId; }
    public void setPartyId(String PartyId) { this.PartyId = PartyId; }

    public String getPartyName() { return PartyName; }
    public void setPartyName(String PartyName) { this.PartyName = PartyName; }

    public String getHostName() { return HostName; }
    public void setHostName(String HostName) { this.HostName = HostName; }

    public String getStartTime() { return PartyStartTime; }
    public void setStartTime(String PartyStartTime) { this.PartyStartTime = PartyStartTime; }

    public String getEndTime() { return PartyEndTime; }
    public void setEndTime(String PartyEndTime) { this.PartyEndTime = PartyEndTime; }

    public String getBYOB() { return BYOB; }
    public void setBYOB(String BYOB) { this.BYOB = BYOB; }

    public String getPartyaddress() { return Address; }
    public void setPartyaddress(String Address) { this.Address = Address; }

    public String getDescription() { return Description; }
    public void setDescription(String Description) { this.Description = Description; }
}




