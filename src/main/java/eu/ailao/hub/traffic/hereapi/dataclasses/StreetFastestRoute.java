package eu.ailao.hub.traffic.hereapi.dataclasses;

/**
 * Created by Petr Marek on 3/30/2016.
 * Class storing about one street on fastest route
 */
public class StreetFastestRoute {
	private String name;
	private int length;
	private int indexInRoute;

	public StreetFastestRoute(String name, int length, int indexInRoute) {
		this.name = name;
		this.length = length;
		this.indexInRoute = indexInRoute;
	}

	public void addLength(int length){
		this.length += length;
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}

	public int getIndexInRoute() {
		return indexInRoute;
	}
}
