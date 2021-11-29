package tech.zapt.zaptsdkreferenceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import tech.zapt.sdk.location.ZaptSDK;
import tech.zapt.sdk.location.ZaptSDKOptions;

public class MapViewActivity extends Activity {

	private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
	private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

	private WebView webView;

	private ZaptSDK zaptSDK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		webView = (WebView) findViewById(R.id.webView);
		initializeZaptSDK();
		startWebView();
	}

	public void initializeZaptSDK() {
		zaptSDK = ZaptSDK.getInstance(this);
		zaptSDK.requestPermissions(this);
		zaptSDK.verifyBluetooth(null, null);
		zaptSDK.setWebViewClient(webView);
		if (!zaptSDK.isInitialized()) {
			zaptSDK.initialize("-md9bemdysyr-5-_hw9v");
		}
	}

	public void startWebView() {
		String url = zaptSDK.getMapLink();

		webView.setWebChromeClient(new WebChromeClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.loadUrl(url);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_FINE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("zapt.tech", "fine location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
			case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("zapt.tech", "background location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
		}
	}
}
