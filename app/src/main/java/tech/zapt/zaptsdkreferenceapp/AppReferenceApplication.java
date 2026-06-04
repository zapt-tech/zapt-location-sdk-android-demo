package tech.zapt.zaptsdkreferenceapp;

import android.app.Application;

import java.util.Collection;

import tech.zapt.sdk.location.ZaptSDK;
import tech.zapt.sdk.location.ZaptSDKOptions;
import tech.zapt.sdk.location.beacon.Beacon;
import tech.zapt.sdk.location.beacon.BeaconListener;

public class AppReferenceApplication  extends Application {

	private ZaptSDK locationSDK;

	@Override
	public void onCreate() {
		super.onCreate();
		// uncomment for background

		ZaptSDKOptions sdkOptions = ZaptSDKOptions.getInstance();
		sdkOptions.setBackgroundBetweenScanPeriod(30000L);
		sdkOptions.setDebug(true);
		locationSDK = ZaptSDK.getInstance(this);
		if(!locationSDK.isInitialized()) {
			locationSDK.initialize("-o5l-b9uozvv8jnkemkj");
		}
		locationSDK.addBeaconListener(new BeaconListener() {
			@Override
			public void onScan(Collection<Beacon> collection) {
				System.out.println("onScan: " + collection.size());
//				LocalNotificationManager localNotificationManager = LocalNotificationManager.getInstance(AppReferenceApplication.this);
//				localNotificationManager.sendNotification(MapViewActivity.class, "Encontrou beacon");
			}
		});
	}
}
