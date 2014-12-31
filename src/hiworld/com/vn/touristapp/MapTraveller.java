package hiworld.com.vn.touristapp;

import hiworld.com.vn.touristapp.common.CalculateDistanceMap;
import hiworld.com.vn.touristapp.common.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTraveller extends Activity implements OnMapReadyCallback {

	// MapFragment mMap;
	ProgressDialog mProgressDialog;
	GoogleApiClient googleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_traveller);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Loading...");
		mProgressDialog.setMessage("Vui lòng chờ trong giây lát...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
		MapFragment mMapTraveller = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mMapTraveller.getMapAsync(this);

	}

	@Override
	public void onMapReady(GoogleMap map) {
		mProgressDialog.dismiss();
		String address = null, city = null;
		String addressTourG = null, cityTourG = null;
		GPSTracker gpsTracker = new GPSTracker(this);
		Geocoder geocoder;
		List<Address> addresses = null;
		List<Address> addresses1 = null;
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(gpsTracker.getLatitude(),
					gpsTracker.getLongitude(), 1);

			addresses1 = geocoder.getFromLocation(
					gpsTracker.getLatitude() + 0.11,
					gpsTracker.getLongitude() + 0.11, 1);

			address = addresses.get(0).getAddressLine(0);
			addressTourG = addresses1.get(0).getAddressLine(0);

			city = addresses.get(0).getAddressLine(0);
			cityTourG = addresses1.get(0).getAddressLine(1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LatLng latLng = new LatLng(gpsTracker.getLatitude(),
				gpsTracker.getLongitude());
		LatLng latLngTourGuideLatLng = new LatLng(21.039901, 105.7899911);
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		map.addMarker(new MarkerOptions().title(address).snippet(city)
				.position(latLng));

		double distanceString = CalculateDistanceMap.CalculationByDistance(latLng, latLngTourGuideLatLng);
		map.addMarker(new MarkerOptions().title(addressTourG + "," + cityTourG)
				.snippet(String.valueOf(distanceString))
				.position(latLngTourGuideLatLng));
		
	}

}
