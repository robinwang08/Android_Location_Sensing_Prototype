package edu.dartmouth.cs.biorhythm.location.collection;

import edu.dartmouth.stresscalendar.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//Essentially a copy of Survey.java
public class ChangeLoc extends Activity {
	
	private static final String PROX_ALERT_INTENT = "edu.dartmouth.testinglocation.ProximityAlert";

	Spinner whereSpinner;
	Spinner whoSpinner;
	Spinner moodSpinner;
	private int pos;
	private int id;
	private double lat;
	private double lng;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_survey);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pos = extras.getInt("position");
		}
		
		// Add in the three spinners
				whereSpinner = (Spinner) findViewById(R.id.answer1);
				ArrayAdapter<CharSequence> whereAdapter = ArrayAdapter
						.createFromResource(this, R.array.where_array,
								android.R.layout.simple_spinner_item);
				whereAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				whereSpinner.setAdapter(whereAdapter);

				whoSpinner = (Spinner) findViewById(R.id.answer3);
				ArrayAdapter<CharSequence> whoAdapter = ArrayAdapter
						.createFromResource(this, R.array.who_array,
								android.R.layout.simple_spinner_item);
				whoAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				whoSpinner.setAdapter(whoAdapter);
				
				loadData();
	}
	
	public void loadData(){
		
		StringBuffer sb = new StringBuffer();
		
		
		LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToPosition(pos);
		
		id=cursor.getInt(0);
		sb.append(String.valueOf(cursor.getInt(0)) + " ");
		lat = cursor.getDouble(1);
		sb.append(String.valueOf(cursor.getDouble(1)) + " ");
		lng = cursor.getDouble(2);
		sb.append(String.valueOf(cursor.getDouble(2)) + " ");
		sb.append(String.valueOf(cursor.getString(3)) + " ");
		sb.append(String.valueOf(cursor.getInt(4)) + " ");
		sb.append(String.valueOf(cursor.getString(5)) + " ");
		sb.append(String.valueOf(cursor.getString(6)) + " ");
		sb.append(String.valueOf(cursor.getString(7)));
		
		
		myDatabase.close();
		

		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
	}
	
	// remove row and proximity alert
		public void onNotSigClicked(View v) {
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
			myDatabase.open();
			myDatabase.deleterow(id);
			myDatabase.close();

			Intent intent = new Intent(PROX_ALERT_INTENT + lat + lng);
			PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
					intent, 0);
			locManager.removeProximityAlert(proximityIntent);
			finish();
		}

		public void onSaveClicked(View v) {
			// Save profile
			saveProfile();
			finish();
		}

		public void saveProfile() {
			String namedata;
			namedata = (String) ((EditText) findViewById(R.id.locationname))
					.getText().toString();

			String wheredata;
			wheredata = whereSpinner.getSelectedItem().toString();
			if (whereSpinner.getSelectedItemPosition() == 5)
				wheredata = (String) ((EditText) findViewById(R.id.whereothertext))
						.getText().toString();

			String whodata;
			whodata = whoSpinner.getSelectedItem().toString();

			// Change to use a loop later
			CheckBox chbx1 = (CheckBox) findViewById(R.id.check1);
			CheckBox chbx2 = (CheckBox) findViewById(R.id.check2);
			CheckBox chbx3 = (CheckBox) findViewById(R.id.check3);
			CheckBox chbx4 = (CheckBox) findViewById(R.id.check4);
			CheckBox chbx5 = (CheckBox) findViewById(R.id.check5);
			CheckBox chbx6 = (CheckBox) findViewById(R.id.check6);
			CheckBox chbx7 = (CheckBox) findViewById(R.id.check7);
			CheckBox chbx8 = (CheckBox) findViewById(R.id.check8);
			CheckBox chbx9 = (CheckBox) findViewById(R.id.check9);
			CheckBox chbx10 = (CheckBox) findViewById(R.id.check10);
			CheckBox chbx11 = (CheckBox) findViewById(R.id.check11);
			CheckBox chbx12 = (CheckBox) findViewById(R.id.check12);
			CheckBox chbx13 = (CheckBox) findViewById(R.id.check13);
			CheckBox chbx14 = (CheckBox) findViewById(R.id.check14);
			CheckBox chbx15 = (CheckBox) findViewById(R.id.check15);
			CheckBox chbx16 = (CheckBox) findViewById(R.id.check16);

			StringBuffer results = new StringBuffer();
			if (chbx1.isChecked())
				results.append(chbx1.getText() + " ");
			if (chbx2.isChecked())
				results.append(chbx2.getText() + " ");
			if (chbx3.isChecked())
				results.append(chbx3.getText() + " ");
			if (chbx4.isChecked())
				results.append(chbx4.getText() + " ");
			if (chbx5.isChecked())
				results.append(chbx5.getText() + " ");
			if (chbx6.isChecked())
				results.append(chbx6.getText() + " ");
			if (chbx7.isChecked())
				results.append(chbx7.getText() + " ");
			if (chbx8.isChecked())
				results.append(chbx8.getText() + " ");
			if (chbx9.isChecked())
				results.append(chbx9.getText() + " ");
			if (chbx10.isChecked())
				results.append(chbx10.getText() + " ");
			if (chbx11.isChecked())
				results.append(chbx11.getText() + " ");
			if (chbx12.isChecked())
				results.append(chbx12.getText() + " ");
			if (chbx13.isChecked())
				results.append(chbx13.getText() + " ");
			if (chbx14.isChecked())
				results.append(chbx14.getText() + " ");
			if (chbx15.isChecked())
				results.append(chbx15.getText() + " ");
			if (chbx16.isChecked())
				results.append(chbx16.getText() + " ");

			LabelDatabase myDatabase = new LabelDatabase(getBaseContext());
			myDatabase.open();
			myDatabase.fillinDB(id, namedata, whodata, wheredata,
					results.toString());
			myDatabase.close();
		}
	
	
}