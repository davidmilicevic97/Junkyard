package com.dayvidlakoni.junkyard;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class HelpPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.help_preferences);

		init();
	}

	private void init() {
		final Preference about = (Preference) findPreference("HELP_PREFS_ABOUT");
		final Preference bug = (Preference) findPreference("HELP_PREFS_BUG");
		final Preference suggest = (Preference) findPreference("HELP_PREFS_SUGGEST");

		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent aboutIntent = new Intent(getActivity(), About.class);
				startActivity(aboutIntent);
				return true;
			}
		});

		bug.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent bugIntent = new Intent(getActivity(), Bug.class);
				startActivity(bugIntent);
				return true;
			}
		});

		suggest.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent suggestIntent = new Intent(getActivity(),
						AddNewLocationActivity.class);
				startActivity(suggestIntent);
				return true;
			}
		});

	}
}
