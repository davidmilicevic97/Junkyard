package com.davidmilicevic97.productslist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllProductsActivity extends ListActivity {

	private ProgressDialog pDialog;

	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> productsList;

	// proveri ovo! trebalo bi da radi
	// url to get all products list
	private static String url_all_products = "http://junkyard.davidmi3.5gbfree.com/products_list/get_all_products.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";

	// products JSONArray
	JSONArray products = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_products);

		// hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// loading products in background thread
		new LoadAllProducts().execute();

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String pid = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				Intent intent = new Intent(getApplicationContext(),
						EditProductActivity.class);
				intent.putExtra(TAG_PID, pid);

				startActivityForResult(intent, 100);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 100) {
			// if result code 100 is received means user edited/deleted product
			// reload this screen again

			Intent intent = getIntent();
			finish();
			startActivity(intent);

		}
	}

	// Background AsyncTask to load all products by making HTTP Request
	class LoadAllProducts extends AsyncTask<String, String, String> {

		// before starting background thread show progress dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(AllProductsActivity.this);
			pDialog.setMessage("Loading products. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// getting all products from url
		@Override
		protected String doInBackground(String... args) {
			// building parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from url
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET",
					params);

			// JSON response
			Log.d("All products: ", json.toString());

			try {
				// checking for SUCCESS tag
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// getting array of products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through all products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_NAME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_NAME, name);

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no products found
					// launch NewProductActivity
					Intent intent = new Intent(getApplicationContext(),
							NewProductActivity.class);
					// closing all previous activities
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		// after completing background task dismiss the progress dialog
		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();

			// updating UI from background thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// updating parsed JSON data into ListView
					ListAdapter adapter = new SimpleAdapter(
							AllProductsActivity.this, productsList,
							R.layout.list_item, new String[] { TAG_PID,
									TAG_NAME },
							new int[] { R.id.pid, R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}

	}

}
