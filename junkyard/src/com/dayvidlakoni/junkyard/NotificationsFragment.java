package com.dayvidlakoni.junkyard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class NotificationsFragment extends ListFragment {

	private static final String GET_ALL_NOTIFICATIONS = "http://davidmilicevic97.comyr.com/junkyard_gcm/get_all_notifications.php";
	private static final List<NameValuePair> PARAMS = new ArrayList<NameValuePair>();

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOTIFICATIONS = "notifications";

	private ArrayList<ArrayList<String>> allNotifications;

	private NotificationFragment notificationFragment;
	private boolean isTablet = false;

	private LoadAllNotifications asyncTask;

	public NotificationsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("NOTIFICATIONS_FRAGMENT", "onCreate()");

		allNotifications = new ArrayList<ArrayList<String>>();

		isTablet = getArguments().getBoolean("TABLET");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		asyncTask = (LoadAllNotifications) new LoadAllNotifications().execute();

		Log.d("NOTIFICATIONS_FRAGMENT", "onViewCreated()");
	}

	@Override
	public void onPause() {
		super.onPause();

		asyncTask.cancel(true);

		Log.d("NOTIFICATIONS_FRAGMENT", "onPause()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
		allNotifications.clear();

		Log.d("NOTIFICATIONS_FRAGMENT", "onDestroy()");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ArrayList<String> notification = allNotifications.get(position);

		String notificationTitle = notification.get(0);
		String notificationText = notification.get(1);
		String notificationTime = notification.get(2);
		String notificationImageUrl = notification.get(3);

		if (isTablet) {
			notificationFragment.changeNotification(notificationTitle,
					notificationText, notificationTime, notificationImageUrl);
		} else {
			Bundle args = new Bundle();
			args.putString(GCMCommonUtilities.NOTIFICATION_TITLE,
					notificationTitle);
			args.putString(GCMCommonUtilities.NOTIFICATION_TEXT,
					notificationText);
			args.putString(GCMCommonUtilities.NOTIFICATION_TIME,
					notificationTime);
			args.putString(GCMCommonUtilities.NOTIFICATION_IMAGE,
					notificationImageUrl);

			Intent showNotification = new Intent(getActivity(),
					NotificationActivity.class);
			showNotification.putExtras(args);
			startActivity(showNotification);
		}

	}

	private JSONObject getAllNotifications() {
		InputStream is = null;
		JSONObject jsonObject = null;
		String json = "";

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(PARAMS, "utf-8");
			String url = GET_ALL_NOTIFICATIONS + "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.d("JSON JSON JSON", json);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	class LoadAllNotifications extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			JSONObject json = getAllNotifications();

			if (json != null) {

				String title, text, time, imageUrl;

				try {
					Log.d("NotificationsFragment JSON", "json nije null");

					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						JSONArray notifications = json
								.getJSONArray(TAG_NOTIFICATIONS);

						for (int i = 0; i < notifications.length(); i++) {
							JSONObject obj = notifications.getJSONObject(i);

							title = obj
									.getString(GCMCommonUtilities.NOTIFICATION_TITLE);
							text = obj
									.getString(GCMCommonUtilities.NOTIFICATION_TEXT);
							time = obj
									.getString(GCMCommonUtilities.NOTIFICATION_TIME);
							imageUrl = obj
									.getString(GCMCommonUtilities.NOTIFICATION_IMAGE);

							Log.d("Notifications array object", title + " / "
									+ text + " / " + time + " / " + imageUrl);

							ArrayList<String> notification = new ArrayList<String>();
							notification.add(title);
							notification.add(text);
							notification.add(time);
							notification.add(imageUrl);

							allNotifications.add(notification);
						}
					} else {
						Log.d("NotificationsFragment JSON",
								"Not successfull...");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.d("NotificationsFragment AsyncTask", "json je null");
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			Log.d("NOTIFICATIONS_FRAGMENT", "AsyncTask onPostExecute()");

			NotificationsAdapter adapter = new NotificationsAdapter(
					getActivity(), R.layout.notifications_fragment_item,
					allNotifications);
			setListAdapter(adapter);

			if (isTablet) {
				ArrayList<String> notification = allNotifications.get(0);

				Bundle args = new Bundle();
				args.putString(GCMCommonUtilities.NOTIFICATION_TITLE,
						notification.get(0));
				args.putString(GCMCommonUtilities.NOTIFICATION_TEXT,
						notification.get(1));
				args.putString(GCMCommonUtilities.NOTIFICATION_TIME,
						notification.get(2));
				args.putString(GCMCommonUtilities.NOTIFICATION_IMAGE,
						notification.get(3));

				notificationFragment = new NotificationFragment();
				notificationFragment.setArguments(args);

				getFragmentManager()
						.beginTransaction()
						.add(R.id.tablet_notification_fragment_container,
								notificationFragment).commit();
			}

		}

	}

}
