package com.dayvidlakoni.junkyard;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class UserPreferencesFragment extends PreferenceFragment {

	public static String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_preferences);

		final CheckBoxPreference autoUpdate = (CheckBoxPreference) findPreference(PREF_AUTO_UPDATE);

		autoUpdate
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						// pitam ovako zato sto se ovo poziva pre nego sto se
						// promeni checkbox
						// tako da ovo znaci da trenutno nije checked i da ce
						// postati
						if (!autoUpdate.isChecked()) {
							getActivity().startService(
									new Intent(getActivity(),
											LocationsUpdateService.class));
						}

						return true;
					}
				});
	}

}
