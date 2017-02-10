package com.dayvidlakoni.junkyard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoritesDialog extends DialogFragment {

	public interface OnDeleteClickedListener {
		public void onDeleteClicked(long id);
	};

	private OnDeleteClickedListener deleteInterface;
	private long idClicked;

	public FavoritesDialog(long id, Fragment instance) {
		deleteInterface = (OnDeleteClickedListener) instance;

		idClicked = id;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

		LinearLayout view = new LinearLayout(getActivity());
		LayoutInflater li = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.favorites_fragment_dialog, view, true);
		ad.setView(view);

		TextView tvDelete = (TextView) view
				.findViewById(R.id.tvFavoritesDelete);
		tvDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteInterface.onDeleteClicked(idClicked);
				dismiss();
			}
		});

		return ad.create();
	}

}
