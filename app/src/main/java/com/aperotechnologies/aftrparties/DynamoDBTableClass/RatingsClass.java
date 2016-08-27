package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 27/08/16.
 */

@DynamoDBDocument
public class RatingsClass
{
    private String partyendtime;
    private String partyid;
    private String isactive;


    public String getPartyendtime() {
        return partyendtime;
    }
    public void setPartyendtime(String partyendtime) {
        this.partyendtime = partyendtime;
    }

    public String getPartyid() {
        return partyid;
    }
    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public String getIsactive() {
        return isactive;
    }
    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }


}
