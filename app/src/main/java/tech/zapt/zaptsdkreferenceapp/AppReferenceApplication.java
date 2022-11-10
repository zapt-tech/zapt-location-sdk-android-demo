package tech.zapt.zaptsdkreferenceapp;

import android.app.Application;

import java.util.Collection;

import tech.zapt.sdk.location.ZaptSDK;
import tech.zapt.sdk.location.beacon.Beacon;
import tech.zapt.sdk.location.beacon.BeaconListener;

public class AppReferenceApplication  extends Application {

	private ZaptSDK locationSDK;

	@Override
	public void onCreate() {
		super.onCreate();
		// uncomment for background
		locationSDK = ZaptSDK.getInstance(this);
		if(!locationSDK.isInitialized()) {
			locationSDK.initialize("-mljkeqchm-ea2sxrou8");
		}
		locationSDK.addBeaconListener(new BeaconListener() {
			@Override
			public void onScan(Collection<Beacon> collection) {
				LocalNotificationManager localNotificationManager = LocalNotificationManager.getInstance(AppReferenceApplication.this);
				localNotificationManager.sendNotification(MapViewActivity.class, "Encontrou beacon");
			}
		});
	}
}
