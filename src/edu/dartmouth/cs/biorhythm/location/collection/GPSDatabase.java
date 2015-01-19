package edu.dartmouth.cs.biorhythm.location.collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GPSDatabase {
	private DbHelper dbHelper;
	public final String DBNAME = "gps1";
	public final int DBVERSION = 1;
	public SQLiteDatabase db;
	public final String COLUMN1 = "locationId";
	public final String COLUMN2 = "latitude";
	public final String COLUMN3 = "longitude";
	public final String COLUMN4 = "time";
	public final String TABLENAME = "location";
	public final String CREATERDB = "create table location(locationId integer primary key autoincrement,"
			+ "latitude real not null, longitude real not null, time real not null);";

	public GPSDatabase(Context context) {
		dbHelper = new DbHelper(context);
	}

	public class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DBNAME, null, DBVERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATERDB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}

	public long insertRows(Double column2, Double column3, long column4) {
		ContentValues value = new ContentValues();
		value.put(COLUMN2, column2);
		value.put(COLUMN3, column3);
		value.put(COLUMN4, column4);
		return db.insert(TABLENAME, null, value);
	}

	public Cursor getAllRows() {
		Cursor cursor = db.query(TABLENAME, new String[] { COLUMN1, COLUMN2,
				COLUMN3, COLUMN4 }, null, null, null, null, null);
		return cursor;
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void del() {
		db.delete(TABLENAME, null, null);
	}
}