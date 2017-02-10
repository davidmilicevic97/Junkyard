package com.dayvidlakoni.junkyard;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocationsContentProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri
			.parse("content://com.dayvidlakoni.junkyardprovider/locations");

	public static final String ID_KEY = "_id";
	public static final String ADDRESS_KEY = "address";
	// latitude i longitude mi trebaju da bih mogao da crtam na mapi
	public static final String LATITUDE_KEY = "latitude";
	public static final String LONGITUDE_KEY = "longitude";
	// ovo mi treba zbog textView-a u location_item_view da ne bih morao da
	// pravim svoj SimpleCursorAdapter
	public static final String LOCATION_KEY = "location";
	// ako je 1 onda jeste, ako je 0 onda nije
	public static final String FAVORITES_KEY = "favorites";
	public static final String TYPE_KEY = "type";

	private static LocationDatabaseHelper dbHelper;

	private static final int ALL_LOCATIONS = 1;
	private static final int SINGLE_LOCATION = 2;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.dayvidlakoni.junkyardprovider", "locations",
				ALL_LOCATIONS);
		uriMatcher.addURI("com.dayvidlakoni.junkyardprovider", "locations/#",
				SINGLE_LOCATION);
	}

	public static void dropTableAndCreateNew() {
		Log.d("LOCATIONS_CONTENT_PROVIDER",
				"Entered dropTableAndCreateNew() method. I'll try to call Helper's drop() now!");
		dbHelper.drop();
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();

		dbHelper = new LocationDatabaseHelper(context,
				LocationDatabaseHelper.DATABASE_NAME, null,
				LocationDatabaseHelper.DATABASE_VERSION);

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase database = dbHelper.getWritableDatabase();

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(LocationDatabaseHelper.LOCATION_TABLE);

		// ako je upit za samo jedan red
		switch (uriMatcher.match(uri)) {
		case SINGLE_LOCATION:
			qb.appendWhere(ID_KEY + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}

		switch (PreferencesCommonUtilities.getLocationsSortOrder(getContext())) {
		case 1:
			sortOrder = ADDRESS_KEY + " ASC";
			break;
		case 2:
			sortOrder = ADDRESS_KEY + " DESC";
			break;
		case 3:
			sortOrder = TYPE_KEY + " ASC";
			break;
		default:
			sortOrder = ADDRESS_KEY + " ASC";
			break;
		}
		Cursor c = qb.query(database, projection, selection, selectionArgs,
				null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALL_LOCATIONS:
			return "vnd.android.cursor.dir/vnd.dayvidlakoni.junkyard";
		case SINGLE_LOCATION:
			return "vnd.android.cursor.item/vnd.dayvidlakoni.junkyard";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		long rowId = database.insert(LocationDatabaseHelper.LOCATION_TABLE,
				null, values);

		if (rowId > 0) {
			Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertedUri, null);
			return insertedUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		int count;
		switch (uriMatcher.match(uri)) {
		case ALL_LOCATIONS:
			count = database.delete(LocationDatabaseHelper.LOCATION_TABLE,
					where, whereArgs);
			break;
		case SINGLE_LOCATION:
			String segment = uri.getPathSegments().get(1);
			count = database.delete(LocationDatabaseHelper.LOCATION_TABLE,
					ID_KEY
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		SQLiteDatabase database = dbHelper.getWritableDatabase();

		int count;
		switch (uriMatcher.match(uri)) {
		case ALL_LOCATIONS:
			count = database.update(LocationDatabaseHelper.LOCATION_TABLE,
					values, where, whereArgs);
			break;
		case SINGLE_LOCATION:
			String segment = uri.getPathSegments().get(1);
			count = database.update(LocationDatabaseHelper.LOCATION_TABLE,
					values, ID_KEY
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public static class LocationDatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "locations.db";
		private static final int DATABASE_VERSION = 1;
		private static final String LOCATION_TABLE = "locations";

		private static final String DATABASE_CREATE = "create table "
				+ LOCATION_TABLE + " (" + ID_KEY
				+ " integer primary key autoincrement, " + ADDRESS_KEY
				+ " TEXT, " + LOCATION_KEY + " TEXT, " + LATITUDE_KEY
				+ " FLOAT, " + LONGITUDE_KEY + " FLOAT, " + TYPE_KEY
				+ " TEXT, " + FAVORITES_KEY + " INTEGER);";

		// private SQLiteDatabase locationsDatabase;

		public LocationDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("LOCATION_DATABASE_HELPER", "Entered onCreate() method!");
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
			onCreate(db);
		}

		public void drop() {
			Log.d("LOCATION_DATABASE_HELPER", "Entered drop() method!");
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
			onCreate(db);
		}

	}

}
