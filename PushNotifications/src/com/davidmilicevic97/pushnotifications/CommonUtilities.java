package com.davidmilicevic97.pushnotifications;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {

	static final String SERVER_URL = "http://davidmilicevic97.comyr.com/push_notifications/register.php";
	static final String SENDER_ID = "476764079166";

	static final String TAG = "Android GCM";

	static final String DISPLAY_MESSAGE_ACTION = "com.davidmilicevic97.pushnotifications.DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";

	/**
	 * This method is defined in common helper because it's used both by the UI
	 * and the background service
	 * 
	 * @param context - app context
	 * @param message - message to be displayed
	 */
	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
