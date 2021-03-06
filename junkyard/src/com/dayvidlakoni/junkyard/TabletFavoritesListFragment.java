package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TabletFavoritesListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		AdapterView.OnItemLongClickListener,
		FavoritesDialog.OnDeleteClickedListener {

	private SimpleCursorAdapter adapter;
	private TabletFavoritesListFragment currInstance;
	private TabletShowFavoritesListItemFragment showItemFragment;

	public TabletFavoritesListFragment() {
	}

	public void setCurrInstance(TabletFavoritesListFragment currInstance) {
		this.currInstance = currInstance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String where = LocationsContentProvider.FAVORITES_KEY + "=" + 1;

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		String selected = prefs.getString(
				FilterLocationsDialog.PREF_TYPES_STRING, "All");
		if (!selected.equals("All")) {
			where += " AND " + LocationsContentProvider.TYPE_KEY + "='"
					+ selected + "'";
		}

		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.ADDRESS_KEY,
				LocationsContentProvider.TYPE_KEY,
				LocationsContentProvider.FAVORITES_KEY };
		Cursor cursor = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, projection, where, null,
				null);

		adapter = new MyCursorAdapter(getActivity(),
				R.layout.location_item_view, cursor, new String[] {
						LocationsContentProvider.ADDRESS_KEY,
						LocationsContentProvider.TYPE_KEY }, new int[] {
						R.id.adress, R.id.location }, 0);
		setListAdapter(adapter);

		Log.d("TABLET_FAVORITES_LIST_FRAGMENT", "onCreate()");
		
		getLoaderManager()
				.initLoader(0, null, TabletFavoritesListFragment.this);

		refreshFavoriteLocations();
	}

	private void refreshFavoriteLocations() {
		getLoaderManager().restartLoader(0, null,
				TabletFavoritesListFragment.this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setOnItemLongClickListener(this);

		showItemFragment = (TabletShowFavoritesListItemFragment) getFragmentManager()
				.findFragmentByTag(TabletShowFavoritesListItemFragment.TAG);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String where = LocationsContentProvider.ID_KEY + "=" + id;
		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.LATITUDE_KEY,
				LocationsContentProvider.LONGITUDE_KEY,
				LocationsContentProvider.ADDRESS_KEY,
				LocationsContentProvider.FAVORITES_KEY };
		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, projection, where, null,
				null);

		if (query.moveToFirst() == false) {
			Log.d("ListView Item Click", "No results for query");
		} else {
			Log.d("ListView Item Click",
					"id: "
							+ query.getLong(query
									.getColumnIndex(LocationsContentProvider.ID_KEY)));

			double locationLatitude = query.getDouble(query
					.getColumnIndex(LocationsContentProvider.LATITUDE_KEY));
			double locationLongitude = query.getDouble(query
					.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY));
			String locationAddress = query.getString(query
					.getColumnIndex(LocationsContentProvider.ADDRESS_KEY));
			int favorite = query.getInt(query
					.getColumnIndex(LocationsContentProvider.FAVORITES_KEY));
			boolean isFavorite = (favorite == 1);

			showItemFragment.changeItem(id, isFavorite, locationLatitude,
					locationLongitude, locationAddress);
		}
		query.close();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		FavoritesDialog favsDialog = new FavoritesDialog(id, currInstance);
		favsDialog.show(getFragmentManager(), "FAVORITES_DIALOG");

		return true;
	}

	@Override
	public void onDeleteClicked(long id) {
		String where = LocationsContentProvider.ID_KEY + "=" + id;
		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, null, where, null, null);

		query.moveToFirst();
		ContentValues values = new ContentValues();
		values.put(LocationsContentProvider.ADDRESS_KEY, query.getString(query
				.getColumnIndex(LocationsContentProvider.ADDRESS_KEY)));
		values.put(LocationsContentProvider.LOCATION_KEY, query.getString(query
				.getColumnIndex(LocationsContentProvider.LOCATION_KEY)));
		values.put(LocationsContentProvider.LATITUDE_KEY, query.getDouble(query
				.getColumnIndex(LocationsContentProvider.LATITUDE_KEY)));
		values.put(
				LocationsContentProvider.LONGITUDE_KEY,
				query.getDouble(query
						.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY)));
		values.put(LocationsContentProvider.TYPE_KEY, query.getString(query
				.getColumnIndex(LocationsContentProvider.TYPE_KEY)));
		values.put(LocationsContentProvider.FAVORITES_KEY, 0);

		getActivity().getContentResolver().update(
				LocationsContentProvider.CONTENT_URI, values, where, null);

		query.close();

		getLoaderManager().restartLoader(0, null,
				TabletFavoritesListFragment.this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String where = LocationsContentProvider.FAVORITES_KEY + "=" + 1;
		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.ADDRESS_KEY,
				LocationsContentProvider.TYPE_KEY };

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		String selected = prefs.getString(
				FilterLocationsDialog.PREF_TYPES_STRING, "All");
		if (!selected.equals("All")) {
			where += " AND " + LocationsContentProvider.TYPE_KEY + "='"
					+ selected + "'";
		}

		CursorLoader loader = new CursorLoader(getActivity(),
				LocationsContentProvider.CONTENT_URI, projection, where, null,
				null);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
