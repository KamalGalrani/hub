package eu.ailao.hub;

import eu.ailao.hub.hereapi.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
		TrafficInformationGetter trafficInformationGetter = new TrafficInformationGetter();

		ArrayList<BoundingBox> boundingBoxes = (ArrayList<BoundingBox>) trafficInformationGetter.getStreetBoundingBoxes(streetName);
		StreetFlowInfo streetFlowInfo = trafficInformationGetter.getStreetFlowInfo(streetName, boundingBoxes);
		StreetIncidentInfo streetIncidentInfo = trafficInformationGetter.getStreetIncidentInfo(streetName, boundingBoxes);

		System.out.println(streetFlowInfo);
		System.out.println(streetIncidentInfo);

		return;
	}
}
