package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

class WorkDay {

	private LocalDateTime open, close, current;
	private LinkedList<ScheduledPhase> phases;
	private int phaseId, workMinutes;
	
	WorkDay(int phaseId, LocalDateTime open, LocalDateTime close, int workers) {
		if (open.isAfter(close)) throw new IllegalArgumentException("open must be before close");
		this.phaseId = phaseId;
		this.open = current = open;
		this.close = close;
		int totalMinutes = (int) open.until(close, ChronoUnit.MINUTES);
		workMinutes = totalMinutes*workers;
		phases = new LinkedList<>();
	}
	
	public int getPhaseId() {
		return phaseId;
	}
	
	public LocalDateTime getCurrentTime() {
		return current;
	}
	
	public int getRemainingMinutes() {
		return workMinutes;
	}
	
	public boolean isFinished() {
		return current.equals(close);
	}
	
	public void add(ScheduledPhase phase) {
		if (phase.getPhase().getId() != phaseId) throw new IllegalArgumentException("phase id must match the phase id of this workday");
		int minutes = phase.getPhase().getTotalMinutes();
		if (minutes > getRemainingMinutes()) throw new IllegalArgumentException("Not enough working minutes");
		workMinutes -= minutes;
		current = phase.getScheduledFinishDate();
		phases.add(phase);
	}

	public LocalDateTime getOpen() {
		return open;
	}

	public LocalDateTime getClose() {
		return close;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		phases.forEach(scheduled -> builder.append(scheduled).append("\n"));
		return builder.toString();
	}
}
