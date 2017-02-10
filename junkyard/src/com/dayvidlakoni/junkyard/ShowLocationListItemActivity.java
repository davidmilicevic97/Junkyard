package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class ShowLocationListItemActivity extends Activity {

	public static String IS_FAVORITE = "ShowLocationListItemFavorite";
	public static String ID_CLICKED = "ShowLocationListItemIdClicked";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_location_list_item_activity);

		Bundle extras = getIntent().getExtras();

		long id = extras.getLong(ID_CLICKED);
		boolean favorite = extras.getBoolean(IS_FAVORITE);

		ShowLocationListItemFragment itemFragment = new ShowLocationListItemFragment(
				id, favorite);
		// itemFragment.setCurrentInstance(itemFragment);
		itemFragment.setArguments(extras);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(
				R.id.show_location_list_item_activity_fragment_container,
				itemFragment);
		transaction.commit();

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
