package com.dayvidlakoni.junkyard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabletShowLocationsListFragment extends Fragment {

	public TabletShowLocationsListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.tablet_show_locations_list_fragment,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TabletLocationsListFragment listFragment = new TabletLocationsListFragment();
		listFragment.setCurrInstance(listFragment);

		TabletShowLocationsListItemFragment showItemFragment = new TabletShowLocationsListItemFragment();
		// showItemFragment.setCurrentInstance(showItemFragment);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.tablet_locations_list_fragment_container, listFragment);
		ft.replace(R.id.tablet_show_locations_list_item_fragment_container,
				showItemFragment, TabletShowLocationsListItemFragment.TAG);
		ft.commit();
	}

}
