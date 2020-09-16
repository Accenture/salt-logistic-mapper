package de.salt.sce.model.edifact;


import java.io.Serializable;

public class Cni implements Serializable {

	private String consolidationTtemNumber;
	private String documentMessageNumber;
	
	public String getConsolidationTtemNumber() {
		return consolidationTtemNumber;
	}
	public void setConsolidationTtemNumber(String consolidationTtemNumber) {
		this.consolidationTtemNumber = consolidationTtemNumber;
	}
	public String getDocumentMessageNumber() {
		return documentMessageNumber;
	}
	public void setDocumentMessageNumber(String documentMessageNumber) {
		this.documentMessageNumber = documentMessageNumber;
	}

}
