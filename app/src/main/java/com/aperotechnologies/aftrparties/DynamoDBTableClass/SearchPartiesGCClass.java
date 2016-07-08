package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 19/04/16.
 */

public class SearchPartiesGCClass {

    private String gatecrasherid;
    private String gcrequeststatus;
    private String gcattendancestatus;


    public String getGatecrasherid() { return gatecrasherid; }
    public void setGatecrasherid(String gatecrasherid) { this.gatecrasherid = gatecrasherid; }

    public String getGcrequeststatus() { return gcrequeststatus; }
    public void setGcrequeststatus(String gcrequeststatus) { this.gcrequeststatus = gcrequeststatus; }

    public String getGcattendancestatus() { return gcattendancestatus; }
    public void setGcattendancestatus(String gcattendancestatus) { this.gcattendancestatus = gcattendancestatus; }



}
