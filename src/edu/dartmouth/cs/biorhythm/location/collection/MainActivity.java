package edu.dartmouth.cs.biorhythm.location.collection;

import edu.dartmouth.stresscalendar.R;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/* EXTRA IMPORTS
 import java.util.ArrayList;
 import android.location.Location;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import android.app.ActionBar;
 import android.app.Fragment;
 import android.app.FragmentTransaction;
 import android.app.ActionBar.Tab;
 import android.content.IntentFilter;
 import android.database.Cursor;
 import android.location.LocationManager;
 import android.os.Environment;
 import android.view.View;
 import android.widget.Toast;
 */

public class MainActivity extends Activity {

	/*
	 * public static ArrayList<Location> testList; public static int counter2;
	 * public static int surveycounter; private static Context context;
	 */

	// Number of minutes before starting tracking services
	private final int ALARM_MINUTES = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * MainActivity.context = getApplicationContext(); counter2++;
		 * surveycounter = 0; testList = new ArrayList<Location>();
		 */

		// Set alarm for the TrackingService for every ten minutes
		Intent intent = new Intent(MainActivity.this, TrackingService.class);
		PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0,
				intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * ALARM_MINUTES, pintent);

		// Starts a permanent service that will call upon another service to
		// cluster the gathered data every X hours
		startService(new Intent(this, ProxService.class));
	}

	// Show significant locations in the database
	public void showLocs(View v) {
		Intent i = new Intent(this, ShowLocations.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	// Disabling the back button on MainActivity
	@Override
	public void onBackPressed() {
	}

	/*
	 * METHODS TO TEST/CHECK THE CONTENTS OF THE DATABASES AND LISTS
	 * 
	 * public static void getSigLocation() { counter2 = 0; toFile(); testFile();
	 * Intent i = new Intent(MainActivity.context, Cluster.class);
	 * i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * MainActivity.context.startActivity(i); }
	 * 
	 * public void toFile() { GPSDatabase myDatabase = new
	 * GPSDatabase(MainActivity.context); myDatabase.open(); Cursor cursor =
	 * myDatabase.getAllRows(); cursor.moveToFirst(); for (int i = 0; i <
	 * cursor.getCount(); i++) { String getTime = getDate(cursor.getLong(3),
	 * "dd/MM/yyyy hh:mm:ss.SSS");
	 * writeToExternalStoragePublic((MainActivity.counter2 + ".txt"),
	 * cursor.getDouble(1), cursor.getDouble(2), getTime); cursor.moveToNext();
	 * } myDatabase.close(); }
	 * 
	 * public static void writeToExternalStoragePublic(String filename, double
	 * lat, double lng, String time) { String packageName =
	 * MainActivity.context.getPackageName(); String path =
	 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
	 * "/Android/data/" + packageName + "/files/"; try { boolean exists = (new
	 * File(path)).exists(); if (!exists) { new File(path).mkdirs(); } // Open
	 * output stream FileOutputStream fOut = new FileOutputStream(path +
	 * filename, true); // write integers as separated ascii's
	 * fOut.write((Double.valueOf(lat).toString() + " ").getBytes());
	 * fOut.write((Double.valueOf(lng).toString() + " ").getBytes());
	 * fOut.write((time + "\n").getBytes()); fOut.flush(); fOut.close(); } catch
	 * (IOException e) { e.printStackTrace(); } }
	 * 
	 * public static String getDate(long milliSeconds, String dateFormat) { //
	 * Create a DateFormatter object for displaying date DateFormat formatter =
	 * new SimpleDateFormat(dateFormat); return formatter.format(milliSeconds);
	 * }
	 * 
	 * public static void testFile() { for (int i = 0; i < testList.size(); i++)
	 * { writeToExternalStoragePublic( ("test" + MainActivity.counter2 +
	 * ".txt"), testList.get(i) .getLatitude(), testList.get(i).getLongitude(),
	 * "null"); } testList.clear(); }
	 */

}
