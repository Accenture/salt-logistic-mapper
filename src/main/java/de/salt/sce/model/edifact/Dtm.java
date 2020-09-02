package de.salt.sce.model.edifact;


import java.io.Serializable;

public class Dtm implements Serializable {

	private String dateTimePeriod;

	public String getDateTimePeriod() {
		return dateTimePeriod;
	}

	public void setDateTimePeriod(String dateTimePeriod) {
		this.dateTimePeriod = dateTimePeriod;
	}

}
