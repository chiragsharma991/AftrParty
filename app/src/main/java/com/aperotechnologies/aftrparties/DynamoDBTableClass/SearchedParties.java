package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

/**
 * Created by hasai on 03/05/16.
 */



public class SearchedParties {

     String partyid = "N/A";
     String partyname = "N/A";;
     String starttime = "N/A";;
     String endtime = "N/A";;
//     String Date = "N/A";;
     String hostfbid = "N/A";;
     String hostqbid = "N/A";
     String hostname = "N/A";
//     String PartyType = "N/A";//Predefine desc
     String partydescription = "N/A";
     String byob = "N/A";
     String partyaddress = "N/A";
     List<String> partylatlong;
//     String DialogID = "N/A";
     List<SearchedParties> gatecrashers;


    
    public String getPartyID() {
        return partyid;
    }

    public void setPartyID(String partyid) {
        this.partyid = partyid;
    }

    public String getPartyName() {
        return partyname;
    }

    public void setPartyName(String partyname) {
        this.partyname = partyname;
    }

    public String getStartTime() {
        return starttime;
    }

    public void setStartTime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndTime() {
        return endtime;
    }

    public void setEndTime(String endtime) {
        this.endtime = endtime;
    }

//    public String getDate() {
//        return Date;
//    }
//
//    public void setDate(String Date) {
//        this.Date = Date;
//    }

    public String getHostFBID() {
        return hostfbid;
    }

    public void setHostFBID(String hostfbid) {
        this.hostfbid = hostfbid;
    }

    public String getHostQBID() {
        return hostqbid;
    }

    public void setHostQBID(String hostqbid) {
        this.hostqbid = hostqbid;
    }

    public String getHostName() {
        return hostname;
    }

    public void setHostName(String hostname) {
        this.hostname = hostname;
    }

//    public String getPartyType() {
//        return PartyType;
//    }
//
//    public void setPartyType(String PartyType) {
//        this.PartyType = PartyType;
//    }

    public String getPartyDescription() {
        return partydescription;
    }

    public void setPartyDescription(String partydescription) {
        this.partydescription = partydescription;
    }

    public String getBYOB() {
        return byob;
    }

    public void setBYOB(String byob) {
        this.byob = byob;
    }

    public String getPartyAddress() {
        return partyaddress;
    }

    public void setPartyAddress(String partyaddress) {
        this.partyaddress = partyaddress;
    }

    public List<String> getPartylatlong() {
        return partylatlong;
    }

    public void setPartylatlong(List<String> partylatlong) {
        this.partylatlong = partylatlong;
    }
//
//    public String getPartyImage() {
//        return PartyImage;
//    }
//
//    public void setPartyImage(String PartyImage) {
//        this.PartyImage = PartyImage;
//    }


//    public String getDialogID() {
//        return DialogID;
//    }
//
//    public void setDialogID(String DialogID) {
//        this.DialogID = DialogID;
//    }


    public List<SearchedParties> getGatecrashers() {
        return gatecrashers;
    }

    public void setGatecrashers(List<SearchedParties> gatecrashers) {
        this.gatecrashers = gatecrashers;
    }

}
