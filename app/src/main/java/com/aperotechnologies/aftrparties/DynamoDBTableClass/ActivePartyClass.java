package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by hasai on 21/06/16.
 */
@DynamoDBDocument
public class ActivePartyClass {

        private String partyid;
        private String partyname;
        private String partystatus;
        private String starttime;
        private String endtime;
        private String startblocktime;
        private String endblocktime;



        public String getPartyid() { return partyid; }
        public void setPartyid(String partyid) { this.partyid = partyid; }

        public String getPartyname() { return partyname; }
        public void setPartyname(String partyname) { this.partyname = partyname; }

        public String getPartystatus() { return partystatus; }
        public void setPartystatus(String partystatus) { this.partystatus = partystatus; }

        public String getStarttime() { return starttime; }
        public void setStarttime(String starttime) { this.starttime = starttime; }

        public String getEndtime() { return endtime; }
        public void setEndtime(String endtime) { this.endtime = endtime; }




        public String getStartblocktime() { return startblocktime; }
        public void setStartblocktime(String startblocktime) { this.startblocktime = startblocktime; }

        public String getEndblocktime() { return endblocktime; }
        public void setEndblocktime(String endblocktime) { this.endblocktime = endblocktime; }

    }


/*"partyIdStatus": [
    {
      "partyId": "1",
      "partyStatus": "Approved"
    }
  ],*/
