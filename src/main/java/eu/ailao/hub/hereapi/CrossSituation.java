package eu.ailao.hub.hereapi;

/**
 * Created by Petr Marek on 10.03.2016.
 * Class with information about traffic flow on one crossing
 */
public class CrossSituation {
	private String owningStreet;
	private String secondStreet;
	private double jamFactor;

	public CrossSituation(String owningStreet, String secondStreet, double jamFactor) {
		this.owningStreet = owningStreet;
		this.secondStreet = secondStreet;
		this.jamFactor = jamFactor;
	}

	public String getOwningStreet() {
		return owningStreet;
	}

	public String getSecondStreet() {
		return secondStreet;
	}

	public double getJamFactor() {
		return jamFactor;
	}
}
