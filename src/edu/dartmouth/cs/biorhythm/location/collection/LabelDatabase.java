package edu.dartmouth.cs.biorhythm.location.collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LabelDatabase {

	private DbHelper dbHelper;
	public final String DBNAME = "gps2";
	public final int DBVERSION = 1;
	public SQLiteDatabase db;
	public final String COLUMN2 = "latitude";
	public final String COLUMN3 = "longitude";
	public final String COLUMN4 = "locname";
	public final String COLUMN5 = "asked";
	public final String COLUMN6 = "type";
	public final String COLUMN7 = "activities";
	public final String COLUMN8 = "who";
	public final String COLUMN1 = "locationId";
	//add freq
	public final String TABLENAME = "labels";
	public final String CREATERDB = "create table labels(locationId integer primary key autoincrement,"
			+ "latitude real not null, longitude real not null, locname text not null, "
			+ "asked integer not null, type text not null, activities text not null, who text not null);";

	public LabelDatabase(Context context) {
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

	public void updateAsked(int id) {
		Integer ids = id;
		ContentValues cv = new ContentValues();
		cv.put(COLUMN5, 1);
		String where = COLUMN1 + "=?";
		String[] value = { ids.toString() };
		db.update(TABLENAME, cv, where, value);
	}

	public long insertRows(double column2, double column3, String column4,
			int column5, String column6, String column7, String column8) {
		ContentValues value = new ContentValues();
		value.put(COLUMN2, column2);
		value.put(COLUMN3, column3);
		value.put(COLUMN4, column4);
		value.put(COLUMN5, column5);
		value.put(COLUMN6, column6);
		value.put(COLUMN7, column7);
		value.put(COLUMN8, column8);

		return db.insert(TABLENAME, null, value);
	}

	public Cursor getAllRows() {
		Cursor cursor = db.query(TABLENAME, new String[] { COLUMN1, COLUMN2,
				COLUMN3, COLUMN4, COLUMN5, COLUMN6, COLUMN7, COLUMN8 }, null,
				null, null, null, null);
		return cursor;
	}

	// Method to be added to fill in the answers into the DB
	public void fillinDB(int id, String namedata, String whodata,
			String wheredata, String action) {

		Integer ids = id;
		// Create content values that contains the name of the column you want
		// to update and the value you want to assign to it
		ContentValues cv = new ContentValues();
		cv.put(COLUMN4, namedata);
		cv.put(COLUMN6, wheredata);
		cv.put(COLUMN7, action);
		cv.put(COLUMN8, whodata);

		String where = COLUMN1 + "=?";
		String[] value = { ids.toString() }; // The value for the where clause.

		db.update(TABLENAME, cv, where, value);

	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();

	}

	public void close() {
		dbHelper.close();

	}

	public void deleterow(int id) {
		db.delete(TABLENAME, COLUMN1 + "=" + id, null);
	}

}