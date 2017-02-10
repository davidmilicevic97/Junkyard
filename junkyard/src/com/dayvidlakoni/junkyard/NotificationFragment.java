package com.dayvidlakoni.junkyard;

import java.io.InputStream;
import java.net.URL;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationFragment extends Fragment {

	private TextView time, title, text;
	private View rootView;

	public NotificationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.notification_fragment,
				container, false);

		this.rootView = rootView;

		time = (TextView) rootView.findViewById(R.id.tvNotificationTime);
		title = (TextView) rootView.findViewById(R.id.tvNotificationTitle);
		text = (TextView) rootView.findViewById(R.id.tvNotificationText);

		time.setText(getArguments().getString(
				GCMCommonUtilities.NOTIFICATION_TIME));
		title.setText(getArguments().getString(
				GCMCommonUtilities.NOTIFICATION_TITLE));
		text.setText(getArguments().getString(
				GCMCommonUtilities.NOTIFICATION_TEXT));

		setImage(rootView,
				getArguments().getString(GCMCommonUtilities.NOTIFICATION_IMAGE));

		return rootView;
	}

	public void changeNotification(String notificationTitle,
			String notificationText, String notificationTime,
			String notificationImageUrl) {

		title.setText(notificationTitle);
		text.setText(notificationText);
		time.setText(notificationTime);
		setImage(rootView, notificationImageUrl);
	}

	private void setImage(View rootView, final String imageUrl) {
		final ImageView image = (ImageView) rootView
				.findViewById(R.id.ivNotificationImage);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream is = (InputStream) new URL(imageUrl)
							.getContent();
					final Drawable d = Drawable
							.createFromStream(is, "src name");

					Log.d("FAAK", "Image loaded");

					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							image.setImageDrawable(d);
							image.invalidate();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

}
