package com.dayvidlakoni.junkyard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Bug extends Activity {

	private Button submit;
	private EditText etBug;
	String bug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bug_layout);

		submit = (Button) findViewById(R.id.bSubmitBug);
		etBug = (EditText) findViewById(R.id.etBug);

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bug = etBug.getText().toString();

				if (isInputCorrect()) {
					sendEmail();
				} else {
					Animation shakeEditText = AnimationUtils.loadAnimation(
							Bug.this, R.anim.textbox_shake);
					etBug.startAnimation(shakeEditText);

					Toast.makeText(Bug.this, getString(R.string.describe_bug),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private boolean isInputCorrect() {
		return bug.length() > 50;
	}

	private void sendEmail() {
		String[] to = { "davidmilicevic97@gmail.com" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug");
		emailIntent.putExtra(Intent.EXTRA_TEXT, bug);

		try {
			startActivity(Intent.createChooser(emailIntent,
					getString(R.string.send_email)));
			finish();
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(Bug.this, getString(R.string.no_email_client),
					Toast.LENGTH_LONG).show();
		}
	}
}
