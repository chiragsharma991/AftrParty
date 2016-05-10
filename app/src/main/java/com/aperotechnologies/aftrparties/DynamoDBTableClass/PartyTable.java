package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

/**
 * Created by hasai on 03/05/16.
 */
@DynamoDBTable(tableName = "Party")
public class PartyTable {

    private String PartyId;
    private String PartyName;
    private String PartyStartTime;
    private String PartyEndTime;
    private String Date;
    private String HostFBID;
    private String HostUserId;
    private String HostName;
    private String PredefineDesc;
    private String Description;
    private String BYOB;
    private String PartyAddress;
    private String LatLong;
    private String PartyImage;
    private String FlagMask;
    private String DialogId;
    private List<UserIdStatusClass> UserIdStatus;


    @DynamoDBHashKey(attributeName = "PartyId")
    public String getPartyId() {
        return PartyId;
    }

    public void setPartyId(String PartyId) {
        this.PartyId = PartyId;
    }

    @DynamoDBAttribute(attributeName = "PartyName")
    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String PartyName) {
        this.PartyName = PartyName;
    }

    @DynamoDBAttribute(attributeName = "PartyStartTime")
    public String getPartyStartTime() {
        return PartyStartTime;
    }

    public void setPartyStartTime(String PartyStartTime) {
        this.PartyStartTime = PartyStartTime;
    }

    @DynamoDBAttribute(attributeName = "PartyEndTime")
    public String getPartyEndTime() {
        return PartyEndTime;
    }

    public void setPartyEndTime(String PartyEndTime) {
        this.PartyEndTime = PartyEndTime;
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

    @DynamoDBAttribute(attributeName = "HostUserId")
    public String getHostUserId() {
        return HostUserId;
    }

    public void setHostUserId(String HostUserId) {
        this.HostUserId = HostUserId;
    }

    @DynamoDBAttribute(attributeName = "HostName")
    public String getHostName() {
        return HostName;
    }

    public void setHostName(String HostName) {
        this.HostName = HostName;
    }

    @DynamoDBAttribute(attributeName = "PredefineDesc")
    public String getPredefineDesc() {
        return PredefineDesc;
    }

    public void setPredefineDesc(String PredefineDesc) {
        this.PredefineDesc = PredefineDesc;
    }

    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
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

    @DynamoDBAttribute(attributeName = "LatLong")
    public String getLatLong() {
        return LatLong;
    }

    public void setLatLong(String LatLong) {
        this.LatLong = LatLong;
    }

    @DynamoDBAttribute(attributeName = "PartyImage")
    public String getPartyImage() {
        return PartyImage;
    }

    public void setPartyImage(String PartyImage) {
        this.PartyImage = PartyImage;
    }

    @DynamoDBAttribute(attributeName = "FlagMask")
    public String getFlagMask() {
        return FlagMask;
    }

    public void setFlagMask(String FlagMask) {
        this.FlagMask = FlagMask;
    }

    @DynamoDBAttribute(attributeName = "DialogId")
    public String getDialogId() {
        return DialogId;
    }

    public void setDialogId(String DialogId) {
        this.DialogId = DialogId;
    }


    public List<UserIdStatusClass> getUserIdStatus() {
        return UserIdStatus;
    }

    public void setUserIdStatus(List<UserIdStatusClass> UserIdStatus) {
        this.UserIdStatus = UserIdStatus;
    }

}
