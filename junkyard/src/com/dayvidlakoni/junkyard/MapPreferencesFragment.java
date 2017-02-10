package com.dayvidlakoni.junkyard;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class MapPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.map_preferences);

		final Preference button = (Preference) findPreference("MAP_PREF_ZOOM_DIALOG_BUTTON");
		final CheckBoxPreference allowZoom = (CheckBoxPreference) findPreference("PREF_ALLOW_AUTO_ZOOM");

		button.setEnabled(allowZoom.isChecked());
		button.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showZoomDialog();
				return true;
			}
		});

		allowZoom
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						// radim ovako zato sto se ovo poziva pre nego sto se
						// promeni checkbox
						// tako da ovo znaci da trenutno nije checked i da ce
						// postati
						button.setEnabled(!allowZoom.isChecked());

						return true;
					}
				});

	}

	private void showZoomDialog() {
		ZoomPreferencesDialog zoomDialog = new ZoomPreferencesDialog();
		zoomDialog.show(getFragmentManager(), "ZOOM_DIALOG");
	}

}
