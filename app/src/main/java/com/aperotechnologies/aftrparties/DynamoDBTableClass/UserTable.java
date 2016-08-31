package com.aperotechnologies.aftrparties.DynamoDBTableClass;

/**
 * Created by hasai on 05/04/16.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "AP_Users")
public class UserTable {

    private String FacebookID;
    private String QuickBloxID;
    private String LinkedInID;
    private String SocialEmail;
    private String FBUserName;
    private String FBCurrentLocation;
    private String FBHomeLocation;
    private String BirthDate;
    private int FBFriendsCount;
    private String Gender;
    private List<String> ProfilePicUrl;
    private String LKProfilePicUrl;
    private int LKConnectionsCount;
    private String LKHeadLine;
    private String Name;
    private String Email;
    private String PhoneNumber;
    private String ProfileStatus;
    private String DeviceToken;
    private List<PartiesClass> parties;
    private String Registrationstatus;
    private List<PaidGCClass> paidgc;
    private List<ActivePartyClass> activeparty;
    private String currentmaskstatus;
    private String Imageflag;
    private List<PartyMaskStatusClass> PartyMaskStatus;
    private List<RatingsClass> Ratings;


    @DynamoDBHashKey(attributeName = "facebookid")
    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String FacebookID) {
        this.FacebookID = FacebookID;
    }


    @DynamoDBAttribute(attributeName = "quickbloxid")
    public String getQuickBloxID() {
        return QuickBloxID;
    }

    public void setQuickBloxID(String QuickBloxID) {
        this.QuickBloxID = QuickBloxID;
    }


    @DynamoDBAttribute(attributeName = "linkedinid")
    public String getLinkedInID() {
        return LinkedInID;
    }

    public void setLinkedInID(String LinkedInID) {
        this.LinkedInID = LinkedInID;
    }


    @DynamoDBAttribute(attributeName = "socialemail")
    public String getSocialEmail() {
        return SocialEmail;
    }

    public void setSocialEmail(String SocialEmail) {
        this.SocialEmail = SocialEmail;
    }


    @DynamoDBAttribute(attributeName = "fbusername")
    public String getFBUserName() {
        return FBUserName;
    }

    public void setFBUserName(String FBUserName) {
        this.FBUserName = FBUserName;
    }


    @DynamoDBAttribute(attributeName = "fbcurrentlocation")
    public String getFBCurrentLocation() {
        return FBCurrentLocation;
    }

    public void setFBCurrentLocation(String FBCurrentLocation) {
        this.FBCurrentLocation = FBCurrentLocation;
    }


    @DynamoDBAttribute(attributeName = "fbhomelocation")
    public String getFBHomeLocation() {
        return FBHomeLocation;
    }

    public void setFBHomeLocation(String FBHomeLocation) {
        this.FBHomeLocation = FBHomeLocation;
    }


    @DynamoDBAttribute(attributeName = "birthdate")
    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }


    @DynamoDBAttribute(attributeName = "fbfriendscount")
    public int getFBFriendsCount() {
        return FBFriendsCount;
    }

    public void setFBFriendsCount(int FBFriendsCount) {
        this.FBFriendsCount = FBFriendsCount;
    }


    @DynamoDBAttribute(attributeName = "gender")
    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }


    @DynamoDBAttribute(attributeName = "profilepicurl")
    public  List<String> getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl( List<String> ProfilePicUrl) {
        this.ProfilePicUrl = ProfilePicUrl;
    }

    @DynamoDBAttribute(attributeName = "lkprofilepicurl")
    public String getLKProfilePicUrl() {
        return LKProfilePicUrl;
    }

    public void setLKProfilePicUrl(String LKProfilePicUrl) {
        this.LKProfilePicUrl = LKProfilePicUrl;
    }


    @DynamoDBAttribute(attributeName = "lkconnectionscount")
    public int getLKConnectionsCount() {
        return LKConnectionsCount;
    }

    public void setLKConnectionsCount(int LKConnectionsCount) {
        this.LKConnectionsCount = LKConnectionsCount;
    }


    @DynamoDBAttribute(attributeName = "lkheadline")
    public String getLKHeadLine() {
        return LKHeadLine;
    }

    public void setLKHeadLine(String LKHeadLine) {
        this.LKHeadLine = LKHeadLine;
    }


    @DynamoDBAttribute(attributeName = "devicetoken")
    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken) {
        this.DeviceToken = DeviceToken;
    }


    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }


    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }


    @DynamoDBAttribute(attributeName = "phonenumber")
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }



    @DynamoDBAttribute(attributeName = "profilestatus")
    public String getProfileStatus() {
        return ProfileStatus;
    }

    public void setProfileStatus(String ProfileStatus) {
        this.ProfileStatus = ProfileStatus;
    }


    @DynamoDBAttribute(attributeName = "registrationstatus")
    public String getRegistrationStatus() {return Registrationstatus;}

    public void setRegistrationStatus(String Registrationstatus){
        this.Registrationstatus = Registrationstatus;
    }


    @DynamoDBAttribute(attributeName = "currentmaskstatus")
    public String getcurrentmaskstatus()
    {
        return currentmaskstatus;
    }

    public void setcurrentmaskstatus(String currentmaskstatus)
    {
        this.currentmaskstatus = currentmaskstatus;
    }


    public List<PaidGCClass> getPaidgc() {
        return paidgc;
    }

    public void setPaidgc(List<PaidGCClass> paidgc) {
        this.paidgc = paidgc;
    }


    @DynamoDBAttribute(attributeName = "imageflag")
    public String getImageflag() {return Imageflag;}

    public void setImageflag(String Imageflag){
        this.Imageflag = Imageflag;
    }

    public List<ActivePartyClass> getActiveparty() {
        return activeparty;
    }

    public void setActiveparty(List<ActivePartyClass> activeparty) {
        this.activeparty = activeparty;
    }

    public List<PartiesClass> getParties() {
        return parties;
    }

    public void setParties(List<PartiesClass> parties) {
        this.parties = parties;
    }


    public List<PartyMaskStatusClass> getPartymaskstatus() {
        return PartyMaskStatus;
    }

    public void setPartymaskstatus(List<PartyMaskStatusClass> PartyMaskStatus) {
        this.PartyMaskStatus = PartyMaskStatus;
    }


    public List<RatingsClass> getRatings() {
        return Ratings;
    }

    public void setRatings(List<RatingsClass> Ratings) {
        this.Ratings = Ratings;
    }

}



