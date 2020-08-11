package de.salt.sce.mapper.model.edifact;

import java.util.List;

public class Transport {
	
	private List<Shipment> shipments;
	
	public List<Shipment> getShipments() {
		return shipments;
	}

	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
	}
	
}
