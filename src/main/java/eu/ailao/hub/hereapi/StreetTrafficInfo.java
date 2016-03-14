package eu.ailao.hub.hereapi;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 10.03.2016.
 * Class with information about traffic flow info in street
 */
public class StreetTrafficInfo {
	private ArrayList<CrossSituation> situationsOnCrosses = new ArrayList<>();

	public void addSituationOnCross(String owningStreet, String secondStreet, double jamFactor) {
		CrossSituation crossSituation = new CrossSituation(owningStreet, secondStreet, jamFactor);
		situationsOnCrosses.add(crossSituation);
	}

	public ArrayList<CrossSituation> getSituationsOnCrosses() {
		return situationsOnCrosses;
	}
}