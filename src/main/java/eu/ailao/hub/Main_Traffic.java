package eu.ailao.hub;

import eu.ailao.hub.hereapi.BoundingBox;
import eu.ailao.hub.hereapi.StreetTrafficInfo;
import eu.ailao.hub.hereapi.TrafficConnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 14.03.2016.
 * Main class for showing traffic flow in the street
 */
public class Main_Traffic {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Insert street name as argument please.");
			System.exit(-1);
		}
		String streetName = args[0];
		TrafficConnector trafficConnector = new TrafficConnector();

		JSONObject streetPosition = trafficConnector.GETRequest("https://geocoder.cit.api.here.com/6.2/geocode.json?searchtext=" + streetName + "%2C%20Prague&app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&gen=8");
		BoundingBox boundingBox = trafficConnector.getStreetBoundingBox(streetPosition);
		JSONObject trafficFlow = trafficConnector.GETRequest("https://traffic.cit.api.here.com/traffic/6.1/flow.json?app_id=m0gORGqDQz7BRg7MUiC3&app_code=GgpH8vtSsoG0h7rh_a9mnA&bbox=" + boundingBox.getTopLeftLatitude() + "," + boundingBox.getTopLeftLongitude() + ";" + boundingBox.getBottomRightLatitude() + "," + boundingBox.getBottomRightLongitude());
		JSONArray street = trafficConnector.getStreetsJson(trafficFlow, streetName);
		StreetTrafficInfo streetTrafficInfo = trafficConnector.getStreetTrafficInfo(street);

		System.out.println(streetTrafficInfo);
		return;
	}
}
