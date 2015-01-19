package edu.dartmouth.cs.biorhythm.location.collection;
/*package edu.dartmouth.stresscalendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;

public class Cluster extends Activity {

	float[] curDist = new float[1];
	float distance;
	float distance1;
	public ArrayList<Location> locList;
	public ArrayList<Location> siglocList;
	public ArrayList<Location> dbsiglocList;
	private static final long POINT_RADIUS = 75; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;
	private static final String PROX_ALERT_INTENT = "edu.dartmouth.testinglocation.ProximityAlert";
	public int prevProxAlerts;
	public static final int LONG_ENOUGH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// intialization
		locList = new ArrayList<Location>();
		siglocList = new ArrayList<Location>();
		dbsiglocList = new ArrayList<Location>();

		prevProxAlerts = getDBcount();
		transferDB();

		runCluster();
		eliminateDuplicates();
		inserttoDB();
		setAlerts();
	}

	public int getDBcount() {
		int count = 0;
		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		count = cursor.getCount();
		myDatabase.close();
		return count;
	}

	public void transferDB() {
		GPSDatabase myDatabase = new GPSDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			Location tempLocation = new Location("dummyprovider");
			tempLocation.setLatitude(cursor.getDouble(1));
			tempLocation.setLongitude(cursor.getDouble(2));
			locList.add(tempLocation);
			cursor.moveToNext();
		}

		myDatabase.del();
		myDatabase.close();
	}

	// start the clustering
	public void runCluster() {

		int tenMinutes = 0;
		double latitude = 0.0;
		double longitude = 0.0;
		int removeCounter = 0;
		int i = 0;

		while (locList.size() > 1) {
			for (int j = 1; j < locList.size(); j++) {

				Location.distanceBetween(locList.get(i).getLatitude(), locList
						.get(i).getLongitude(), locList.get(j).getLatitude(),
						locList.get(j).getLongitude(), curDist);
				distance = curDist[0];

				// set threshold
				// make more efficient - take out all at once (the i's)
				if (distance < 100) {
					// locations are similar
					tenMinutes++;
				} else {
					locList.remove(i);
					break;
				}

				if (j == locList.size() - 1 && tenMinutes < LONG_ENOUGH) {
					while (j >= 0) {
						locList.remove(i);
						j--;
					}
					break;
				}

				if (j == locList.size() - 1 && tenMinutes >= LONG_ENOUGH) {
					removeCounter = j;

					for (int k = i; k <= j; k++) {
						latitude += locList.get(k).getLatitude();
						longitude += locList.get(k).getLongitude();
					}

					while (removeCounter > 0) {
						locList.remove(i);
						removeCounter--;
					}
					latitude /= (j - i + 1);
					longitude /= (j - i + 1);
					Location temploc = new Location("dummyprovider");
					temploc.setLatitude(latitude);
					temploc.setLongitude(longitude);
					siglocList.add(temploc);
					latitude = 0;
					longitude = 0;
					tenMinutes = 0;
					break;
				}

				Location.distanceBetween(locList.get(i).getLatitude(), locList
						.get(i).getLongitude(), locList.get(j + 1)
						.getLatitude(), locList.get(j + 1).getLongitude(),
						curDist);
				distance1 = curDist[0];

				if ((tenMinutes >= LONG_ENOUGH && distance1 > 100)) {
					removeCounter = j;

					for (int k = i; k <= j; k++) {
						latitude += locList.get(k).getLatitude();
						longitude += locList.get(k).getLongitude();
					}

					while (removeCounter > 0) {
						locList.remove(i);
						removeCounter--;
					}
					latitude /= (j - i + 1);
					longitude /= (j - i + 1);
					Location temploc = new Location("dummyprovider");
					temploc.setLatitude(latitude);
					temploc.setLongitude(longitude);
					siglocList.add(temploc);
					latitude = 0;
					longitude = 0;
					tenMinutes = 0;
					break;
				}
			}
		}

		for (int y = 0; y < siglocList.size(); y++) {
			writeToExternalStoragePublic("siglocList1.txt", y, siglocList
					.get(y).getLatitude(), siglocList.get(y).getLongitude());
		}

		locList.clear();
	}

	// add in the duplicate remover for the database
	public void eliminateDuplicates() {

		// need to remove the ones that match with previous sig locations

		if (getDBcount() > 0) {
			LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
			myDatabase.open();
			Cursor cursor = myDatabase.getAllRows();
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				Location tempLocation = new Location("dummyprovider");
				tempLocation.setLatitude(cursor.getDouble(1));
				tempLocation.setLongitude(cursor.getDouble(2));
				dbsiglocList.add(tempLocation);
				cursor.moveToNext();
			}
			myDatabase.close();
		}

		for (int i = 0; i < siglocList.size(); i++) {
			for (int j = i + 1; j < siglocList.size(); j++) {
				Location.distanceBetween(siglocList.get(i).getLatitude(),
						siglocList.get(i).getLongitude(), siglocList.get(j)
								.getLatitude(), siglocList.get(j)
								.getLongitude(), curDist);
				distance = curDist[0];
				// set threshold
				if (distance < 50) {
					// Locations are similar
					siglocList.remove(j);
					j--;
					continue;
				}
			}
		}

		for (int i = 0; i < dbsiglocList.size(); i++) {
			for (int j = 0; j < siglocList.size(); j++) {
				Location.distanceBetween(siglocList.get(j).getLatitude(),
						siglocList.get(j).getLongitude(), dbsiglocList.get(i)
								.getLatitude(), dbsiglocList.get(i)
								.getLongitude(), curDist);
				distance = curDist[0];
				// set threshold
				if (distance < 50) {
					// Locations are similar
					siglocList.remove(j);
					j--;
					continue;
				}
			}
		}

		for (int i = 0; i < siglocList.size(); i++) {
			writeToExternalStoragePublic("siglocList2.txt", i, siglocList
					.get(i).getLatitude(), siglocList.get(i).getLongitude());
		}

		dbsiglocList.clear();
	}

	// set up database to hold survey labels
	public void inserttoDB() {
		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		for (int i = 0; i < siglocList.size(); i++) {
			myDatabase.insertRows(siglocList.get(i).getLatitude(), siglocList
					.get(i).getLongitude(), "null", 0, "null", "null", "null");
		}
		myDatabase.close();
		}

	public void setAlerts() {
		for (int l = 0; l < siglocList.size(); l++) {
			addProximityAlert(siglocList.get(l).getLatitude(), siglocList
					.get(l).getLongitude());
		}
		siglocList.clear();
		finish();
	}

	// add the proximity alerts and receivers
	private void addProximityAlert(double latitude, double longitude) {
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

	public void writeToExternalStoragePublic(String filename, int i,
			double lat, double lng) {
		String packageName = this.getPackageName();
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/data/" + packageName + "/files/";
		try {
			boolean exists = (new File(path)).exists();
			if (!exists) {
				new File(path).mkdirs();
			}
			// Open output stream
			FileOutputStream fOut = new FileOutputStream(path + filename, true);
			// write integers as separated ascii's
			fOut.write((Integer.valueOf(i).toString() + " ").getBytes());
			fOut.write((Double.valueOf(lat).toString() + " ").getBytes());
			fOut.write((Double.valueOf(lng).toString() + "\n").getBytes());
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
*/