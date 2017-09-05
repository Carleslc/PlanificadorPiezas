package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.time.format.FormatStyle;
import java.util.Map;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;

public class OrderDTO {
	
	private String id;
	private Map<Integer, Double> phases;
	private LocalDate startDate, finishDate;
	
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
	
	public void setFinishDate(LocalDate finishDate) {
		this.finishDate = finishDate;
	}
	
	public LocalDate getFinishDate() {
		return finishDate;
	}
	
	@Override
	public String toString() {
		return "#" + id + " | Inicio: " + DateUtils.format(startDate, FormatStyle.SHORT) + " | Fases: " + phases;
	}
	
}