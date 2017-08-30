package com.nil.planificadorPiezas.domain;

import java.util.Map;

public class Piece {
	
	public String id;
	public Map<Integer, Double> phases ;
	
	Piece(String id, Map<Integer, Double> phases)  {
		this.id = id;
		this.phases = phases;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<Integer, Double> getPhases(){
		return phases;
	}
	
}