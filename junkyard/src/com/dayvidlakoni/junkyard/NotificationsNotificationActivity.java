package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

// obrisi iz manifesta kad ne bude trebalo vise!!!
public class NotificationsNotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications_notification_activity);

		NotificationFragment nf = new NotificationFragment();
		nf.setArguments(getIntent().getExtras());

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction
				.replace(R.id.notificationsnotificationactivitycontainer, nf);
		transaction.commit();
	}

}
