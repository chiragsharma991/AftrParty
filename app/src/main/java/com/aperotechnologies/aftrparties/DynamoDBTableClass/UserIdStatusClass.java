package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 19/04/16.
 */
@DynamoDBDocument
public class UserIdStatusClass {

    private String UserId;
    private String UserPartyStatus;
    private String UserGoingStatus;


    public String getUserId() { return UserId; }
    public void setUserId(String UserId) { this.UserId = UserId; }

    public String getUserPartyStatus() { return UserPartyStatus; }
    public void setUserPartyStatus(String UserPartyStatus) { this.UserPartyStatus = UserPartyStatus; }

    public String getUserGoingStatus() { return UserGoingStatus; }
    public void setUserGoingStatus(String UserGoingStatus) { this.UserGoingStatus = UserGoingStatus; }



}
