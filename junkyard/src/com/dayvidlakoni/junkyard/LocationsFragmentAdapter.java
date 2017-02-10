package com.dayvidlakoni.junkyard;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationsFragmentAdapter extends ArrayAdapter<String> {
	private Context context;
	private int resource;

	public LocationsFragmentAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);

		this.context = context;
		this.resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("LOCATIONS_FRAGMENT_ADAPTER", "getView(" + position + ")");

		LinearLayout view;

		if (convertView == null) {
			view = new LinearLayout(context);
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			li.inflate(resource, view, true);
		} else {
			view = (LinearLayout) convertView;
		}

		int imageViewResource;
		String textViewText;
		switch (position) {
		case 0:
			imageViewResource = R.drawable.icon_type;
			break;
		case 1:
			imageViewResource = R.drawable.icon_nearest;
			// textViewText = context
			// .getString(R.string.locationsfragment_nearest);
			break;
		case 2:
			imageViewResource = R.drawable.icon_favorites;
			// textViewText =
			// context.getString(R.string.locationsfragment_favs);
			break;
		case 3:
			imageViewResource = R.drawable.icon_list;
			// textViewText =
			// context.getString(R.string.locationsfragment_list);
			break;
		case 4:
			imageViewResource = R.drawable.icon_map;
			// textViewText = context.getString(R.string.locationsfragment_map);
			break;
		default:
			imageViewResource = R.drawable.ikonica;
			// textViewText = "";
			break;
		}

		textViewText = getItem(position);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.iv_locations_fragment_item);
		TextView textView = (TextView) view
				.findViewById(R.id.tv_locations_fragment_item);

		imageView.setImageResource(imageViewResource);
		textView.setText(textViewText);

		return view;
	}
}
