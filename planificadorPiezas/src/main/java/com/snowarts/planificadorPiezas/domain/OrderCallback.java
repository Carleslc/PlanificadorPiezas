package com.snowarts.planificadorPiezas.domain;

public interface OrderCallback {

	void onProcessed(Result result);
	
	void onError(Exception e);
	
}