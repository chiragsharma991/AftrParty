package com.aperotechnologies.aftrparties.DynamoDBTableClass;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.aperotechnologies.aftrparties.Constants.Configuration_Parameter;
import com.aperotechnologies.aftrparties.Constants.ConstsCore;
import com.aperotechnologies.aftrparties.Reusables.GenerikFunctions;
import com.aperotechnologies.aftrparties.Reusables.LoginValidations;
import com.aperotechnologies.aftrparties.Reusables.Validations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasai on 10/08/16.
 */
public class AWSPaymentOperations {

    static Configuration_Parameter m_config = Configuration_Parameter.getInstance();


    /**/
    //function for storing chat history retention
    public static class storechathistoryretention extends AsyncTask<String, Void, Void>
    {
        Context cont;
        String DialogId;

        public storechathistoryretention(Context cont, String DialogId) {
            this.cont = cont;
            this.DialogId = DialogId;
        }

        @Override
        protected Void doInBackground(String... params)
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
                    payTable.setchathistoryretention(DialogIdList);
                    m_config.mapper.save(payTable);
                    GenerikFunctions.showToast(cont,"Chat history is retained succesfully");
                    GenerikFunctions.hDialog();
                }
                else
                {
                    DialogIdList = paymentTable.getchathistoryretention();
                    DialogIdList.add(DialogId);
                    paymentTable.setchathistoryretention(DialogIdList);
                    m_config.mapper.save(paymentTable);
                    GenerikFunctions.showToast(cont,"Chat history is retained succesfully");
                    GenerikFunctions.hDialog();

                }

            }
            catch (Exception e)
            {
                GenerikFunctions.showToast(cont,"Chat history is not retained succesfully");
                GenerikFunctions.hDialog();
            }

            return null;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void v)
        {


        }
    }
    /**/

//    private class store extends AsyncTask<String, Void, Void>
//    {
//
//        @Override
//        protected Void doInBackground(String... params)
//        {
//
//            Long subVal = Validations.getCurrentTime() + ConstsCore.FifteenDayVal;
//            List<PartyMaskStatusClass> partymaskstatuslist = user.getPartymaskstatus();
//
//            try {
//                if (partymaskstatuslist == null || partymaskstatuslist.size() == 0) {
//                    PartyMaskStatusClass partymaskstatus = new PartyMaskStatusClass();
//                    partymaskstatus.setMaskstatus("Unmask");
//                    partymaskstatus.setMasksubscriptiondate(String.valueOf(subVal));
//                    masksubscriptionTime = String.valueOf(subVal);
//                    partymaskstatuslist = new ArrayList<>();
//                    partymaskstatuslist.add(partymaskstatus);
//                    user.setPartymaskstatus(partymaskstatuslist);
//                    m_config.mapper.save(user);
//                    Toast.makeText(getApplicationContext(),"Data is saved Successfully", Toast.LENGTH_SHORT).show();
//                    GenerikFunctions.hDialog();
//
//
//                    Handler h = new Handler();
//                    h.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            cb_unmask.setChecked(true);
//                            cb_mask.setChecked(false);
//                            cb_unmask.setEnabled(false);
//                            cb_mask.setEnabled(false);
//                        }
//                    });
//
//
//                }
//                else
//                {
//                    //
//                    PartyMaskStatusClass partymaskstatus = partymaskstatuslist.get(0);
//                    partymaskstatus.setMaskstatus("Unmask");
//                    partymaskstatus.setMasksubscriptiondate(String.valueOf(subVal));
//                    masksubscriptionTime = String.valueOf(subVal);
//                    partymaskstatuslist.add(0, partymaskstatus);
//                    user.setPartymaskstatus(partymaskstatuslist);
//                    m_config.mapper.save(user);
//                    Toast.makeText(getApplicationContext(), "Data is updated Successfully", Toast.LENGTH_SHORT).show();
//                    GenerikFunctions.hDialog();
//
//
//                    Handler h = new Handler();
//                    h.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            cb_unmask.setChecked(true);
//                            cb_mask.setChecked(false);
//                            cb_unmask.setEnabled(false);
//                            cb_mask.setEnabled(false);
//                        }
//                    });
//                }
//            }
//            catch(Exception e)
//            {
//                Toast.makeText(getApplicationContext(), "Data is not saved Successfully", Toast.LENGTH_SHORT).show();
//                GenerikFunctions.hDialog();
//
//            }
//            return null;
//        }
//
//
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Void v)
//        {
//
//
//        }
//    }


    /**/









}
