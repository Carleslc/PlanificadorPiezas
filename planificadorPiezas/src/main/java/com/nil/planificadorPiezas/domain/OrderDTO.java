package com.nil.planificadorPiezas.domain;

import java.time.LocalDate;
import java.util.Map;

public class OrderDTO {
	
	private String id;
	private Map<Integer, Double> phases;
	private LocalDate startDate;
	
	public OrderDTO(String id, Map<Integer, Double> phases, LocalDate startDate) {
		this.id = id;
		this.phases = phases;
		this.startDate = startDate;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<Integer, Double> getPhases() {
		return this.phases;
	}

	public LocalDate getStartDate() {
		return startDate;
	}
	
	@Override
	public String toString() {
		return "#" + id + " | Start: " + startDate + " | Phases: " + phases;
	}
	
}