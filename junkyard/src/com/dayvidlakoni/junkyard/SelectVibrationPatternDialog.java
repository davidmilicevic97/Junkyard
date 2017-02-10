package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectVibrationPatternDialog extends DialogFragment {

	public static final String PREF_VIBRATION_REPEATING = "PREF_VIBRATION_REPEATING";
	public static final String PREF_VIBRATION_ON = "PREF_VIBRATION_ON";
	public static final String PREF_VIBRATION_OFF = "PREF_VIBRATION_OFF";

	private EditText etRepeating, etOn, etOff;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEditor;

	public SelectVibrationPatternDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

		LinearLayout view = new LinearLayout(getActivity());
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.select_vibration_pattern_dialog_view, view, true);
		ad.setView(view);

		etRepeating = (EditText) view
				.findViewById(R.id.etVibrationDialogRepeating);
		etOn = (EditText) view.findViewById(R.id.etVibrationDialogOnDuration);
		etOff = (EditText) view.findViewById(R.id.etVibrationDialogOffDuration);

		// pozivam ovde zato sto mi trebaju edittext-ovi
		initialize();

		ad.setPositiveButton(
				getString(R.string.notificationprefs_vibrationprefs_dialogPositiveButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isInputCorrect()) {
							prefsEditor.putString(PREF_VIBRATION_REPEATING,
									etRepeating.getText().toString());
							prefsEditor.putString(PREF_VIBRATION_ON, etOn
									.getText().toString());
							prefsEditor.putString(PREF_VIBRATION_OFF, etOff
									.getText().toString());
							prefsEditor.apply();
						} else {
							Animation shakeEditText = AnimationUtils
									.loadAnimation(getActivity(),
											R.anim.textbox_shake);
							etRepeating.startAnimation(shakeEditText);
							etOn.startAnimation(shakeEditText);
							etOff.startAnimation(shakeEditText);

							Toast.makeText(
									getActivity(),
									getString(R.string.notificationprefs_vibrationprefs_dialogIncorrectInput),
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		ad.setNegativeButton(
				getString(R.string.notificationprefs_vibrationprefs_dialogNegativeButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		ad.setNeutralButton(
				getString(R.string.notificationprefs_vibrationprefs_dialogNeutralButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ovde nista ne radim, vec sve radim u onStart() kako
						// mi se ne bi gasio dijalog ako je los input
						// ipak, treba ovo da ostane ovde, da bi se button
						// napravio
					}
				});

		ad.setCancelable(false);
		return ad.create();
	}

	@Override
	public void onStart() {
		super.onStart();

		AlertDialog dialog = (AlertDialog) getDialog();
		if (dialog != null) {
			Button neutralButton = (Button) dialog
					.getButton(Dialog.BUTTON_NEUTRAL);
			neutralButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isInputCorrect()) {
						Vibrator vibrator = (Vibrator) getActivity()
								.getSystemService(Context.VIBRATOR_SERVICE);

						int repeating = Integer.parseInt(etRepeating.getText()
								.toString());
						int on = Integer.parseInt(etOn.getText().toString());
						int off = Integer.parseInt(etOff.getText().toString());

						if (repeating > 0) {
							repeating *= 2;
							long[] pattern = new long[repeating];
							pattern[0] = 0;
							pattern[1] = on;
							for (int i = 2; i < repeating; i += 2) {
								pattern[i] = off;
								pattern[i + 1] = on;
							}

							vibrator.vibrate(pattern, -1);
						}
					} else {
						Animation shakeEditText = AnimationUtils.loadAnimation(
								getActivity(), R.anim.textbox_shake);
						etRepeating.startAnimation(shakeEditText);
						etOn.startAnimation(shakeEditText);
						etOff.startAnimation(shakeEditText);

						Toast.makeText(
								getActivity(),
								getString(R.string.notificationprefs_vibrationprefs_dialogIncorrectInput),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

	}

	// proveravam da li je uneto makar nesto u edittext-ove
	private boolean isInputCorrect() {
		return !etRepeating.getText().toString().isEmpty()
				&& !etOn.getText().toString().isEmpty()
				&& !etOff.getText().toString().isEmpty();
	}

	// trebalo bi da ovde postavlja tekst u edittext-ove i jos nesto ako treba
	// :D kao na primer, da nadje prefs
	private void initialize() {
		prefs = getActivity().getSharedPreferences("JUNKYARD_PREFS",
				Activity.MODE_PRIVATE);
		prefsEditor = prefs.edit();

		String repeating = prefs.getString(PREF_VIBRATION_REPEATING, "");
		String on = prefs.getString(PREF_VIBRATION_ON, "");
		String off = prefs.getString(PREF_VIBRATION_OFF, "");

		etRepeating.setText(repeating);
		etOn.setText(on);
		etOff.setText(off);
	}
}
