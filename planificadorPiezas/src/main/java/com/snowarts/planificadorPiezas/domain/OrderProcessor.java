package com.snowarts.planificadorPiezas.domain;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import com.snowarts.planificadorPiezas.data.DataController;
import com.snowarts.planificadorPiezas.data.Reporter;

class OrderProcessor {
	
	private Order order;
	private Result result;
	private List<Result> results;
	private OrderCallback callback;
	private DataController data;
	
	OrderProcessor(Order order, DataController data) {
		this.order = order;
		this.data = data;
		results = new LinkedList<>();
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
	
	private void compute() throws ClassNotFoundException, SQLException, IOException {
		data.save(order.toDTO());
		Reporter reporter = data.getScheduleReporter();
		reporter.clear();
		
		List<OrderDTO> dtos = data.getAll();
		reporter.writeOrders(dtos);
		
		LocalTime openTime = data.getOpenTime();
		List<Order> orders = dtos.stream().map(dto -> new Order(dto)).collect(Collectors.toCollection(LinkedList::new));
		Scheduler scheduler = new Scheduler(data.getPhases(), openTime, data.getCloseTime());
		
		PriorityQueue<Phase> remaining = orders.stream().flatMap(o -> o.getPhases().stream()).collect(Collectors.toCollection(PriorityQueue::new));
		
		while (!remaining.isEmpty()) {
			Phase phase = remaining.poll();
			scheduler.add(phase, remaining);
		}
		
		reporter.writeSchedule(scheduler.getScheduledPhases().stream()
				.map(scheduledPhase -> new ScheduledPhaseDTO(scheduledPhase))
				.collect(Collectors.toList()));
		
		orders.stream().sorted((o1, o2) -> o1.getScheduledFinishDate().compareTo(o2.getScheduledFinishDate())).forEach(o -> {
			Result result = new Result(o.getId(), o.getScheduledFinishDate());
			addResult(result, reporter);
		});
		reporter.writeResults(results);
		reporter.close();
	}
	
	private void addResult(Result result, Reporter reporter) {
		results.add(result);
		if (result.getId().equals(order.getId())) this.result = result;
		try {
			data.setFinishDate(result.getId(), result.getFinishDate());
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}