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
	 * @return JsonArray with one street only in both directions
	 */
	public JSONArray getStreetsJson(JSONObject trafficFlow, String street) {
		JSONArray streetsArray = new JSONArray();
		JSONArray RWS = (JSONArray) trafficFlow.get("RWS");
		JSONArray RW = (JSONArray) ((JSONObject) RWS.get(0)).get("RW");
		for (Object streetObject : RW) {
			JSONObject JSONstreet = (JSONObject) streetObject;
			if (JSONstreet.get("DE").equals(street)) {
				streetsArray.put(JSONstreet);
			}
		}
		return streetsArray;
	}

	/**
	 * Parse street traffic flow info into StreetTrafficInfo.class
	 * @param streets JsonArray with all information about traffic flow in streets
	 * @return StreetTrafficInfo object
	 */
	public StreetTrafficInfo getStreetTrafficInfo(JSONArray streets) {
		StreetTrafficInfo streetTrafficInfo = new StreetTrafficInfo();
		for (Object streetObject : streets) {
			JSONObject street=(JSONObject)streetObject;
			//Parsing of crazy JSON format
			JSONArray FIS = (JSONArray) street.get("FIS");
			for (Object FI : FIS) {
				JSONArray FI2 = ((JSONObject) FI).getJSONArray("FI");
				for (Object FI3 : FI2) {
					String secondStreet = ((JSONObject) FI3).getJSONObject("TMC").getString("DE");
					JSONArray CF = ((JSONObject) FI3).getJSONArray("CF");
					for (Object CF2 : CF) {
						Double jamFactor = ((JSONObject) CF2).getDouble("JF");

						//Parse subsegments
						try {
							JSONObject SSS = ((JSONObject) CF2).getJSONObject("SSS");
							JSONArray SS = SSS.getJSONArray("SS");
							for (Object SS2 : SS) {
								streetTrafficInfo.addSituationOnCross(street.getString("DE"), secondStreet, ((JSONObject) SS2).getDouble("JF"));
							}
						} catch (Exception e) {
						}

						streetTrafficInfo.addSituationOnCross(street.getString("DE"), secondStreet, jamFactor);
					}
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
