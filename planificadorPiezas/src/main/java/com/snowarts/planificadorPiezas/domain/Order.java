package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.simpleyaml.utils.Validate;

class Order implements Comparable<Order> {
	
	private List<Phase> phases;
	private OrderDTO dto;
	
	Order(OrderDTO fromDTO) {
		Validate.notNull(fromDTO);
		dto = fromDTO;
		phases = dto.getPhases()
				.entrySet().stream()
				.map(idHours -> new Phase(idHours.getKey(), idHours.getValue(), this))
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

	@Override
	public int compareTo(Order o) {
		return getStartDate().compareTo(o.getStartDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dto.getId().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return dto.getId().equals(other.dto.getId());
	}
	
}