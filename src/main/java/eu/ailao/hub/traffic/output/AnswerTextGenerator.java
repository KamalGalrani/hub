package eu.ailao.hub.traffic.output;

import eu.ailao.hub.traffic.hereapi.dataclasses.CrossSituation;
import eu.ailao.hub.traffic.hereapi.dataclasses.Incident;
import eu.ailao.hub.traffic.hereapi.dataclasses.StreetFlowInfo;
import eu.ailao.hub.traffic.hereapi.dataclasses.StreetIncidentInfo;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 17.03.2016.
 * Class for generating question text from data
 */
public class AnswerTextGenerator {

	/**
	 * Generates answer sentence
	 * @param streetFlowInfo information about street flow
	 * @return answer sentence
	 */
	public String generateAnswerText(StreetFlowInfo streetFlowInfo) {
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
			int minJamFactorOutput = (int) ((minJamFactor + 1) / 2);
			int maxJamFactorOutput = (int) ((maxJamFactor + 1) / 2);
			if (minJamFactor == maxJamFactor) {
				return "Actual traffic situation on " + street + " is " + maxJamFactorOutput + ".";
			}
			return "Actual traffic situation on " + street + " is between degrees " + minJamFactorOutput + " and " + maxJamFactorOutput + ".";
		} else {
			return "Sorry, I don't have data for this street.";
		}
	}

	/**
	 * Generates answer sentence
	 * @param streetIncidentInfo information about street flow
	 * @return answer sentence
	 */
	public String generateAnswerText(StreetIncidentInfo streetIncidentInfo) {
		String toReturn = "";
		ArrayList<Incident> incidents = streetIncidentInfo.getIncidents();
		for (Incident incident : incidents) {
			if (incident.isRoadClosed()) {
				toReturn += "The road is closed ";
			} else {
				toReturn += "The traffic is limited ";
			}
			toReturn += "between " + incident.getOrigin() + " and " + incident.getTo() + " ";
			toReturn += "in the direction to " + incident.getDirection() +" ";
			switch (incident.getType()){
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

		return toReturn;
	}
}
