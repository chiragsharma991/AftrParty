package com.aperotechnologies.aftrparties.Constants;

import android.app.NotificationManager;
import android.app.ProgressDialog;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.users.model.QBUser;

import java.util.Collection;

/**
 * Created by mpatil on 28/04/15.
 */
//Singleton pattern class. Variables used across multiple activities are used here
public class Configuration_Parameter
{
    private static Configuration_Parameter myObj;
    //Harshada

    public QBGroupChatManager groupChatManager;
    public QBPrivateChatManager privateChatManager;
    public QBChatService chatService;
    public Collection<Integer> onlineUsers;
    public NotificationManager notificationManager;
    public AmazonDynamoDB ddbClient;
    public DynamoDBMapper mapper;
    public ProgressDialog pDialog;
    public String FBloginWithQB = "No";
    public String REG_ID = "registration_id";


    /*shared preference*/
    //Meghana
    public String Entered_User_Name = "UserName";
    public String Entered_Email = "Email";
    public String Entered_Contact_No = "ContactNo";
    public String LoggedInFBUserID = "";
    public String FBLoginDone ="FBLoginDone";
    public String LILoginDone ="LILoginDone";
    //Harshada
    public String qbuser = "";

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
