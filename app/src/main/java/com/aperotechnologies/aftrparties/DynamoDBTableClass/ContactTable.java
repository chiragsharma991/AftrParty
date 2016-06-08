package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.aperotechnologies.aftrparties.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 09/05/16.
 */
@DynamoDBTable(tableName = "AP_Contacts")
public class ContactTable
{

    private String FBID;
    private List<Contact> contact;
//    private  String Name;
//    private String ContactName;
//    private String ContactNo;

    @DynamoDBHashKey(attributeName = "FBID")
    public String getFBID()
    {
        return FBID;
    }

    public void setFBID(String FBID) {
        this.FBID = FBID;
    }

    //@DynamoDBAttribute(attributeName = "Contact")
    public List<Contact> getContact()
    {
        return contact;
    }

    public void setContact(List<Contact> contact)
    {
        this.contact = contact;
    }

}
