package com.nil.planificadorPiezas.domain;

public interface PieceCallback {

	void onProcessed(Result result);
	
	void onError(Exception e);
	
}