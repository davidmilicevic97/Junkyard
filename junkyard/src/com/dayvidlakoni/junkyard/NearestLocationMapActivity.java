package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class NearestLocationMapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearest_location_map_activity);
		
		NearestLocationMapFragment nearestFragment = new NearestLocationMapFragment();
		// nearestFragment.setCurrentInstance(nearestFragment);

		FragmentManager fmNearest = getFragmentManager();
		FragmentTransaction nearestTransaction = fmNearest.beginTransaction();
		nearestTransaction.replace(
				R.id.nearest_location_map_activity_fragment_container,
				nearestFragment);
		nearestTransaction.commit();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
