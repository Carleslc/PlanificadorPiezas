package com.snowarts.planificadorPiezas.domain;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.snowarts.planificadorPiezas.data.DataController;

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
		List<OrderDTO> dtos = data.getAll();
		debug(dtos);
		LocalTime openTime = data.getOpenTime();
		List<Order> orders = dtos.stream().map(dto -> new Order(dto, openTime)).collect(Collectors.toCollection(LinkedList::new));
		Scheduler scheduler = new Scheduler(data.getPhases(), openTime, data.getCloseTime());
		
		ArrayDeque<Phase> remaining = new ArrayDeque<>();
		int maxPhases = orders.stream().map(o -> o.getPhases().size()).max(Integer::compare).get();
		for (int i = 0; i < maxPhases; ++i) {
			for (Order order : orders) {
				List<Phase> phases = order.getPhases();
				if (i < phases.size()) remaining.add(phases.get(i));
			}
		}
		
		while (!remaining.isEmpty()) {
			Phase phase = remaining.poll();
			scheduler.add(phase, remaining);
		}
		
		System.out.println(scheduler);
		result = new Result(order.getId(), orders.get(orders.indexOf(this.order)).getScheduledFinishDate().toLocalDate());
	}
	
	private <T> void debug(Collection<T> s) {
		System.out.println(String.join("\n", s.stream().map(T::toString).collect(Collectors.toList())) + "\n");
	}
	
}