package eu.ailao.hub.traffic.hereapi.dataclasses;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 15.03.2016.
 * Class containing all traffic incidents in one street
 */
public class StreetIncidentInfo {
	ArrayList<Incident> incidents = new ArrayList<>();

	public void addIncident(String active, boolean roadClosed, String type, String startTime, String endTime, String criticality, String comment, String origin, String to, String direction) {
		Incident incident = new Incident(active, roadClosed, type, startTime, endTime, criticality, comment, origin, to, direction);
		incidents.add(incident);
	}

	public ArrayList<Incident> getIncidents() {
		return incidents;
	}

	@Override
	public String toString() {
		JSONArray toPrint=new JSONArray(incidents);
		return toPrint.toString();
	}
}
