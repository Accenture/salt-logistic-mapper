package de.salt.sce.model.edifact;

import java.io.Serializable;

public class Rff implements Serializable {

	private String reference;
	private String qualifier;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

}
