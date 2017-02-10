package com.dayvidlakoni.junkyard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class NoInternetConnectionFirstTimeDialog extends DialogFragment {

	public NoInternetConnectionFirstTimeDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity(),
				AlertDialog.THEME_HOLO_LIGHT);

		LinearLayout view = new LinearLayout(getActivity());
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.no_connection_dialog_view, view, true);
		ad.setView(view);

		ad.setPositiveButton(
				getString(R.string.noconnectiondialog_dialogPositiveButton),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
					}
				});

		ad.setCancelable(false);

		return ad.create();
	}

}
