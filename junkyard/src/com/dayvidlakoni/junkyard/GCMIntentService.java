package com.dayvidlakoni.junkyard;

import static com.dayvidlakoni.junkyard.GCMCommonUtilities.SENDER_ID;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String PREF_NOTIFICATION_DEFAULT_RINGTONE_BOOLEAN = "PREF_NOTIFICATION_DEFAULT_RINGTONE";
	private static final String PREF_NOTIFICATION_RINGTONE = "PREF_NOTIFICATION_RINGTONE";
	private static final String PREF_NOTIFICATION_DEFAULT_VIBRATION_BOOLEAN = "PREF_NOTIFICATION_DEFAULT_VIBRATION";
	private static final String PREF_NOTIFICATION_ID = "PREF_NOTIFICATION_ID";
	private static final String PREF_NOTIFICATION_DEFAULT_LED = "PREF_NOTIFICATION_DEFAULT_LED";
	private static final String PREF_NOTIFICATION_LED = "PREF_NOTIFICATION_LED";

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		GCMServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// // DEBUUUUUUG ^^
		// // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// NotificationManager notificationManager = (NotificationManager)
		// context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification.Builder nBuilder = new Notification.Builder(context);
		//
		// nBuilder.setTicker("junkyard").setContentTitle("onUnregistered")
		// .setContentText("Pozvan je onUnregistered u GCMIntentService!")
		// .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
		// Notification notification = nBuilder.getNotification();
		//
		// notificationManager.notify(1000000000, notification);
		// //
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// // GOTOV DEBUG

		Log.i(TAG, "Device unregistered");
		GCMServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");

		String notificationTitle = intent.getExtras().getString(
				GCMCommonUtilities.NOTIFICATION_TITLE);
		String notificationText = intent.getExtras().getString(
				GCMCommonUtilities.NOTIFICATION_TEXT);
		String notificationTime = intent.getExtras().getString(
				GCMCommonUtilities.NOTIFICATION_TIME);
		String notificationImageUrl = intent.getExtras().getString(
				GCMCommonUtilities.NOTIFICATION_IMAGE);

		generateNotification(context, notificationTime, notificationTitle,
				notificationText, notificationImageUrl);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	private static void generateNotification(Context context, String time,
			String title, String text, String imageUrl) {
		int icon = R.drawable.notification_icon;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nBuilder = new Notification.Builder(context);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// Bundle args = new Bundle();
		// args.putString(GCMCommonUtilities.NOTIFICATION_TIME, time);
		// args.putString(GCMCommonUtilities.NOTIFICATION_TITLE, title);
		// args.putString(GCMCommonUtilities.NOTIFICATION_TEXT, text);
		// args.putString(GCMCommonUtilities.NOTIFICATION_IMAGE, imageUrl);
		// notificationIntent.putExtras(args);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.notification_icon_large);
		nBuilder.setSmallIcon(icon).setLargeIcon(largeIcon)
				.setTicker("junkyard").setWhen(when).setContentTitle(title)
				.setContentText(text).setContentInfo("info")
				.setContentIntent(intent).setAutoCancel(true);

		// getting preferences
		SharedPreferences defPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences prefs = context.getSharedPreferences(
				"JUNKYARD_PREFS", Activity.MODE_PRIVATE);

		// setting led
		boolean defaultLED = defPrefs.getBoolean(PREF_NOTIFICATION_DEFAULT_LED,
				true);
		if (defaultLED) {
			Log.d("NOTIFICATION_LED", "It's default!");

			nBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
		} else {
			String colorString = defPrefs.getString(PREF_NOTIFICATION_LED,
					"#ff00ff00");

			Log.d("NOTIFICATION_LED", "It's not default! colorString = "
					+ colorString);

			int color = 0;
			try {
				Log.d("NOTIFICATION_LED", "It worked");
				color = Color.parseColor(colorString);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			nBuilder.setLights(color, 200, 1500);

		}
		// end of setting led

		// getting notification
		Notification notification = nBuilder.getNotification();

		// setting sound
		boolean defaultSound = defPrefs.getBoolean(
				PREF_NOTIFICATION_DEFAULT_RINGTONE_BOOLEAN, true);
		if (defaultSound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		} else {
			String soundUriString = defPrefs.getString(
					PREF_NOTIFICATION_RINGTONE, null);

			Log.i(TAG, "Notification sound URI: " + soundUriString);

			Uri soundUri = Uri.parse(soundUriString);
			notification.sound = soundUri;
		}
		// end of setting sound

		// setting vibration
		boolean defaultVibration = defPrefs.getBoolean(
				PREF_NOTIFICATION_DEFAULT_VIBRATION_BOOLEAN, true);

		if (defaultVibration) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		} else {
			int repeating = Integer
					.parseInt(prefs
							.getString(
									SelectVibrationPatternDialog.PREF_VIBRATION_REPEATING,
									"1"));
			int on = Integer.parseInt(prefs.getString(
					SelectVibrationPatternDialog.PREF_VIBRATION_ON, "300"));
			int off = Integer.parseInt(prefs.getString(
					SelectVibrationPatternDialog.PREF_VIBRATION_OFF, "0"));

			if (repeating > 0) {
				repeating *= 2;
				long[] pattern = new long[repeating];
				pattern[0] = 0;
				pattern[1] = on;
				for (int i = 2; i < repeating; i += 2) {
					pattern[i] = off;
					pattern[i + 1] = on;
				}

				notification.vibrate = pattern;
			} else {
				notification.vibrate = null;
			}
		}
		// end of setting vibration

		int id = prefs.getInt(PREF_NOTIFICATION_ID, 1);
		notificationManager.notify(id, notification);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(PREF_NOTIFICATION_ID, id + 1);
		editor.apply();
	}
}
