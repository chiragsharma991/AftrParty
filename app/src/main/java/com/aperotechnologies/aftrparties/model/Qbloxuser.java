package com.aperotechnologies.aftrparties.model;

import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by hasai on 02/06/16.
 */
public class Qbloxuser extends QBUser {

    private Integer Id;
    private String Customdata;
    private String Email;
    private String FullName;
    private String FacebookId;
    private static QBUser user;



    public Integer getId() {
        return Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public String getCustomdata(){
        return Customdata;
    }

    public void setCustomdata(String Customdata){
        this.Customdata = Customdata;
    }

    public String getEmail(){
        return Email;
    }

    public void setEmail(String Email){
        this.Email = Email;
    }

    public String getFullName(){
        return FullName;
    }

    public void setFullName(String FullName){
        this.FullName = FullName;
    }

    public String getFacebookId(){
        return FacebookId;
    }

    public void setFacebookId(String FacebookId){
        this.FacebookId = FacebookId;
    }
    
    
    public static QBUser getQBUser(){
        return user;
    }

    public static void setQBUser(QBUser user){
        Qbloxuser.user = user;
    }
}

