package de.salt.sce.model.edifact;


import java.io.Serializable;

public class Tdt implements Serializable {

	private String transportStageQualifier;
	
	public String getTransportStageQualifier() {
		return transportStageQualifier;
	}
	public void setTransportStageQualifier(String transportStageQualifier) {
		this.transportStageQualifier = transportStageQualifier;
	}

}
