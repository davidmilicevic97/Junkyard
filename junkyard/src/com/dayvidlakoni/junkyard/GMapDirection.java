package com.dayvidlakoni.junkyard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GMapDirection {
	public String TAG = "ashwileugn";
	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";
	static Document doc = null;

	public GMapDirection() {
	}

	public Document getDocument(LatLng start, LatLng end, String mode) {
		String url = "http://maps.googleapis.com/maps/api/directions/xml?"
				+ "origin=" + start.latitude + "," + start.longitude
				+ "&destination=" + end.latitude + "," + end.longitude
				+ "&sensor=false&units=metric&mode=" + mode;

		try {
			doc = (new pleaseMakeThisWork().execute(url)).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public String getDurationText(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("duration");
		Node node1 = nl1.item(nl1.getLength() - 1);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "text"));
		Log.i("DurationText", node2.getTextContent());
		return node2.getTextContent();
	}

	public int getDurationValue(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("duration");
		Node node1 = nl1.item(nl1.getLength() - 1);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "value"));
		Log.i("DurationValue", node2.getTextContent());
		return Integer.parseInt(node2.getTextContent());
	}

	public String getDistanceText(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("distance");
		Node node1 = nl1.item(nl1.getLength() - 1);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "text"));
		Log.i("DistanceText", node2.getTextContent());
		return node2.getTextContent();
	}

	public int getDistanceValue(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("distance");
		Node node1 = nl1.item(nl1.getLength() - 1);
		NodeList nl2 = node1.getChildNodes();
		Node node2 = nl2.item(getNodeIndex(nl2, "value"));
		Log.i("DistanceValue", node2.getTextContent());
		return Integer.parseInt(node2.getTextContent());
	}

	public String getStartAddress(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("start_address");
		Node node1 = nl1.item(0);
		Log.i("StartAddress", node1.getTextContent());
		return node1.getTextContent();
	}

	public String getEndAddress(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("end_address");
		Node node1 = nl1.item(0);
		Log.i("StartAddress", node1.getTextContent());
		return node1.getTextContent();
	}

	public String getCopyRights(Document doc) {
		NodeList nl1 = doc.getElementsByTagName("copyrights");
		Node node1 = nl1.item(0);
		Log.i("CopyRights", node1.getTextContent());
		return node1.getTextContent();
	}

	public ArrayList<LatLng> getDirection(Document doc) {
		NodeList nl1, nl2, nl3;
		ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
		nl1 = doc.getElementsByTagName("step");
		if (nl1.getLength() > 0) {
			for (int i = 0; i < nl1.getLength(); i++) {
				Node node1 = nl1.item(i);
				nl2 = node1.getChildNodes();

				Node locationNode = nl2
						.item(getNodeIndex(nl2, "start_location"));
				nl3 = locationNode.getChildNodes();
				Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
				double lat = Double.parseDouble(latNode.getTextContent());
				Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				double lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));

				locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "points"));
				ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
				for (int j = 0; j < arr.size(); j++) {
					listGeopoints.add(new LatLng(arr.get(j).latitude, arr
							.get(j).longitude));
				}

				locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
				nl3 = locationNode.getChildNodes();
				latNode = nl3.item(getNodeIndex(nl3, "lat"));
				lat = Double.parseDouble(latNode.getTextContent());
				lngNode = nl3.item(getNodeIndex(nl3, "lng"));
				lng = Double.parseDouble(lngNode.getTextContent());
				listGeopoints.add(new LatLng(lat, lng));
			}
		}

		return listGeopoints;
	}

	// dodato za desni drawer!
	public ArrayList<String> getInstructions(Document doc) {
		NodeList nl1, nl2, nl3;
		ArrayList<String> listGeopoints = new ArrayList<String>();
		nl1 = doc.getElementsByTagName("step");
		if (nl1.getLength() > 0) {
			for (int i = 0; i < nl1.getLength(); i++) {
				Node node1 = nl1.item(i);
				nl2 = node1.getChildNodes();

				Node locationNode = nl2
						.item(getNodeIndex(nl2, "start_location"));
				nl3 = locationNode.getChildNodes();
				Node instructionNode = nl2.item(getNodeIndex(nl2,
						"html_instructions"));
				String instructions = instructionNode.getTextContent();
				for (int k = 0; k < instructions.length(); k++) {
					boolean check = false;
					if (instructions.charAt(k) == '<') {

						int counter1 = 0;
						int counter2 = 0;
						while (k < instructions.length()
								&& (counter1 == 0 || counter1 > counter2)) {
							if (instructions.charAt(k) == '<')
								counter1++;
							if (instructions.charAt(k) == '>')
								counter2++;
							if (k < instructions.length() - 1)
								instructions = instructions.substring(0, k)
										+ instructions.substring(k + 1,
												instructions.length());
							else
								instructions = instructions.substring(0, k);

						}
						if (k < instructions.length() - 1
								&& instructions.charAt(k + 1) != '<')
							k--;

					}
					if (k > -1 && k + 6 < instructions.length()
							&& instructions.substring(k, k + 6)
									.equals("&nbsp;")) {
						instructions = instructions.substring(0, k)
								+ instructions.substring(k + 6,
										instructions.length());
					}
				}
				listGeopoints.add((i + 1) + ". " + instructions);

			}
		}
		return listGeopoints;
	}

	private int getNodeIndex(NodeList nl, String nodename) {
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equals(nodename))
				return i;
		}
		return -1;
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}
}

class pleaseMakeThisWork extends AsyncTask<String, Void, Document> {

	@Override
	protected Document doInBackground(String... url) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url[0]);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			InputStream in = response.getEntity().getContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document dom = builder.parse(in);
			return dom;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("_________________", "------------------EeEeEeE");
		}
		return null;
	}

	@Override
	protected void onPostExecute(Document result) {
		Log.d("------", "222222222222222222");
		super.onPostExecute(result);
	}
}