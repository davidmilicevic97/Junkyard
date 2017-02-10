package com.dayvidlakoni.junkyard;

public class LocationItem {
	private String address, type;
	private double longitude, latitude;

	public LocationItem(String address, String longitude, String latitude,
			String type) {
		this.address = address;
		this.longitude = Double.parseDouble(longitude);
		this.latitude = Double.parseDouble(latitude);
		this.type = type;
	}

	public String getAdress() {
		return address;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getLocationString() {
		return latitude + ", " + longitude;
	}

	public String getType() {
		return type;
	}
}
