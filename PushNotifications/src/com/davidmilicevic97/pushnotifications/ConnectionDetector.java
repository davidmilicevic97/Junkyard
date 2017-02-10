package com.davidmilicevic97.pushnotifications;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

	private Context context;

	public ConnectionDetector(Context context) {
		this.context = context;
	}

	// Checking for all possible internet providers
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivityMng = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityMng != null) {
			NetworkInfo[] info = connectivityMng.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
			}
		}

		return false;
	}
}
