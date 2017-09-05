package com.snowarts.planificadorPiezas.domain;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.snowarts.planificadorPiezas.data.DataController;

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
		PrintWriter scheduleLog = data.getScheduleWriter();
		List<OrderDTO> dtos = data.getAll();
		scheduleLog.println("PEDIDOS");
		scheduleLog.println(String.join("\n", dtos.stream().map(OrderDTO::toString).collect(Collectors.toList())));
		scheduleLog.println(); scheduleLog.println();
		LocalTime openTime = data.getOpenTime();
		List<Order> orders = dtos.stream().map(dto -> new Order(dto, openTime)).collect(Collectors.toCollection(LinkedList::new));
		Scheduler scheduler = new Scheduler(data.getPhases(), openTime, data.getCloseTime());
		
		LinkedList<Phase> remaining = orders.stream().flatMap(o -> o.getPhases().stream()).collect(Collectors.toCollection(LinkedList::new));
		Collections.sort(remaining);
		
		while (!remaining.isEmpty()) {
			Phase phase = remaining.poll();
			scheduler.add(phase, remaining);
		}
		
		scheduleLog.println(scheduler);
		scheduleLog.println("RESULTADOS");
		orders.stream().sorted((o1, o2) -> o1.getScheduledFinishDate().compareTo(o2.getScheduledFinishDate())).forEach(o -> {
			Result result = new Result(o.getId(), o.getScheduledFinishDate().toLocalDate());
			results.add(result);
			scheduleLog.println(result);
		});
		saveResults();
		scheduleLog.close();
	}
	
	private void saveResults() throws ClassNotFoundException, SQLException {
		for (Result result : results) {
			if (result.getId().equals(order.getId())) this.result = result;
			data.setFinishDate(result.getId(), result.getFinishDate());
		}
	}
}