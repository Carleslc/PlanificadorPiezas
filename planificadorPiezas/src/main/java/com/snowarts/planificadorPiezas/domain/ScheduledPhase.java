package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;

import static com.snowarts.planificadorPiezas.data.utils.DateUtils.format;

class ScheduledPhase {

	private Phase phase;
	private LocalDateTime start, finish;
	
	ScheduledPhase(Phase phase, LocalDateTime start, LocalDateTime finish) {
		this.phase = phase;
		this.start = start;
		this.finish = finish;
	}

	public Phase getPhase() {
		return phase;
	}
	
	public LocalDateTime getScheduledStartDate() {
		return start;
	}
	
	public LocalDateTime getScheduledFinishDate() {
		return finish;
	}
	
	@Override
	public String toString() {
		return phase + " | " + format(start, FormatStyle.SHORT) + " -> " + format(finish, FormatStyle.SHORT);
	}
	
}
