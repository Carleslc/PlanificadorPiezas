package com.nil.planificadorPiezas.domain;

class Phase {

	private int id;
	private int hours, minutes;
	
	Phase(int id, double hoursRaw) {
		this.id = id;
		hours = (int) hoursRaw;
		minutes = (int) (hoursRaw - hours)*60;
	}
	
	int getHours() {
		return hours;
	}
	
	int getMinutes() {
		return minutes;
	}
	
	int getTotalMinutes() {
		return hours*60 + minutes;
	}
	
	int getId() {
		return id;
	}
	
}
