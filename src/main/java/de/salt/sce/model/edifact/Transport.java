package de.salt.sce.model.edifact;

import java.io.Serializable;
import java.util.List;

public class Transport implements Serializable {
	
	private List<Shipment> shipments;
	
	public List<Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
	}
	
}
