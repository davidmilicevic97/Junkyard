package com.dayvidlakoni.junkyard;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NotificationLEDPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notification_preferences_led_fragment);
	}

}
