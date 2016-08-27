package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 19/04/16.
 */
@DynamoDBDocument
public class GateCrashersClass {

    private String gatecrasherid;
    private String gcrequeststatus;
    private String gcattendancestatus;
    private String gclkid;
    private String gcqbid;
    private String gcfbprofilepic;
    private String ratingsbyhost;
    private String ratingsbygc;


    public String getGatecrasherid() { return gatecrasherid; }
    public void setGatecrasherid(String gatecrasherid) { this.gatecrasherid = gatecrasherid; }

    public String getGcrequeststatus() { return gcrequeststatus; }
    public void setGcrequeststatus(String gcrequeststatus) { this.gcrequeststatus = gcrequeststatus; }

    public String getGcattendancestatus() { return gcattendancestatus; }
    public void setGcattendancestatus(String gcattendancestatus) { this.gcattendancestatus = gcattendancestatus; }

    public String getgclkid() { return gclkid; }
    public void setgclkid(String gclkid) { this.gclkid = gclkid; }

    public String getgcqbid() { return gcqbid; }
    public void setgcqbid(String gcqbid) { this.gcqbid = gcqbid; }

    public String getgcfbprofilepic() { return gcfbprofilepic; }
    public void setgcfbprofilepic(String gcfbprofilepic) { this.gcfbprofilepic = gcfbprofilepic; }

    public String getRatingsbyhost() { return ratingsbyhost; }
    public void setRatingsbyhost(String ratingsbyhost) { this.ratingsbyhost = ratingsbyhost; }

    public String getRatingsbygc() { return ratingsbygc; }
    public void setRatingsbygc(String ratingsbygc) { this.ratingsbygc = ratingsbygc; }



}
