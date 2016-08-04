package com.aperotechnologies.aftrparties.utils;

/**
 * Created by hasai on 22/04/16.
 */

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils
{
    private static long MILLIS_VALUE = 1000;
    private static String STRING_TODAY = "Today";
    private static String STRING_YESTERDAY = "Yesterday";
    private static String format = "";

    public static StringBuilder longToMessageDate(long dateLong)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLong * MILLIS_VALUE);
        int inputDate = calendar.getTime().getDate();
        Locale locale = new Locale("en","IN");
        Date time = calendar.getTime();
        long time1 = calendar.getTimeInMillis();
        Log.e("time "," "+time+" --- "+time1);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        int mYear = calendar1.get(Calendar.YEAR);
        String mMonth = getMonth(calendar1.get(Calendar.MONTH));
        int mDay = calendar1.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar1.get(Calendar.HOUR_OF_DAY);
        //Log.e("mHour"," "+mHour);
        int mMin = calendar1.get(Calendar.MINUTE);
//        String finalHour = "";
//        String finalMin = "";
//        if(mMin >= 60){
//            if(mHour == 23){
//                finalHour = "00";
//            }else{
//                finalHour = String.valueOf(mHour + 1);
//            }
//            if(mMin - 60 >= 10){
//                finalMin = String.valueOf(mMin - 60);
//            }else{
//                finalMin = "0" + String.valueOf(mMin - 60);
//            }
//
//        }else{
//            finalHour = String.valueOf(mHour);
//            finalMin = String.valueOf(mMin);
//        }
//        return mMonth+" "+mDay+","+mYear+" "+finalHour+ ":"+finalMin;

        StringBuilder value;
        String hours,mins;
        if (mHour == 0)
        {
            mHour += 12;
            format = "AM";
        }
        else if (mHour == 12)
        {
            format = "PM";
        } else if (mHour > 12)
        {
            mHour -= 12;
            format = "PM";
        } else
        {
            format = "AM";
        }

        if(mHour < 10)
        {
            hours = "0" + mHour;
        }else
        {
            hours = String.valueOf(mHour);
        }

        if(mMin < 10)
        {
            mins = "0" + mMin;
        }else
        {
            mins = String.valueOf(mMin);
        }


//        Log.e("time",""+new StringBuilder().append(hour).append(" : ").append(min)
//                .append(" ").append(format));
        value = new StringBuilder().append(mMonth).append(" ").append(mDay).append(" ").append(mYear).append(" ").append(hours).append(" : ").append(mins)
                .append(" ").append(format);
        return value;

    }



    public static String longToMessageListHeaderDate(long dateLong) {
        String timeString;

        Locale locale = new Locale("en");

        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.getTime().getDate();

        calendar.setTimeInMillis(dateLong * MILLIS_VALUE);
        int inputDate = calendar.getTime().getDate();

        if (inputDate == currentDate) {
            timeString = STRING_TODAY;
        } else if (inputDate == currentDate - 1) {
            timeString = STRING_YESTERDAY;
        } else {
            Date time = calendar.getTime();
            timeString = new SimpleDateFormat("EEEE", locale).format(time) + ", " + inputDate  + " " +
                    new SimpleDateFormat("MMMM", locale).format(time);
        }

        return timeString;
    }



    public static String getMonth(int val) {
        String month = "";

        switch (val) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "March";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
            default:
                break;

        }

        return month;
    }


    private static int getHour(int hour) {
        int hr = 0;

        switch (hour) {
            case 0:
                hr = 5;
                break;
            case 1:
                hr = 6;
                break;
            case 2:
                hr = 7;
                break;
            case 3:
                hr = 8;
                break;
            case 4:
                hr = 9;
                break;
            case 5:
                hr = 10;
                break;
            case 6:
                hr = 11;
                break;
            case 7:
                hr = 12;
                break;
            case 8:
                hr = 13;
                break;
            case 9:
                hr = 14;
                break;
            case 10:
                hr = 15;
                break;
            case 11:
                hr = 16;
                break;
            case 12:
                hr = 17;
                break;
            case 13:
                hr = 18;
                break;
            case 14:
                hr = 19;
                break;
            case 15:
                hr = 20;
                break;
            case 16:
                hr = 21;
                break;
            case 17:
                hr = 22;
                break;
            case 18:
                hr = 23;
                break;
            case 19:
                hr = 00;
                break;
            case 20:
                hr = 01;
                break;
            case 21:
                hr = 02;
                break;
            case 22:
                hr = 03;
                break;
            case 23:
                hr = 04;
                break;
            default:
                break;

        }

        return hr;
    }


}