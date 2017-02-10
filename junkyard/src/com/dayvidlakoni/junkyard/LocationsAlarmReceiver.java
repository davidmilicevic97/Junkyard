package com.dayvidlakoni.junkyard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationsAlarmReceiver extends BroadcastReceiver {

	public static final String ACTION_REFRESH_LOCATIONS_ALARM = "com.dayvidlakoni.junkyard.ACTION_REFRESH_LOCATIONS_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LOCATIONS_ALARM_RECEIVER", "Alarm received!");

		Intent startIntent = new Intent(context, LocationsUpdateService.class);
		context.startService(startIntent);
	}

}
