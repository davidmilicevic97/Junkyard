package com.davidmilicevic97.pushnotifications;

import static com.davidmilicevic97.pushnotifications.CommonUtilities.SENDER_ID;
import static com.davidmilicevic97.pushnotifications.CommonUtilities.SERVER_URL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;

	EditText txtName, txtEmail;
	Button btnRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		cd = new ConnectionDetector(getApplicationContext());

		// check for Internet connection
		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		}

		// check if GCM configuration is set
		if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
				|| SENDER_ID.length() == 0) {
			alert.showAlertDialog(getApplicationContext(),
					"Configuration Error",
					"Please set your Server URL and GCM Sender ID", false);

			return;
		}

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();

				if (name.trim().length() > 0 && email.trim().length() > 0) {
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);

					intent.putExtra("name", name);
					intent.putExtra("email", email);
					startActivity(intent);
					
					finish();
				} else {
					alert.showAlertDialog(RegisterActivity.this,
							"Registration Error", "Please enter your details",
							false);
				}
			}
		});
	}

}
