package eu.ailao.hub.traffic.hereapi.dataclasses;

/**
 * Created by Petr Marek on 3/30/2016.
 */
public class Position {
	private double latitude;
	private double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
