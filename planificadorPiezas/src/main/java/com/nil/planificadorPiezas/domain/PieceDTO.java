package com.nil.planificadorPiezas.domain;

import java.util.HashMap;
import java.util.Map;


public class PieceDTO {
	private Map<Integer, Double> phase = new HashMap<Integer, Double>();
	
	public PieceDTO(Map<Integer, Double> pieceDTO_list) {
		this.phase = pieceDTO_list;
	}
	
	public Map<Integer, Double>  getPhases(){
		return this.phase;
	}
	
}