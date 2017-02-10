package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class ShowAllInMapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_all_in_map_activity);

		ShowAllInMapFragment mapFragment = new ShowAllInMapFragment();
		// mapFragment.setCurrentInstance(mapFragment);

		FragmentManager fmMap = getFragmentManager();
		FragmentTransaction mapTransaction = fmMap.beginTransaction();
		mapTransaction.replace(
				R.id.show_all_in_map_activity_fragment_container, mapFragment);
		mapTransaction.commit();

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
