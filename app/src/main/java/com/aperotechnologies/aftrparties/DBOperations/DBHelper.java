package com.aperotechnologies.aftrparties.DBOperations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aperotechnologies.aftrparties.Login.LoginTableColumns;

/**
 * Created by mpatil on 13/05/16.
 */
public class DBHelper extends SQLiteOpenHelper
{

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 1;

    // private SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "Aftrparties.db";

    private static DBHelper mInstance = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        //USER TABLE CREATION
        
        
        String CREATE_TABLE_USER = "CREATE TABLE " + LoginTableColumns.USERTABLE + "("
                + LoginTableColumns.FB_USER_ID + " TEXT PRIMARY KEY ,"
                + LoginTableColumns.FB_USER_NAME + " TEXT, "
                + LoginTableColumns.FB_USER_GENDER + " TEXT, "
                + LoginTableColumns.FB_USER_BIRTHDATE + " TEXT, "
                + LoginTableColumns.FB_USER_EMAIL + " TEXT, "
                + LoginTableColumns.FB_USER_PROFILE_PIC + " TEXT, "
                + LoginTableColumns.FB_USER_HOMETOWN_ID + " TEXT, "
                + LoginTableColumns.FB_USER_HOMETOWN_NAME + " TEXT, "
                + LoginTableColumns.FB_USER_CURRENT_LOCATION_ID + " TEXT, "
                + LoginTableColumns.FB_USER_FRIENDS + " TEXT, "

                + LoginTableColumns.LI_USER_ID + " TEXT, "
                + LoginTableColumns.LI_USER_EMAIL + " TEXT, "
                + LoginTableColumns.LI_USER_PROFILE_PIC + " TEXT, "
                + LoginTableColumns.LI_USER_CONNECTIONS + " TEXT, "
                + LoginTableColumns.LI_USER_FIRST_NAME + " TEXT, "
                + LoginTableColumns.LI_USER_LAST_NAME + " TEXT, "
                + LoginTableColumns.LI_USER_HEADLINE + " TEXT, "

                + LoginTableColumns.FB_USER_CURRENT_LOCATION_NAME + " TEXT) ";

        db.execSQL(CREATE_TABLE_USER);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
       db.execSQL("DROP TABLE IF EXISTS " + LoginTableColumns.USERTABLE);

    }
}