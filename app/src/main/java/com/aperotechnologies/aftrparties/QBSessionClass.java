package com.aperotechnologies.aftrparties;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Host.HostActivity;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Settings.SettingsActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Created by hasai on 20/07/16.
 */
public class QBSessionClass {

    private static QBSessionClass instance;
    Configuration_Parameter m_config;


    public static synchronized QBSessionClass getInstance() {
        if(instance == null) {
            instance = new QBSessionClass();
        }
        return instance;
    }

    private QBSessionClass() {
        m_config = Configuration_Parameter.getInstance();

    }

    public void getQBSession(final QBEntityCallback callback, final String accessToken, final ProgressBar pBar, final Context cont){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(cont);

        QBAuth.createSession(new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle)
            {
                try
                {
                    String qbtoken = QBAuth.getBaseService().getToken();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");
                    if(pBar != null){
                        pBar.setVisibility(View.GONE);
                    }

                    if(pBar == null)//if(cont instanceof HostActivity || cont instanceof SettingsActivity || cont instanceof TransparentActivity)
                    {
                        GenerikFunctions.hDialog();
                    }

                }

                QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, String.valueOf(accessToken),
                        null, new QBEntityCallback<QBUser>()
                        {
                            @Override
                            public void onSuccess(final QBUser user, Bundle args)
                            {
                                try {
                                    user.setPassword(BaseService.getBaseService().getToken());
                                    QBChatService.getInstance().login(user, new QBEntityCallback()
                                    {
                                        @Override
                                        public void onSuccess(Object o, Bundle bundle)
                                        {

                                            Log.e("onSuccess chat","");
                                            SharedPreferences.Editor editor= sharedPreferences.edit();
                                            editor.putString(m_config.QuickBloxID, String.valueOf(user.getId()));
                                            editor.apply();
                                            callback.onSuccess(null, null);

                                        }

                                        @Override
                                        public void onError(QBResponseException e)
                                        {
                                            Log.e("onError chat","");
                                            e.printStackTrace();
                                            callback.onError(e);

                                            GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");
                                            if(pBar != null){
                                                pBar.setVisibility(View.GONE);
                                            }

                                            if(pBar == null)//if(cont instanceof HostActivity || cont instanceof SettingsActivity || cont instanceof TransparentActivity)
                                            {
                                                GenerikFunctions.hDialog();
                                            }


                                        }


                                    });

                                }
                                catch (Exception e){

                                    Log.e("in catch Exception","");

                                    e.printStackTrace();
                                    GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");
                                    if(pBar != null){
                                        pBar.setVisibility(View.GONE);
                                    }

                                    if(pBar == null)//if(cont instanceof HostActivity || cont instanceof SettingsActivity || cont instanceof TransparentActivity)
                                    {
                                        GenerikFunctions.hDialog();
                                    }

                                    callback.onError((QBResponseException) e);
                                }

                            }

                            @Override
                            public void onError(QBResponseException e)
                            {
                                e.printStackTrace();
                                GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");
                                if(pBar != null){
                                    pBar.setVisibility(View.GONE);
                                }

                                if(pBar == null)//cont instanceof HostActivity || cont instanceof SettingsActivity || cont instanceof TransparentActivity)
                                {
                                    GenerikFunctions.hDialog();
                                }

                                callback.onError(e);
                            }
                        });

            }

            @Override
            public void onError(QBResponseException e)
            {
                e.printStackTrace();
                GenerikFunctions.showToast(cont, "There was an error while loading data, please try again after some time.");
                if(pBar != null){
                    pBar.setVisibility(View.GONE);
                }

                if(pBar == null)
                {
                    GenerikFunctions.hDialog();
                }



            }
        });


    }

}
