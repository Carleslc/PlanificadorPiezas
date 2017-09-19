package com.snowarts.planificadorPiezas.data;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.Result;
import com.snowarts.planificadorPiezas.domain.ScheduledPhaseDTO;

public interface Reporter extends Closeable {
	
	void writeOrder(OrderDTO order) throws IOException;
	
	void writeScheduledPhase(ScheduledPhaseDTO scheduledPhase) throws IOException;
	
	void writeResult(Result result) throws IOException;
	
	void clear() throws IOException;
	
	default void close() throws IOException {}
	
	default void writeOrders(List<OrderDTO> orders) throws IOException {
		for (OrderDTO order : orders) writeOrder(order);
	}
	
	default void writeSchedule(List<ScheduledPhaseDTO> schedule) throws IOException {
		for (ScheduledPhaseDTO scheduledPhase : schedule) writeScheduledPhase(scheduledPhase);
	}
	
	default void writeResults(List<Result> results) throws IOException {
		for (Result result : results) writeResult(result);
	}
	
}
