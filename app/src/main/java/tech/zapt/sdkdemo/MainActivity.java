package tech.zapt.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import tech.zapt.sdk.location.ZaptSDK;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private static final String visitableId = "-ltvysf4acgzdxdhf81y";

    private ZaptSDK zaptSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getActionBar().setIcon(R.drawable.ic_stat_name);

//        visitableId = UserInfo.getInstance(this).getVisitableId();

        webView = (WebView) findViewById(R.id.webView);

        initializeZaptSDK();
        String url = zaptSDK.getMapLink();
        startWebView(url);
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