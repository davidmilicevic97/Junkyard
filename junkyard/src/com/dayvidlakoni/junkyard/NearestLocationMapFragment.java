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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class NearestLocationMapFragment extends Fragment implements
		OnMarkerClickListener {

	private MapView mapView;
	private GoogleMap map;
	private Circle nearestCircle;
	private Polyline polyline = null;

	// ako je prvi put, samo onda crtam markere
	private boolean firstLoading = true;

	private Locations[] nearestLocations;

	private double fromLatitude, fromLongitude, toLatitude, toLongitude;

	public NearestLocationMapFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		nearestLocations = new Locations[5];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d("NEAREST_LOCATION_MAP_FRAGMENT", "onCreateView()");

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
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				loadLocations(location);
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

		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		Log.d("NEAREST_LOCATION_MAP_FRAGMENT", "onDestroy()");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onResume() {
		map.setMyLocationEnabled(true);
		mapView.onResume();
		Log.d("NEAREST_LOCATION_MAP_FRAGMENT", "onResume()");

		super.onResume();
	}

	@Override
	public void onPause() {
		map.setMyLocationEnabled(false);
		mapView.onPause();
		Log.d("NEAREST_LOCATION_MAP_FRAGMENT", "onPause()");
		super.onPause();
	}

	private void sortNearestLocations(Location location, int numLocations) {
		float[] distances = new float[5];
		float[] currDist = new float[1];

		for (int i = 0; i < numLocations; i++) {
			Location.distanceBetween(location.getLatitude(),
					location.getLongitude(), nearestLocations[i].getLatitude(),
					nearestLocations[i].getLongitude(), currDist);
			distances[i] = currDist[0];
		}

		for (int i = 0; i < numLocations - 1; i++) {

			int minIdx = i;

			for (int j = i + 1; j < numLocations; j++) {
				if (distances[j] < distances[minIdx])
					minIdx = j;
			}

			if (minIdx != i) {
				float pom = distances[i];
				distances[i] = distances[minIdx];
				distances[minIdx] = pom;

				Locations lPom = new Locations(nearestLocations[i]);
				nearestLocations[i] = new Locations(nearestLocations[minIdx]);
				nearestLocations[minIdx] = new Locations(lPom);
			}
		}
	}

	private void loadLocations(Location location) {
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

		int i = 0;
		if (query.moveToFirst()) {
			query.moveToPrevious();
			for (i = 0; query.moveToNext(); i++) {
				latitude = query.getDouble(query
						.getColumnIndex(LocationsContentProvider.LATITUDE_KEY));
				longitude = query
						.getDouble(query
								.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY));
				address = query.getString(query
						.getColumnIndex(LocationsContentProvider.ADDRESS_KEY));

				if (firstLoading)
					map.addMarker(new MarkerOptions()
							.position(new LatLng(latitude, longitude))
							.title(address).alpha(0.7f)
							.snippet(getString(R.string.marker_directions)));

				// ovo je za najblize lokacije
				if (i < 5) {
					nearestLocations[i] = new Locations(latitude, longitude);
				} else {
					if (i == 5) {
						sortNearestLocations(location, 5);
					}

					int j;
					float[] nearestResult = new float[1];
					float[] currResult = new float[1];
					for (j = 4; j > -1; j--) {
						Location.distanceBetween(location.getLatitude(),
								location.getLongitude(),
								nearestLocations[j].getLatitude(),
								nearestLocations[j].getLongitude(),
								nearestResult);
						Location.distanceBetween(location.getLatitude(),
								location.getLongitude(), latitude, longitude,
								currResult);

						if (currResult[0] >= nearestResult[0])
							break;
					}

					if (j == 4)
						continue;

					for (int k = 4; k > j + 1; k--)
						nearestLocations[k] = new Locations(
								nearestLocations[k - 1]);
					nearestLocations[j + 1] = new Locations(latitude, longitude);
				}
			}
		}
		query.close();

		if (i <= 5)
			sortNearestLocations(location, i);

		// nasao sam najblize lokacije, sad treba da nacrtam krug
		float[] maxDist = new float[1];
		int lastIdx = Math.min(i - 1, 4); // ako ima manje od 5 lokacija
		Location.distanceBetween(location.getLatitude(),
				location.getLongitude(),
				nearestLocations[lastIdx].getLatitude(),
				nearestLocations[lastIdx].getLongitude(), maxDist);

		for (int x = 0; x <= lastIdx; x++)
			Log.d("NEAREST_LOCATIONS " + (x + 1),
					"Lat: " + nearestLocations[x].getLatitude() + " / Lng: "
							+ nearestLocations[x].getLongitude());

		if (nearestCircle != null)
			nearestCircle.remove();
		new Color();
		nearestCircle = map.addCircle(new CircleOptions()
				.center(new LatLng(location.getLatitude(), location
						.getLongitude())).radius(maxDist[0])
				.strokeColor(Color.RED).fillColor(Color.TRANSPARENT)
				.strokeWidth(1).fillColor(Color.argb(25, 255, 0, 0)));

		if (firstLoading) {
			// da bi se svih 5 lokacija videlo na mapi
			LatLng point = new LatLng(nearestLocations[0].getLatitude(),
					nearestLocations[0].getLongitude());
			LatLngBounds bounds = new LatLngBounds(point, point);

			for (int asd = 1; asd <= lastIdx; asd++)
				bounds = bounds.including(new LatLng(nearestLocations[asd]
						.getLatitude(), nearestLocations[asd].getLongitude()));

			// bounds = bounds.including(new LatLng(nearestLocations[1]
			// .getLatitude(), nearestLocations[1].getLongitude()));
			// bounds = bounds.including(new LatLng(nearestLocations[2]
			// .getLatitude(), nearestLocations[2].getLongitude()));
			// bounds = bounds.including(new LatLng(nearestLocations[3]
			// .getLatitude(), nearestLocations[3].getLongitude()));
			// bounds = bounds.including(new LatLng(nearestLocations[4]
			// .getLatitude(), nearestLocations[4].getLongitude()));
			// da se vidi i trenutna lokacija
			bounds = bounds.including(new LatLng(location.getLatitude(),
					location.getLongitude()));

			// map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
		}

		firstLoading = false;
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

		// final LatLngBounds bounds = new LatLngBounds(new LatLng(Math.min(
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

	private class Locations {
		private double latitude, longitude;

		public Locations(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public Locations(Locations location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}
	}
}
