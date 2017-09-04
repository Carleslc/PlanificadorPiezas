package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import static com.snowarts.planificadorPiezas.data.utils.DateUtils.format;

class WorkDay {

	private LocalDateTime open, close, current;
	private LinkedList<ScheduledPhase> phases;
	private int workMinutes;
	
	WorkDay(int phaseId, LocalDateTime open, LocalDateTime close) {
		if (open.isAfter(close)) throw new IllegalArgumentException("open must be before close");
		this.open = current = open;
		this.close = close;
		workMinutes = (int) open.until(close, ChronoUnit.MINUTES);
		phases = new LinkedList<>();
	}
	
	public LocalDateTime getCurrentTime() {
		return current;
	}
	
	public int getRemainingMinutes() {
		return workMinutes;
	}
	
	public boolean isFinished() {
		return workMinutes == 0;
	}
	
	public void add(ScheduledPhase phase) {
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
	
	public List<ScheduledPhase> getPhases() {
		return phases;
	}
	
	@Override
	public String toString() {
		return format(open, FormatStyle.SHORT) + " < " + format(current, FormatStyle.SHORT) + " > " + format(close, FormatStyle.SHORT);
	}
}
