package com.snowarts.planificadorPiezas.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		//debug(dtos);
		Stream<Order> orders = dtos.stream().map(dto -> new Order(dto)).sorted();
		LinkedList<Phase> remaining = orders.flatMap(o -> o.getPhases().stream()).collect(Collectors.toCollection(LinkedList::new));
		Map<Order, LocalDateTime> results = new HashMap<>();
		Scheduler scheduler = new Scheduler(data.getPhases(), data.getWorkers(), data.getOpenTime(), data.getCloseTime());
		
		ListIterator<Phase> remainingIterator = remaining.listIterator();
		while (remainingIterator.hasNext()) {
			Phase phase = remainingIterator.next();
			LocalDateTime finish = scheduler.add(phase, remainingIterator);
			Order related = phase.getRelated();
			LocalDateTime currentResult = results.get(related);
			if (currentResult == null || finish.isAfter(currentResult)) {
				results.put(related, finish);
			}
		}
		
		//System.out.println(scheduler);
		result = new Result(order.getId(), results.get(order).toLocalDate());
	}
	
	/*private <T> void debug(Collection<T> s) {
		System.out.println(String.join("\n", s.stream().map(T::toString).collect(Collectors.toList())) + "\n");
	}*/
	
}