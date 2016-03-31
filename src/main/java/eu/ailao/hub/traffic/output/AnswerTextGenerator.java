package eu.ailao.hub.traffic.output;

import eu.ailao.hub.traffic.analyze.TrafficTopic;
import eu.ailao.hub.traffic.hereapi.dataclasses.*;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 17.03.2016.
 * Class for generating question text from data
 */
public class AnswerTextGenerator {

	/**
	 * Generates answer sentence about street flow
	 * @param topic topic of question
	 * @param streetFlowInfo information about street flow
	 * @return answer sentence
	 */
	public String generateAnswerText(TrafficTopic topic, StreetFlowInfo streetFlowInfo) {
		ArrayList<CrossSituation> crossSituations = streetFlowInfo.getSituationsOnCrosses();
		if (crossSituations.size() != 0) {
			String street = crossSituations.get(0).getOwningStreet();
			float maxJamFactor = 0;
			float minJamFactor = 10;
			for (CrossSituation crossSituation : crossSituations) {
				float crossJamFactor = (float) crossSituation.getJamFactor();
				if (crossJamFactor > maxJamFactor) {
					maxJamFactor = crossJamFactor;
				}
				if (crossJamFactor < minJamFactor) {
					minJamFactor = crossJamFactor;
				}
			}
			maxJamFactor = maxJamFactor / 2;
			minJamFactor = maxJamFactor / 2;
			int minJamFactorOutput = (int) ((minJamFactor + 2) / 2);
			int maxJamFactorOutput = (int) ((maxJamFactor + 2) / 2);
			if (minJamFactorOutput==6){
				minJamFactorOutput--;
			}
			if (maxJamFactorOutput==6){
				maxJamFactorOutput--;
			}
			if (minJamFactorOutput == maxJamFactorOutput) {
				return "Actual traffic situation on " + street + " is " + maxJamFactorOutput + ".";
			}
			return "Actual traffic situation on " + street + " is between degrees " + minJamFactorOutput + " and " + maxJamFactorOutput + ".";
		} else {
			return "Sorry, I don't have data for this street.";
		}
	}

	/**
	 * Generates answer sentence about street incidents, constructions, closed roads, and ends of restrictions
	 * @param topic topic of question
	 * @param streetIncidentInfo information about street flow
	 * @return answer sentence
	 */
	public String generateAnswerText(TrafficTopic topic, StreetIncidentInfo streetIncidentInfo) {
		switch (topic) {
			case INCIDENT:
				return generateAnswerTextIncident(streetIncidentInfo);
			case RESTRICTION_END:
				return generateAnswerTextRestrictionEnds(streetIncidentInfo);
			case CLOSURE:
				return generateAnswerTextClosure(streetIncidentInfo);
			case CONSTRUCTION:
				return generateAnswerTextConstruction(streetIncidentInfo);
			default:
				return "Sorry, I don't know what you ask.";
		}
	}

	/**
	 * Generates answer sentence about fastest road
	 * @param topic topic of question
	 * @param fastestRouteInfo information about fastest road
	 * @return answer sentence
	 */
	public String generateAnswerText(TrafficTopic topic, FastestRouteInfo fastestRouteInfo) {
		String toReturn = "";
		toReturn += "The fastest route takes ";
		toReturn += fastestRouteInfo.getTime() / 60 + " ";
		toReturn += "minutes through ";
		for (int i = 0; i < fastestRouteInfo.getThroughStreets().size(); i++) {
			if (i == fastestRouteInfo.getThroughStreets().size() - 1) {
				toReturn += fastestRouteInfo.getThroughStreets().get(i);
			} else if (i == fastestRouteInfo.getThroughStreets().size() - 2) {
				toReturn += fastestRouteInfo.getThroughStreets().get(i) + " and ";
			} else {
				toReturn += fastestRouteInfo.getThroughStreets().get(i) + ", ";
			}
		}
		toReturn += ".";
		return toReturn;
	}

