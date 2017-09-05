package com.snowarts.planificadorPiezas.domain;

public class PhaseDTO {

	private int id;
	private double rawHours;
	private boolean external;
	
	public PhaseDTO(int id, double rawHours, boolean external) {
		this.id = id;
		this.rawHours = rawHours;
		this.external = external;
	}

	public int getId() {
		return id;
	}

	public double getRawHours() {
		return rawHours;
	}

	public boolean isExternal() {
		return external;
	}
	
	@Override
	public String toString() {
		return "#" + id + " -> " + rawHours + "h" + (external ? " (Proveedor externo)" : "");
	}
}
