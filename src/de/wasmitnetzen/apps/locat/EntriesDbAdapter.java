package de.wasmitnetzen.apps.locat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntriesDbAdapter {	
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";

	private static final String TAG = "EntriesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
			"create table entries (_id integer primary key autoincrement, "
					+ "title text not null, body text not null, longitude integer, latitude integer);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "entries";
	private static final int DATABASE_VERSION = 3;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS entries");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public EntriesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the entry database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public EntriesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}


	/**
	 * Create a new entry using the title and body provided. If the entry is
	 * successfully created return the new rowId for that entry, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title the title of the entry
	 * @param body the body of the entry
	 * @return rowId or -1 if failed
	 */
	public long createEntry(String title, String body) {
		ContentValues initialValues = new ContentValues();
		if (title.length() == 0) title ="(no title)";
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_BODY, body);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the entry with the given rowId
	 * 
	 * @param rowId id of entry to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteEntry(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 * 
	 * @return Cursor over all entries
	 */
	public Cursor fetchAllEntries() {

		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
				KEY_BODY}, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 * 
	 * @param rowId id of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException if entry could not be found/retrieved
	 */
	public Cursor fetchEntry(long rowId) throws SQLException {

		Cursor mCursor =

				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
						KEY_TITLE, KEY_BODY, KEY_LATITUDE, KEY_LONGITUDE}, KEY_ROWID + "=" + rowId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the entry using the details provided. The entry to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId id of entry to update
	 * @param title value to set entry title to
	 * @param body value to set entry body to
	 * @return true if the entry was successfully updated, false otherwise
	 */
	public boolean updateEntry(long rowId, String title, String body) {
		ContentValues args = new ContentValues();
		if (title.length() == 0) title ="(no title)";
		args.put(KEY_TITLE, title);
		args.put(KEY_BODY, body);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public Cursor getLocation(long rowId) throws SQLException {
		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
						KEY_LATITUDE, KEY_LONGITUDE}, KEY_ROWID + "=" + rowId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean updateLocation(long rowId, Integer latitude, Integer longitude) {
		ContentValues args = new ContentValues();
		args.put(KEY_LATITUDE, latitude);
		args.put(KEY_LONGITUDE, longitude);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
