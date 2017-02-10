package com.dayvidlakoni.junkyard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabletNotificationsFragment extends Fragment {

	public TabletNotificationsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.tablet_notifications_fragment,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		NotificationsFragment notificationsFragment = new NotificationsFragment();
		
		Bundle args = new Bundle();
		args.putBoolean("TABLET", true);
		notificationsFragment.setArguments(args);

		getFragmentManager()
				.beginTransaction()
				.add(R.id.tablet_notifications_fragment_container,
						notificationsFragment).commit();
	}
}
