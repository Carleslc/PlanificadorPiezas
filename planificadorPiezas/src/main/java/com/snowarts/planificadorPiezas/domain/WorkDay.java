package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class WorkDay {

	private LocalDateTime open, close, current;
	private int workMinutes, workers;
	
	WorkDay(LocalDateTime open, LocalDateTime close, int workers) {
		if (open.isAfter(close)) throw new IllegalArgumentException("open must be before close");
		this.open = current = open;
		this.close = close;
		this.workers = workers;
		int totalMinutes = (int) open.until(close, ChronoUnit.MINUTES);
		workMinutes = totalMinutes*workers;
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
		int minutes = phase.getPhase().getTotalMinutes();
		if (minutes > getRemainingMinutes()) throw new IllegalArgumentException("Not enough working minutes");
		workMinutes -= minutes;
		current = phase.getScheduledFinishDate();
	}

	public LocalDateTime getOpen() {
		return open;
	}

	public LocalDateTime getClose() {
		return close;
	}
	
	@Override
	public String toString() {
		return open + " < " + current + " > " + close + ". Workers: " + workers;
	}
}
