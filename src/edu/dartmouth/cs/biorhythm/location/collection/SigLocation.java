package edu.dartmouth.cs.biorhythm.location.collection;
/*package edu.dartmouth.stresscalendar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SigLocation extends Activity {

	float[] curDist = new float[1];
	float distance;
	public ArrayList<Location> dbLocationList;
	public ArrayList<Location> sigLocationList;
	private static final long POINT_RADIUS = 100; 
	private static final long PROX_ALERT_EXPIRATION = -1;
	private static final String PROX_ALERT_INTENT = "edu.dartmouth.testinglocation.ProximityAlert";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbLocationList = new ArrayList<Location>();
		sigLocationList = new ArrayList<Location>();
		
		
		toFile();
		transferDB();
		doClustering();
		setAlerts();
	}

	public void doClustering() {
		int tenMinutes = 0;
		double latitude = 0.0;
		double longitude = 0.0;
		int removeCounter = 0;

		for (int i = 0; i < dbLocationList.size(); i++) {

			for (int j = i + 1; j < dbLocationList.size() - 1; j++) {
				Location.distanceBetween(dbLocationList.get(i).getLatitude(), dbLocationList
						.get(i).getLongitude(), dbLocationList.get(j).getLatitude(),
						dbLocationList.get(j).getLongitude(), curDist);
				distance = curDist[0];
				// listContents.add(distance + " " +
				// dbLocationList.get(j).getLatitude()
				// + " " + dbLocationList.get(j).getLongitude());
				// set threshold
				if (distance < 200) {
					// Locations are similar
					tenMinutes++;
				} else {

					dbLocationList.remove(i);
					break;
				}

				
				Location.distanceBetween(dbLocationList.get(i).getLatitude(), dbLocationList
						.get(i).getLongitude(), dbLocationList.get(j+1).getLatitude(),
						dbLocationList.get(j+1).getLongitude(), curDist);
				distance = curDist[0];
				// Set a threshold for how many "ten minutes"
				if (tenMinutes > 6 && distance > 200) {
					removeCounter = j - i + 1;
					for (int k = i; k <= j; k++) {
						latitude += dbLocationList.get(k).getLatitude();
						longitude += dbLocationList.get(k).getLongitude();
					}

					while (removeCounter > 0) {
						dbLocationList.remove(i);
						removeCounter--;
					}

					latitude /= (j - i + 1);
					longitude /= (j - i + 1);
					Location temploc = new Location("dummyprovider");
					temploc.setLatitude(dbLocationList.get(i).getLatitude());
					temploc.setLongitude(dbLocationList.get(i).getLongitude());
					sigLocationList.add(temploc);
					tenMinutes = 0;
					break;
				}

			}

		}
		
		ArrayList<String> listContents = new ArrayList<String>();
		for (int z = 0; z < sigLocationList.size(); z++) {
			listContents.add("Lat=" + sigLocationList.get(z).getLatitude() + "  " + "Log "
					+ sigLocationList.get(z).getLongitude());
		}
		ListAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, listContents);
		ListView list = (ListView) findViewById(R.id.dblist);
		list.setAdapter(adapter);
			
	}

			
	// set up the proximity alerts for surveys - should use notification
	public void setAlerts() {
		for (int l = 0; l < sigLocationList.size(); l++) {
			addProximityAlert(sigLocationList.get(l).getLatitude(),
					sigLocationList.get(l).getLongitude());
		}

	}

	private void addProximityAlert(double latitude, double longitude) {

		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Intent intent = new Intent(PROX_ALERT_INTENT + latitude + longitude);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);

		locManager.addProximityAlert(latitude, longitude, POINT_RADIUS,
				PROX_ALERT_EXPIRATION, proximityIntent);

		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT + latitude
				+ longitude);
		registerReceiver(new ProximityIntentReceiver(), filter);

	}

	public void showDB() {

		GPSDatabase myDatabase = new GPSDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		ArrayList<String> listContents = new ArrayList<String>();
		for (int i = 0; i < cursor.getCount(); i++) {
			listContents.add("Lat=" + cursor.getDouble(1) + "  " + "Log "
					+ cursor.getDouble(2));

			cursor.moveToNext();
		}
		myDatabase.close();
		ListAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, listContents);
		ListView list = (ListView) findViewById(R.id.dblist);
		list.setAdapter(adapter);
	}

	public void transferDB() {
		Location tempLocation = new Location("dummyprovider");
		GPSDatabase myDatabase = new GPSDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			tempLocation.setLatitude(cursor.getDouble(1));
			tempLocation.setLongitude(cursor.getDouble(2));
			dbLocationList.add(tempLocation);

			cursor.moveToNext();
		}
		myDatabase.close();

	}
	
	public void toFile(){
		GPSDatabase myDatabase = new GPSDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		
		for (int i = 0; i < cursor.getCount(); i++) {

			String getTime = getDate(cursor.getLong(3),"dd/MM/yyyy hh:mm:ss.SSS");
			writeToExternalStoragePublic((MainActivity.counter2 +".txt"), cursor.getDouble(1), cursor.getDouble(2), getTime ,cursor.getString(4));
			cursor.moveToNext();
		}
		MainActivity.counter2=0;
		myDatabase.close();
	}
	
	
	public void writeToExternalStoragePublic(String filename, double lat, double lng, String time, String provider) {
	    String packageName = this.getPackageName();
	    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + packageName + "/files/";
	        try {
	            boolean exists = (new File(path)).exists();
	            if (!exists) {
	                new File(path).mkdirs();
	            }
	            // Open output stream
	            FileOutputStream fOut = new FileOutputStream(path + filename,true);
	            // write integers as separated ascii's
	            fOut.write((Double.valueOf(lat).toString() + " ").getBytes());
	            fOut.write((Double.valueOf(lng).toString() + " ").getBytes());
	            fOut.write((time + " ").getBytes());
	            fOut.write((provider + "\n").getBytes());
	            // Close output stream
	            fOut.flush();
	            fOut.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	     return formatter.format(milliSeconds);
	}

}*/