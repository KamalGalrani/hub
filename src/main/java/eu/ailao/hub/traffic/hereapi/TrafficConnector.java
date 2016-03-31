package eu.ailao.hub.traffic.hereapi;

import eu.ailao.hub.traffic.hereapi.dataclasses.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
	 * Parse street traffic flow info into StreetFlowInfo.class
	 * @param streets JsonArray with all information about traffic flow in streets
	 * @return StreetFlowInfo object
	 */
	public StreetFlowInfo getStreetFlowInfo(JSONArray streets) {
		StreetFlowInfo streetFlowInfo = new StreetFlowInfo();
		for (Object streetObject : streets) {
			JSONObject street = (JSONObject) streetObject;
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
								streetFlowInfo.addSituationOnCross(street.getString("DE"), secondStreet, ((JSONObject) SS2).getDouble("JF"));
							}
						} catch (Exception e) {
						}

						streetFlowInfo.addSituationOnCross(street.getString("DE"), secondStreet, jamFactor);
					}
				}
			}
		}
		return streetFlowInfo;
	}

	/**
	 * Parse bounding box coordinates from street position info Json
	 * @param streetPositionInfo Json with all information about street position
	 * @return bounding box of street
	 */
	public List<BoundingBox> getStreetBoundingBoxes(JSONObject streetPositionInfo) {
		ArrayList<BoundingBox> boundingBoxes = new ArrayList<>();
		try {
			JSONObject response = streetPositionInfo.getJSONObject("Response");
			JSONArray view = response.getJSONArray("View");
			JSONObject viewObject = view.getJSONObject(0);
			JSONArray result = viewObject.getJSONArray("Result");
			for (Object resultObject : result) {
				JSONObject resultObjectJSON = (JSONObject) resultObject;
				JSONObject location = resultObjectJSON.getJSONObject("Location");
				JSONObject mapView = location.getJSONObject("MapView");

				JSONObject topLeft = mapView.getJSONObject("TopLeft");
				double topLeftLatitude = topLeft.getDouble("Latitude");
				double topLeftLongitude = topLeft.getDouble("Longitude");

				JSONObject bottomRight = mapView.getJSONObject("BottomRight");
				double bottomRightLatitude = bottomRight.getDouble("Latitude");
				double bottomRightLongitude = bottomRight.getDouble("Longitude");
				boundingBoxes.add(new BoundingBox(topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude));
			}
		} catch (Exception e) {
		}
		return boundingBoxes;
	}

	/**
	 * Gets street position from JSON answer of HERE api
	 * @param streetPositionInfo JSON from HERE api
	 * @return position of street
	 */
	public Position getStreetPosition(JSONObject streetPositionInfo) {
		Position position = null;
		try {
			JSONObject response = streetPositionInfo.getJSONObject("Response");
			JSONArray view = response.getJSONArray("View");
			JSONObject viewObject = view.getJSONObject(0);
			JSONArray result = viewObject.getJSONArray("Result");
			JSONObject resultObjectJSON = result.getJSONObject(0);
			JSONObject location = resultObjectJSON.getJSONObject("Location");
			JSONArray navigationPositions = location.getJSONArray("NavigationPosition");
			JSONObject navigationPosition = navigationPositions.getJSONObject(0);
			Double latitude = navigationPosition.getDouble("Latitude");
			Double longitude = navigationPosition.getDouble("Longitude");
			position = new Position(latitude, longitude);

		} catch (Exception e) {
		}
		return position;
	}

	/**
	 * Filters only incidents, which are in selected street
	 * @param trafficIncidents all traffic incidents
	 * @param streetName name of selected street
	 * @return JSON array of incidents in selected street only
	 */
	public JSONArray getStreetIncidents(JSONObject trafficIncidents, String streetName) {
		JSONArray incidentsArray = new JSONArray();
		try {
			JSONArray trafficItems = trafficIncidents.getJSONObject("TRAFFICITEMS").getJSONArray("TRAFFICITEM");
			for (Object trafficItem : trafficItems) {
				boolean incidentAddedAlready = false;
				JSONObject trafficItemJSON = (JSONObject) trafficItem;
				JSONObject defined = trafficItemJSON.getJSONObject("LOCATION").getJSONObject("DEFINED");

				//origin
				JSONArray descriptions = defined.getJSONObject("ORIGIN").getJSONObject("ROADWAY").getJSONArray("DESCRIPTION");
				for (Object description : descriptions) {
					JSONObject descriptionJSON = (JSONObject) description;
					if (descriptionJSON.getString("content").equals(streetName)) {
						incidentsArray.put(trafficItemJSON);
						incidentAddedAlready = true;
						break;
					}
				}

				if (incidentAddedAlready) continue;

				//to
				JSONArray descriptions2 = defined.getJSONObject("TO").getJSONObject("ROADWAY").getJSONArray("DESCRIPTION");
				for (Object description : descriptions2) {
					JSONObject descriptionJSON = (JSONObject) description;
					if (descriptionJSON.getString("content").equals(streetName)) {
						incidentsArray.put(trafficItemJSON);
						break;
					}
				}
			}
		} catch (Exception e) {
		}
		return incidentsArray;
	}

	/**
	 * Get info about fastest path from JSON
	 * @param route JSON with information
	 * @return object with information about fastest route
	 */
	public FastestRouteInfo getFastestRouteInfo(JSONObject route) {
		int NUMBER_OF_STREETS = 3;
		String fromStreet;
		String toStreet;
		int time;
		JSONObject response = route.getJSONObject("response");
		JSONArray routeArray = response.getJSONArray("route");
		JSONObject oneRoute = routeArray.getJSONObject(0);

		JSONArray waypoint = oneRoute.getJSONArray("waypoint");

		JSONObject fromWaypoint = waypoint.getJSONObject(0);
		fromStreet = fromWaypoint.getString("mappedRoadName");

		JSONObject toWaypoint = waypoint.getJSONObject(1);
		toStreet = toWaypoint.getString("mappedRoadName");

		JSONObject summary = oneRoute.getJSONObject("summary");
		time = summary.getInt("travelTime");

		JSONArray legArray = oneRoute.getJSONArray("leg");
		JSONObject leg = legArray.getJSONObject(0);
		JSONArray maneuverArray = leg.getJSONArray("maneuver");

		ArrayList<String> longestStreets = getLongestStreets(maneuverArray, NUMBER_OF_STREETS);

		return new FastestRouteInfo(fromStreet, toStreet, longestStreets, time);
	}

	/**
	 * Gets three longest streets from maneuvers along the fastest route
	 * @param maneuverArray Array of maneuvers
	 * @param numberOfStreets Number of streets to return
	 * @return n longest streets on fastest route
	 */
	private ArrayList<String> getLongestStreets(JSONArray maneuverArray, int numberOfStreets){
		ArrayList<StreetFastestRoute> passingStreets = new ArrayList<>();

		//Add streets with same name together
		for (int i = 0; i < maneuverArray.length(); i++) {
			JSONObject maneuverJSON = maneuverArray.getJSONObject(i);
			String roadName = maneuverJSON.getString("nextRoadName");
			int length = maneuverJSON.getInt("length");
			if (!roadName.equals("")) {
				if (passingStreets.size() == 0 || !passingStreets.get(passingStreets.size() - 1).getName().equals(roadName)) {
					passingStreets.add(new StreetFastestRoute(roadName, length, i));
				} else {
					passingStreets.get(passingStreets.size() - 1).addLength(length);
				}
			}
		}

		//Sort them according to length
		passingStreets.sort(new Comparator<StreetFastestRoute>() {
			@Override
			public int compare(StreetFastestRoute o1, StreetFastestRoute o2) {
				if (o1.getLength() < o2.getLength()) {
					return 1;
				}
				if (o1.getLength() == o2.getLength()) {
					return 0;
				}
				return -1;
			}
		});

		//Get the longest ones
		ArrayList<StreetFastestRoute> longestStreets = new ArrayList<>();
		for (int i = 0; i < numberOfStreets; i++) {
			longestStreets.add(passingStreets.get(i));
		}

		//Sort them according to index in the path
		longestStreets.sort(new Comparator<StreetFastestRoute>() {
			@Override
			public int compare(StreetFastestRoute o1, StreetFastestRoute o2) {
				if (o1.getIndexInRoute() < o2.getIndexInRoute()) {
					return -1;
				}
				if (o1.getIndexInRoute() == o2.getIndexInRoute()) {
					return 0;
				}
				return 1;
			}
		});

		//Get their names
		ArrayList<String> streets = new ArrayList<>();
		for (int i = 0; i < numberOfStreets; i++) {
			streets.add(longestStreets.get(i).getName());
		}

		return streets;
	}

	/**
	 * Parse information about traffic incidents into one streetIncidentInfo object
	 * @param streetIncidents traffic incidents to parse
	 * @return object containing information about all traffic incidents in one street
	 */
	public StreetIncidentInfo getStreetIncidentInfo(JSONArray streetIncidents) {
		StreetIncidentInfo streetIncidentInfo = new StreetIncidentInfo();
		for (Object incident : streetIncidents) {
			JSONObject incidentJSON = (JSONObject) incident;
			String active = incidentJSON.getString("TRAFFICITEMSTATUSSHORTDESC");
			String type = incidentJSON.getString("TRAFFICITEMTYPEDESC");
			String startTime = incidentJSON.getString("STARTTIME");
			String endTime = incidentJSON.getString("ENDTIME");
			String criticality = incidentJSON.getJSONObject("CRITICALITY").getString("DESCRIPTION");
			String comment = incidentJSON.getString("COMMENTS");

			JSONObject description = (JSONObject) incidentJSON.getJSONObject("LOCATION").getJSONObject("DEFINED").getJSONObject("ORIGIN").getJSONObject("POINT").getJSONArray("DESCRIPTION").get(0);
			String origin = description.getString("content");

			description = (JSONObject) incidentJSON.getJSONObject("LOCATION").getJSONObject("DEFINED").getJSONObject("ORIGIN").getJSONObject("DIRECTION").getJSONArray("DESCRIPTION").get(0);
			String direction = description.getString("content");

			description = (JSONObject) incidentJSON.getJSONObject("LOCATION").getJSONObject("DEFINED").getJSONObject("TO").getJSONObject("POINT").getJSONArray("DESCRIPTION").get(0);
			String to = description.getString("content");

			boolean isClosed = incidentJSON.getJSONObject("TRAFFICITEMDETAIL").getBoolean("ROADCLOSED");

			streetIncidentInfo.addIncident(active, isClosed, type, startTime, endTime, criticality, comment, origin, to, direction);
		}
		return streetIncidentInfo;
	}
}
