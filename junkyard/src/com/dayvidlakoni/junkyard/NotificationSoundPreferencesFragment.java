package com.dayvidlakoni.junkyard;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;

public class NotificationSoundPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notification_preferences_sound_fragment);

		final CheckBoxPreference defaultRingtone = (CheckBoxPreference) findPreference("PREF_NOTIFICATION_DEFAULT_RINGTONE");
		final RingtonePreference ringtone = (RingtonePreference) findPreference("PREF_NOTIFICATION_RINGTONE");

		ringtone.setEnabled(!defaultRingtone.isChecked());
		defaultRingtone
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						ringtone.setEnabled(defaultRingtone.isChecked());

						return true;
					}
				});

	}

}
