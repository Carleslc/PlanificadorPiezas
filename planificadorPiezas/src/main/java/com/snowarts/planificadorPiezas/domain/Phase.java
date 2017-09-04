package com.snowarts.planificadorPiezas.domain;

class Phase {

	private int id;
	private int hours, minutes;
	private boolean postponed;
	private Order related;
	
	Phase(int id, double rawHours, Order related) {
		this(id, (int) rawHours, (int) (rawHours - ((int) rawHours))*60, related);
	}
	
	Phase(int id, int hours, int minutes, Order related) {
		this.id = id;
		this.related = related;
		this.hours = hours;
		this.minutes = minutes;
		postponed = false;
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

	public Order getRelated() {
		return related;
	}
	
	public void setPostponed() {
		postponed = true;
	}
	
	public boolean isPostponed() {
		return postponed;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phase other = (Phase) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		String s = "Pedido " + related.getId() + " Fase " + id + " | " + hours + "h";
		if (minutes > 0) s += " " + minutes + "m";
		return s;
	}
}
