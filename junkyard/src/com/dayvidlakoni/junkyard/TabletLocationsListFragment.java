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
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class TabletLocationsListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		AdapterView.OnItemLongClickListener,
		LocationsListDialog.OnAddToFavoritesClickedListener {

	private SimpleCursorAdapter adapter;
	private TabletLocationsListFragment currInstance;
	private TabletShowLocationsListItemFragment showItemFragment;

	public TabletLocationsListFragment() {
	}

	public void setCurrInstance(TabletLocationsListFragment currInstance) {
		this.currInstance = currInstance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String selection = null;
		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		String selected = prefs.getString(
				FilterLocationsDialog.PREF_TYPES_STRING, "All");

		if (!selected.equals("All")) {
			selection = LocationsContentProvider.TYPE_KEY + "='" + selected
					+ "'";
		}

		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.ADDRESS_KEY,
				LocationsContentProvider.TYPE_KEY,
				LocationsContentProvider.FAVORITES_KEY };
		Cursor cursor = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, projection, selection,
				null, null);

		adapter = new MyCursorAdapter(getActivity(),
				R.layout.location_item_view, cursor, new String[] {
						LocationsContentProvider.ADDRESS_KEY,
						LocationsContentProvider.TYPE_KEY }, new int[] {
						R.id.adress, R.id.location }, 0);
		setListAdapter(adapter);

		getLoaderManager().initLoader(0, null, this);

		refreshLocations();
	}

	private void refreshLocations() {
		getLoaderManager().restartLoader(0, null,
				TabletLocationsListFragment.this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setOnItemLongClickListener(this);

		// incijalizuj TabletShowLocationsListItemFragment
		showItemFragment = (TabletShowLocationsListItemFragment) getFragmentManager()
				.findFragmentByTag(TabletShowLocationsListItemFragment.TAG);
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

		LocationsListDialog listDialog = new LocationsListDialog(id,
				currInstance);
		listDialog.show(getFragmentManager(), "LOCATIONS_LIST_DIALOG");

		return true;
	}

	@Override
	public void onAddToFavoritesClicked(long id) {
		String where = LocationsContentProvider.ID_KEY + "=" + id;
		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, null, where, null, null);

		if (query.moveToFirst() == false) {
			Log.d("ListView Item Long Click", "No results for query");
		} else {
			// 1 jeste, 0 nije
			int favorite = query.getInt(query
					.getColumnIndex(LocationsContentProvider.FAVORITES_KEY));

			if (favorite == 0) {
				ContentValues values = new ContentValues();
				values.put(
						LocationsContentProvider.ADDRESS_KEY,
						query.getString(query
								.getColumnIndex(LocationsContentProvider.ADDRESS_KEY)));
				values.put(
						LocationsContentProvider.LOCATION_KEY,
						query.getString(query
								.getColumnIndex(LocationsContentProvider.LOCATION_KEY)));
				values.put(
						LocationsContentProvider.LATITUDE_KEY,
						query.getDouble(query
								.getColumnIndex(LocationsContentProvider.LATITUDE_KEY)));
				values.put(
						LocationsContentProvider.LONGITUDE_KEY,
						query.getDouble(query
								.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY)));
				values.put(
						LocationsContentProvider.TYPE_KEY,
						query.getString(query
								.getColumnIndex(LocationsContentProvider.TYPE_KEY)));
				values.put(LocationsContentProvider.FAVORITES_KEY, 1);

				getActivity().getContentResolver().update(
						LocationsContentProvider.CONTENT_URI, values, where,
						null);
			}

		}
		query.close();

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.ADDRESS_KEY,
				LocationsContentProvider.TYPE_KEY };

		String selection = null;
		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		String selected = prefs.getString(
				FilterLocationsDialog.PREF_TYPES_STRING, "All");

		if (!selected.equals("All")) {
			selection = LocationsContentProvider.TYPE_KEY + "='" + selected
					+ "'";
		}

		CursorLoader loader = new CursorLoader(getActivity(),
				LocationsContentProvider.CONTENT_URI, projection, selection,
				null, null);

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
