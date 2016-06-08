package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

/**
 * Created by hasai on 03/05/16.
 */
@DynamoDBTable(tableName = "AP_Parties")
public class PartyTable {

    private String PartyID;
    private String PartyName;
    private String StartTime;
    private String EndTime;
    private String Date;
    private String HostFBID;
    private String HostQBID;
    private String HostName;
    private String PartyType;//Predefine desc
    private String PartyDescription;
    private String BYOB;
    private String PartyAddress;
    private List<String> PartyLatLong;
    private String PartyImage;
    private String MaskStatus;
    private String DialogID;
    private List<GateCrashersClass> GateCrashers;


    @DynamoDBHashKey(attributeName = "PartyID")
    public String getPartyID() {
        return PartyID;
    }

    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    @DynamoDBAttribute(attributeName = "PartyName")
    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    @DynamoDBAttribute(attributeName = "StartTime")
    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    @DynamoDBAttribute(attributeName = "EndTime")
    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    @DynamoDBAttribute(attributeName = "HostFBID")
    public String getHostFBID() {
        return HostFBID;
    }

    public void setHostFBID(String HostFBID) {
        this.HostFBID = HostFBID;
    }

    @DynamoDBAttribute(attributeName = "HostQBID")
    public String getHostQBID() {
        return HostQBID;
    }

    public void setHostQBID(String HostQBID) {
        this.HostQBID = HostQBID;
    }

    @DynamoDBAttribute(attributeName = "HostName")
    public String getHostName() {
        return HostName;
    }

    public void setHostName(String HostName) {
        this.HostName = HostName;
    }

    @DynamoDBAttribute(attributeName = "PartyType")
    public String getPartyType() {
        return PartyType;
    }

    public void setPartyType(String PartyType) {
        this.PartyType = PartyType;
    }

    @DynamoDBAttribute(attributeName = "PartyDescription")
    public String getPartyDescription() {
        return PartyDescription;
    }

    public void setPartyDescription(String PartyDescription) {
        this.PartyDescription = PartyDescription;
    }

    @DynamoDBAttribute(attributeName = "BYOB")
    public String getBYOB() {
        return BYOB;
    }

    public void setBYOB(String BYOB) {
        this.BYOB = BYOB;
    }

    @DynamoDBAttribute(attributeName = "PartyAddress")
    public String getPartyAddress() {
        return PartyAddress;
    }

    public void setPartyAddress(String PartyAddress) {
        this.PartyAddress = PartyAddress;
    }

    @DynamoDBAttribute(attributeName = "PartyLatLong")
    public List<String> getPartyLatLong() {
        return PartyLatLong;
    }

    public void setPartyLatLong(List<String> PartyLatLong) {
        this.PartyLatLong = PartyLatLong;
    }

    @DynamoDBAttribute(attributeName = "PartyImage")
    public String getPartyImage() {
        return PartyImage;
    }

    public void setPartyImage(String PartyImage) {
        this.PartyImage = PartyImage;
    }

    @DynamoDBAttribute(attributeName = "MaskStatus")
    public String getMaskStatus() {
        return MaskStatus;
    }

    public void setMaskStatus(String MaskStatus) {
        this.MaskStatus = MaskStatus;
    }

    @DynamoDBAttribute(attributeName = "DialogID")
    public String getDialogID() {
        return DialogID;
    }

    public void setDialogID(String DialogID) {
        this.DialogID = DialogID;
    }


    public List<GateCrashersClass> getGateCrashers() {
        return GateCrashers;
    }

    public void setGateCrashers(List<GateCrashersClass> GateCrashers) {
        this.GateCrashers = GateCrashers;
    }

}
