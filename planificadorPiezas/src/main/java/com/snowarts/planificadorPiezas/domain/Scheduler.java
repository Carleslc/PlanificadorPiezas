package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class Scheduler {
	
	private final LocalTime dayOpening, dayClosing;
	private ArrayList<LinkedList<WorkDay>> phases;
	
	Scheduler(int numPhases, LocalTime dayOpening, LocalTime dayClosing) {
		this.dayOpening = dayOpening;
		this.dayClosing = dayClosing;
		phases = new ArrayList<>(numPhases);
		fillPhases(numPhases);
	}
	
	private void fillPhases(int numPhases) {
		for (int i = 1; i <= numPhases; i++) phases.add(new LinkedList<>());
	}
	
	void add(Phase phase, Deque<Phase> remaining) {
		LinkedList<WorkDay> phaseQueue = phases.get(phase.getId() - 1);
		if (phaseQueue.isEmpty()) {
			LocalDateTime startDate = phase.getRelated().getStartDate();
			WorkDay day = newWorkDay(phase.getId(), fixDate(startDate).toLocalDate());
			phaseQueue.add(day);
		}
		WorkDay currentDay = phaseQueue.getLast();
		LocalDateTime start = currentDay.getCurrentTime();
		LocalDateTime lastScheduledPhaseTime = fixDate(phase.getRelated().getScheduledFinishDate());
		if (!phase.equals(phase.getRelated().getState()) || start.isBefore(lastScheduledPhaseTime)) {
			if (phase.isPostponed()) {
				currentDay.setCurrentTime(lastScheduledPhaseTime);
				start = lastScheduledPhaseTime;
			} else {
				remaining.add(phase);
				phase.setPostponed();
				return;
			}
		}
		int remainingMinutes = currentDay.getRemainingMinutes();
		int minutes = phase.getTotalMinutes();
		ScheduledPhase scheduled;
		if (remainingMinutes >= minutes) {
			scheduled = new ScheduledPhase(phase, start, start.plusMinutes(minutes));
		} else {
			// Split Phase
			double rawHoursFirst = remainingMinutes/60.0;
			Phase first = new Phase(phase.getId(), rawHoursFirst, false, phase.getRelated());
			int splittedMinutes = minutes - remainingMinutes;
			double rawHoursSecond = splittedMinutes/60.0;
			Phase second = new Phase(phase.getId(), rawHoursSecond, false, phase.getRelated());
			scheduled = new ScheduledPhase(first, start, start.plusMinutes(remainingMinutes));
			remaining.push(second);
		}
		add(scheduled, currentDay, phaseQueue);
	}
	
	private LocalDateTime fixDate(LocalDateTime current) {
		if (current.toLocalTime().isAfter(dayClosing)) current = current.toLocalDate().plusDays(1).atTime(dayOpening);
		return current;
	}
	
	private void add(ScheduledPhase scheduled, WorkDay workDay, LinkedList<WorkDay> phaseQueue) {
		Phase phase = scheduled.getPhase();
		if (scheduled.getPhase().getTotalMinutes() > 0) {
			workDay.add(scheduled);
			phase.getRelated().add(scheduled);
		}
		checkDay(workDay, phase.getId(), phaseQueue);
	}
	
	private void checkDay(WorkDay workDay, int phaseId, LinkedList<WorkDay> phaseQueue) {
		if (workDay.isFinished()) phaseQueue.add(newWorkDay(phaseId, workDay.getOpen().plusDays(1).toLocalDate()));
	}
	
	WorkDay newWorkDay(int phaseId, LocalDate day) {
		return new WorkDay(phaseId, day.atTime(dayOpening), day.atTime(dayClosing));
	}
	
	@Override
	public String toString() {
		StringBuilder phaseBuilder = new StringBuilder();
		for (int i = 0; i < phases.size(); i++) {
			phaseBuilder.append("PLANIFICACIÓN FASE ").append(i + 1).append("\n");
			List<WorkDay> days = phases.get(i);
			if (days.isEmpty()) phaseBuilder.append("No hay nada para esta fase.").append("\n");
			days.forEach(workDay -> {
				List<ScheduledPhase> scheduledPhases = workDay.getPhases();
				scheduledPhases.forEach(scheduled -> phaseBuilder.append(scheduled).append("\n"));
			});
			phaseBuilder.append("\n");
		}
		return phaseBuilder.toString();
	}

}
