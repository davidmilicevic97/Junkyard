package com.dayvidlakoni.junkyard;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationsAdapter extends ArrayAdapter<ArrayList<String>> {

	private Context context;
	private int resource;

	public NotificationsAdapter(Context context, int textViewResourceId,
			ArrayList<ArrayList<String>> objects) {
		super(context, textViewResourceId, objects);

		this.context = context;
		this.resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view;

		ArrayList<String> notification = getItem(position);

		String title = notification.get(0);
		String text = notification.get(1);
		String time = notification.get(2);
		final String imageUrl = notification.get(3);

		Log.d("NotificationsAdapter", "getView[" + position + "] : " + title
				+ " / " + text + " / " + time + " / " + imageUrl);

		if (convertView == null) {
			view = new LinearLayout(context);
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			li.inflate(resource, view, true);
		} else {
			view = (LinearLayout) convertView;
		}

		TextView tvTitle = (TextView) view
				.findViewById(R.id.tvNotificationItemTitle);
		TextView tvText = (TextView) view
				.findViewById(R.id.tvNotificationItemText);
		TextView tvTime = (TextView) view
				.findViewById(R.id.tvNotificationItemTime);

		tvTitle.setText(title);
		tvText.setText(text);
		tvTime.setText(time);
		setImage(view, imageUrl);

		return view;
	}

	private void setImage(View view, final String imageUrl) {
		final ImageView image = (ImageView) view
				.findViewById(R.id.ivNotificationItemImage);

		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				InputStream is = null;
				try {
					is = (InputStream) new URL(imageUrl).getContent();
				} catch (Exception e) {
					e.printStackTrace();
				}
				final Drawable d = Drawable.createFromStream(is, "src name");

				return d;
			}

			@Override
			protected void onPostExecute(Drawable result) {
				image.setImageDrawable(result);
				image.invalidate();
			}

		}.execute();

	}

}
