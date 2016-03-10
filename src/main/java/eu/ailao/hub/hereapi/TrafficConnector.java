package eu.ailao.hub.hereapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Petr Marek on 10.03.2016.
 * Class handling of getting information from HERE api
 */
public class TrafficConnector {

	/**
	 * Sends request to API
	 * @param request request to send
	 * @return response to request
	 */
	public JSONObject GETRequest(String request) {
		String response = "";
		try {
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF8"));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				response += output;
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return new JSONObject(response);
	}

	/**
	 * Return all information about traffic flow in one street
	 * @param trafficFlow Json with traffic flow information of all streets within some bounding box
	 * @param street Name of street we want to find
	 * @return Json with one street only
	 */
	//TODO: There are two streets with the same name but different orientation, return both or specify which we want
	public JSONObject getStreetJson(JSONObject trafficFlow, String street) {
		JSONArray RWS = (JSONArray) trafficFlow.get("RWS");
		JSONArray RW = (JSONArray) ((JSONObject) RWS.get(0)).get("RW");
		for (Object streetObject : RW) {
			JSONObject JSONstreet = (JSONObject) streetObject;
			if (JSONstreet.get("DE").equals(street)) {
				return JSONstreet;
			}
		}
		return null;
	}

	/**
	 * Parse street traffic flow info into StreetTrafficInfo.class
	 * @param street Json with all information about traffic flow in street
	 * @return StreetTrafficInfo object
	 */
	//TODO Simplify this method
	//FIXME Repair of bad parsing, for example Evropská street
	public StreetTrafficInfo getStreetTrafficInfo(JSONObject street) {
		StreetTrafficInfo streetTrafficInfo = new StreetTrafficInfo();
		//Parsing of crazy JSON format
		JSONArray FIS = (JSONArray) street.get("FIS");
		for (Object FI : FIS) {
			JSONObject JSONFI = (JSONObject) FI;
			JSONArray FI2 = (JSONArray) JSONFI.get("FI");
			for (Object FI3 : FI2) {
				JSONObject JSONFI3 = (JSONObject) FI3;
				String secondStreet = (String) ((JSONObject) JSONFI3.get("TMC")).get("DE");
				JSONArray CF = (JSONArray) JSONFI3.get("CF");
				for (Object CF2 : CF) {
					JSONObject JSONCF2 = (JSONObject) CF2;
					Double jamFactor = (double) JSONCF2.get("JF");
					streetTrafficInfo.addSituationOnCross((String) street.get("DE"), secondStreet, jamFactor);
				}
			}
		}
		return streetTrafficInfo;
	}

	/**
	 * Parse bounding box coordinates from street position info Json
	 * @param streetPositionInfo Json with all information about street position
	 * @return bounding box of street
	 */
	public BoundingBox getStreetBoundingBox(JSONObject streetPositionInfo) {
		JSONObject response = streetPositionInfo.getJSONObject("Response");
		JSONArray view = response.getJSONArray("View");
		JSONObject viewObject = view.getJSONObject(0);
		JSONArray result = viewObject.getJSONArray("Result");
		JSONObject resultObject = result.getJSONObject(0);
		JSONObject location = resultObject.getJSONObject("Location");
		JSONObject mapView = location.getJSONObject("MapView");
		JSONObject bottomRight = mapView.getJSONObject("BottomRight");
		double bottomRightLatitude = bottomRight.getDouble("Latitude");
		double bottomRightLongitude = bottomRight.getDouble("Longitude");
		JSONObject topLeft = mapView.getJSONObject("TopLeft");
		double topLeftLatitude = topLeft.getDouble("Latitude");
		double topLeftLongitude = topLeft.getDouble("Longitude");
		return new BoundingBox(bottomRightLatitude, bottomRightLongitude, topLeftLatitude, topLeftLongitude);
	}
}
