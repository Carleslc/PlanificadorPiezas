package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
		phases = new LinkedList<>();
		setWorkMinutes();
	}
	
	private void setWorkMinutes() {
		workMinutes = (int) current.until(close, ChronoUnit.MINUTES);
	}
	
	public LocalDateTime getCurrentTime() {
		return current;
	}
	
	public void setCurrentTime(LocalDateTime current) {
		LocalTime time = current.toLocalTime();
		LocalTime openTime = open.toLocalTime();
		LocalTime closeTime = close.toLocalTime();
		if (time.isBefore(openTime) || time.isAfter(closeTime)) throw new IllegalArgumentException("time must be between open and close");
		LocalDate date = current.toLocalDate();
		if (!date.isEqual(open.toLocalDate())) {
			open = LocalDateTime.of(date, openTime);
			close = LocalDateTime.of(date, closeTime);
		}
		this.current = current;
		setWorkMinutes();
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
