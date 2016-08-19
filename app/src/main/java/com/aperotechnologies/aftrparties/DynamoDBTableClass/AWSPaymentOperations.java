package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Chats.DialogsActivity;
import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.LocalNotifications.SetLocalNotifications;
import com.aperotechnologies.aftrparties.QuickBloxOperations.QBChatDialogCreation;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;
import com.quickblox.chat.model.QBDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hasai on 10/08/16.
 */
public class AWSPaymentOperations {

    static Configuration_Parameter m_config = Configuration_Parameter.getInstance();


    /**/
    //function for storing chat history retention in PaymentTable
    public static class storechathistoryretention extends AsyncTask<String, Void, Boolean>
    {
        Context cont;
        String DialogId;
        boolean value = false;

        public storechathistoryretention(Context cont, String DialogId) {
            this.cont = cont;
            this.DialogId = DialogId;
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            String facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();
            List DialogIdList = new ArrayList();

            try
            {
                PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class,facebookid);

                if(paymentTable == null || paymentTable.equals(null))
                {

                    DialogIdList.add(DialogId);
                    PaymentTable payTable = new PaymentTable();
                    paymentTable.setFacebookID(facebookid);
                    payTable.setchathistoryretention(DialogIdList);
                    m_config.mapper.save(payTable);
                    value = true;

                }
                else
                {

                    DialogIdList = paymentTable.getchathistoryretention();
                    if(DialogIdList == null || DialogIdList.size() == 0)
                    {
                        DialogIdList = new ArrayList();
                    }
                    DialogIdList.add(DialogId);
                    paymentTable.setchathistoryretention(DialogIdList);
                    m_config.mapper.save(paymentTable);
                    value = true;

                    Log.e("45345345345"," "+DialogIdList);

                }

            }
            catch (Exception e)
            {
                GenerikFunctions.showToast(cont,"Chat history is not retained succesfully");
                GenerikFunctions.hDialog();
                value = false;
            }

