package com.dayvidlakoni.junkyard;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class NotificationVibrationPreferencesFragment extends
		PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notification_preferences_vibration_fragment);

		final CheckBoxPreference defaultVibration = (CheckBoxPreference) findPreference("PREF_NOTIFICATION_DEFAULT_VIBRATION");
		final Preference vibrationPatternButton = (Preference) findPreference("PREF_NOTIFICATION_VIBRATION");

		vibrationPatternButton.setEnabled(!defaultVibration.isChecked());
		defaultVibration
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						vibrationPatternButton.setEnabled(defaultVibration
								.isChecked());

						return true;
					}
				});

		vibrationPatternButton
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						showVibrationPatternDialog();

						return true;
					}
				});
	}

	private void showVibrationPatternDialog() {
		SelectVibrationPatternDialog vibrationDialog = new SelectVibrationPatternDialog();
		vibrationDialog.show(getFragmentManager(), "VIBRATION_DIALOG");
	}

}
