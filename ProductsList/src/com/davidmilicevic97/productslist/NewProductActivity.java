package com.davidmilicevic97.productslist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewProductActivity extends Activity {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	EditText inputName, inputPrice, inputDesc;

	// url to create new product
	private static String url_create_product = "http://junkyard.davidmi3.5gbfree.com/products_list/create_product.php";

	// JSON node names
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_product);

		inputName = (EditText) findViewById(R.id.inputName);
		inputPrice = (EditText) findViewById(R.id.inputPrice);
		inputDesc = (EditText) findViewById(R.id.inputDesc);

		Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);
		btnCreateProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// creating new product in background thread
				new CreateNewProduct().execute();
			}
		});

	}

	// background AsyncTask to create new product
	class CreateNewProduct extends AsyncTask<String, String, String> {

		// before starting background thread show progress dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(NewProductActivity.this);
			pDialog.setMessage("Creating product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// creating product
		@Override
		protected String doInBackground(String... args) {
			String name = inputName.getText().toString();
			String price = inputPrice.getText().toString();
			String description = inputDesc.getText().toString();

			// building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("price", price));
			params.add(new BasicNameValuePair("description", description));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);

			// JSON response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent intent = new Intent(getApplicationContext(), AllProductsActivity.class);
					startActivity(intent);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
					Toast.makeText(getApplicationContext(),
							"Failed to create product", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// dismiss the dialog
			pDialog.dismiss();
		}

	}

}
