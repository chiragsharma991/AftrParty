package com.aperotechnologies.aftrparties.Reusables;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hasai on 27/05/16.
 */
public class Validations {

    private static String format = "";
    private static Configuration_Parameter m_config;

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


    public static String checkWordsCount(String input){
        String regex = "^[a-zA-Z0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        String[] words = input.trim().split("\\s+");
        Log.e("words length "," "+words.length);

        String msg;
        if(input.replaceAll("\\s+", " ").trim().equals("") || input.replaceAll("\\s+", " ").trim().equals(" "))
        {
            msg = "Please fill the Description.";
        }
//        else if(!matcher.matches())
//        {
//            msg = "No special characters allowed.";
//        }
        else if(words.length > 30){

            msg = "Description should be of 30 words only.";

        }else{

            msg = "true";
        }

        return msg;

    }


    //Uploading iamges on cloudinary

    public static String getUrlfromCloudinary(Context cont, String picturePath){

        Log.e("picturePath"," "+picturePath);

        m_config = Configuration_Parameter.getInstance();
        String url = "";
        Map config = new HashMap();
        config.put("cloud_name", m_config.cloud_name);
        config.put("api_key", m_config.api_key);
        config.put("api_secret", m_config.api_secret);

        Cloudinary cloudinary = new Cloudinary("cloudinary://585356451553425:ylB_rZgnwVT823PH3_HtZo79Sf4@dklb21dyh");
        //Log.e("cloudinary"," "+cloudinary);



        try {
            String Id = UUID.randomUUID().toString();
            // upload picture to cloudinary
            cloudinary.uploader().upload(picturePath, ObjectUtils.asMap("public_id", Id));
            //fetch image from cloudinary
            url = cloudinary.url().generate(String.valueOf(Id));

//            cloudinary.uploader().destroy(Id,
//                    ObjectUtils.emptyMap());

            Log.e("public_id"," "+Id+" "+url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static void deleteImagefromCloudinary(String Public_Id){

        Log.e("public_id"," "+Public_Id);

        m_config = Configuration_Parameter.getInstance();
        Map config = new HashMap();
        config.put("cloud_name", m_config.cloud_name);
        config.put("api_key", m_config.api_key);
        config.put("api_secret", m_config.api_secret);

        Cloudinary cloudinary = new Cloudinary("cloudinary://585356451553425:ylB_rZgnwVT823PH3_HtZo79Sf4@dklb21dyh");

        try {
            cloudinary.uploader().destroy(Public_Id,
                   ObjectUtils.emptyMap());


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }





    /* Creating file uri to store image */
    public static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /* returning image */

    public static File getOutputMediaFile(int type)
    {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                ConstsCore.IMAGE_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(ConstsCore.IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        +  ConstsCore.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File mediaFile;
        if (type ==  ConstsCore.MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        }

        else
        {
            return null;
        }
        Log.i("mediaFile",mediaFile.getPath().toString());
        return mediaFile;
    }

    public static Bitmap decodeFile(String path)
    {
        int orientation;
        try
        {
            if (path == null)
            {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 4;
            while (true)
            {
                if (width_tmp / 2 < REQUIRED_SIZE  || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();

            if ((orientation == 3))
            {
                m.postRotate(180);
                m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            }
            else if (orientation == 6)
            {
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);



                return bitmap;
            }
            else if (orientation == 8)
            {
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            }
            return bitmap;
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,0, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static boolean isValidEmailId(String email)
    {
        return  android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

//        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
//                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
//                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
//                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    //Meghana
    public static boolean isEmpty(EditText etText)
    {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isValidMobile(EditText edt )
    {
        String text=edt.getText().toString().trim();
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", text))
        {
            if(text.length() < 10 || text.length() > 10)
            {
                check = false;
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }




    //Harshada


    // calendar function
    public static Calendar getCalendar() {
        /** Current Day data using calendar*/
        final Calendar calendar = Calendar.getInstance();
        return calendar;
    }

    // function for displaying time in AM/PM
    public static StringBuilder showTime(int hour, int min) {
        StringBuilder time;
        String hours,mins;
        if (hour == 0)
        {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12)
        {
            format = "PM";
        } else if (hour > 12)
        {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if(hour < 10)
        {
            hours = "0" + hour;
        }else
        {
            hours = String.valueOf(hour);
        }

        if(min < 10)
        {
            mins = "0" + min;
        }else
        {
            mins = String.valueOf(min);
        }


//        Log.e("time",""+new StringBuilder().append(hour).append(" : ").append(min)
//                .append(" ").append(format));
        time = new StringBuilder().append(hours).append(" : ").append(mins)
                .append(" ").append(format);
        return time;
    }


    //function for displaying month
    public static String getMonthNo(int val) {
        String month = null;

        switch (val) {
            case 0:
                month = "01";
                break;
            case 1:
                month = "02";
                break;
            case 2:
                month = "03";
                break;
            case 3:
                month = "04";
                break;
            case 4:
                month = "05";
                break;
            case 5:
                month = "06";
                break;
            case 6:
                month = "07";
                break;
            case 7:
                month = "08";
                break;
            case 8:
                month = "09";
                break;
            case 9:
                month = "10";
                break;
            case 10:
                month = "11";
                break;
            case 11:
                month = "12";
                break;
            default:
                break;

        }

        return month;
    }

    //function for comparing dates
    public static boolean getCurrentDate(int startDate, int startMon, int startYear){
        Boolean flag = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Validations.getCalendar();
        int day  = c.get(Calendar.DAY_OF_MONTH);
        int mon = c.get(Calendar.MONTH);//mMonth;
        int year = c.get(Calendar.YEAR);//mYear;
        String date = year+"-"+Validations.getMonthNo(mon)+"-"+day;
        String startdate = startYear+"-"+Validations.getMonthNo(startMon)+"-"+startDate;
        try {
            Date date1 = sdf.parse(date);
            Date date2 = sdf.parse(startdate);
            if(date1.compareTo(date2)==0){
                flag = true;
            }else{
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;

    }


    public static String getDateNo(int val) {
        String date;

        if(val < 10){
            date = "0" +String.valueOf(val);
        }else{
            date = String.valueOf(val);
        }


        return date;
    }







}
