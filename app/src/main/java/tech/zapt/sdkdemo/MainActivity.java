package tech.zapt.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tech.zapt.sdk.location.ZaptSDK;
import tech.zapt.sdk.location.ZaptSDKOptions;
import tech.zapt.sdk.location.ZaptUserInfo;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private static final String visitableId = "-ltvysf4acgzdxdhf81y";

    private ZaptSDK zaptSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);

        initializeZaptSDK();
        String url = zaptSDK.getMapLink();
        startWebView(url);


        ZaptUserInfo userInfo = ZaptUserInfo.getInstance(this);
        userInfo.setUserName("Pedro Nunes");
        userInfo.getData().put("age", "30-40");
        userInfo.getData().put("departament", "IT");
        userInfo.getData().put("externalId", "93417");
        userInfo.commit();

        ZaptSDKOptions options = ZaptSDKOptions.getInstance();

        //enable logging
        options.setDebug(Boolean.TRUE);

        //Interval in ms to send data to the cloud
        options.setSyncInterval(60000);

        //Number of retries if request fails
        options.setHttpRetries(5);
    }

    public void initializeZaptSDK(){
        zaptSDK = ZaptSDK.getInstance(this);
        if(!zaptSDK.isInitialized()){
            zaptSDK.requestPermissions(this);
            zaptSDK.initialize(visitableId);
        }
    }

    public void startWebView(String url) {
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
    }
}