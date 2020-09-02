package de.salt.sce.model.edifact;

import java.io.Serializable;

public class Sts implements Serializable {

	private String event;
	private String reason;
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

}
