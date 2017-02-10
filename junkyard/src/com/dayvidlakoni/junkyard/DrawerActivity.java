package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DrawerActivity extends Activity {

	private static String SAVE_SELECTED_ITEM = "SAVE_SELECTED_ITEM";

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private String[] ListViewItems;

	private int selectedItem = -1;

	private Fragment fragmentToShow;

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private class TabletDrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItemTablet(position);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("LALALALALALALALA", "Pozvao onCreate!!!  ");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_activity_layout);

		// moram ovde zato sto ne mogu getString() van metode
		ListViewItems = new String[] {
				getString(R.string.draweractivity_locations),
				getString(R.string.draweractivity_notifications) };

		// proveravam da li je telefon ili tablet
		boolean isTablet = false;
		if ((drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout)) == null) {
			Log.d("LALALALALALALALA", "Tablet jeeee!");

			isTablet = true;
			drawerLayout = (DrawerLayout) findViewById(R.id.tablet_drawer_layout);
		}

		drawerList = (ListView) findViewById(R.id.left_drawer);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, ListViewItems));

		if (isTablet)
			drawerList
					.setOnItemClickListener(new TabletDrawerItemClickListener());
		else
			drawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(DrawerActivity.this,
				drawerLayout, R.drawable.ic_drawer, android.R.string.ok,
				android.R.string.no) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			if (isTablet)
				selectItemTablet(0);
			else
				selectItem(0);
		} else {
			selectedItem = savedInstanceState.getInt(SAVE_SELECTED_ITEM, 0);

			if (isTablet)
				selectItemTablet(selectedItem);
			else
				selectItem(selectedItem);

			Log.d("DRAWER_ACTIVITY", "selectedItem=" + selectedItem);

			getActionBar().setTitle(ListViewItems[selectedItem]);
		}
	}

	private void selectItem(int position) {
		Log.d("DRAWER ACTIVITY", "selectItem(" + position + ")");

		getActionBar().setTitle(ListViewItems[position]);

		switch (position) {
		case 0:
			fragmentToShow = new LocationsFragment();
			break;
		case 1:
			fragmentToShow = new NotificationsFragment();

			Bundle args = new Bundle();
			args.putBoolean("TABLET", false);
			fragmentToShow.setArguments(args);

			break;
		default:
			fragmentToShow = new LocationsFragment();
			break;
		}

		FragmentManager fm = getFragmentManager();

		fm.beginTransaction()
				.replace(R.id.drawer_layout_fragment_container, fragmentToShow)
				.commit();

		selectedItem = position;
		drawerList.setItemChecked(position, true);
		drawerLayout.closeDrawer(drawerList);
	}

	private void selectItemTablet(int position) {
		Log.d("DRAWER ACTIVITY", "selectItemTablet(" + position + ")");

		getActionBar().setTitle(ListViewItems[position]);

		switch (position) {
		case 0:
			fragmentToShow = new TabletLocationsFragment();
			break;
		case 1:
			fragmentToShow = new TabletNotificationsFragment();
			break;
		default:
			fragmentToShow = new TabletLocationsFragment();
			break;
		}

		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
				.replace(R.id.drawer_layout_fragment_container, fragmentToShow,
						"ASD").commit();

		selectedItem = position;
		drawerList.setItemChecked(position, true);
		drawerLayout.closeDrawer(drawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		Log.d("DRAWER_ACTIVITY", "onSaveInstanceState()  " + selectedItem);

		outState.putInt(SAVE_SELECTED_ITEM, selectedItem);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("DRAWER_ACTIVITY", "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("DRAWER_ACTIVITY", "onPause()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// ako hocu nesto da promenim u action baru kad je otvoren ili zatvoren
		// to radim ovde
		boolean drawerOpened = drawerLayout.isDrawerOpen(drawerList);

		menu.findItem(R.id.settingsMenuItem).setVisible(!drawerOpened);

		if (drawerOpened)
			menu.findItem(R.id.refreshMenuItem).setVisible(false);
		else {
			if (selectedItem == 0)
				menu.findItem(R.id.refreshMenuItem).setVisible(true);
			else
				menu.findItem(R.id.refreshMenuItem).setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// posaljem item u ActionBarDrawerToggle i ako je on handle-ovao vratice
		// true i onda nista ne moram da radim vise
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.settingsMenuItem:
			Intent settingsIntent = new Intent(this, PreferencesActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.refreshMenuItem:
			this.startService(new Intent(this, LocationsUpdateService.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