            finally
            {
                return  value;
            }
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true){
                GenerikFunctions.showToast(cont,"Chat history is retained succesfully");
                GenerikFunctions.hDialog();

            }
            else
            {
                GenerikFunctions.showToast(cont,"Chat history is not retained succesfully");
                GenerikFunctions.hDialog();
            }

        }
    }
    /**/


    public static class setPartyPurchaseinAWS extends AsyncTask<String, Void, Boolean>
    {

        Long subscriptiondate;
        Context cont;
        String loginUserfbId;
        UserTable user;
        boolean value = false;

        public setPartyPurchaseinAWS(Context cont, Long subscriptiondate, String loginUserfbId)
        {
            this.subscriptiondate = subscriptiondate;
            this.cont = cont;
            this.loginUserfbId = loginUserfbId;
        }


        @Override
        protected Boolean doInBackground(String... params)
        {



            try {

                user = m_config.mapper.load(UserTable.class,loginUserfbId);
                List<PaidGCClass> paidgclist = user.getPaidgc();

                //check whether is paid or unpaid user
                if (paidgclist == null || paidgclist.size() == 0)
                {

                    Log.e(" user is unpaid user"," ");
                    // here user is unpaid user
                    PaidGCClass paidGCClass = new PaidGCClass();
                    paidGCClass.setPaidstatus("Yes");
                    paidGCClass.setSubscriptiondate(String.valueOf(subscriptiondate));
                    paidgclist = new ArrayList<>();
                    paidgclist.add(paidGCClass);
                    user.setPaidgc(paidgclist);
                    m_config.mapper.save(user);
                    value = true;


                } else {
                    Log.e("user is paid user "," ");
                    // here user is paid user
                    PaidGCClass paidGCClass = paidgclist.get(0);
                    paidGCClass.setPaidstatus("Yes");
                    paidGCClass.setSubscriptiondate(String.valueOf(subscriptiondate));
                    paidgclist.set(0, paidGCClass);
                    user.setPaidgc(paidgclist);
                    m_config.mapper.save(user);
                    value = true;


                }
            } catch (Exception e) {

                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                value = false;

            }

            finally {
                return value;
            }
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true)
            {
                Log.e("onPostExecute", " true");
                updactivepartyinAppPurchase(cont, user, String.valueOf(subscriptiondate));

            }else{
                Log.e("onPostExecute", " false");
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
            }

        }
    }

    private static void updactivepartyinAppPurchase(Context cont, UserTable user, String subscriptiondate){

        Log.e("updactivepartyinAppPurchase"," "+user.getActiveparty() );

        List<ActivePartyClass> activepartyList = user.getActiveparty();

        if (activepartyList == null ||  activepartyList.equals(null) || activepartyList.size() == 0)
        {
            Log.e("ActivePartyList", "null");
            new AWSPaymentOperations.storeGCMultipleParties(cont,subscriptiondate).execute();

        }
        else
        {

            Log.e("ActivePartyList", "not null");

            //if there is an active party update endblocktime of active party
            ActivePartyClass activeParty = activepartyList.get(0);

            String EndBlockTime = "";
            if ((Long.parseLong(activeParty.getStartblocktime()) + ConstsCore.hourVal) > Long.parseLong(activeParty.getEndtime())) {
                EndBlockTime = activeParty.getEndtime();

            } else {

                EndBlockTime = String.valueOf(Long.parseLong(activeParty.getStarttime()) + ConstsCore.hourVal);
            }


            activeParty.setPartyid(activeParty.getPartyid());
            activeParty.setPartyname(activeParty.getPartyname());
            activeParty.setStarttime(activeParty.getStarttime());
            activeParty.setEndtime(activeParty.getEndtime());
            activeParty.setPartystatus(activeParty.getPartystatus());
            activeParty.setStartblocktime(activeParty.getStartblocktime());
            activeParty.setEndblocktime(EndBlockTime);
            activepartyList.set(0, activeParty);
            user.setActiveparty(activepartyList);
            m_config.mapper.save(user);

            new AWSPaymentOperations.storeGCMultipleParties(cont,subscriptiondate).execute();


        }

    }


    public static class setInAppPurchaseinAWSUser extends AsyncTask<String, Void, Boolean>
    {

        boolean value = false;
        Long subscriptiondate;
        String  loginUserfbId;
        Context cont;
        CheckBox cb_mask, cb_unmask;
        UserTable user;

        public setInAppPurchaseinAWSUser(Context cont, Long subscriptiondate, String loginUserfbId, CheckBox cb_mask, CheckBox cb_unmask) {
            this.subscriptiondate = subscriptiondate;
            this.loginUserfbId = loginUserfbId;
            this.cont = cont;
            this.cb_mask = cb_mask;
            this.cb_unmask = cb_unmask;
        }


        @Override
        protected Boolean doInBackground(String... params)
        {

            try {

                user = m_config.mapper.load(UserTable.class,loginUserfbId);

                List<PartyMaskStatusClass> partymaskstatuslist = user.getPartymaskstatus();
                Log.e("partymaskstatuslist"," "+partymaskstatuslist+" "+m_config.masksubscriptionTime);

                if (partymaskstatuslist == null || partymaskstatuslist.size() == 0) {
                    Log.e("in null"," ");

                    PartyMaskStatusClass partymaskstatus = new PartyMaskStatusClass();
                    partymaskstatus.setMaskstatus("Unmask");
                    partymaskstatus.setMasksubscriptiondate(String.valueOf(subscriptiondate));
                    m_config.masksubscriptionTime = String.valueOf(subscriptiondate);
                    partymaskstatuslist = new ArrayList<>();
                    partymaskstatuslist.add(partymaskstatus);
                    user.setPartymaskstatus(partymaskstatuslist);
                    m_config.mapper.save(user);
                    value = true;


                }
                else
                {

                    Log.e("not null"," ");
                    PartyMaskStatusClass partymaskstatus = partymaskstatuslist.get(0);
                    partymaskstatus.setMaskstatus("Unmask");
                    partymaskstatus.setMasksubscriptiondate(String.valueOf(subscriptiondate));
                    m_config.masksubscriptionTime = String.valueOf(subscriptiondate);
                    partymaskstatuslist.set(0, partymaskstatus);
                    user.setPartymaskstatus(partymaskstatuslist);
                    m_config.mapper.save(user);
                    value = true;


                }
            }
            catch(Exception e)
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                value = false;

            }

            finally {

                return value;

            }
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true)
            {
                Log.e("came here"," ");
                new AWSPaymentOperations.storePartymaskstatus(cont, m_config.masksubscriptionTime).execute();
                Handler h = new Handler();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if(cb_mask != null  || cb_unmask != null ) {
                            cb_unmask.setChecked(true);
                            cb_mask.setChecked(false);
                            cb_unmask.setEnabled(false);
                            cb_mask.setEnabled(false);
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
            }

        }
    }

    /***/

    //function for storing party mask status in paymentTable
    public static class storePartymaskstatus extends AsyncTask<String, Void, Boolean>
    {

        Context cont;
        String subscriptiondate;
        boolean value = false;
        String facebookid;

        public storePartymaskstatus(Context cont, String subscriptiondate) {
            this.cont = cont;
            this.subscriptiondate = subscriptiondate;
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

            try
            {
                PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class,facebookid);

                if(paymentTable == null || paymentTable.equals(null))
                {
                    paymentTable = new PaymentTable();
                    List<PartyMaskStatusClass> partyMaskStatuslist = new ArrayList<>();
                    PartyMaskStatusClass partyMaskStatus = new PartyMaskStatusClass();
                    partyMaskStatus.setMaskstatus("Unmask");
                    partyMaskStatus.setMasksubscriptiondate(subscriptiondate);
                    partyMaskStatuslist.add(partyMaskStatus);
                    paymentTable.setFacebookID(facebookid);
                    paymentTable.setPartymaskstatus(partyMaskStatuslist);
                    m_config.mapper.save(paymentTable);
                    value = true;

                }
                else
                {
                    List<PartyMaskStatusClass> partyMaskStatuslist = paymentTable.getPartymaskstatus();
                    if(partyMaskStatuslist == null || partyMaskStatuslist.size() == 0)
                    {
                        PartyMaskStatusClass partymaskstatus = new PartyMaskStatusClass();
                        partymaskstatus.setMaskstatus("Unmask");
                        partymaskstatus.setMasksubscriptiondate(subscriptiondate);
                        partyMaskStatuslist = new ArrayList<>();
                        partyMaskStatuslist.add(partymaskstatus);
                        paymentTable.setPartymaskstatus(partyMaskStatuslist);
                        m_config.mapper.save(paymentTable);
                        value = true;

                    }
                    else
                    {
                        PartyMaskStatusClass partymaskstatus = partyMaskStatuslist.get(0);
                        partymaskstatus.setMaskstatus("Unmask");
                        partymaskstatus.setMasksubscriptiondate(subscriptiondate);
                        partyMaskStatuslist.set(0, partymaskstatus);
                        paymentTable.setPartymaskstatus(partyMaskStatuslist);
                        m_config.mapper.save(paymentTable);
                        value = true;
                    }

                }

            }
            catch (Exception e)
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                value = false;
            }


            finally {
                return  value;
            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true)
            {
                Toast.makeText(cont, "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                SetLocalNotifications.setLNotificationforPartyMaskStatus(cont, Long.parseLong(subscriptiondate), facebookid);

            }
            else
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
            }

        }
    }
    /***/

    /****/

    //function for storing party mask status in paymentTable
    public static class storeGCMultipleParties extends AsyncTask<String, Void, Boolean>
    {

        Context cont;
        String subscriptiondate;
        boolean value = false;
        String facebookid;

        public storeGCMultipleParties(Context cont, String subscriptiondate) {
            this.cont = cont;
            this.subscriptiondate = subscriptiondate;
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

            try
            {
                PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class,facebookid);

                if(paymentTable == null || paymentTable.equals(null))
                {
                    paymentTable = new PaymentTable();
                    List<PaidGCClass> paidGCList = new ArrayList<>();
                    PaidGCClass paidGC = new PaidGCClass();
                    paidGC.setPaidstatus("Yes");
                    paidGC.setSubscriptiondate(subscriptiondate);
                    paidGCList.add(paidGC);
                    paymentTable.setFacebookID(facebookid);
                    paymentTable.setPaidgc(paidGCList);
                    m_config.mapper.save(paymentTable);
                    value = true;

                }
                else
                {
                    List<PaidGCClass> paidGCList = paymentTable.getPaidgc();
                    if(paidGCList == null || paidGCList.size() == 0)
                    {
                        PaidGCClass paidGC = new PaidGCClass();
                        paidGC.setPaidstatus("Yes");
                        paidGC.setSubscriptiondate(subscriptiondate);
                        paidGCList = new ArrayList<>();
                        paidGCList.add(paidGC);
                        paymentTable.setPaidgc(paidGCList);
                        m_config.mapper.save(paymentTable);
                        value = true;

                    }
                    else
                    {
                        PaidGCClass paidGC = paidGCList.get(0);
                        paidGC.setPaidstatus("Yes");
                        paidGC.setSubscriptiondate(subscriptiondate);
                        paidGCList.set(0, paidGC);
                        paymentTable.setPaidgc(paidGCList);
                        m_config.mapper.save(paymentTable);
                        value = true;
                    }

                }

            }
            catch (Exception e)
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                value = false;
            }


            finally {
                return  value;
            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true)
            {
                Toast.makeText(cont, "Data is saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
                SetLocalNotifications.setLNotificationforGcMultipleParties(cont, Long.parseLong(subscriptiondate), facebookid);

            }
            else
            {
                Toast.makeText(cont, "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
                GenerikFunctions.hDialog();
            }

        }
    }
    /***/

    /****/

    //function for storing private chat data in Payment table
    public static class storePrivateChat extends AsyncTask<String, Void, Boolean>
    {

        Context cont;
        Long subscriptiondate;
        String dialogId;
        String oppFbId;
        String msg;
        boolean value = false;
        String facebookid;
        QBDialog dialogs;

        public storePrivateChat(Context cont, Long subscriptiondate, String dialogId, String oppFbId, QBDialog dialogs, String msg) {
            this.cont = cont;
            this.subscriptiondate = subscriptiondate;
            this.dialogId = dialogId;
            this.oppFbId = oppFbId;
            this.msg = msg;
            this.dialogs = dialogs;
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            facebookid = LoginValidations.initialiseLoggedInUser(cont).getFB_USER_ID();

            try
            {
                PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class,facebookid);

                if(paymentTable == null || paymentTable.equals(null))
                {
                    paymentTable = new PaymentTable();
                    List<PrivateChatClass> privateChatList = new ArrayList<>();
                    PrivateChatClass privatechat = new PrivateChatClass();
                    privatechat.setFbid(oppFbId);
                    privatechat.setSubscriptiondate(String.valueOf(subscriptiondate));
                    privatechat.setDialogId(dialogId);
                    privateChatList.add(privatechat);
                    paymentTable.setFacebookID(facebookid);
                    paymentTable.setPrivatechat(privateChatList);
                    m_config.mapper.save(paymentTable);
                    value = true;

                }
                else
                {
                    List<PrivateChatClass> privateChatList = paymentTable.getPrivatechat();
                    if(privateChatList == null || privateChatList.size() == 0)
                    {
                        PrivateChatClass privatechat = new PrivateChatClass();
                        privatechat.setFbid(oppFbId);
                        privatechat.setSubscriptiondate(String.valueOf(subscriptiondate));
                        privatechat.setDialogId(dialogId);
                        privateChatList.add(privatechat);
                        paymentTable.setPrivatechat(privateChatList);
                        m_config.mapper.save(paymentTable);
                        value = true;

                    }
                    else
                    {

                        for(int i = 0; i < privateChatList.size(); i++)
                        {
                            if(privateChatList.get(i).getFbid().equals(oppFbId))
                            {
                                PrivateChatClass privatechat = new PrivateChatClass();
                                privatechat.setFbid(oppFbId);
                                privatechat.setSubscriptiondate(String.valueOf(subscriptiondate));
                                privatechat.setDialogId(dialogId);
                                privateChatList.set(i, privatechat);
                                paymentTable.setPrivatechat(privateChatList);
                                m_config.mapper.save(paymentTable);
                                value = true;
                                break;
                            }
                        }


                    }

                }

            }
            catch (Exception e)
            {
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont, msg);
                value = false;
            }


            finally {
                return  value;
            }

        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean v)
        {
            if(v == true)
            {
                GenerikFunctions.hDialog();
                GenerikFunctions.showToast(cont, msg);
                String loginUserFbId = facebookid;
                if(msg.equals("1-1 Chat Created Successfully")) {
                    QBChatDialogCreation.openPChat(cont, dialogs);
                }else{
                    Intent intent = new Intent(cont, DialogsActivity.class);
                    cont.startActivity(intent);
                }
                SetLocalNotifications.setLNotificationforPrivateChat(cont, subscriptiondate, dialogId, oppFbId, loginUserFbId);
            }
            else
            {
                GenerikFunctions.hDialog();
                if(msg.equals("1-1 Chat Created Successfully"))
                {
                    GenerikFunctions.showToast(cont, "Unable to create 1-1 chat, Please try again after some time");
                }else{
                    GenerikFunctions.showToast(cont, "Unable to update 1-1 chat, Please try again after some time");
                }

            }

        }
    }
    /****/


    /****/

    // remove data from AWS PaymentTable once chat is deleted
    public static void deletePrivateChatData(String facebookid, String oppfbid)
    {
        try
        {
            PaymentTable paymentTable = m_config.mapper.load(PaymentTable.class, facebookid);
            if(paymentTable != null)
            {
                List<PrivateChatClass> privateChatlist = paymentTable.getPrivatechat();
                if(privateChatlist != null || privateChatlist.size() != 0)
                {
                    for (int i = 0; i < privateChatlist.size(); i++)
                    {
                        if(privateChatlist.get(i).getFbid().equals(oppfbid))
                        {
                            privateChatlist.remove(i);
                            paymentTable.setPrivatechat(privateChatlist);
                            m_config.mapper.save(paymentTable);
                            break;

                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }






}

