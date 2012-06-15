package de.wasmitnetzen.apps.locat.background;

import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LocationCheckListener implements LocationListener{
	AsyncTask<Object, Integer, Long> backgroundThread = new AsyncTask<Object, Integer, Long>() {
		protected Long doInBackground(Object... params) {
			Location location = (Location) params[0];
			Log.d("LocationCheckTask", "New location received: " +String.valueOf(location.getLatitude()) + ": " + String.valueOf(location.getLongitude()));			
			return null;
		}		
	};

	public void onLocationChanged(Location location) {
		backgroundThread.execute(location);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}	
}
