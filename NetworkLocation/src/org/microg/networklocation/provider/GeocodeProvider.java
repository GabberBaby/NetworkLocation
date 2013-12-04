package org.microg.networklocation.provider;

import android.location.Address;
import android.location.GeocoderParams;
import android.util.Log;
import org.microg.networklocation.MainService;
import org.microg.networklocation.source.GeocodeSource;

import java.util.ArrayList;
import java.util.List;

public class GeocodeProvider extends internal.com.android.location.provider.GeocodeProvider {
	private static final String TAG = "LocationGeocodeProvider";
	private static final String UNKNOWN_RESULT_ERROR = "unknown";
	private List<GeocodeSource> sources;

	@Override
	public String onGetFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params,
									List<Address> addrs) {
		for (GeocodeSource source : sources) {
			if (source.isSourceAvailable()) {
				List<Address> addresses = null;
				try {
					addresses =
							source.getFromLocation(latitude, longitude, params.getClientPackage(), params.getLocale());
				} catch (Throwable t) {
					Log.w(TAG, source.getName() + " throws exception!", t);
				}
				if ((addresses != null) && !addresses.isEmpty()) {
					addrs.addAll(addresses);
					if (MainService.DEBUG) {
						Log.d(TAG, latitude + "/" + longitude + " reverse geolocated to:" + addrs.get(0));
					}
					return null; // null means everything is ok!
				}
			}
		}
		if (MainService.DEBUG) {
			Log.d(TAG, "Could not reverse geolocate: " + latitude + "/" + longitude);
		}
		return UNKNOWN_RESULT_ERROR;
	}

	@Override
	public String onGetFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude,
										double upperRightLatitude, double upperRightLongitude, int maxResults,
										GeocoderParams params, List<Address> addrs) {
		for (GeocodeSource source : sources) {
			if (source.isSourceAvailable()) {
				List<Address> addresses = null;
				try {
					addresses =
							source.getFromLocationName(locationName, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, params.getClientPackage(), params.getLocale());
				} catch (Throwable t) {
					Log.w(TAG, source.getName() + " throws exception!", t);
				}
				if ((addresses != null) && !addresses.isEmpty()) {
					addrs.addAll(addresses);
					if (MainService.DEBUG) {
						Log.d(TAG, locationName + " forward geolocated to:" + addrs.get(0));
					}
					return null; // null means everything is ok!
				}
			}
		}

		if (MainService.DEBUG) {
			Log.d(TAG, "Could not forward geolocate: " + locationName);
		}
		return UNKNOWN_RESULT_ERROR;
	}

	public void setSources(List<GeocodeSource> sources) {
		this.sources = new ArrayList<GeocodeSource>(sources);
	}

}
