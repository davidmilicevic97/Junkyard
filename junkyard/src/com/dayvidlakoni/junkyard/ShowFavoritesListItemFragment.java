package com.dayvidlakoni.junkyard;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowFavoritesListItemFragment extends Fragment implements
		OnMarkerClickListener {

	private MapView mapView;
	private GoogleMap map;
	private Polyline polyline = null;

	public static String LOCATION_LATITUDE = "LOCATION_LATITUDE";
	public static String LOCATION_ADDRESS = "LOCATION_ADDRESS";
	public static String LOCATION_LONGITUDE = "LOCATION_LONGITUDE";

	private double latitude, longitude;
	private String address;

	private boolean isFavorite;
	private long id;
	private ToggleButton tbFav;

	private double fromLatitude, fromLongitude, toLatitude, toLongitude;

	public ShowFavoritesListItemFragment(long id, boolean isFavorite) {
		this.id = id;
		this.isFavorite = isFavorite;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_layout, container, false);

		latitude = getArguments().getDouble(LOCATION_LATITUDE);
		longitude = getArguments().getDouble(LOCATION_LONGITUDE);
		address = getArguments().getString(LOCATION_ADDRESS);

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

		int zoomLevel = 13;
		if (PreferencesCommonUtilities.isZoomSetupEnabled(getActivity()))
			zoomLevel = PreferencesCommonUtilities.getZoomLevel(getActivity());

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				latitude, longitude), zoomLevel));
		map.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude)).title(address)
				.alpha(0.7f).snippet(getString(R.string.marker_directions)));

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

		tbFav = (ToggleButton) rootView.findViewById(R.id.tbFavorite);
		tbFav.setChecked(isFavorite);

		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();

		if (isFavorite != tbFav.isChecked()) {
			if (isFavorite) // ako je bio favorite, sad vise nije
				removeFromFavorites();
			else
				addToFavorites(); // u suprotnom, dodat je u favorites
		}
	}

	private void addToFavorites() {
		String where = LocationsContentProvider.ID_KEY + "=" + id;
		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, null, where, null, null);

		if (query.moveToFirst() == false) {
			Log.d("ShowLocationListItemFragment - FAVORITE CHANGED",
					"No results for query");
		} else {

			ContentValues values = new ContentValues();
			values.put(
					LocationsContentProvider.ADDRESS_KEY,
					query.getString(query
							.getColumnIndex(LocationsContentProvider.ADDRESS_KEY)));
			values.put(
					LocationsContentProvider.LOCATION_KEY,
					query.getString(query
							.getColumnIndex(LocationsContentProvider.LOCATION_KEY)));
			values.put(
					LocationsContentProvider.LATITUDE_KEY,
					query.getDouble(query
							.getColumnIndex(LocationsContentProvider.LATITUDE_KEY)));
			values.put(
					LocationsContentProvider.LONGITUDE_KEY,
					query.getDouble(query
							.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY)));
			values.put(LocationsContentProvider.FAVORITES_KEY, 1);

			getActivity().getContentResolver().update(
					LocationsContentProvider.CONTENT_URI, values, where, null);

		}
		query.close();
	}

	private void removeFromFavorites() {
		String where = LocationsContentProvider.ID_KEY + "=" + id;
		Cursor query = getActivity().getContentResolver().query(
				LocationsContentProvider.CONTENT_URI, null, where, null, null);

		query.moveToFirst();
		ContentValues values = new ContentValues();
		values.put(LocationsContentProvider.ADDRESS_KEY, query.getString(query
				.getColumnIndex(LocationsContentProvider.ADDRESS_KEY)));
		values.put(LocationsContentProvider.LOCATION_KEY, query.getString(query
				.getColumnIndex(LocationsContentProvider.LOCATION_KEY)));
		values.put(LocationsContentProvider.LATITUDE_KEY, query.getDouble(query
				.getColumnIndex(LocationsContentProvider.LATITUDE_KEY)));
		values.put(
				LocationsContentProvider.LONGITUDE_KEY,
				query.getDouble(query
						.getColumnIndex(LocationsContentProvider.LONGITUDE_KEY)));
		values.put(LocationsContentProvider.FAVORITES_KEY, 0);

		getActivity().getContentResolver().update(
				LocationsContentProvider.CONTENT_URI, values, where, null);

		query.close();
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

	@Override
	public boolean onMarkerClick(Marker mark) {
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

		mark.showInfoWindow();

		fromLatitude = map.getMyLocation().getLatitude();
		fromLongitude = map.getMyLocation().getLongitude();
		toLatitude = mark.getPosition().latitude;
		toLongitude = mark.getPosition().longitude;

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
