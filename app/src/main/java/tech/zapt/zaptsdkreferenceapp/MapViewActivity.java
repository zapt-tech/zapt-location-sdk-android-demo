package tech.zapt.zaptsdkreferenceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.util.HashMap;
import java.util.Map;

import tech.zapt.sdk.location.ZaptSDK;

public class MapViewActivity extends Activity {

	private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
	private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

	private WebView zaptWebView;
	private ZaptSDK zaptSDK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		zaptWebView = (WebView) findViewById(R.id.webView);
		initializeZaptSDK();
		configureInstallReferrer();
	}


	public void initializeZaptSDK() {
		zaptSDK = ZaptSDK.getInstance(this.getApplicationContext());
		zaptSDK.requestPermissions(this);
		if(!zaptSDK.isBluetoothEnabled()) {
			// add your message asking user to enable or enable it yourself.
			// zaptSDK has this method that adds a default alert message. But it's preferred to use yours.
			zaptSDK.verifyBluetoothAndCreateAlert(null, null, this);

		}
		if (!zaptSDK.isInitialized()) {
			zaptSDK.initialize("-ol4g6xderbaq83rmhve");
		}
	}

	private void configureInstallReferrer(){
		final InstallReferrerClient referrerClient;
		referrerClient = InstallReferrerClient.newBuilder(this).build();
		referrerClient.startConnection(new InstallReferrerStateListener() {
			@Override
			public void onInstallReferrerSetupFinished(int responseCode) {
				switch (responseCode) {
					case InstallReferrerClient.InstallReferrerResponse.OK:
						ReferrerDetails response = null;
						try {
							response = referrerClient.getInstallReferrer();
							String referrerUrl = response.getInstallReferrer();
							boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
							startWebView(referrerUrl);
						} catch (RemoteException e) {
							startWebView(null);
						}
						break;
					case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
						// API not available on the current Play Store app.
						startWebView(null);
						break;
					case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
						// Connection couldn't be established.
						startWebView(null);
						break;
				}
			}

			@Override
			public void onInstallReferrerServiceDisconnected() {
				// Try to restart the connection on the next request to
				// Google Play by calling the startConnection() method.
			}
		});
	}

	public void startWebView(String paramsStr) {
		// Add custom options in order to customize map behaviour
		Map<String, String> opts = new HashMap<>();
		opts.put("bottomNavigation", "false");

		if(paramsStr != null) {
			String [] params = paramsStr.split("&");
			for(String param : params) {
				String [] paramKeyValue = param.split("=");
				if(!"placeId".equals(paramKeyValue[0])){
					opts.put(paramKeyValue[0], paramKeyValue[1]);
				}
			}
		}

		String urlSuffix = handleIntent(getIntent());

		// Get map link with the opts
		String url = zaptSDK.getMapLink(opts);

		if(urlSuffix != null && !urlSuffix.isEmpty()) {
			if(!url.contains("?")) {
				url+="?";
			}
			if(!urlSuffix.startsWith("&") && !url.endsWith("&")) {
				urlSuffix="&" + urlSuffix;
			}
			url+=urlSuffix;
		}

		// init Webview with the URL
		zaptWebView.loadUrl(url);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String urlSuffix = handleIntent(intent);
		zaptWebView.loadUrl(urlSuffix);
	}

	private String handleIntent(Intent intent) {
		String appLinkAction = intent.getAction();
		Uri appLinkData = intent.getData();
		if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
			String url = appLinkData.toString()
				.replace("https://app.zapt.tech/instant/viracopos/", "")
				.replace("https://app.zapt.tech/instant/viracopos", "")
				.replace("?", "");
			return url;
		}
		return "";
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_FINE_LOCATION: {
				if(grantResults == null || grantResults.length == 0) {
					Log.d("zapt.tech", "No permission granted yet");
				} else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
