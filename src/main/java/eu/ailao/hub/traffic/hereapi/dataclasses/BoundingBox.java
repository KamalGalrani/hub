package eu.ailao.hub.traffic.hereapi.dataclasses;

/**
 * Created by Petr Marek on 10.03.2016.
 * Class representing bounding box, which is defined by latitude and longitude of top left corner and bottom right corner
 */
public class BoundingBox {
	private double topLeftLatitude;
	private double topLeftLongitude;
	private double bottomRightLatitude;
	private double bottomRightLongitude;

	public BoundingBox(double topLeftLatitude, double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude) {
		this.topLeftLatitude = topLeftLatitude;
		this.topLeftLongitude = topLeftLongitude;
		this.bottomRightLatitude = bottomRightLatitude;
		this.bottomRightLongitude = bottomRightLongitude;
	}

	public double getTopLeftLatitude() {
		return topLeftLatitude;
	}

	public double getTopLeftLongitude() {
		return topLeftLongitude;
	}

	public double getBottomRightLatitude() {
		return bottomRightLatitude;
	}

	public double getBottomRightLongitude() {
		return bottomRightLongitude;
	}
}
