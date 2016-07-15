package com.aperotechnologies.aftrparties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.GateCrasher.GateCrasherSearchActivity;
import com.aperotechnologies.aftrparties.History.HistoryActivity;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Created by hasai on 15/07/16.
 */
public class NewClass {

    public static void QBSession(final String accessToken, final String checkfromWhere)
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
                    e.printStackTrace();
                    GenerikFunctions.hDialog();
                }

                QBLoginforNotification(accessToken, checkfromWhere);

            }

            @Override
            public void onError(QBResponseException e)
            {
                e.printStackTrace();
                GenerikFunctions.hDialog();

            }
        });
    }




    public static void QBLoginforNotification(String accessToken, final String checkfromWhere)
    {
        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                null, new QBEntityCallback<QBUser>()
                {
                    @Override
                    public void onSuccess(QBUser user, Bundle args)
                    {
                        Log.e("Facebook login","Success"+" ");
                        try {
                            user.setPassword(BaseService.getBaseService().getToken());

                            QBChatLogin(user, checkfromWhere);

                        }catch (Exception e){
                            e.printStackTrace();
                            GenerikFunctions.hDialog();
                        }

                    }

                    @Override
                    public void onError(QBResponseException e)
                    {
                        e.printStackTrace();
                        GenerikFunctions.hDialog();
                    }
                });

    }


    public static void QBChatLogin(final QBUser user, final String checkfromWhere){


        QBChatService.getInstance().login(user, new QBEntityCallback()
        {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                Log.e("ChatServicelogin","Success ");



            }

            @Override
            public void onError(QBResponseException e)
            {
                e.printStackTrace();
                GenerikFunctions.hDialog();

            }
        });


    }



}

