package com.dayvidlakoni.junkyard;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationUpdate {

	public interface OnLocationUpdatedListener {
		public void locationUpdated(Location location);
	}

	private OnLocationUpdatedListener onLocationUpdated;

	private LocationManager locationManager;

	private LocationListener GPSLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("LOCATION_UPDATE", "GPS Provider enabled!");

			locationManager.removeUpdates(NetworkLocationListener);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("LOCATION_UPDATE", "GPS Provider disabled!");

			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, time, distance,
					NetworkLocationListener);
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d("LOCATION_UPDATE", "Location changed (GPS)");

			onLocationUpdated.locationUpdated(location);
		}
	};

	private LocationListener NetworkLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("LOCATION_UPDATE", "Network Provider enabled!");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("LOCATION_UPDATE", "Network Provider disabled!");
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d("LOCATION_UPDATE", "Location changed (Network)");

			onLocationUpdated.locationUpdated(location);
		}
	};

	private static final int time = 5000;
	private static final int distance = 5;

	public LocationUpdate(Context context, Fragment instance) {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		onLocationUpdated = (OnLocationUpdatedListener) instance;
	}

	public void startUpdatingLocation() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				time, distance, GPSLocationListener);
	}

	public void stopUpdatingLocation() {
		locationManager.removeUpdates(GPSLocationListener);
		locationManager.removeUpdates(NetworkLocationListener);
	}

	public Location getLastKnownLocation() {
		return locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
}
