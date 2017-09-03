package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

class Order {
	
	private List<Phase> phases;
	private OrderDTO dto;
	
	Order(OrderDTO fromDTO) {
		dto = fromDTO;
		phases = dto.getPhases()
				.entrySet().stream()
				.map(idHours -> new Phase(idHours.getKey(), idHours.getValue()))
				.collect(Collectors.toList());
	}
	
	String getId() {
		return dto.getId();
	}
	
	List<Phase> getPhases() {
		return phases;
	}
	
	LocalDate getStartDate() {
		return dto.getStartDate();
	}
	
	OrderDTO toDTO() {
		return dto;
	}
	
}