	/**
	 * Generates answer sentence about street incidents
	 * @param streetIncidentInfo information about street incidents
	 * @return answer sentence
	 */
	private String generateAnswerTextIncident(StreetIncidentInfo streetIncidentInfo) {
		String toReturn = "";
		ArrayList<Incident> incidents = streetIncidentInfo.getIncidents();
		for (Incident incident : incidents) {
			if (incident.isActive().equals("ACTIVE")) {
				if (incident.isRoadClosed()) {
					toReturn += "The road is closed ";
				} else {
					toReturn += "The traffic is limited ";
				}
				toReturn += "between " + incident.getOrigin() + " and " + incident.getTo() + " ";
				toReturn += "in the direction to " + incident.getDirection() + " ";
				switch (incident.getType()) {
					case "CONSTRUCTION":
						toReturn += "due to construction ";
						break;
					case "ACCIDENT":
						toReturn += "due to accident ";
						break;
					case "WEATHER":
						toReturn += "due to weather ";
				}
				toReturn += "until " + incident.getEndTime() + ". ";
			}
		}
		if (toReturn.equals("")){
			toReturn = "There is no traffic incident.";
		}
		return toReturn;
	}

	/**
	 * Generates answer sentence about ends of restrictions
	 * @param streetIncidentInfo information about restrictions ends
	 * @return answer sentence
	 */
	private String generateAnswerTextRestrictionEnds(StreetIncidentInfo streetIncidentInfo) {
		String toReturn = "";
		ArrayList<Incident> incidents = streetIncidentInfo.getIncidents();
		for (Incident incident : incidents) {
			if (incident.isActive().equals("ACTIVE")) {
				toReturn += "The ";
				switch (incident.getType()) {
					case "CONSTRUCTION":
						toReturn += "construction ";
						break;
					case "ACCIDENT":
						toReturn += "accident ";
						break;
					default:
						toReturn += "restriction ";
				}
				toReturn += "between " + incident.getOrigin() + " and " + incident.getTo() + " ";
				toReturn += "will end at " + incident.getEndTime() + ". ";
			}
		}
		if (toReturn.equals("")) {
			toReturn = "There are no traffic restrictions in this street.";
		}
		return toReturn;
	}

	/**
	 * Generates answer sentence about streets closures
	 * @param streetIncidentInfo information about closures
	 * @return answer sentence
	 */
	private String generateAnswerTextClosure(StreetIncidentInfo streetIncidentInfo) {
		String toReturn = "";
		ArrayList<Incident> incidents = streetIncidentInfo.getIncidents();
		for (Incident incident : incidents) {
			if (incident.isActive().equals("ACTIVE") && incident.isRoadClosed()) {
				toReturn += "The route is closed ";
				switch (incident.getType()) {
					case "CONSTRUCTION":
						toReturn += "due to construction ";
						break;
					case "ACCIDENT":
						toReturn += "due to accident ";
						break;
					case "WEATHER":
						toReturn += "due to weather ";
				}
				toReturn += "between " + incident.getOrigin() + " and " + incident.getTo() + " ";
				toReturn += "until " + incident.getEndTime() + ". ";
			}
		}
		if (toReturn.equals("")) {
			if (incidents.size() == 0) {
				toReturn = "The route is fully passable.";
			} else {
				toReturn = "The route is limitedly passable.";
			}

		}
		return toReturn;
	}

	/**
	 * Generates answer sentence about constructions
	 * @param streetIncidentInfo information about constructions
	 * @return answer sentence
	 */
	private String generateAnswerTextConstruction(StreetIncidentInfo streetIncidentInfo) {
		String toReturn = "";
		ArrayList<Incident> incidents = streetIncidentInfo.getIncidents();
		for (Incident incident : incidents) {
			if (incident.isActive().equals("ACTIVE") && incident.getType().equals("CONSTRUCTION")) {
				toReturn += "There is construction ";
				toReturn += "between " + incident.getOrigin() + " and " + incident.getTo() + " ";
				toReturn += "until " + incident.getEndTime() + ". ";
			}
		}
		if (toReturn.equals("")) {
			toReturn = "There are no construction works on this route.";
		}
		return toReturn;
	}
}
