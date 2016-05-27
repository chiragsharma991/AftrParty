package com.aperotechnologies.aftrparties.InAppPurchase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aperotechnologies.aftrparties.R;
import com.aperotechnologies.aftrparties.util.IabHelper;
import com.aperotechnologies.aftrparties.util.IabResult;
import com.aperotechnologies.aftrparties.util.Inventory;
import com.aperotechnologies.aftrparties.util.Purchase;

/**
 * Created by hasai on 25/05/16.
 */
public class InAppBillingActivity extends Activity {

        private static final String TAG = "com.aperotechnologies.aftrparties.InAppPurchase.inappbilling";
        IabHelper mHelper;

        static final String ITEM_SKU = "ap_privat_chat";//"android.test.purchased";//

        private Button clickButton;
        private Button buyButton;

        @Override
        protected void onStart() {
            super.onStart();
            setContentView(R.layout.pay);
            buyButton = (Button) findViewById(R.id.buyButton);
            clickButton = (Button) findViewById(R.id.clickButton);
            clickButton.setEnabled(false);

            String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqiS7HW5sBPoBBxCNFf1OGtRMPVzuzqhQOq6T1ZBuO6PMHzpAaiVzVZfccoUmc0Lj/Wyu4lpaK9H+aQy3oEKS9EbyQQ6GVKFLFOCFo8w8TP/Z5U14oiTvmpnLDLxhCeKRbMow+Z04hhgbnezHsikIPkULezF6psxrZ6kvzlT23pg91Yn2y/kgiLJMnxpj2G1RUfVaFlVXA/SM23yWYkK1qeTxeoZ5l36TfPjazWN8TXHmQvPX9SVTWg0tnTtxDVNfNvRMUsLd9t75JGhRimqhhgEGleQtiMZYasnmGyvWa3QGSN5CjtzW6aNocC4Cw3DcnIzM/tcriK6M75PpRMXVpwIDAQAB";
            //"<your license key here>";

            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.e(TAG, "In-app Billing setup failed: " +
                                result);
                    } else {
                        Log.e(TAG, "In-app Billing is set up OK");
                    }
                }
            });
        }



        public void buyClick(View view) {
            try {

                ///10001 - is requestCode
                mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");

            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode,
                                        Intent data)
        {

            Log.e("inside onActivityResult"," "+!mHelper.handleActivityResult(requestCode,
                    resultCode, data));

            if (!mHelper.handleActivityResult(requestCode,
                    resultCode, data)) {

                Log.e("inside mHelper.handleActivityResult"," ");


                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase)
            {
//            // if we were disposed of in the meantime, quit.
//            if (mHelper == null) return;
//
//
            if (result.isFailure()) {
                // Handle error
                Log.e("Error purchasing:"," ---- " + result+" ");
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                Log.e("---- "," ");

                Log.e("Purchase Success"," "+purchase.getToken());
                consumeItem();
                buyButton.setEnabled(false);
            }


//                Log.d(TAG, "Purchase finished: " + result + ", purchase: "
//                        + purchase);
//
//                if (result.isFailure()) {
//                    Log.e("Error purchasing:"," ---- " + result);
//
//                    return;
//                }
//
//                if (purchase.getSku().equals(ITEM_SKU)) {
//                    // remove query inventory method from here and put consumeAsync() directly
//
//                    Log.e("Purchase Success"," "+purchase.getToken());
//
//                    try {
//                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//                    } catch (IabHelper.IabAsyncInProgressException e) {
//                        e.printStackTrace();
//                    }
//
//                }

            }
        };


    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
            Log.e("consumeItem ","try");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e("consumeItem ","catch");
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure()) {
                // Handle failure
                Log.e("QueryInventory ","failed");
            } else {
                try {
                    Log.e("QueryInventory ","consumeAsync");
                    mHelper.consumeAsync(inv.getPurchase(ITEM_SKU),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e("QueryInventory ","catch");
                    e.printStackTrace();
                }
            }
        }


    };


        IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
                new IabHelper.OnConsumeFinishedListener() {
                    public void onConsumeFinished(Purchase purchase,
                                                  IabResult result) {
                        // if we were disposed of in the meantime, quit.
                        if (mHelper == null) return;

                        if (result.isSuccess()) {
                            clickButton.setEnabled(true);
                            Log.e("--- "," "+"consume success");
                        } else {
                            // handle error
                            Log.e("--- "," "+"consume failed");
                        }
                    }
                };


        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mHelper != null) try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
    }




//keytool -exportcert -alias aftrparties -keystore Users/hasai/Documents/Harshada/AftrParties/Docs/AftrPartiesKey/aftrpartieskeystore.jks | openssl sha1 -binary | openssl base64

//U2a248y9BNwUWs2YwdA7v3w3Do8=