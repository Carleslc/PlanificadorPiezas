package com.snowarts.planificadorPiezas.domain;

class Phase {

	private int id;
	private int hours, minutes;
	
	Phase(int id, double rawHours) {
		this.id = id;
		hours = (int) rawHours;
		minutes = (int) (rawHours - hours)*60;
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
