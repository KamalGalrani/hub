package eu.ailao.hub.traffic.hereapi;

import eu.ailao.hub.traffic.hereapi.dataclasses.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petr Marek on 16.03.2016.
 * Class for getting information about traffic flow and incidents
 */
public class TrafficInformationGetter {

	/**
	 * Return information about incidents in street
	 * @param streetName name of street
	 * @param boundingBoxes bounding box in which street lies
	 * @return information about incidents in street
	 */
	public StreetIncidentInfo getStreetIncidentInfo(String streetName, List<BoundingBox> boundingBoxes) {
		TrafficConnector trafficConnector = new TrafficConnector();
		JSONArray trafficIncidentsOnStreet=new JSONArray();
		for (BoundingBox boundingBox : boundingBoxes) {
			JSONObject trafficIncidents = trafficConnector.GETRequest("https://traffic.cit.api.here.com/traffic/6.0/incidents.json?bbox=" + boundingBox.getTopLeftLatitude() + "," + boundingBox.getTopLeftLongitude() + ";" + boundingBox.getBottomRightLatitude() + "," + boundingBox.getBottomRightLongitude() + "&criticality=0%2C1%2C2%2C3&app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA");
			//JSONObject trafficIncidents = new TestSituations().getTrafficIncidents();
			concatJSONArrays(trafficIncidentsOnStreet,trafficConnector.getStreetIncidents(trafficIncidents, streetName));
		}
		StreetIncidentInfo streetIncidentInfo = trafficConnector.getStreetIncidentInfo(trafficIncidentsOnStreet);
		return streetIncidentInfo;
	}

	/**
	 * Return information about flow in street
	 * @param streetName name of street
	 * @param boundingBoxes bounding box in which street lies
	 * @return information about traffic flow in street
	 */
	public StreetFlowInfo getStreetFlowInfo(String streetName, List<BoundingBox> boundingBoxes) {
		TrafficConnector trafficConnector = new TrafficConnector();
		JSONArray streets=new JSONArray();
		for (BoundingBox boundingBox : boundingBoxes) {
			JSONObject trafficFlow = trafficConnector.GETRequest("https://traffic.cit.api.here.com/traffic/6.1/flow.json?app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&bbox=" + boundingBox.getTopLeftLatitude() + "," + boundingBox.getTopLeftLongitude() + ";" + boundingBox.getBottomRightLatitude() + "," + boundingBox.getBottomRightLongitude());
			concatJSONArrays(streets,trafficConnector.getStreetsJson(trafficFlow, streetName));
		}
		StreetFlowInfo streetFlowInfo = trafficConnector.getStreetFlowInfo(streets);
		return streetFlowInfo;
	}

	public FastestRouteInfo getFastestRouteInfo(Position from, Position to){
		TrafficConnector trafficConnector = new TrafficConnector();
		JSONObject fastestRoute = trafficConnector.GETRequest("https://route.cit.api.here.com/routing/7.2/calculateroute.json?waypoint0="+from.getLatitude()+"%2C"+from.getLongitude()+"&waypoint1="+to.getLatitude()+"%2C"+to.getLongitude()+"&mode=fastest%3Bcar%3Btraffic%3Aenabled&routeattributes=lg&maneuverattributes=le%2Cnr&app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&departure=now");
		FastestRouteInfo fastestRouteInfo = trafficConnector.getFastestRouteInfo(fastestRoute);
		return fastestRouteInfo;
	}

	/**
	 * Return possible bounding boxes of street
	 * @param streetName name of street
	 * @return list of bounding boxes in which street can lie
	 */
	public List<BoundingBox> getStreetBoundingBoxes(String streetName) {
		TrafficConnector trafficConnector = new TrafficConnector();
		try {
			streetName=URLEncoder.encode(streetName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject streetPosition = trafficConnector.GETRequest("https://geocoder.cit.api.here.com/6.2/geocode.json?searchtext=" + streetName + "%2C%20Prague&app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&gen=8");
		ArrayList<BoundingBox> boundingBoxes = (ArrayList<BoundingBox>) trafficConnector.getStreetBoundingBoxes(streetPosition);
		return boundingBoxes;
	}

	public Position getStreetPosition(String streetName){
		TrafficConnector trafficConnector = new TrafficConnector();
		try {
			streetName=URLEncoder.encode(streetName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject streetPosition = trafficConnector.GETRequest("https://geocoder.cit.api.here.com/6.2/geocode.json?searchtext=" + streetName + "%2C%20Prague&app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&gen=8");
		Position position = trafficConnector.getStreetPosition(streetPosition);
		return position;
	}

	/**
	 * Concat two JSON arrays
	 * @param resultArray array in which the result will be stored
	 * @param arrayToAdd array to add to resultArray
	 */
	private void concatJSONArrays(JSONArray resultArray, JSONArray arrayToAdd){
		for(Object o: arrayToAdd){
			resultArray.put(o);
		}
	}
}
