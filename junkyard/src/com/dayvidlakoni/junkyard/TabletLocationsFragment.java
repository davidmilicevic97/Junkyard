package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabletLocationsFragment extends Fragment {

	private int inflatedFragment = 0;
	// private String INFLATED_FRAGMENT_KEY = "INFLATED_KEY";
	private String TABLET_LOCATIONS_FRAGMENT_LIST_TAG = "TABLET_LOCATIONS_FRAGMENT_LIST";

	public static Fragment infFrag1 = null, infFrag2 = null;

	public TabletLocationsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.tablet_locations_fragment, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Log.d("TABLET_LOCATIONS_FRAGMENT", "onViewCreated()");

		TabletLocationsFragmentList fragment2 = new TabletLocationsFragmentList();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(
				R.id.tablet_locations_fragment_locations_fragment_list_container,
				fragment2, TABLET_LOCATIONS_FRAGMENT_LIST_TAG);
		infFrag2 = fragment2;

		// if (savedInstanceState == null) {
		// Log.d("TABLET_LOCATIONS_FRAGMENT", "savedInstanceState == null");
		//
		// NearestLocationMapFragment fragment1 = new
		// NearestLocationMapFragment();
		// // fragment1.setCurrentInstance(fragment1);
		// ft.replace(R.id.tablet_locations_fragment_fragment_container,
		// fragment1);
		//
		// inflatedFragment = 1;
		// infFrag1 = fragment1;
		// } else {
		// Log.d("TABLET_LOCATIONS_FRAGMENT", "savedInstanceState != null");

		// inflatedFragment = savedInstanceState.getInt(INFLATED_FRAGMENT_KEY);

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		inflatedFragment = prefs.getInt("INFLATED_FRAGMENT", 1);

		Log.d("TABLET_LOCATIONS_FRAGMENT", "inflatedFragment = "
				+ inflatedFragment);

		switch (inflatedFragment) {
		case 1:
			NearestLocationMapFragment nearest = new NearestLocationMapFragment();
			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					nearest);
			infFrag1 = nearest;
			break;
		case 2:
			TabletShowFavoritesListFragment favorites = new TabletShowFavoritesListFragment();
			ft.replace(R.id.tablet_locations_fragment_fragment_container,
					favorites);
			infFrag1 = favorites;
			break;
		case 3:
			TabletShowLocationsListFragment list = new TabletShowLocationsListFragment();
			ft.replace(R.id.tablet_locations_fragment_fragment_container, list);
			infFrag1 = list;
			break;
		case 4:
			ShowAllInMapFragment map = new ShowAllInMapFragment();
			ft.replace(R.id.tablet_locations_fragment_fragment_container, map);
			infFrag1 = map;
			break;
		}
		// }

		fragment2.setInflatedFragment(inflatedFragment);
		ft.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("TABLET_LOCATIONS_FRAGMENT", "onResume()");
	}

	@Override
	public void onPause() {
		Log.d("TABLET_LOCATIONS_FRAGMENT", "onPause()");
		Log.d("TABLET_LOCATIONS_FRAGMENT", infFrag1.toString());

		infFrag1.onPause();
		infFrag2.onPause();
		super.onPause();
	}

	// Ne treba mi jer se ne poziva uopste, zato sto sam izbrisao
	// super.onSaveInstanceState() u DrawerActivity!
	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	//
	// Log.d("TABLET_LOCATIONS_FRAGMENT", "onSaveInstanceState() called!");
	//
	// FragmentManager fm = getFragmentManager();
	// TabletLocationsFragmentList fragment = (TabletLocationsFragmentList) fm
	// .findFragmentByTag(TABLET_LOCATIONS_FRAGMENT_LIST_TAG);
	// inflatedFragment = fragment.getInflatedFragment();
	//
	// Log.d("TABLET_LOCATIONS_FRAGMENT", "inflatedFragment = "
	// + inflatedFragment);
	//
	// outState.putInt(INFLATED_FRAGMENT_KEY, inflatedFragment);
	// }
}
