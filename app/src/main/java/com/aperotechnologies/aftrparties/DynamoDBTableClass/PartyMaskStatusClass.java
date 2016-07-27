package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 26/07/16.
 */


@DynamoDBDocument
public class PartyMaskStatusClass {

    private String maskstatus;
    private String masksubscriptiondate;


    public String getMaskstatus() { return maskstatus; }
    public void setMaskstatus(String maskstatus) { this.maskstatus = maskstatus; }

    public String getMasksubscriptiondate() { return masksubscriptiondate; }
    public void setMasksubscriptiondate(String masksubscriptiondate) { this.masksubscriptiondate = masksubscriptiondate; }



}


