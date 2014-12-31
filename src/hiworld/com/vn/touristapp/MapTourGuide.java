package hiworld.com.vn.touristapp;

import hiworld.com.vn.touristapp.common.GPSTracker;
import hiworld.com.vn.touristapp.common.GetDistance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

public class MapTourGuide extends Activity implements OnMapReadyCallback {
	ProgressDialog mProgressDialog;
	GoogleApiClient googleApiClient;
	GeoPoint point, point1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_tourguide);
		MapFragment mMapTraveller = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapTourGuide);
		mMapTraveller.getMapAsync(this);

	}

	@Override
	public void onMapReady(GoogleMap mapTourGuide) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Loading...");
		mProgressDialog.setMessage("Vui lòng chờ trong giây lát...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		mapTourGuide.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				mProgressDialog.dismiss();

			}
		});

		String address = null, city = null;
		String address1 = null, city1 = null;
		GPSTracker gpsTracker = new GPSTracker(this);
		Geocoder geocoder = null;
		List<Address> addresses = null;
		List<Address> addresses1 = null;
		geocoder = new Geocoder(this, Locale.getDefault());
		// pos 1
		try {
			addresses = geocoder.getFromLocation(gpsTracker.getLatitude(),
					gpsTracker.getLongitude(), 1);
			addresses1 = geocoder.getFromLocation(
					gpsTracker.getLatitude() + 0.01,
					gpsTracker.getLongitude() + 0.01, 1);
			address = addresses.get(0).getAddressLine(0);
			address1 = addresses1.get(0).getAddressLine(0);

			city = addresses.get(0).getAddressLine(1);
			city1 = addresses1.get(0).getAddressLine(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LatLng latLng = new LatLng(gpsTracker.getLatitude(),
				gpsTracker.getLongitude());
		LatLng latLng2 = new LatLng(gpsTracker.getLatitude() + 0.01,
				gpsTracker.getLongitude() + 0.01);

		mapTourGuide.setMyLocationEnabled(true);
		mapTourGuide.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		mapTourGuide.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
				.title(address).snippet(city).position(latLng));
		mapTourGuide.addMarker(new MarkerOptions().title(address1 + city1)
				.snippet(addresses.get(0).getAdminArea()).position(latLng2));
		point = new GeoPoint((int) (gpsTracker.getLatitude() * 1E6),
				(int) (gpsTracker.getLongitude() * 1E6));
		point1 = new GeoPoint((int) ((gpsTracker.getLatitude() + 0.11) * 1E6),
				(int) ((gpsTracker.getLongitude() + 0.11) * 1E6));
		try {
			getDistance(point, point1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDistance(GeoPoint src, GeoPoint dest) throws Exception {

		StringBuilder urlString = new StringBuilder();
		urlString
				.append("http://maps.googleapis.com/maps/api/directions/json?");
		urlString.append("origin=");// from
		urlString.append(Double.toString((double) src.getLatitudeE6() / 1E6));
		urlString.append(",");
		urlString.append(Double.toString((double) src.getLongitudeE6() / 1E6));
		urlString.append("&destination=");// to
		urlString.append(Double.toString((double) dest.getLatitudeE6() / 1E6));
		urlString.append(",");
		urlString.append(Double.toString((double) dest.getLongitudeE6() / 1E6));
		urlString.append("&mode=walking&sensor=true");
		Log.d("xxx", "URL=" + urlString.toString());

		// get the JSON And parse it to get the directions data.
		HttpURLConnection urlConnection = null;
		URL url = null;

		url = new URL(urlString.toString());
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.connect();

		InputStream inStream = urlConnection.getInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				inStream));

		String temp, response = "";
		while ((temp = bReader.readLine()) != null) {
			// Parse data
			response += temp;
		}
		// Close the reader, stream & connection
		bReader.close();
		inStream.close();
		urlConnection.disconnect();

		// Sortout JSONresponse
		JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
		JSONArray array = object.getJSONArray("routes");
		// Log.d("JSON","array: "+array.toString());

		// Routes is a combination of objects and arrays
		JSONObject routes = array.getJSONObject(0);
		// Log.d("JSON","routes: "+routes.toString());

		String summary = routes.getString("summary");
		// Log.d("JSON","summary: "+summary);

		JSONArray legs = routes.getJSONArray("legs");
		// Log.d("JSON","legs: "+legs.toString());

		JSONObject steps = legs.getJSONObject(0);
		// Log.d("JSON","steps: "+steps.toString());

		JSONObject distance = steps.getJSONObject("distance");
		// Log.d("JSON","distance: "+distance.toString());

		String sDistance = distance.getString("text");
		int iDistance = distance.getInt("value");
		System.out.println("Distance :S: " + sDistance);
		System.out.println("Distance :I: " + sDistance);

	}

}
