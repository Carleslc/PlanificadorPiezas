package com.snowarts.planificadorPiezas.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.Result;
import com.snowarts.planificadorPiezas.domain.ScheduledPhaseDTO;

class MixedReporter implements Reporter {

	private List<Reporter> reporters;
	
	public MixedReporter(Reporter... reporters) {
		this.reporters = Arrays.asList(reporters);
	}
	
	@Override
	public void writeOrder(OrderDTO order) throws IOException {
		for (Reporter reporter : reporters) reporter.writeOrder(order);
	}

	@Override
	public void writeScheduledPhase(ScheduledPhaseDTO scheduledPhase) throws IOException {
		for (Reporter reporter : reporters) reporter.writeScheduledPhase(scheduledPhase);
	}

	@Override
	public void writeResult(Result result) throws IOException {
		for (Reporter reporter : reporters) reporter.writeResult(result);
	}
	
	@Override
	public void writeOrders(List<OrderDTO> orders) throws IOException {
		for (Reporter reporter : reporters) reporter.writeOrders(orders);
	}
	
	@Override
	public void writeSchedule(List<ScheduledPhaseDTO> schedule) throws IOException {
		for (Reporter reporter : reporters) reporter.writeSchedule(schedule);
	}
	
	@Override
	public void writeResults(List<Result> results) throws IOException {
		for (Reporter reporter : reporters) reporter.writeResults(results);
	}
	
	@Override
	public void clear() throws IOException {
		for (Reporter reporter : reporters) reporter.clear();
	}
	
	@Override
	public void close() throws IOException {
		for (Reporter reporter : reporters) reporter.close();
	}

}
