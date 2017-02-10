package com.dayvidlakoni.junkyard;

import static com.dayvidlakoni.junkyard.GCMCommonUtilities.SENDER_ID;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

// SPLASH SCREEN
public class MainActivity extends Activity {

	public static final String FIRST_TIME = "FIRST_TIME";

	private AsyncTask<Void, Void, Void> registerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SharedPreferences prefs = getSharedPreferences("JUNKYARD_PREFS",
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		// moram ovako, jer sam morao da iskomentarisem
		// super.onSaveInstanceState u DrawerActivity-ju jer mi je bagovalo zbog
		// toga u layout-u za tablet
		// ovako pamtim koji od 4 mogucnosti u TabletLocationsFragmentList mi je
		// izabran
		editor.putInt("INFLATED_FRAGMENT", 1);
		editor.apply();
		boolean firstTime = prefs.getBoolean(FIRST_TIME, true);

		Log.d("MAIN_ACTIVITY", "App started.");

		if (firstTime) {
			Log.d("MAIN_ACTIVITY", "It's first time!");

			// OVO MORA DA SE PROVERI DA LI RADI LEPO!!!
			ConnectionDetector cd = new ConnectionDetector(MainActivity.this);

			if (cd.isConnected()) {
				Log.d("MAIN_ACTIVITY", "And it's connected to the Internet");

				startService(new Intent(MainActivity.this,
						LocationsUpdateService.class));

				editor = prefs.edit();
				editor.putBoolean(FIRST_TIME, false);
				editor.apply();
			} else {
				Log.d("MAIN_ACTIVITY", "And it's not connected to the Internet");

				NoInternetConnectionFirstTimeDialog dialog = new NoInternetConnectionFirstTimeDialog();
				dialog.show(getFragmentManager(), "NO_INTERNET_CONNECTION");

				// finish();
				return;
			}
		}

		// proveravam da li postoje google play services
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (resultCode == ConnectionResult.SUCCESS) {
			// Toast.makeText(getApplicationContext(),
			// "Google Play Services installed", Toast.LENGTH_LONG).show();
		} else {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, 1, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							finish();
						}
					});

			errorDialog.setCancelable(true);
			errorDialog.show();
			return;
		}

		registerOnGCM();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(MainActivity.this, DrawerActivity.class);
				startActivity(i);

				finish();
			}
		}, 1500);

		// brisem sva obavestenja
		NotificationManager nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	private void registerOnGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		final String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Toast.makeText(getApplicationContext(),
				// "Already registered with GCM", Toast.LENGTH_LONG)
				// .show();
			} else {
				final Context context = this;
				registerTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						GCMServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						registerTask = null;
					}
				};
				registerTask.execute(null, null, null);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

}