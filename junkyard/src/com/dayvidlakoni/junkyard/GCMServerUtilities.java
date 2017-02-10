package com.dayvidlakoni.junkyard;

import static com.dayvidlakoni.junkyard.GCMCommonUtilities.SERVER_URL;
import static com.dayvidlakoni.junkyard.GCMCommonUtilities.SERVER_URL_UNREGISTER;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class GCMServerUtilities {

	private static final String TAG = "GCMServerUtilities";

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	static void register(final Context context, final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");

		String serverUrl = SERVER_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// kad GCM vrati registrationId pokusavam da obavestim svoj server
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attemp #" + i + " to register");

			try {
				post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				return;
			} catch (IOException e) {
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);

				if (i == MAX_ATTEMPTS)
					break;

				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				backoff *= 2;
			}
		}
	}

	static void unregister(final Context context, final String regId) {
		// // DEBUUUUUUG ^^
		// // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// NotificationManager notificationManager = (NotificationManager)
		// context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification.Builder nBuilder = new Notification.Builder(context);
		//
		// nBuilder.setTicker("junkyard").setContentTitle("unregister")
		// .setContentText("Pozvan je i unregister u GCMServerUtilities!")
		// .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
		// Notification notification = nBuilder.getNotification();
		//
		// notificationManager.notify(1000000001, notification);
		// //
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// // GOTOV DEBUG

		Log.i(TAG, "Unregistering device (regId = " + regId + ")");

		String serverUrl = SERVER_URL_UNREGISTER;
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void post(String endpoint, Map<String, String> params)
			throws IOException {

		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());

			if (iterator.hasNext())
				bodyBuilder.append('&');
		}

		String body = bodyBuilder.toString();

		Log.v(TAG, "Posting '" + body + "' to " + url);

		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			Log.e("URL", "> " + url);

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");

			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();

			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}

		} finally {
			if (conn != null)
				conn.disconnect();
		}

	}

}
