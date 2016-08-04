package com.aperotechnologies.aftrparties;

import android.app.Application;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.facebook.FacebookSdk;
import com.quickblox.core.QBSettings;

/**
 * Created by hasai on 28/06/16.
 */
public class MyApplication  extends Application
{

    static final String APP_ID = "40454";//"34621";
    static final String AUTH_KEY = "GXzMGfcx-pAQOBP";//"sYpuKrOrGT4pG6d";//"q6aK9sm6GCSmtru";
    static final String AUTH_SECRET = "TZC8fTnAUDXSuYs";//"hVx9RNMT4emBK5K";//"uTOm5-R4zYyR-DV";
    static final String ACCOUNT_KEY = "VLBr2asUuw9uHDFC7qgb";//"VLBr2asUuw9uHDFC7qgb";//"bzbtQDLez742xU468TXt";
    Configuration_Parameter m_config;


    @Override
    public void onCreate() {
        // Simply add the handler, and that's it! No need to add any code
        // to every activity. Everything is contained in MyLifecycleHandler
        // with just a few lines of code. Now *that's* nice.
        //registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        registerActivityLifecycleCallbacks(new BaseLifeCycleCallbacks());
        m_config = Configuration_Parameter.getInstance();

        // Initialize the Amazon Cognito credentials provider
        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:bd2ea8c9-5aa9-4e32-b8e5-20235fc7f4ac", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        m_config.ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        m_config.mapper = new DynamoDBMapper(m_config.ddbClient);


        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

}
