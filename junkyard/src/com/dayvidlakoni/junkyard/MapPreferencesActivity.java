package com.dayvidlakoni.junkyard;

import java.util.List;

import android.preference.PreferenceActivity;

public class MapPreferencesActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.map_preferences_headers, target);
	}

}
