package hiworld.com.vn.touristapp.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class GetDistance extends MapActivity {
	
	
	private void getDistance(GeoPoint src, GeoPoint dest) throws Exception {

        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append( Double.toString((double)src.getLatitudeE6() / 1E6));
        urlString.append(",");
        urlString.append( Double.toString((double)src.getLongitudeE6() / 1E6));
        urlString.append("&destination=");//to
        urlString.append( Double.toString((double)dest.getLatitudeE6() / 1E6));
        urlString.append(",");
        urlString.append( Double.toString((double)dest.getLongitudeE6() / 1E6));
        urlString.append("&mode=walking&sensor=true");
        Log.d("xxx","URL="+urlString.toString());

        // get the JSON And parse it to get the directions data.
        HttpURLConnection urlConnection= null;
        URL url = null;

        url = new URL(urlString.toString());
        urlConnection=(HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();

        InputStream inStream = urlConnection.getInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

        String temp, response = "";
        while((temp = bReader.readLine()) != null){
            //Parse data
            response += temp;
        }
        //Close the reader, stream & connection
        bReader.close();
        inStream.close();
        urlConnection.disconnect();

        //Sortout JSONresponse 
        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
        JSONArray array = object.getJSONArray("routes");
            //Log.d("JSON","array: "+array.toString());

        //Routes is a combination of objects and arrays
        JSONObject routes = array.getJSONObject(0);
            //Log.d("JSON","routes: "+routes.toString());

        String summary = routes.getString("summary");
            //Log.d("JSON","summary: "+summary);

        JSONArray legs = routes.getJSONArray("legs");
            //Log.d("JSON","legs: "+legs.toString());

        JSONObject steps = legs.getJSONObject(0);
                //Log.d("JSON","steps: "+steps.toString());

        JSONObject distance = steps.getJSONObject("distance");
            //Log.d("JSON","distance: "+distance.toString());

                String sDistance = distance.getString("text");
                int iDistance = distance.getInt("value");
                System.out.println("Distance :S: "+ sDistance);
                System.out.println("Distance :I: "+ sDistance);

    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
