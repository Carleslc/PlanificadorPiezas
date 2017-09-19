package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;

import static com.snowarts.planificadorPiezas.data.utils.DateUtils.format;

class ScheduledPhase implements Comparable<ScheduledPhase> {

	private Phase phase;
	private LocalDateTime start, finish;
	
	ScheduledPhase(Phase phase, LocalDateTime start, LocalDateTime finish) {
		this.phase = phase;
		this.start = start;
		this.finish = finish;
	}

	Phase getPhase() {
		return phase;
	}
	
	LocalDateTime getScheduledStartDate() {
		return start;
	}
	
	LocalDateTime getScheduledFinishDate() {
		return finish;
	}
	
	@Override
	public int compareTo(ScheduledPhase o) {
		int c = start.compareTo(o.start);
		if (c == 0) c = finish.compareTo(o.finish);
		return c;
	}
	
	@Override
	public String toString() {
		return phase + " | " + format(start, FormatStyle.SHORT) + " -> " + format(finish, FormatStyle.SHORT);
	}
	
}
