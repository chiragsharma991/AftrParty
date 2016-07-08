package com.aperotechnologies.aftrparties.LocalNotifications;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.DynamoDBTableClass.AWSLoginOperations;
import com.aperotechnologies.aftrparties.PNotifications.PlayServicesHelper;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Created by hasai on 04/07/16.
 */
public class LoginforNotifications {




    public void QBSessionforNotification(final Context cont, final String from, final String accessToken)
    {

        QBAuth.createSession(new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("createSession", "onsuccess");
                try
                {
                    String qbtoken = QBAuth.getBaseService().getToken();
                    Log.e("qbtoken ", qbtoken);
                }
                catch (Exception e)
                {
                    Toast.makeText(cont, "Login Failed, Please try again after some time",Toast.LENGTH_SHORT).show();
                }

                QBLoginforNotification(cont, from, accessToken);

            }

            @Override
            public void onError(QBResponseException e)
            {
                Toast.makeText(cont, "Login Failed, Please try again after some time",Toast.LENGTH_SHORT).show();

            }
        });
    }






    public void QBLoginforNotification(final Context cont, final String from, String accessToken)
    {
        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                null, new QBEntityCallback<QBUser>()
                {
                    @Override
                    public void onSuccess(QBUser user, Bundle args)
                    {
                        Log.e("Facebook login","Success"+" ");
                        QBChatLoginforNotification(cont, from, user);

                    }

                    @Override
                    public void onError(QBResponseException e)
                    {
                        Toast.makeText(cont, "Login Failed, Please try again after some time",Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public Boolean QBChatLoginforNotification(final Context cont, final String from, final QBUser user){
        final Boolean[] value = {false};

        QBChatService.getInstance().login(user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("ChatServicelogin","Success ");

                value[0] = true;

            }

            @Override
            public void onError(QBResponseException e)
            {

                Toast.makeText(cont, "Login Failed, Please try again after some time",Toast.LENGTH_SHORT).show();
                value[0] = false;
            }
        });

        return value[0];
    }

}
