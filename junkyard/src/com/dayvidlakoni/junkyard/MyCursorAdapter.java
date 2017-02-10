package com.dayvidlakoni.junkyard;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MyCursorAdapter extends SimpleCursorAdapter {

	private int layout;
	// private Context context;
	private String[] from;
	private int[] to;
	private TextView tv1, tv2;
	private String s1, s2;

	public MyCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);

		// this.context = context;
		this.layout = layout;
		this.from = from;
		this.to = to;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		Log.d("MY_CURSOR_ADAPTER", "newView() " + cursor.getPosition());

		if (cursor == null)
			return null; // mislim da ovo treba

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = li.inflate(layout, parent, false);

		s1 = cursor.getString(cursor.getColumnIndex(from[0]));
		s2 = cursor.getString(cursor.getColumnIndex(from[1]));
		tv1 = (TextView) view.findViewById(to[0]);
		tv2 = (TextView) view.findViewById(to[1]);

		tv1.setText(s1);

		int id = context.getResources().getIdentifier("type_" + s2, "string",
				context.getPackageName());

		tv2.setText(context.getResources().getString(id));

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// super.bindView(view, context, cursor);

		s1 = cursor.getString(cursor.getColumnIndex(from[0]));
		s2 = cursor.getString(cursor.getColumnIndex(from[1]));
		tv1 = (TextView) view.findViewById(to[0]);
		tv2 = (TextView) view.findViewById(to[1]);

		tv1.setText(s1);

		int id = context.getResources().getIdentifier("type_" + s2, "string",
				context.getPackageName());

		tv2.setText(context.getResources().getString(id));
	}

}
