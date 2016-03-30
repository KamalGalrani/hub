package eu.ailao.hub.traffic.hereapi.dataclasses;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 3/30/2016.
 */
public class FastestRouteInfo {
	private String fromStreet;
	private String toStreet;
	private ArrayList<String> throughStreets;
	int time;

	public FastestRouteInfo(String fromStreet, String toStreet, ArrayList<String> throughStreets, int time) {
		this.fromStreet = fromStreet;
		this.toStreet = toStreet;
		this.throughStreets = throughStreets;
		this.time = time;
	}

	public String getFromStreet() {
		return fromStreet;
	}

	public String getToStreet() {
		return toStreet;
	}

	public ArrayList<String> getThroughStreets() {
		return throughStreets;
	}

	public int getTime() {
		return time;
	}
}
