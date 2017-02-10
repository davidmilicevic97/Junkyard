package com.davidmilicevic97.productslist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

public class EditProductActivity extends Activity {

	EditText txtName, txtPrice, txtDesc, txtCreatedAt;
	Button btnSave, btnDelete;

	String pid;

	ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	private static final String url_product_details = "http://junkyard.davidmi3.5gbfree.com/products_list/get_product_details.php";
	private static final String url_update_product = "http://junkyard.davidmi3.5gbfree.com/products_list/update_product.php";
	private static final String url_delete_product = "http://junkyard.davidmi3.5gbfree.com/products_list/delete_product.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "price";
	private static final String TAG_DESCRIPTION = "description";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_product);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);

		Intent i = getIntent();
		pid = i.getStringExtra(TAG_PID);

		// getting complete product details in background thread
		new GetProductDetails().execute();

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// starting background task to update product
				new SaveProductDetails().execute();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// deleting product in background thread
				new DeleteProduct().execute();
			}
		});

	}

	// background AsyncTask to get complete product details
	class GetProductDetails extends AsyncTask<String, String, String> {

		// before starting background thread show progress dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Loading product details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// getting product details in background thread
		@Override
		protected String doInBackground(String... args) {

			// building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pid", pid));

			// getting product details by making HTTP request
			final JSONObject json = jsonParser.makeHttpRequest(
					url_product_details, "GET", params);

			// JSON response
			Log.d("Single Product Details", json.toString());

			// updating UI from background thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// check for success tag
					int success;
					try {
						// ISKOMENTARISANO ZOBG NetworkOnMainThreadException

						// // building parameters
						// List<NameValuePair> params = new
						// ArrayList<NameValuePair>();
						// params.add(new BasicNameValuePair("pid", pid));
						//
						// // getting product details by making HTTP request
						// JSONObject json = jsonParser.makeHttpRequest(
						// url_product_details, "GET", params);
						// // JSON response
						// Log.d("Single Product Details", json.toString());
						
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_PRODUCT);

							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// product with this pid found
							txtName = (EditText) findViewById(R.id.inputName);
							txtPrice = (EditText) findViewById(R.id.inputPrice);
							txtDesc = (EditText) findViewById(R.id.inputDesc);

							txtName.setText(product.getString(TAG_NAME));
							txtPrice.setText(product.getString(TAG_PRICE));
							txtDesc.setText(product.getString(TAG_DESCRIPTION));
						} else {
							// product with pid not found
							Toast.makeText(getApplicationContext(),
									"Product not found", Toast.LENGTH_SHORT)
									.show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});

			return null;
		}

		// after completing background thread dismiss the progress dialog
		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}

	}

	// background AsyncTask to save product details
	class SaveProductDetails extends AsyncTask<String, String, String> {

		// before starting background thread show progress dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Saving product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// saving product
		@Override
		protected String doInBackground(String... args) {
			// getting updated data from EditTexts
			String name = txtName.getText().toString();
			String price = txtPrice.getText().toString();
			String description = txtDesc.getText().toString();

			// building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_NAME, name));
			params.add(new BasicNameValuePair(TAG_PRICE, price));
			params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));

			// sending modified data through http request
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
					Toast.makeText(getApplicationContext(),
							"Failed to update product", Toast.LENGTH_SHORT)
							.show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// after completing background thread dismiss the progress dialog
		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}

	}

	// background AsyncTask to delete product
	class DeleteProduct extends AsyncTask<String, String, String> {

		// before starting background thread show progress dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(EditProductActivity.this);
			pDialog.setMessage("Deleting product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// deleting product
		@Override
		protected String doInBackground(String... args) {

			int success;
			try {
				// building parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));

				// getting product details by making http request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// JSON response
				Log.d("Delete Product", json.toString());

				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					setResult(100, i);
					finish();
				} else {
					// failed to delete
					Toast.makeText(getApplicationContext(),
							"Failed to delete product", Toast.LENGTH_SHORT)
							.show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// after completing background thread dismiss the progress dialog
		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
		}
	}

}
