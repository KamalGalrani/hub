package eu.ailao.hub.traffic;

import eu.ailao.hub.traffic.hereapi.CrossSituation;
import eu.ailao.hub.traffic.hereapi.StreetFlowInfo;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 17.03.2016.
 * Class for generating question text from data
 */
public class AnswerTextGenerator {

	public String generateAnswerText(StreetFlowInfo streetFlowInfo){
		ArrayList<CrossSituation> crossSituations = streetFlowInfo.getSituationsOnCrosses();
		String street=crossSituations.get(0).getOwningStreet();
		float maxJamFactor=0;
		for (CrossSituation crossSituation : crossSituations){
			float crossJamFactor=(float)crossSituation.getJamFactor();
			if (crossJamFactor>maxJamFactor){
				maxJamFactor=crossJamFactor;
			}
		}

		return "Actual traffic situation on "+street+" street is "+maxJamFactor;
	}
}
