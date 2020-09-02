package de.salt.sce.model.edifact;


import java.io.Serializable;

public class Pci implements Serializable {

	private String markingInstructionsCode;
	private String shippingMarksDescription1;
	
	public String getMarkingInstructionsCode() {
		return markingInstructionsCode;
	}
	public void setMarkingInstructionsCode(String markingInstructionsCode) {
		this.markingInstructionsCode = markingInstructionsCode;
	}
	public String getShippingMarksDescription1() {
		return shippingMarksDescription1;
	}
	public void setShippingMarksDescription1(String shippingMarksDescription1) {
		this.shippingMarksDescription1 = shippingMarksDescription1;
	}	

}
