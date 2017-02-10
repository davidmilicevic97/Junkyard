package com.dayvidlakoni.junkyard;

import java.util.List;

import android.preference.PreferenceActivity;

public class NotificationPreferencesActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.notification_preferences_headers, target);
	}
}
