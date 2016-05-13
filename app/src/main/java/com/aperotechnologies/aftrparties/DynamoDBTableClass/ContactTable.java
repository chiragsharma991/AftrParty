package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by hasai on 09/05/16.
 */
@DynamoDBTable(tableName = "AP_Contacts")
public class ContactTable {


    private  String Name;
    private String ContactName;
    private String ContactNo;


    @DynamoDBHashKey(attributeName = "Name")
    public String getName() {
        return Name;
    }

    public void setName(String UserId) {
        this.Name = Name;
    }


    @DynamoDBAttribute(attributeName = "ContactName")
    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String ContactName) {
        this.ContactName = ContactName;
    }

    @DynamoDBAttribute(attributeName = "ContactNo")
    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String ContactNo) {
        this.ContactNo = ContactNo;
    }


}
