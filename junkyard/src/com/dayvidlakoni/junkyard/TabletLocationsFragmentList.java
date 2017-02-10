package com.dayvidlakoni.junkyard;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class TabletLocationsFragmentList extends ListFragment {

	private int inflatedFragment;

	private LocationsFragmentAdapter adapter;
	private ArrayList<String> items;

	public TabletLocationsFragmentList() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("TABLET_LOCATIONS_FRAGMENT_LIST", "onCreate()");
		super.onCreate(savedInstanceState);

		items = new ArrayList<String>();
		items.add(getActivity().getString(R.string.locationsfragment_filter));
		items.add(getActivity().getString(R.string.locationsfragment_nearest));
		items.add(getActivity().getString(R.string.locationsfragment_favs));
		items.add(getActivity().getString(R.string.locationsfragment_list));
		items.add(getActivity().getString(R.string.locationsfragment_map));

		adapter = new LocationsFragmentAdapter(getActivity(),
				R.layout.locations_fragment_item, items);
		setListAdapter(adapter);
	}

	public void setInflatedFragment(int inflatedFragment) {
		this.inflatedFragment = inflatedFragment;
	}

	public int getInflatedFragment() {
		return inflatedFragment;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		switch (position) {
		case 0:

			FilterLocationsDialog dialog = new FilterLocationsDialog();
			dialog.show(getFragmentManager(), "FILTER_LOCATIONS");

			break;
		case 1:

			NearestLocationMapFragment nearestFragment = new NearestLocationMapFragment();

			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					nearestFragment);

			TabletLocationsFragment.infFrag1 = nearestFragment;

			inflatedFragment = 1;
			Log.d("TABLET_LOCATIONS_FRAGMENT_LIST",
					"Setting inflatedFragment = 1");

			break;
		case 2:

			TabletShowFavoritesListFragment favsFragment = new TabletShowFavoritesListFragment();

			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					favsFragment);

			TabletLocationsFragment.infFrag1 = favsFragment;

			inflatedFragment = 2;
			Log.d("TABLET_LOCATIONS_FRAGMENT_LIST",
					"Setting inflatedFragment = 2");

			break;
		case 3:

			TabletShowLocationsListFragment listFragment = new TabletShowLocationsListFragment();

			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					listFragment);

			TabletLocationsFragment.infFrag1 = listFragment;

			inflatedFragment = 3;
			Log.d("TABLET_LOCATIONS_FRAGMENT_LIST",
					"Setting inflatedFragment = 3");

			break;
		case 4:

			ShowAllInMapFragment mapFragment = new ShowAllInMapFragment();

			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					mapFragment);

			TabletLocationsFragment.infFrag1 = mapFragment;

			inflatedFragment = 4;
			Log.d("TABLET_LOCATIONS_FRAGMENT_LIST",
					"Setting inflatedFragment = 4");

			break;
		default:
			Toast.makeText(getActivity(),
					"No OnClickListener yet for this item!", Toast.LENGTH_SHORT)
					.show();
			break;
		}

		ft.commit();
	}

	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("INFLATED_FRAGMENT", inflatedFragment);
		editor.apply();
	}
}
