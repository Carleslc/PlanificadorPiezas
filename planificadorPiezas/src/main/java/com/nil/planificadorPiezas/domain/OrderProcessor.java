package com.nil.planificadorPiezas.domain;

import java.sql.SQLException;

import com.nil.planificadorPiezas.data.DataController;

class OrderProcessor {

	private Order order;
	private Result result;
	private OrderCallback callback;
	private DataController data;
	
	OrderProcessor(Order order, DataController data) {
		this.order = order;
		this.data = data;
	}
	
	void setCallback(OrderCallback callback) {
		this.callback = callback;
	}
	
	/** Process order asynchronously */
	void process() {
		new Thread(() -> {
			try {
				compute();
				if (callback != null) callback.onProcessed(result);
			} catch (Exception e) {
				callback.onError(e);
			}
		}).start();
	}
	
	private void compute() throws ClassNotFoundException, SQLException {
		data.save(order.toDTO());
		data.printAll();
		
		// Set the result
		result = new Result(order.getId(), order.getStartDate());
	}
	
}