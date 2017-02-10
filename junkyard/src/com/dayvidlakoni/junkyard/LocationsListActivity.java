package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class LocationsListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_list_activity);

		LocationsListFragment listFragment = new LocationsListFragment();
		listFragment.setCurrentInstance(listFragment);

		FragmentManager fmList = getFragmentManager();
		FragmentTransaction listTransaction = fmList.beginTransaction();
		listTransaction.replace(
				R.id.locations_list_activity_fragment_container, listFragment);
		listTransaction.commit();

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
