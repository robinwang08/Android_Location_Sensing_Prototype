package edu.dartmouth.cs.biorhythm.location.collection;

import java.util.ArrayList;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
//import android.widget.Toast;

public class TrackingService extends Service implements LocationListener {

	private double latitude;
	private double longitude;

	// Array-list to hold the X number of locations
	ArrayList<Location> xLocationList;

	float[] curDist = new float[1];
	float distance1;
	float distance2;
	float distance3;

	// Gather 10 locations
	private int counter;
	// Minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
	// Minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0;

	// Number of points to be collected
	private static final int COLLECT_POINTS = 10;
	// Declaring a Location Manager
	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		counter = 0;
		latitude = 0;
		longitude = 0;
		xLocationList = new ArrayList<Location>();

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
				MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Remove further updates
		if (locationManager != null) {
			locationManager.removeUpdates(TrackingService.this);
		}
		super.onDestroy();
	}

	// Check to see if moving, cluster the points and get average
	public void clusterPoints() {

		// Check if moving - using distance and smoothing
		for (int h = 0; h < xLocationList.size() - 2; h++) {

			if (h == 0) {
				Location.distanceBetween(xLocationList.get(h).getLatitude(),
						xLocationList.get(h).getLongitude(),
						xLocationList.get(h + 1).getLatitude(), xLocationList
								.get(h + 1).getLongitude(), curDist);

				distance1 = curDist[0];
				Location.distanceBetween(
						xLocationList.get(h + 1).getLatitude(), xLocationList
								.get(h + 1).getLongitude(),
						xLocationList.get(h + 2).getLatitude(), xLocationList
								.get(h + 2).getLongitude(), curDist);
				// Smoothing
				distance2 = curDist[0];
				distance3 = (distance1 + distance2) / 2;
				continue;
			}

			Location.distanceBetween(xLocationList.get(h + 1).getLatitude(),
					xLocationList.get(h + 1).getLongitude(),
					xLocationList.get(h + 2).getLatitude(),
					xLocationList.get(h + 2).getLongitude(), curDist);

			// Results -- add in weights maybe?
			distance1 = curDist[0];
			distance2 = distance3;
			distance3 = (distance1 + distance2) / 2;
		}

		// Threshold -- whether moving or not, 25m for now
		if (distance3 < 25) {

			for (int i = 0; i < xLocationList.size(); i++) {
				latitude += xLocationList.get(i).getLatitude();
				longitude += xLocationList.get(i).getLongitude();
			}

			latitude /= xLocationList.size();
			longitude /= xLocationList.size();

			GPSDatabase myDatabase = new GPSDatabase(getBaseContext());
			myDatabase.open();

			// Added in time for future calculations
			myDatabase.insertRows(latitude, longitude,
					System.currentTimeMillis());
			myDatabase.close();
		}

		// End the service
		xLocationList.clear();
		this.stopSelf();
	}

	public void addLoc(Location loc) {
		if (loc == null) {
			return;
		}
		// Check accuracy!
		if (loc.getAccuracy() > 50) {
			return;
		}

		// Increment the counter
		counter++;

		// Add the location
		xLocationList.add(loc);

		// Cluster the points and make a significant location
		if (counter >= COLLECT_POINTS) {
			clusterPoints();
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			addLoc(loc);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}