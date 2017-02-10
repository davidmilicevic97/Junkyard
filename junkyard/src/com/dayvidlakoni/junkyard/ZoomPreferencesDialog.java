package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ZoomPreferencesDialog extends DialogFragment {

	public static final String PREF_SEEKBAR_MAP_PREFS = "PREF_SEEKBAR_MAP_PREFS";

	public ZoomPreferencesDialog() {

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

		LinearLayout view = new LinearLayout(getActivity());
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.zoom_dialog_custom_view_layout, view, true);
		ad.setView(view);

		SharedPreferences prefs = getActivity().getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		final SeekBar sb = (SeekBar) view.findViewById(R.id.sbZoom);
		final TextView sbProgress = (TextView) view
				.findViewById(R.id.tvZoomSeekBarProgress);
		sb.setMax(21);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser)
					sbProgress.setText(progress + "");
				sbProgress.setFreezesText(true);
			}
		});

		ad.setPositiveButton(
				getString(R.string.zoomdialog_dialogPositiveButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putInt(PREF_SEEKBAR_MAP_PREFS, sb.getProgress());
						editor.apply();

						Log.d("ZOOM_LEVEL", sb.getProgress() + "");
					}
				});
		ad.setNegativeButton(
				getString(R.string.zoomdialog_dialogNegativeButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		int zoomLevel = prefs.getInt(PREF_SEEKBAR_MAP_PREFS, 0);
		sb.setProgress(zoomLevel);
		sbProgress.setText(zoomLevel + "");

		ad.setCancelable(false);

		return ad.create();
	}

}
