package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class FilterLocationsDialog extends DialogFragment {

	public static String PREF_TYPES_INT = "PREF_TYPES_INT";
	public static String PREF_TYPES_STRING = "PREF_TYPES_STRING";

	private int selected = 0;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		final SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		selected = prefs.getInt(PREF_TYPES_INT, 0);

		builder.setSingleChoiceItems(R.array.types, selected,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
					}
				})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt(PREF_TYPES_INT, selected);

						String type = getActivity().getResources()
								.getStringArray(R.array.database_types)[selected];
						editor.putString(PREF_TYPES_STRING, type);

						editor.apply();
					}
				}).setNegativeButton("Cancel", null);

		return builder.create();
	}
}
