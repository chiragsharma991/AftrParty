package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 21/06/16.
 */
@DynamoDBDocument
public class PaidGCClass {

    private String paidstatus;
    private String subscriptiondate;


    public String getPaidstatus() { return paidstatus; }
    public void setPaidstatus(String paidstatus) { this.paidstatus = paidstatus; }

    public String getSubscriptiondate() { return subscriptiondate; }
    public void setSubscriptiondate(String subscriptiondate) { this.subscriptiondate = subscriptiondate; }

}
