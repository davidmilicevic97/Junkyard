package com.dayvidlakoni.junkyard;

import org.w3c.dom.Document;

import com.google.android.gms.maps.model.LatLng;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class Directions extends ListActivity {

	public static String EXTRA_FROM_LATITUDE = "DirectionsFromLatitude";
	public static String EXTRA_FROM_LONGITUDE = "DirectionsFromLongitude";
	public static String EXTRA_TO_LATITUDE = "DirectionsToLatitude";
	public static String EXTRA_TO_LONGITUDE = "DirectionsToLongitude";
	public static String EXTRA_TITLE = "DirectionsTitle";

	private static String MAP_LINK_PART1 = "http://maps.google.com/maps?saddr=";
	private static String MAP_LINK_PART2 = "&daddr=";

	private double fromLatitude, fromLongitude, toLatitude, toLongitude;
	private String title = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		fromLatitude = extras.getDouble(EXTRA_FROM_LATITUDE);
		fromLongitude = extras.getDouble(EXTRA_FROM_LONGITUDE);
		toLatitude = extras.getDouble(EXTRA_TO_LATITUDE);
		toLongitude = extras.getDouble(EXTRA_TO_LONGITUDE);
		title = extras.getString(EXTRA_TITLE);

		getActionBar().setTitle(title);

		LatLng fromPosition = new LatLng(fromLatitude, fromLongitude);
		LatLng toPosition = new LatLng(toLatitude, toLongitude);

		GMapDirection md = new GMapDirection();
		Document doc = md.getDocument(fromPosition, toPosition,
				GMapDirection.MODE_WALKING);

		setListAdapter(new ArrayAdapter<String>(Directions.this,
				R.layout.directions_list_item, md.getInstructions(doc)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.directions_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.getDirectionsItem:
			try {
				String mapLink = MAP_LINK_PART1 + Double.toString(fromLatitude)
						+ "," + Double.toString(fromLongitude) + MAP_LINK_PART2
						+ Double.toString(toLatitude) + ","
						+ Double.toString(toLongitude);
				Intent directionsIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(mapLink));
				startActivity(directionsIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(Directions.this,
						"There is no app that can find directions!",
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
