package eu.ailao.hub.traffic.hereapi;

/**
 * Created by Petr Marek on 21.03.2016.
 * Class containing information about question topics
 */
public class TrafficQuestionInfo {

	private TrafficTopic trafficTopic;
	private String streetName;

	public TrafficQuestionInfo(TrafficTopic trafficTopic, String streetName) {
		this.trafficTopic = trafficTopic;
		this.streetName = streetName;
	}

	public TrafficTopic getTrafficTopic() {
		return trafficTopic;
	}

	public String getStreetName() {
		return streetName;
	}
}
