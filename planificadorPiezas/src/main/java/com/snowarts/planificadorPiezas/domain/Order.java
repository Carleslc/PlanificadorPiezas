package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.simpleyaml.utils.Validate;

class Order implements Comparable<Order> {
	
	private ArrayList<Phase> phases;
	private LinkedList<ScheduledPhase> scheduler;
	private OrderDTO dto;
	private LocalDateTime startDate;
	private int state;
	
	Order(OrderDTO fromDTO, LocalTime startTime) {
		Validate.notNull(fromDTO);
		Validate.notNull(startTime);
		dto = fromDTO;
		startDate = dto.getStartDate().atTime(startTime)
				.plusMinutes(getTotalMinutes(dto.getPhases().stream()
						.filter(phase -> phase.isExternal())
						.mapToDouble(phase -> phase.getRawHours())
						.sum()));
		phases = dto.getPhases().stream()
				.filter(phase -> !phase.isExternal())
				.map(phase -> new Phase(phase.getId(), phase.getRawHours(), phase.isExternal(), this))
				.collect(Collectors.toCollection(ArrayList::new));
		state = 0;
		scheduler = new LinkedList<>();
	}
	
	private static int getTotalMinutes(double rawHours) {
		int hours = (int) rawHours;
		int minutes = (int) ((rawHours - hours)*60);
		return hours*60 + minutes;
	}
	
	String getId() {
		return dto.getId();
	}
	
	ArrayList<Phase> getPhases() {
		return phases;
	}
	
	Phase getState() {
		if (state >= phases.size()) return null;
		return phases.get(state);
	}
	
	void add(ScheduledPhase scheduled) {
		scheduler.add(scheduled);
		int scheduledPhaseMinutes = scheduler.stream()
				.filter(s -> s.getPhase().equals(scheduled.getPhase()))
				.mapToInt(s -> s.getPhase().getTotalMinutes())
				.sum();
		if (scheduledPhaseMinutes == getState().getTotalMinutes()) state++;
	}
	
	LocalDateTime getScheduledFinishDate() {
		return scheduler.isEmpty() ? getStartDate() : scheduler.getLast().getScheduledFinishDate();
	}
	
	LocalDateTime getStartDate() {
		return startDate;
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