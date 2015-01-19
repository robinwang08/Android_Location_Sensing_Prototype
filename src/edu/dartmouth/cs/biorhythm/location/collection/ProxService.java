package edu.dartmouth.cs.biorhythm.location.collection;

import java.util.ArrayList;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

public class ProxService extends Service {

	public ArrayList<Location> proxsiglocList;
	private static final long POINT_RADIUS = 75; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;
	private static final String PROX_ALERT_INTENT = "edu.dartmouth.testinglocation.ProximityAlert";
	public final static int SERV_ALARM_HOURS = 24;
	private IntentFilter mLocationUpdateFilter;
	final static String ACTION = "NotifyServiceAction";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		proxsiglocList = new ArrayList<Location>();

		// Set up an alarm to call up a Clustering Service, which does the
		// clustering every X hours
		Intent clusterIntent = new Intent(this, ClusterService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0,
				clusterIntent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * 60 * SERV_ALARM_HOURS, pintent);

		// The clustering service will broadcast to this service to set up
		// proximity alerts
		mLocationUpdateFilter = new IntentFilter();
		mLocationUpdateFilter.addAction(ACTION);
		registerReceiver(mLocationUpdateReceiver, mLocationUpdateFilter);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Receiver for the broadcasts from the Cluster Service
	private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// Receive an Arraylist of significant locations from the Cluster
			// Service and add an proximity alert for each signicant location in
			// that list
			proxsiglocList = intent.getParcelableArrayListExtra("Locations");
			for (int l = 0; l < proxsiglocList.size(); l++) {
				addProximityAlert(proxsiglocList.get(l).getLatitude(),
						proxsiglocList.get(l).getLongitude());
			}
			proxsiglocList.clear();
		}
	};

	public void addProximityAlert(double latitude, double longitude) {
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent intent = new Intent(PROX_ALERT_INTENT + latitude + longitude);
		intent.putExtra("lat", latitude);
		intent.putExtra("lng", longitude);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		locManager.addProximityAlert(latitude, longitude, POINT_RADIUS,
				PROX_ALERT_EXPIRATION, proximityIntent);
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT + latitude
				+ longitude);
		registerReceiver(new ProximityIntentReceiver(), filter);
	}

}