package com.nil.planificadorPiezas.domain;

import java.io.IOException;

import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.nil.planificadorPiezas.data.DataController;

public class OrderController {

	private DataController data;
	
	public OrderController() throws IOException, InvalidConfigurationException {
		data = new DataController();
	}
	
	public void process(OrderDTO dto, OrderCallback callback) {
		OrderProcessor processor = new OrderProcessor(getOrder(dto), data);
		processor.setCallback(callback);
		processor.process();
	}
	
	/** Convert OrderDTO to Order */
	private Order getOrder(OrderDTO dto) {
		return new Order(dto);
	}
	
	public boolean exists(String orderId) throws Exception {
		return data.exists(orderId);
	}
	
	public int getPhases() {
		return data.getPhases();
	}

	public int getWorkers() {
		return data.getWorkers();
	}
	
	public int getDailyHours() {
		return data.getDailyHours();
	}
}