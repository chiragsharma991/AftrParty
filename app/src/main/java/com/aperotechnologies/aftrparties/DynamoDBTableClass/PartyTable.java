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
    private List<String> partylatlong;
    private String PartyImage;
    private String MaskStatus;
    private String DialogID;
    private List<GateCrashersClass> gatecrashers;


    @DynamoDBHashKey(attributeName = "partyid")
    public String getPartyID() {
        return PartyID;
    }

    public void setPartyID(String PartyID) {
        this.PartyID = PartyID;
    }

    @DynamoDBAttribute(attributeName = "partyname")
    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    @DynamoDBAttribute(attributeName = "starttime")
    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    @DynamoDBAttribute(attributeName = "endtime")
    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    @DynamoDBAttribute(attributeName = "date")
    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    @DynamoDBAttribute(attributeName = "hostfbid")
    public String getHostFBID() {
        return HostFBID;
    }

    public void setHostFBID(String HostFBID) {
        this.HostFBID = HostFBID;
    }

    @DynamoDBAttribute(attributeName = "hostqbid")
    public String getHostQBID() {
        return HostQBID;
    }

    public void setHostQBID(String HostQBID) {
        this.HostQBID = HostQBID;
    }

    @DynamoDBAttribute(attributeName = "hostname")
    public String getHostName() {
        return HostName;
    }

    public void setHostName(String HostName) {
        this.HostName = HostName;
    }

    @DynamoDBAttribute(attributeName = "partytype")
    public String getPartyType() {
        return PartyType;
    }

    public void setPartyType(String PartyType) {
        this.PartyType = PartyType;
    }

    @DynamoDBAttribute(attributeName = "partydescription")
    public String getPartyDescription() {
        return PartyDescription;
    }

    public void setPartyDescription(String PartyDescription) {
        this.PartyDescription = PartyDescription;
    }

    @DynamoDBAttribute(attributeName = "byob")
    public String getBYOB() {
        return BYOB;
    }

    public void setBYOB(String BYOB) {
        this.BYOB = BYOB;
    }

    @DynamoDBAttribute(attributeName = "partyaddress")
    public String getPartyAddress() {
        return PartyAddress;
    }

    public void setPartyAddress(String PartyAddress) {
        this.PartyAddress = PartyAddress;
    }

    @DynamoDBAttribute(attributeName = "partylatlong")
    public List<String> getPartylatlong() {
        return partylatlong;
    }

    public void setPartylatlong(List<String> partylatlong) {
        this.partylatlong = partylatlong;
    }

    @DynamoDBAttribute(attributeName = "partyimage")
    public String getPartyImage() {
        return PartyImage;
    }

    public void setPartyImage(String PartyImage) {
        this.PartyImage = PartyImage;
    }

    @DynamoDBAttribute(attributeName = "maskstatus")
    public String getMaskStatus() {
        return MaskStatus;
    }

    public void setMaskStatus(String MaskStatus) {
        this.MaskStatus = MaskStatus;
    }

    @DynamoDBAttribute(attributeName = "dialogid")
    public String getDialogID() {
        return DialogID;
    }

    public void setDialogID(String DialogID) {
        this.DialogID = DialogID;
    }


    public List<GateCrashersClass> getGatecrashers() {
        return gatecrashers;
    }

    public void setGatecrashers(List<GateCrashersClass> gatecrashers) {
        this.gatecrashers = gatecrashers;
    }

}
