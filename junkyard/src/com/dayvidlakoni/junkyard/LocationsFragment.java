package com.dayvidlakoni.junkyard;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class LocationsFragment extends ListFragment {

	private LocationsFragmentAdapter adapter;
	private ArrayList<String> items;

	public LocationsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			FilterLocationsDialog dialog = new FilterLocationsDialog();
			dialog.show(getFragmentManager(), "FILTER_LOCATIONS");
			break;
		case 1:
			Intent nearestActivity = new Intent(getActivity(),
					NearestLocationMapActivity.class);
			startActivity(nearestActivity);
			break;
		case 2:
			Intent favsActivity = new Intent(getActivity(),
					FavoritesActivity.class);
			startActivity(favsActivity);
			break;
		case 3:
			Intent listActivity = new Intent(getActivity(),
					LocationsListActivity.class);
			startActivity(listActivity);
			break;
		case 4:
			Intent mapActivity = new Intent(getActivity(),
					ShowAllInMapActivity.class);
			startActivity(mapActivity);
			break;
		default:
			Toast.makeText(getActivity(),
					"No OnClickListener yet for this item!", Toast.LENGTH_SHORT)
					.show();
			break;
		}

	}

}
