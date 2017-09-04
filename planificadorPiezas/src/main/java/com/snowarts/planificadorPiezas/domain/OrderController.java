package com.snowarts.planificadorPiezas.domain;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang.Validate;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.snowarts.planificadorPiezas.data.DataController;

public class OrderController {

	private DataController data;
	
	public OrderController() throws IOException, InvalidConfigurationException, ClassNotFoundException, SQLException {
		data = new DataController();
	}
	
	public void process(OrderDTO dto, OrderCallback callback) {
		Validate.notNull(dto);
		OrderProcessor processor = new OrderProcessor(getOrder(dto), data);
		processor.setCallback(callback);
		processor.process();
	}
	
	/** Convert OrderDTO to Order */
	private Order getOrder(OrderDTO dto) {
		return new Order(dto, data.getOpenTime());
	}
	
	public boolean exists(String orderId) throws Exception {
		Validate.notNull(orderId);
		return data.exists(orderId);
	}
	
	public OrderDTO getOrder(String orderId) throws Exception {
		Validate.notNull(orderId);
		return data.get(orderId);
	}
	
	public void deleteOrder(String orderId) throws Exception {
		Validate.notNull(orderId);
		data.delete(orderId);
	}
	
	public int getPhases() {
		return data.getPhases();
	}
}