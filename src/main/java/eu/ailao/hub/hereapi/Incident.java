package eu.ailao.hub.hereapi;

/**
 * Created by Petr Marek on 15.03.2016.
 */
public class Incident {
	private String active;
	private String type;
	private String startTime;
	private String endTime;
	private String criticality;
	private String comment;
	private String origin;
	private String to;
	private String direction;

	public Incident(String active, String type, String startTime, String endTime, String criticality, String comment, String origin, String to, String direction) {
		this.active = active;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.criticality = criticality;
		this.comment = comment;
		this.origin = origin;
		this.to = to;
		this.direction = direction;
	}

	public String isActive() {
		return active;
	}

	public String getType() {
		return type;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getCriticality() {
		return criticality;
	}

	public String getComment() {
		return comment;
	}

	public String getOrigin() {
		return origin;
	}

	public String getTo() {
		return to;
	}

	public String getDirection() {
		return direction;
	}
}
