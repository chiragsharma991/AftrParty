package com.aperotechnologies.aftrparties.DynamoDBTableClass;

/**
 * Created by hasai on 05/04/16.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "User")
public class UserTable {

    private String FBId;
    private String Login;//check whether have account or not
    private String UserId;
    private String LinkedInId;
    private String UserName;
    private String Email;
    private String FBLIEmail;
    private String DOB;
    private int Age;
    private int Connections;
    private int FriendsCount;
    private String DeviceToken;
    private String Gender;
    private String Phone;
    private String Profilepic;
    private int Ratings;
    private String UserStatusMsg;
    private List<PartyIdStatusClass> PartyIdStatus;


    @DynamoDBHashKey(attributeName = "FBId")
    public String getFBId() {
        return FBId;
    }

    public void setFBId(String FBId) {
        this.FBId = FBId;
    }


    @DynamoDBAttribute(attributeName = "Login")
    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    @DynamoDBAttribute(attributeName = "UserId")
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }


    @DynamoDBAttribute(attributeName = "LinkedInId")
    public String getLinkedInId() {
        return LinkedInId;
    }

    public void setLinkedInId(String LinkedInId) {
        this.LinkedInId = LinkedInId;
    }


    @DynamoDBAttribute(attributeName = "UserName")
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }


    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }


    @DynamoDBAttribute(attributeName = "FBLIEmail")
    public String getFBLIEmail() {
        return FBLIEmail;
    }

    public void setFBLIEmail(String FBLIEmail) {
        this.FBLIEmail = FBLIEmail;
    }


    @DynamoDBAttribute(attributeName = "DOB")
    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }


    @DynamoDBAttribute(attributeName = "Age")
    public int getAge() {
        return Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }


    @DynamoDBAttribute(attributeName = "Connections")
    public int getConnections() {
        return Connections;
    }

    public void setConnections(int Connections) {
        this.Connections = Connections;
    }


    @DynamoDBAttribute(attributeName = "FriendsCount")
    public int getFriendsCount() {
        return FriendsCount;
    }

    public void setFriendsCount(int FriendsCount) {
        this.FriendsCount = FriendsCount;
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



    @DynamoDBAttribute(attributeName = "Phone")
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }



    @DynamoDBAttribute(attributeName = "Profilepic")
    public String getProfilepic() {
        return Profilepic;
    }

    public void setProfilepic(String Profilepic) {
        this.Profilepic = Profilepic;
    }



    @DynamoDBAttribute(attributeName = "Ratings")
    public int getRatings() {
        return Ratings;
    }

    public void setRatings(int Ratings) {
        this.Ratings = Ratings;
    }



    @DynamoDBAttribute(attributeName = "UserStatusMsg")
    public String getUserStatusMsg() {
        return UserStatusMsg;
    }

    public void setUserStatusMsg(String UserStatusMsg) {
        this.UserStatusMsg = UserStatusMsg;
    }


//

    public List<PartyIdStatusClass> getPartyIdStatus() {
        return PartyIdStatus;
    }

    public void setPartyIdStatus(List<PartyIdStatusClass> PartyIdStatus) {
        this.PartyIdStatus = PartyIdStatus;
    }
}



