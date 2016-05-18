package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 19/04/16.
 */
@DynamoDBDocument
public class GateCrashersClass {

    private String GateCrasherID;
    private String GCRequestStatus;
    private String GCAttendanceStatus;


    public String getGateCrasherID() { return GateCrasherID; }
    public void setGateCrasherID(String GateCrasherID) { this.GateCrasherID = GateCrasherID; }

    public String getGCRequestStatus() { return GCRequestStatus; }
    public void setGCRequestStatus(String GCRequestStatus) { this.GCRequestStatus = GCRequestStatus; }

    public String getGCAttendanceStatus() { return GCAttendanceStatus; }
    public void setGCAttendanceStatus(String GCAttendanceStatus) { this.GCAttendanceStatus = GCAttendanceStatus; }



}
