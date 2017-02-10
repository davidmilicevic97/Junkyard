package com.dayvidlakoni.junkyard;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;

public class NotificationPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notification_preferences);

		setupSoundPreferences();
		setupVibrationPreferences();
		setupLEDPreferences();
	}

	private void setupSoundPreferences() {
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

	private void setupVibrationPreferences() {
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

	private void setupLEDPreferences() {
		final CheckBoxPreference defaultLED = (CheckBoxPreference) findPreference("PREF_NOTIFICATION_DEFAULT_LED");
		final ListPreference ledList = (ListPreference) findPreference("PREF_NOTIFICATION_LED");

		ledList.setEnabled(!defaultLED.isChecked());
		defaultLED
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						ledList.setEnabled(defaultLED.isChecked());

						return true;
					}
				});
	}

}
