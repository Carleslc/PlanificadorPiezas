package com.nil.planificadorPiezas.domain;

@FunctionalInterface
public interface PieceCallback {

	void onProcessed(Result result);
	
}