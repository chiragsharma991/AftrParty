package com.aperotechnologies.aftrparties.DynamoDBTableClass;

/**
 * Created by hasai on 10/08/16.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;


/**
 * Created by hasai on 21/06/16.
 */
@DynamoDBDocument
public class PrivateChatClass {

    private String fbid;
    private String subscriptiondate;
    private String dialogId;


    public String getFbid() { return fbid; }
    public void setFbid(String fbid) { this.fbid = fbid; }

    public String getSubscriptiondate() { return subscriptiondate; }
    public void setSubscriptiondate(String subscriptiondate) { this.subscriptiondate = subscriptiondate; }

    public String getDialogId() { return dialogId; }
    public void setDialogId(String dialogId) { this.dialogId = dialogId; }

}
