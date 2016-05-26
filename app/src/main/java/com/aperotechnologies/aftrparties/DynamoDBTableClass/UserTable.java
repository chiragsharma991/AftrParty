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
    //private String InvitedUser;//check whether have account or not
    private String QuickBloxID;
    private String LinkedInID;
    private String SocialEmail;
    private String FBUserName;
    private String FBCurrentLocation;
    private String FBHomeLocation;
    private String BirthDate;
    private int FBFriendsCount;
    private String Gender;
    private String FBProfilePicUrl;
    private String LKProfilePicUrl;
    private int LKConnectionsCount;
    private String LKHeadLine;
    private String Name;
    private String Email;
    private String PhoneNumber;
    private int Ratings;
    private String ProfileStatus;
    private String DeviceToken;
    private List<PartiesClass> Parties;
    private List Images;


    @DynamoDBHashKey(attributeName = "FacebookID")
    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String FacebookID) {
        this.FacebookID = FacebookID;
    }


    @DynamoDBAttribute(attributeName = "QuickBloxID")
    public String getQuickBloxID() {
        return QuickBloxID;
    }

    public void setQuickBloxID(String QuickBloxID) {
        this.QuickBloxID = QuickBloxID;
    }


    @DynamoDBAttribute(attributeName = "LinkedInID")
    public String getLinkedInID() {
        return LinkedInID;
    }

    public void setLinkedInID(String LinkedInID) {
        this.LinkedInID = LinkedInID;
    }


    @DynamoDBAttribute(attributeName = "SocialEmail")
    public String getSocialEmail() {
        return SocialEmail;
    }

    public void setSocialEmail(String SocialEmail) {
        this.SocialEmail = SocialEmail;
    }


    @DynamoDBAttribute(attributeName = "FBUserName")
    public String getFBUserName() {
        return FBUserName;
    }

    public void setFBUserName(String FBUserName) {
        this.FBUserName = FBUserName;
    }


    @DynamoDBAttribute(attributeName = "FBCurrentLocation")
    public String getFBCurrentLocation() {
        return FBCurrentLocation;
    }

    public void setFBCurrentLocation(String FBCurrentLocation) {
        this.FBCurrentLocation = FBCurrentLocation;
    }


    @DynamoDBAttribute(attributeName = "FBHomeLocation")
    public String FBHomeLocation() {
        return FBHomeLocation;
    }

    public void setFBHomeLocation(String FBHomeLocation) {
        this.FBHomeLocation = FBHomeLocation;
    }


    @DynamoDBAttribute(attributeName = "BirthDate")
    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }
    

    @DynamoDBAttribute(attributeName = "FBFriendsCount")
    public int getFBFriendsCount() {
        return FBFriendsCount;
    }

    public void setFBFriendsCount(int FBFriendsCount) {
        this.FBFriendsCount = FBFriendsCount;
    }


    @DynamoDBAttribute(attributeName = "Gender")
    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }


    @DynamoDBAttribute(attributeName = "FBProfilePicUrl")
    public String getFBProfilePicUrl() {
        return FBProfilePicUrl;
    }

    public void setFBProfilePicUrl(String FBProfilePicUrl) {
        this.FBProfilePicUrl = FBProfilePicUrl;
    }

    @DynamoDBAttribute(attributeName = "LKProfilePicUrl")
    public String getLKProfilePicUrl() {
        return LKProfilePicUrl;
    }

    public void setLKProfilePicUrl(String LKProfilePicUrl) {
        this.LKProfilePicUrl = LKProfilePicUrl;
    }


    @DynamoDBAttribute(attributeName = "LKConnectionsCount")
    public int getLKConnectionsCount() {
        return LKConnectionsCount;
    }

    public void setLKConnectionsCount(int LKConnectionsCount) {
        this.LKConnectionsCount = LKConnectionsCount;
    }


    @DynamoDBAttribute(attributeName = "LKHeadLine")
    public String getLKHeadLine() {
        return LKHeadLine;
    }

    public void setLKHeadLine(String LKHeadLine) {
        this.LKHeadLine = LKHeadLine;
    }


    @DynamoDBAttribute(attributeName = "DeviceToken")
    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken) {
        this.DeviceToken = DeviceToken;
    }


    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }


    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }


    @DynamoDBAttribute(attributeName = "PhoneNumber")
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }


    @DynamoDBAttribute(attributeName = "Ratings")
    public int getRatings() {
        return Ratings;
    }

    public void setRatings(int Ratings) {
        this.Ratings = Ratings;
    }


    @DynamoDBAttribute(attributeName = "ProfileStatus")
    public String getProfileStatus() {
        return ProfileStatus;
    }

    public void setProfileStatus(String ProfileStatus) {
        this.ProfileStatus = ProfileStatus;
    }


    @DynamoDBAttribute(attributeName = "Images")
    public List getImages() {
        return Images;
    }

    public void setImages(List Images) {
        this.Images = Images;
    }
//

    public List<PartiesClass> getParties() {
        return Parties;
    }

    public void setParties(List<PartiesClass> Parties) {
        this.Parties = Parties;
    }
}



