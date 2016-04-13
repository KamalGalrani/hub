package eu.ailao.hub.traffic.analyze.dataclases;

/**
 * Created by Petr Marek on 21.03.2016.
 * Class containing information about question topics
 */
public class TrafficQuestionInfo {

	private TrafficTopic trafficTopic;
	private String streetName;
	private String streetNameFrom;
	private String streetNameTo;

	public TrafficQuestionInfo(TrafficTopic trafficTopic, String streetName) {
		this.trafficTopic = trafficTopic;
		this.streetName = streetName;
	}

	public TrafficQuestionInfo(TrafficTopic trafficTopic, String streetNameFrom, String streetNameTo) {
		this.trafficTopic = trafficTopic;
		this.streetNameFrom = streetNameFrom;
		this.streetNameTo = streetNameTo;
	}

	public TrafficTopic getTrafficTopic() {
		return trafficTopic;
	}

	public String getStreetName() {
		return streetName;
	}

	public String getStreetNameFrom() {
		return streetNameFrom;
	}

	public String getStreetNameTo() {
		return streetNameTo;
	}

	@Override
	public String toString() {
		if (streetName!=null){
			return trafficTopic+"\t"+streetName;
		}else{
			return trafficTopic+"\t"+streetNameFrom+"\t"+streetNameTo;
		}
	}
}
