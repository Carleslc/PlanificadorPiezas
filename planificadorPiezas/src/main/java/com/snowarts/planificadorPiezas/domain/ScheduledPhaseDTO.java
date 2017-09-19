package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDateTime;
import java.time.format.FormatStyle;

import static com.snowarts.planificadorPiezas.data.utils.DateUtils.format;

public class ScheduledPhaseDTO {

	private ScheduledPhase scheduledPhase;

	ScheduledPhaseDTO(ScheduledPhase scheduledPhase) {
		this.scheduledPhase = scheduledPhase;
	}

	public String getOrderId() {
		return scheduledPhase.getPhase().getRelated().getId();
	}

	public int getPhaseId() {
		return scheduledPhase.getPhase().getId();
	}

	public int getHours() {
		return scheduledPhase.getPhase().getHours();
	}

	public int getMinutes() {
		return scheduledPhase.getPhase().getMinutes();
	}

	public LocalDateTime getStartDate() {
		return scheduledPhase.getScheduledStartDate();
	}

	public LocalDateTime getFinishDate() {
		return scheduledPhase.getScheduledFinishDate();
	}

	@Override
	public String toString() {
		return scheduledPhase.getPhase() + " | "
				+ format(getStartDate(), FormatStyle.SHORT) + " -> " + format(getFinishDate(), FormatStyle.SHORT);
	}

}
