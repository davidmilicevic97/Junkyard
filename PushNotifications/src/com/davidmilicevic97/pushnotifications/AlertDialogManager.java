package com.davidmilicevic97.pushnotifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AlertDialogManager {
	/**
	 * 
	 * @param context - app context
	 * @param title - dialog title
	 * @param message - dialog message
	 * @param status - success/failure (used to set icon)
	 * 				 - pass null if you don't want icon
	 */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {

		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		if (status != null)
			alertDialog
					.setIcon((status) ? R.drawable.success : R.drawable.fail);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		alertDialog.show();
	}
}
