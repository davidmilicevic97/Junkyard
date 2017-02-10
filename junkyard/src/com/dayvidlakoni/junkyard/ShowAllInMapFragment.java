package com.dayvidlakoni.junkyard;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowAllInMapFragment extends Fragment implements
		OnMarkerClickListener {

	private MapView mapView;
	private GoogleMap map;
	private Polyline polyline = null;

	private double fromLatitude, fromLongitude, toLatitude, toLongitude;

	public ShowAllInMapFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_layout, container, false);

		try {
			MapsInitializer.initialize(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		mapView = (MapView) rootView.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);

		map = mapView.getMap();
		map.setOnMarkerClickListener(this);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

			@Override
			public void onMyLocationChange(Location location) {
				// Log.d("TARZAAAAAAAN", "location changed");
				int zoomLevel = 13;
				if (PreferencesCommonUtilities
						.isZoomSetupEnabled(getActivity()))
					zoomLevel = PreferencesCommonUtilities
							.getZoomLevel(getActivity());

				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						location.getLatitude(), location.getLongitude()),
						zoomLevel));
				map.setOnMyLocationChangeListener(null);
			}
		});

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker mark) {
				// STARTUJEM NOVI ACTIVITY DA PRIKAZE DIRECTIONS LISTU
				Bundle extras = new Bundle();
				extras.putDouble(Directions.EXTRA_FROM_LATITUDE, fromLatitude);
				extras.putDouble(Directions.EXTRA_FROM_LONGITUDE, fromLongitude);
				extras.putDouble(Directions.EXTRA_TO_LATITUDE, toLatitude);
				extras.putDouble(Directions.EXTRA_TO_LONGITUDE, toLongitude);
				extras.putString(Directions.EXTRA_TITLE, mark.getTitle());

				Intent directions = new Intent(getActivity(), Directions.class);
				directions.putExtras(extras);
				startActivity(directions);
			}
		});

		ToggleButton tbFav = (ToggleButton) rootView
				.findViewById(R.id.tbFavorite);
		tbFav.setVisibility(View.GONE);

		loadLocations();

		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onResume() {
		mapView.onResume();

		super.onResume();
	}

	@Override
	public void onPause() {
		mapView.onPause();

		super.onPause();
	}

	private void loadLocations() {
		String[] projection = new String[] { LocationsContentProvider.ID_KEY,
				LocationsContentProvider.LATITUDE_KEY,
				LocationsContentProvider.LONGITUDE_KEY,
				LocationsContentProvider.ADDRESS_KEY };

		String selection = null;
		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		String selected = prefs.getString(
				FilterLocationsDialog.PREF_TYPES_STRING, "All");
		if (!selected.equals("All")) {
			selection = LocationsContentProvider.TYPE_KEY + "='" + selected
					+ "'";
		}

		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, projection, selection,
				null, null);

		double latitude, longitude;
		String address;
		if (query.moveToFirst()) {
			do {
				latitude = query.getDouble(query
						.getColumnIndex(LocationsContentProvider.LATITUDE_KEY));
				longitude = query
						.getDouble(query
								.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY));
				address = query.getString(query
						.getColumnIndex(LocationsContentProvider.ADDRESS_KEY));

				map.addMarker(new MarkerOptions()
						.position(new LatLng(latitude, longitude)).alpha(0.7f)
						.title(address)
						.snippet(getString(R.string.marker_directions)));
			} while (query.moveToNext());
		}
		query.close();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (polyline != null) {
			polyline.remove();
			polyline = null;
		}

		if (map.getMyLocation() == null) {
			Toast.makeText(getActivity(),
					getString(R.string.location_not_available_toast),
					Toast.LENGTH_LONG).show();
			return true;
		}

		marker.showInfoWindow();

		fromLatitude = map.getMyLocation().getLatitude();
		fromLongitude = map.getMyLocation().getLongitude();
		toLatitude = marker.getPosition().latitude;
		toLongitude = marker.getPosition().longitude;

		LatLng fromPosition = new LatLng(fromLatitude, fromLongitude);
		LatLng toPosition = new LatLng(toLatitude, toLongitude);

		GMapDirection md = new GMapDirection();
		Document doc = md.getDocument(fromPosition, toPosition,
				GMapDirection.MODE_WALKING);

		if (doc != null) {
			ArrayList<LatLng> directionPoint = md.getDirection(doc);
			PolylineOptions rectLine = new PolylineOptions().width(3).color(
					Color.RED);
			Log.d("jbg teo je", "jbg teo je");
			for (int i = 0; i < directionPoint.size(); i++) {
				rectLine.add(directionPoint.get(i));
			}
			polyline = map.addPolyline(rectLine);
		} else {
			Log.d("jbg nije teo", "jbg nije teo");
		}

		// final LatLngBounds bnds = new LatLngBounds(new LatLng(Math.min(
		// fromPosition.latitude, toPosition.latitude), Math.min(
		// fromPosition.longitude, toPosition.longitude)), new LatLng(
		// Math.max(fromPosition.latitude, toPosition.latitude), Math.max(
		// fromPosition.longitude, toPosition.longitude)));

		final LatLngBounds bounds = new LatLngBounds(
				new LatLng(Math.min(fromLatitude, toLatitude), Math.min(
						fromLongitude, toLongitude)), new LatLng(Math.max(
						fromLatitude, toLatitude), Math.max(fromLongitude,
						toLongitude)));

		// map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

		return true;
	}

}
