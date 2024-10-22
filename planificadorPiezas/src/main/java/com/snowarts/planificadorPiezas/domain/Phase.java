package com.snowarts.planificadorPiezas.domain;

class Phase implements Comparable<Phase> {

	private int id;
	private String tag;
	private int hours, minutes;
	private boolean postponed, external;
	private Order related;
	
	Phase(int id, double rawHours, boolean external, Order related) {
		this(id, (int) rawHours, getMinutes(rawHours), external, related);
	}
	
	Phase(int id, int hours, int minutes, boolean external, Order related) {
		this.id = id;
		this.related = related;
		this.hours = hours;
		this.minutes = minutes;
		postponed = false;
	}
	
	private static int getMinutes(double rawHours) {
		int hours = (int) rawHours;
		return (int) ((rawHours - hours)*60);
	}
	
	boolean isExternal() {
		return external;
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
	
	void setTag(String tag) {
		this.tag = tag;
	}
	
	String getTag() {
		return tag;
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
	public int compareTo(Phase o) {
		int compare = related.compareTo(o.related);
		if (compare == 0) compare = Integer.compare(id, o.id);
		if (compare == 0) compare = related.getId().compareTo(o.related.getId());
		return compare;
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
		String s = "Pedido " + related.getId() + " Fase " + id + (tag != null ? " (" + tag + ")" : "") + " | " + hours + "h";
		if (minutes > 0) s += " " + minutes + "m";
		return s;
	}
}
