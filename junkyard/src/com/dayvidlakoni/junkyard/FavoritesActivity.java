package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class FavoritesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites_activity);

		FavoritesFragment favsFragment = new FavoritesFragment();
		favsFragment.setCurrentInstance(favsFragment);

		FragmentManager fmFavs = getFragmentManager();
		FragmentTransaction favsTransaction = fmFavs.beginTransaction();
		favsTransaction.replace(R.id.favorites_activity_fragment_container,
				favsFragment, FavoritesFragment.FRAGMENT_TAG);
		favsTransaction.commit();

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
