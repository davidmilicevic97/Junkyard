package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesCommonUtilities {

	public static boolean isZoomSetupEnabled(Context context) {
		SharedPreferences defaultPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		boolean isEnabled = defaultPreferences.getBoolean(
				"PREF_ALLOW_AUTO_ZOOM", false);

		return isEnabled;
	}

	public static int getZoomLevel(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);

		int zoomLevel = preferences.getInt(
				ZoomPreferencesDialog.PREF_SEEKBAR_MAP_PREFS, 0);

		return zoomLevel;
	}

	public static int getLocationsSortOrder(Context context) {
		SharedPreferences defaultPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		String sortOrder = defaultPreferences.getString("PREF_SORT_LOCATIONS",
				"1");
		int ret = Integer.parseInt(sortOrder);

		return ret;
	}

}
