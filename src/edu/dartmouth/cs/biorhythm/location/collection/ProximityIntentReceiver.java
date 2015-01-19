package edu.dartmouth.cs.biorhythm.location.collection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;

public class ProximityIntentReceiver extends BroadcastReceiver {

	
	//Sets the proximity alerts
	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);

		double latitude = intent.getDoubleExtra("lat", -1);
		double longitude = intent.getDoubleExtra("lng", -1);

		if (entering) {
			int id = 0;
			int updated = 0;

			LabelDatabase myDatabase = new LabelDatabase(context);
			myDatabase.open();
			Cursor cursor = myDatabase.getAllRows();
			cursor.moveToFirst();
			//Find the right id!
			for (int i = 0; i < cursor.getCount(); i++) {
				if ((cursor.getDouble(1) == latitude)
						&& (cursor.getDouble(2) == longitude)) {
					id = cursor.getInt(0);
					updated = cursor.getInt(4);
					break;
				}
				cursor.moveToNext();
			}

			if (updated != 1) {
				myDatabase.updateAsked(id);
				Intent surveyIntent = new Intent(context, Survey.class);
				surveyIntent.putExtra("id", id);
				surveyIntent.putExtra("lat", latitude);
				surveyIntent.putExtra("lng", longitude);
				surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(surveyIntent);
			}
			myDatabase.close();

		} else {
			int id = 0;
			int updated = 0;

			LabelDatabase myDatabase = new LabelDatabase(context);
			myDatabase.open();
			Cursor cursor = myDatabase.getAllRows();
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				if ((cursor.getDouble(1) == latitude)
						&& (cursor.getDouble(2) == longitude)) {
					id = cursor.getInt(0);
					updated = cursor.getInt(4);
					break;
				}
				cursor.moveToNext();
			}

			if (updated != 1) {
				myDatabase.updateAsked(id);
				Intent surveyIntent = new Intent(context, Survey.class);
				surveyIntent.putExtra("id", id);
				surveyIntent.putExtra("lat", latitude);
				surveyIntent.putExtra("lng", longitude);
				surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(surveyIntent);
			}
			myDatabase.close();
		}

	}

}
