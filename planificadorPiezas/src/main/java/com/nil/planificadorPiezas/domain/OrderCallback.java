package com.nil.planificadorPiezas.domain;

public interface OrderCallback {

	void onProcessed(Result result);
	
	void onError(Exception e);
	
}