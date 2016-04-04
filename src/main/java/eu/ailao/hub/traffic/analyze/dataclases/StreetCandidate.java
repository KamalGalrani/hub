package eu.ailao.hub.traffic.analyze.dataclases;

/**
 * Created by Petr Marek on 4/4/2016.
 */
public class StreetCandidate {
	private String streetName;
	private float distance;

	public StreetCandidate(String streetName, float distance) {
		this.streetName = streetName;
		this.distance = distance;
	}

	public String getStreetName() {
		return streetName;
	}

	public float getDistance() {
		return distance;
	}
}
