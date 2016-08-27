package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;


/**
 * Created by hasai on 19/04/16.
 */
@DynamoDBDocument
public class PartiesClass {

    private String partyid;
    private String partyname;
    private String partystatus;
    private String starttime;
    private String endtime;
    private String ratingsbyhost;
    private String ratingsbygc;


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

    public String getRatingsbyhost() { return ratingsbyhost; }
    public void setRatingsbyhost(String ratingsbyhost) { this.ratingsbyhost = ratingsbyhost; }

    public String getRatingsbygc() { return ratingsbygc; }
    public void setRatingsbygc(String ratingsbygc) { this.ratingsbygc = ratingsbygc; }

}


/*"partyIdStatus": [
    {
      "partyId": "1",
      "partyStatus": "Approved"
    }
  ],*/