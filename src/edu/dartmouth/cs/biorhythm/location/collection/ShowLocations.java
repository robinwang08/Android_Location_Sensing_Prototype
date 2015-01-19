package edu.dartmouth.cs.biorhythm.location.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.dartmouth.stresscalendar.R;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShowLocations extends ListActivity implements OnItemClickListener {

	public ArrayList<String> locList;
	private double lat;
	private double lng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.labels);
		locList = new ArrayList<String>();
		showLocs();

	}

	public void showLocs() {

		LabelDatabase myDatabase = new LabelDatabase(this);
		myDatabase.open();
		Cursor cursor = myDatabase.getAllRows();
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {

			StringBuffer sb = new StringBuffer();

			sb.append(String.valueOf(cursor.getInt(0)) + " ");
			lat = cursor.getDouble(1);
			// sb.append(String.valueOf(cursor.getDouble(1)) + " ");
			lng = cursor.getDouble(2);
			// sb.append(String.valueOf(cursor.getDouble(2)) + " ");

			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			String result = null;
			try {
				List<Address> list = geocoder.getFromLocation(lat, lng, 1);
				Address address = list.get(0);
				result = address.getAddressLine(0) + ", "
						+ address.getLocality() + " ";

			} catch (IOException e) {
			}

			sb.append(result);
			sb.append(String.valueOf(cursor.getString(3)) + " ");
			sb.append(String.valueOf(cursor.getInt(4)) + " ");
			sb.append(String.valueOf(cursor.getString(5)) + " ");
			sb.append(String.valueOf(cursor.getString(6)) + " ");
			sb.append(String.valueOf(cursor.getString(7)));

			cursor.moveToNext();

			locList.add(sb.toString());

			//Clear the string buffer
			sb.setLength(0);

		}
		myDatabase.close();

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, locList));
		getListView().setTextFilterEnabled(true);

	}

	//Time to edit
	@Override
	protected void onListItemClick(ListView l, View v, final int position,
			long id) {
		super.onListItemClick(l, v, position, id);

		Toast.makeText(this, position + " ", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, ChangeLoc.class);
		i.putExtra("position", position);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}