package com.dayvidlakoni.junkyard;

import java.util.List;

import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.prefereneces_headers, target);
	}
}