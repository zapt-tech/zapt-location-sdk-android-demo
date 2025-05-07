package tech.zapt.zaptsdkreferenceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tech.zapt.sdk.location.ZaptSDK;
import tech.zapt.sdk.location.beacon.Beacon;
import tech.zapt.sdk.location.beacon.BeaconListener;

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
		startWebView();
		listenBeacon();
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

	public void startWebView() {
		// Add custom options in order to customize map behaviour
		Map<String, String> opts = new HashMap<>();
		opts.put("bottomNavigation", "false");

		// Get map link with the opts
		String url = zaptSDK.getMapLink(opts);

		// init Webview with the URL
		zaptWebView.loadUrl(url);
	}

	/**
	 * Example of usage of listening beacons
	 */
	private void listenBeacon() {
		zaptSDK.addBeaconListener(new BeaconListener() {
			@Override
			public void onScan(Collection<Beacon> collection) {
				for (Beacon beacon : collection) {
					Log.i("zapt.tech", "Beacon Found: " + beacon.getDistance());
				}
			}
		});
	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
