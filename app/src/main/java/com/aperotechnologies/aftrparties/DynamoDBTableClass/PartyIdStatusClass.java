package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;


/**
 * Created by hasai on 19/04/16.
 */
@DynamoDBDocument
public class PartyIdStatusClass {


    private String PartyId;
    private String PartyStatus;
    private String PartyStartTime;
    private String PartyEndTime;


    public String getPartyId() { return PartyId; }
    public void setPartyId(String PartyId) { this.PartyId = PartyId; }

    public String getPartyStatus() { return PartyStatus; }
    public void setPartyStatus(String PartyStatus) { this.PartyStatus = PartyStatus; }

    public String getStartTime() { return PartyStartTime; }
    public void setStartTime(String PartyStartTime) { this.PartyStartTime = PartyStartTime; }

    public String getEndTime() { return PartyEndTime; }
    public void setEndTime(String PartyEndTime) { this.PartyEndTime = PartyEndTime; }



}


/*"partyIdStatus": [
    {
      "partyId": "1",
      "partyStatus": "Approved"
    }
  ],*/