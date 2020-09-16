package de.salt.sce.model.edifact;

import java.io.Serializable;

public class Ftx implements Serializable {

	private String freeText;

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

}
