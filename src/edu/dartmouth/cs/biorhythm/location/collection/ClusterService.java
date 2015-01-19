package edu.dartmouth.cs.biorhythm.location.collection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class ClusterService extends Service {

	float[] curDist = new float[1];
	float distance;
	float distance1;
	public ArrayList<Location> locList;
	public ArrayList<Location> siglocList;
	public ArrayList<Location> dbsiglocList;
	
	//How many hours the users need to be in a location for it to be considered a significant location
	public static final int LONG_ENOUGH = 6;
	public static final int DIST_THRESHOLD = 100; 
	private Intent mLocationUpdateBroadcast;

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
		
		locList = new ArrayList<Location>();
		siglocList = new ArrayList<Location>();
		dbsiglocList = new ArrayList<Location>();

		transferDB();
		runCluster();
		eliminateDuplicates();
		inserttoDB();
		setAlerts();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	//Tells you how many entries are in the 2nd database
	public int getDBcount() {
		int count = 0;
		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		count = cursor.getCount();
		myDatabase.close();
		return count;
	}

	//Transfer the significant locations from the 1st database to an ArrayList for ease of manipulation and efficiency (I hope?)
	public void transferDB() {
		
		Toast.makeText(getBaseContext(),"transferring", Toast.LENGTH_LONG)
		.show();
		
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
		//Clear out the 1st database
		myDatabase.del();
		myDatabase.close();
	}

	// Start the clustering
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

				// Set threshold
				// Make more efficient - take out all at once (the i's)?
				if (distance < DIST_THRESHOLD) {
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

				if ((tenMinutes >= LONG_ENOUGH && distance1 > DIST_THRESHOLD)) {
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

		//For testing purposes
		for (int y = 0; y < siglocList.size(); y++) {
			writeToExternalStoragePublic("siglocList1.txt", y, siglocList
					.get(y).getLatitude(), siglocList.get(y).getLongitude());
		}
		locList.clear();
	}

	// Add in the duplicate remover for the database
	public void eliminateDuplicates() {

		// Need to remove the ones that match with previous sig locations

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
				// Set threshold
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
		
		//with freq
		/*public void eliminateDuplicates() {
		boolean blah = false;
		
		if (getDBcount() == 0) {
			LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
			myDatabase.open();
			myDatabase.insertRows(siglocList.get(0).getLatitude(), siglocList
					.get(0).getLongitude(), "null", 0, "null", "null", "null",
					1);
			myDatabase.close();
			siglocList.remove(0);
		}

		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		for (int i = 0; i < siglocList.size(); i++) {
			Cursor cursor = myDatabase.getAllRows();
			cursor.moveToFirst();
			for (int j = 0; j < cursor.getCount(); j++) {

				Location.distanceBetween(siglocList.get(i).getLatitude(),
						siglocList.get(i).getLongitude(), cursor.getDouble(1),
						cursor.getDouble(2), curDist);
				
				distance = curDist[0];

			// Set threshold
				if (distance < 100) {
					// Locations are similar, update freq
					int id = cursor.getInt(0);
					int freq = cursor.getInt(8);
					
					myDatabase.updateFreq(id, (freq + 1));
					siglocList.remove(i);
					i--;
					blah = true;
					break;
				}
					cursor.moveToNext();				
			}
			
			if (blah == false) {
				
				myDatabase.insertRows(siglocList.get(i).getLatitude(),
						siglocList.get(i).getLongitude(), "null", 0, "null",
						"null", "null", 1);
				siglocList.remove(i);
				i--;
			}
			blah=false;
			
		}
		myDatabase.close();
	}

	*/
	}

	// Set up database to hold survey labels
	public void inserttoDB() {
		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		for (int i = 0; i < siglocList.size(); i++) {
			myDatabase.insertRows(siglocList.get(i).getLatitude(), siglocList
					.get(i).getLongitude(), "null", 0, "null", "null", "null");
		}
		myDatabase.close();
		testDB();
	}

	//Send the broadcast to the Prox Service to start the proximity alerts
	public void setAlerts() {
		testDB();
		
		if ((getDBcount() > 0) && (siglocList.size() > 0)) {
			mLocationUpdateBroadcast = new Intent();
			mLocationUpdateBroadcast.setAction(ProxService.ACTION);
			mLocationUpdateBroadcast.putParcelableArrayListExtra("Locations",
					siglocList);
			sendBroadcast(mLocationUpdateBroadcast);
		}
		stopSelf();
	}
	
	//For testing

	void writeToExternalStoragePublic(String filename, int i, double lat,
			double lng) {
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

	public void testDB() {

		LabelDatabase myDatabase = new LabelDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			writeToExternalStoragePublic(("db.txt"),
					cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2),
					cursor.getString(3), cursor.getInt(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7));
			cursor.moveToNext();
		}
		myDatabase.close();
	}

	// write to file v2
	public void writeToExternalStoragePublic(String filename, int id,
			double lat, double lng, String name, int check, String where,
			String who, String action) {
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
			fOut.write((Integer.valueOf(id).toString() + " ").getBytes());
			fOut.write((Double.valueOf(lat).toString() + " ").getBytes());
			fOut.write((Double.valueOf(lng).toString() + " ").getBytes());
			fOut.write((name + " ").getBytes());
			fOut.write((where + " ").getBytes());
			fOut.write((who + " ").getBytes());
			fOut.write((action + " ").getBytes());
			fOut.write((Integer.valueOf(check).toString() + "\n").getBytes());
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
}