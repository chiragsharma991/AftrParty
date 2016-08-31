package com.aperotechnologies.aftrparties.Constants;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.aperotechnologies.aftrparties.GateCrasher.PartyConversion;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by mpatil on 28/04/15.
 */
//Singleton pattern class. Variables used across multiple activities are used here
public class Configuration_Parameter
{
    private static Configuration_Parameter myObj;
    //Harshada

    public QBChatService chatService;
    public Collection<Integer> onlineUsers;
    public NotificationManager notificationManager;

    //AWS variables
    public AmazonDynamoDB ddbClient;
    public DynamoDBMapper mapper;

    public ProgressDialog pDialog;


    /*shared preference*/
    //Meghana
    public String Entered_User_Name = "UserName";
    public String Entered_Email = "Email";
    public String Entered_Contact_No = "ContactNo";
    public String TempEntered_User_Name = "username";
    public String TempEntered_Email = "email";
    public String TempEntered_Contact_No = "contactno";
    public String LoggedInFBUserID = "";
    public String FBLoginDone = "FBLoginDone";
    public String LILoginDone = "LILoginDone";
    public String QBFBLoginDone = "QBFBLoginDone";
    public String BasicFBLIValidationsDone = "BasicFBLIValidationsDone";
    public String FaceDetectDone = "FaceDetectDone";
    public String OTPValidationDone = "OTPValidationDone";
    public String EmailValidationDone = "EmailValidationDone";
    public String FinalStepDone = "FinalStepDone";
    public String Distance = "3";
    public String GenderPreference = "N/A";
    public String PrimaryUrl;
    public Boolean flag = false;


    //Harshada
    public String SessionToken = null;
    public String QBLoginDone = "QBLoginDone";
    public String QuickBloxID = "QuickBloxId";
    public String REG_ID = "registration_id";
    public String temp_regId = "temp_registration_id";
    public String AWSUserDataDone = "No";
    public Context foregroundCont;

    //*//Cloudinary Variables
    public String cloud_name = "apero-technologies";//"dklb21dyh";
    public String api_key = "312553713914717"; //"585356451553425";
    public String api_secret = "4WDIYEDiZC6EY_9Cdk8_s0bFZh8";//"ylB_rZgnwVT823PH3_HtZo79Sf4";
    //*//
    //lastMessge variable is used for updating lastmessage of chat dialog while navigating from ChatActivity to DialogsActivity
    public String lastMessge = "";
    public String QbIdforInappPChat = "";//used for returning QuickbloxId from fragment to Activity for  In app purchase(1-1Chat)
    public String DialogidforHostInappPChat = "";//used for returning DialogId from fragment to Activity for  In app purchase(1-1Chat)
    public String DialogidforReqInappPChat = "";
    public String FbIdforInappPChat = "";//used for returning DialogId from fragment to Activity for  In app purchase(1-1Chat)
    public static String peerchatDialogId = "dialogid ";
    public static String masksubscriptionTime = "";
    public static List inviteesId = new ArrayList();

    public TelephonyManager mTelephony;

    /**
     * Create private constructor
     */

    private Configuration_Parameter()
    {
    }

    /**
     * Create a static method to get instance.
     */

    public static Configuration_Parameter getInstance()
    {
        if(myObj == null)
        {
            myObj = new Configuration_Parameter();
        }
        return myObj;
    }
}
