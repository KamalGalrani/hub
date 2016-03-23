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
			for (CrossSituation crossSituation : crossSituations) {
				float crossJamFactor = (float) crossSituation.getJamFactor();
				if (crossJamFactor > maxJamFactor) {
					maxJamFactor = crossJamFactor;
				}
			}
			return "Actual traffic situation on " + street + " street is " + maxJamFactor;
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
		switch (incidents.size()) {
			case 0:
				return "There is no traffic incident";
			case 1:
				toReturn = "There is incident ";
				break;
			default:
				toReturn = "There are incidents ";
				break;
		}
		for (Incident incident : incidents) {
			toReturn += "with criticality " + incident.getCriticality() + ", ";
		}
		return toReturn;
	}
}
