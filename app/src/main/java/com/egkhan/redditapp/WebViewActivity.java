package com.egkhan.redditapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by EgK on 8/23/2017.
 */

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        WebView webView = (WebView) findViewById(R.id.webview);
        final ProgressBar progressBar= (ProgressBar) findViewById(R.id.webviewLoadingProgressBar);
        final TextView textView = (TextView)findViewById(R.id.webpageLoadingTV);
        progressBar.setVisibility(View.VISIBLE);


        Log.d(TAG, "onCreate: started");


        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        });
    }
}
