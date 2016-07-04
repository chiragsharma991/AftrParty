package com.aperotechnologies.aftrparties.History;

/**
 * Created by hasai on 24/09/15.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.aperotechnologies.aftrparties.R;

public class FBProfileWebView extends Activity {

    //private Button button;
    private WebView webView;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_webview);

        //Get webview
        webView = (WebView) findViewById(R.id.webView1);
        Intent i = getIntent();
        String link = i.getExtras().getString("fbUrl");
        startWebView(link);

    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {


            //If you will not use this method url links will open in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }



        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        */

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webView.loadUrl(url);


    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }





}
