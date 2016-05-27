package com.aperotechnologies.aftrparties.Reusables;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hasai on 27/05/16.
 */
public class Validations {

    public static String getUniqueId(Context cont)
    {
        String uniqueID;
        String FacebookID = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
        Date date= new Date();
        long timeStamp = date.getTime();
        uniqueID = FacebookID+"_"+timeStamp;//UUID.randomUUID().toString();
        Log.e("uniqueId "," "+uniqueID);
        return uniqueID;
    }


    public static int checkWordsCount(String input){
//        String regex = "^\\W*(?:\\w+\\b\\W*){1,30}$";
//
//        Pattern pattern = Pattern.compile(regex);
//
//        Matcher matcher = pattern.matcher(input);
//        System.out.println(matcher.matches());
//
//        Log.e("matches "," "+matcher.matches());
//
//        return matcher.matches();

        String[] words = input.split("\\s+");
        Log.e("words length "," "+words.length);
        return words.length;


    }

}
