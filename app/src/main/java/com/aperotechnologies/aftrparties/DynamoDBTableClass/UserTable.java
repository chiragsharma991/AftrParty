package com.aperotechnologies.aftrparties.DynamoDBTableClass;

/**
 * Created by hasai on 05/04/16.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.List;

@DynamoDBTable(tableName = "AP_Users")
public class UserTable {

    private String FacebookID;
    //private String InvitedUser;//check whether have account or not
    private String QuickBloxID;
    private String LinkedInID;
    private String Name;
    private String Email;
    private String SocialEmail;
    private String BirthDate;
    private int LKConnectionsCount;
    private int FBFriendsCount;
    private String DeviceToken;
    private String Gender;
    private String PhoneNumber;
    private String ProfilePicUrl;
    private int Ratings;
    private String ProfileStatus;
    private List<PartiesClass> Parties;
    private String Location;
    private List Images;


    @DynamoDBHashKey(attributeName = "FacebookID")
    public String getFacebookID() {
        return FacebookID;
    }

    public void setFacebookID(String FacebookID) {
        this.FacebookID = FacebookID;
    }


//    @DynamoDBAttribute(attributeName = "InvitedUser")
//    public String getInvitedUser() {
//        return InvitedUser;
//    }
//
//    public void setInvitedUser(String InvitedUser) {
//        this.InvitedUser = InvitedUser;
//    }

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


    @DynamoDBAttribute(attributeName = "SocialEmail")
    public String getSocialEmail() {
        return SocialEmail;
    }

    public void setSocialEmail(String SocialEmail) {
        this.SocialEmail = SocialEmail;
    }


    @DynamoDBAttribute(attributeName = "BirthDate")
    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }
    
    @DynamoDBAttribute(attributeName = "LKConnectionsCount")
    public int getLKConnectionsCount() {
        return LKConnectionsCount;
    }

    public void setLKConnectionsCount(int LKConnectionsCount) {
        this.LKConnectionsCount = LKConnectionsCount;
    }


    @DynamoDBAttribute(attributeName = "FBFriendsCount")
    public int getFBFriendsCount() {
        return FBFriendsCount;
    }

    public void setFBFriendsCount(int FBFriendsCount) {
        this.FBFriendsCount = FBFriendsCount;
    }


    @DynamoDBAttribute(attributeName = "DeviceToken")
    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken) {
        this.DeviceToken = DeviceToken;
    }


    @DynamoDBAttribute(attributeName = "Gender")
    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }



    @DynamoDBAttribute(attributeName = "PhoneNumber")
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }



    @DynamoDBAttribute(attributeName = "ProfilePicUrl")
    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String ProfilePicUrl) {
        this.ProfilePicUrl = ProfilePicUrl;
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


    @DynamoDBAttribute(attributeName = "Location")
    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
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



