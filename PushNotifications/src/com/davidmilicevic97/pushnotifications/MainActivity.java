package com.davidmilicevic97.pushnotifications;

import static com.davidmilicevic97.pushnotifications.CommonUtilities.SENDER_ID;
import static com.davidmilicevic97.pushnotifications.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.davidmilicevic97.pushnotifications.CommonUtilities.EXTRA_MESSAGE;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView lblMessage;

	AsyncTask<Void, Void, Void> mRegisterTask;

	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;

	public static String name, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cd = new ConnectionDetector(getApplicationContext());

		// check for Internet connection
		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		}

		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		email = intent.getStringExtra("email");

		// make sure the device has the proper dependencies
		GCMRegistrar.checkDevice(this);

		// make sure the manifest was properly set - comment out this line while
		// developing the app, the uncomment it when it's ready
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		// get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// check if regid already presents
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Toast.makeText(getApplicationContext(),
						"Already registered with GCM", Toast.LENGTH_LONG)
						.show();
			} else {
				// try to register again, but not on the UI thread
				// it's also necessary to cancel the thread onDestroy(), hence
				// the use of AsyncTask instead of raw thread
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// register on our server
						// on server creates a new user
						ServerUtilities.register(context, name, email, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}

		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("Unregister Receiver Error", "> " + e.getMessage());
		}

		super.onDestroy();

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

			WakeLocker.acquire(getApplicationContext());

			lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			WakeLocker.release();
		}
	};

}
