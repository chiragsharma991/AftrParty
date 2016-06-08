package com.aperotechnologies.aftrparties.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

/**
 * Created by mpatil on 27/05/16.
 */


@DynamoDBDocument
public class Contact
{
    public String name = "N/A";
    public String ConactNo = "N/A";
    public String email = "N/A";
    private int contact_price;

    public Contact()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getConactNo()
    {
        return ConactNo;
    }

    public void setConactNo(String conactNo)
    {
        ConactNo = conactNo;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String toString()
    {
        return name + "   " + ConactNo + "    " + email;
    }


    public int hashCode()
    {
        System.out.println("In hashcode");
        int hashcode = 0;
        hashcode = contact_price*20;
        hashcode += name.hashCode();
        return hashcode;
    }

    public boolean equals(Object obj)
    {
        System.out.println("In equals");
        if (obj instanceof Contact)
        {
            Contact pp = (Contact) obj;
            return (pp.name.equals(this.name) && pp.ConactNo.equals(this.ConactNo) && pp.email.equals(this.email));
        }
        else
        {
            return false;
        }
    }


 //   - See more at: http://www.java2novice.com/java-collections-and-util/hashset/duplicate/#sthash.1JgUtRvL.dpuf

}
