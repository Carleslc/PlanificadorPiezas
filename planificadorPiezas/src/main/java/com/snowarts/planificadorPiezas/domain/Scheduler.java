package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

class Scheduler {
	
	private final int workers;
	private final LocalTime dayOpening, dayClosing;
	private ArrayList<LinkedList<WorkDay>> phases;
	
	Scheduler(int numPhases, int workers, LocalTime dayOpening, LocalTime dayClosing) {
		this.workers = workers;
		this.dayOpening = dayOpening;
		this.dayClosing = dayClosing;
		phases = new ArrayList<>(numPhases);
		fillPhases(numPhases);
	}
	
	private void fillPhases(int numPhases) {
		for (int i = 1; i <= numPhases; i++) phases.add(new LinkedList<>());
	}
	
	LocalDateTime add(Phase phase, ListIterator<Phase> remaining) {
		LinkedList<WorkDay> phaseQueue = phases.get(phase.getId() - 1);
		if (phaseQueue.isEmpty()) phaseQueue.add(newWorkDay(phase.getId(), phase.getRelated().getStartDate()));
		WorkDay currentDay = phaseQueue.getLast();
		LocalDateTime start = currentDay.getCurrentTime();
		int remainingMinutes = currentDay.getRemainingMinutes();
		int remainingMinutesPerWorker = (int) Math.ceil(((double) remainingMinutes)/workers);
		int minutes = phase.getTotalMinutes();
		int minutesPerWorker = (int) Math.ceil(((double) minutes)/workers);
		ScheduledPhase scheduled;
		if (remainingMinutes >= minutes) {
			scheduled = new ScheduledPhase(phase, start, start.plusMinutes(minutesPerWorker));
		} else {
			// Split Phase
			double rawHoursFirst = remainingMinutes/60.0;
			Phase first = new Phase(phase.getId(), rawHoursFirst, phase.getRelated());
			int splittedMinutes = minutes - remainingMinutes;
			double rawHoursSecond = splittedMinutes/60.0;
			Phase second = new Phase(phase.getId(), rawHoursSecond, phase.getRelated());
			scheduled = new ScheduledPhase(first, start, start.plusMinutes(remainingMinutesPerWorker));
			remaining.add(second);
			remaining.previous();
		}
		currentDay.add(scheduled);
		if (currentDay.isFinished()) phaseQueue.add(newWorkDay(phase.getId(), currentDay.getOpen().plusDays(1).toLocalDate()));
		return scheduled.getScheduledFinishDate();
	}
	
	WorkDay newWorkDay(int phaseId, LocalDate day) {
		return new WorkDay(phaseId, day.atTime(dayOpening), day.atTime(dayClosing), workers);
	}
	
	@Override
	public String toString() {
		StringBuilder phaseBuilder = new StringBuilder();
		for (int i = 0; i < phases.size(); i++) {
			phaseBuilder.append("PLANIFICACIÓN FASE ").append(i + 1).append("\n");
			phases.get(i).forEach(workDay -> phaseBuilder.append(workDay).append("\n"));
		}
		return phaseBuilder.toString();
	}

}
