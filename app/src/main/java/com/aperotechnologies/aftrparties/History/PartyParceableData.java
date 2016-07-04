package com.aperotechnologies.aftrparties.History;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 22/06/16.
 */
public class PartyParceableData implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String PartyId;
    private String PartyName;
    private String PartyStatus;
    private String PartyStartTime;
    private String PartyEndTime;



    public String getPartyId() { return PartyId; }
    public void setPartyId(String PartyId) { this.PartyId = PartyId; }

    public String getPartyName() { return PartyName; }
    public void setPartyName(String PartyName) { this.PartyName = PartyName; }

    public String getStartTime() { return PartyStartTime; }
    public void setStartTime(String PartyStartTime) { this.PartyStartTime = PartyStartTime; }

    public String getEndTime() { return PartyEndTime; }
    public void setEndTime(String PartyEndTime) { this.PartyEndTime = PartyEndTime; }

    public String getPartyStatus() { return PartyStatus; }
    public void setPartyStatus(String PartyStatus) { this.PartyStatus = PartyStatus; }
}



