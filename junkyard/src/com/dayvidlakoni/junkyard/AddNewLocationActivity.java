package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewLocationActivity extends Activity {

	private Button submit;
	private EditText etAddress, etLatitude, etLongitude;
	private String address, latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_location_layout);
		submit = (Button) findViewById(R.id.bSubmit);
		etAddress = (EditText) findViewById(R.id.etAddr);
		etLatitude = (EditText) findViewById(R.id.etLat);
		etLongitude = (EditText) findViewById(R.id.etLong);

		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				address = etAddress.getText().toString();
				latitude = etLatitude.getText().toString();
				longitude = etLongitude.getText().toString();

				if (isInputCorrect())
					sendEmail();
				else {
					Animation shakeEditText = AnimationUtils.loadAnimation(
							AddNewLocationActivity.this, R.anim.textbox_shake);
					etAddress.startAnimation(shakeEditText);
					etLatitude.startAnimation(shakeEditText);
					etLongitude.startAnimation(shakeEditText);

					Toast.makeText(AddNewLocationActivity.this,
							getString(R.string.newlocation_incorrectInput),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void sendEmail() {
		String[] TO = { "davidmilicevic97@gmail.com" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Location Suggestion");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Address: " + address
				+ "\nLatitude: " + latitude + "\nLongitude: " + longitude);

		try {
			startActivity(Intent.createChooser(emailIntent,
					getString(R.string.send_email)));
			finish();
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(AddNewLocationActivity.this,
					getString(R.string.no_email_client), Toast.LENGTH_LONG)
					.show();
		}
	}

	private boolean isInputCorrect() {
		if (!address.isEmpty())
			return true;
		else {
			try {
				double lat = Double.parseDouble(latitude);
				double lng = Double.parseDouble(longitude);

				if (lat > 90 || lat < -90 || lng > 180 || lng < -180)
					return false;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}
	}

}
