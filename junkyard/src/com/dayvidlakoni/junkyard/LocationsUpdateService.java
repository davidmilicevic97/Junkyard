package com.dayvidlakoni.junkyard;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

// OVO APDEJTUJE SVAKI PUT KAD SE UDJE U APP I AKO SE NE UDJE U NAREDNIH 24 SATA ONDA CE APDEJTOVATI AUTOMATSKI (LocationsList:107)
// MILSLM DA NE BI TREBALO DA ZAVISI OD TOGA KAD SE UDJE U APP NEGO DA APDEJTUJE NA SVAKIH 24 SATA OD PRVOG ULASKA U APP
// OVO JE BITNO !!!!!

// SREDIO SAM OVO DA LEPO RADI, SADA APDEJTUJE SVAKI PUT KADA SE U PreferencesActivity STAVI DA MOZE AUTO-REFRESH I POTOM NA SVAKA 24 SATA
// NECU BRISATI OVO, DA BIH ZNAO STA SAM RADIO!

// DODAO SAM I REFRESH DUGME U LocationsActivity

public class LocationsUpdateService extends IntentService {

	private static final String TAG = "LOCATIONS_UPDATE_SERVICE";
	private static final String PREF_CURR_FILE_VERSION = "PREF_CURR_FILE_VERSION";

	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;

	private boolean isAlarmRunning = false;

	public LocationsUpdateService() {
		super("LocationsUpdateService");
	}

	public LocationsUpdateService(String name) {
		super(name);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		String ALARM_ACTION = LocationsAlarmReceiver.ACTION_REFRESH_LOCATIONS_ALARM;
		Intent intentToFire = new Intent(ALARM_ACTION);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		boolean autoUpdateChecked = prefs.getBoolean("PREF_AUTO_UPDATE", false);

		if (!isAlarmRunning) {
			if (autoUpdateChecked) {
				isAlarmRunning = true;
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP; // MOZDA
																		// BEZ
																		// WAKEUP???
				long timeToRefresh = SystemClock.elapsedRealtime() + 86400000;
				alarmManager.setInexactRepeating(alarmType, timeToRefresh,
						86400000, alarmIntent);
			} else {
				isAlarmRunning = false;
				alarmManager.cancel(alarmIntent);
			}
		}
		refreshLocations();

	}

	private void addNewLocation(LocationItem locationItem) {
		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();

		values.put(LocationsContentProvider.ADDRESS_KEY,
				locationItem.getAdress());
		values.put(LocationsContentProvider.LOCATION_KEY,
				locationItem.getLocationString());
		values.put(LocationsContentProvider.LATITUDE_KEY,
				locationItem.getLatitude());
		values.put(LocationsContentProvider.LONGITUDE_KEY,
				locationItem.getLongitude());
		values.put(LocationsContentProvider.TYPE_KEY, locationItem.getType());

		cr.insert(LocationsContentProvider.CONTENT_URI, values);
	}

	public void refreshLocations() {
		URL url;

		try {
			String junkyardLink = getString(R.string.link);
			url = new URL(junkyardLink);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				NodeList nl = docEle.getElementsByTagName("version");
				Element versionElement = (Element) nl.item(0);
				String versionString = versionElement.getFirstChild()
						.getNodeValue();
				int version = Integer.parseInt(versionString);

				SharedPreferences prefs = getSharedPreferences(
						"JUNKYARD_PREFS", Activity.MODE_PRIVATE);
				int currVersion = prefs.getInt(PREF_CURR_FILE_VERSION, 0);

				if (version > currVersion) {
					Log.d("LOCATIONS_UPDATE_SERVICE",
							"New version of file found!");

					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt(PREF_CURR_FILE_VERSION, version);
					editor.apply();

					Log.d("LOCATIONS_UPDATE_SERVICE",
							"I will try to drop table now!");
					LocationsContentProvider.dropTableAndCreateNew();

					nl = docEle.getElementsByTagName("entry");
					if (nl != null && nl.getLength() > 0) {
						for (int i = 0; i < nl.getLength(); i++) {
							Element entry = (Element) nl.item(i);

							Element addressElement = (Element) entry
									.getElementsByTagName("address").item(0);
							Element latitudeElement = (Element) entry
									.getElementsByTagName("latitude").item(0);
							Element longitudeElement = (Element) entry
									.getElementsByTagName("longitude").item(0);
							Element typeElement = (Element) entry
									.getElementsByTagName("type").item(0);

							String address = addressElement.getFirstChild()
									.getNodeValue();
							String longitude = longitudeElement.getFirstChild()
									.getNodeValue();
							String latitude = latitudeElement.getFirstChild()
									.getNodeValue();
							String type = typeElement.getFirstChild()
									.getNodeValue();

							final LocationItem locationItem = new LocationItem(
									address, longitude, latitude, type);

							addNewLocation(locationItem);
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.d(TAG, "IOException");
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "Parser Configuration Exception");
		} catch (SAXException e) {
			Log.d(TAG, "SAX Exception");
		} finally {
		}
	}

}
