package com.aperotechnologies.aftrparties.DynamoDBTableClass;

/**
 * Created by hasai on 05/04/16.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "AP_Payments")
public class PaymentTable {

    private String FacebookID;
    private String QuickBloxID;
    private List<PrivateChatClass> privatechat;
    private List<String> chathistoryretention;
    private List<PartyMaskStatusClass> PartyMaskStatus;
    private List<PaidGCClass> paidgc;


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


    @DynamoDBAttribute(attributeName = "chathistoryretention")
    public  List<String> getchathistoryretention() {
        return chathistoryretention;
    }

    public void setchathistoryretention( List<String> chathistoryretention) {
        this.chathistoryretention = chathistoryretention;
    }

    public List<PrivateChatClass> getPrivatechat() {
        return privatechat;
    }

    public void setPrivatechat(List<PrivateChatClass> privatechat) {
        this.privatechat = privatechat;
    }

    public List<PartyMaskStatusClass> getPartymaskstatus() {
        return PartyMaskStatus;
    }

    public void setPartymaskstatus(List<PartyMaskStatusClass> PartyMaskStatus) {
        this.PartyMaskStatus = PartyMaskStatus;
    }

    public List<PaidGCClass> getPaidgc() {
        return paidgc;
    }

    public void setPaidgc(List<PaidGCClass> paidgc) {
        this.paidgc = paidgc;
    }


}



