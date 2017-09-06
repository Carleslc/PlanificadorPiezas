package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.List;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;

public class OrderDTO {
	
	private String id;
	private List<PhaseDTO> phases;
	private LocalDateTime startDate, finishDate;
	
	public OrderDTO(String id, List<PhaseDTO> phases, LocalDateTime startDate) {
		this.id = id;
		this.phases = phases;
		this.startDate = startDate;
	}
	
	public String getId() {
		return id;
	}
	
	public List<PhaseDTO> getPhases() {
		return phases;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	public void setFinishDate(LocalDateTime finishDate) {
		this.finishDate = finishDate;
	}
	
	public LocalDateTime getFinishDate() {
		return finishDate;
	}
	
	@Override
	public String toString() {
		return "#" + id + " | Inicio: " + DateUtils.format(startDate, FormatStyle.SHORT) + " | Fases: " + phases;
	}
	
}