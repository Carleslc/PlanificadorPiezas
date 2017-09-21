package com.snowarts.planificadorPiezas.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.Result;
import com.snowarts.planificadorPiezas.domain.ScheduledPhaseDTO;

class FileReporter implements Reporter {

	private PrintWriter scheduleLog;
	private String path;
	
	public FileReporter(String path) throws IOException {
		this.path = path;
		scheduleLog = new PrintWriter(path);
	}
	
	@Override
	public void writeOrders(List<OrderDTO> orders) throws IOException {
		scheduleLog.println("PEDIDOS");
		for (OrderDTO order : orders) writeOrder(order);
		scheduleLog.println(); scheduleLog.println();
	}
	
	@Override
	public void writeOrder(OrderDTO order) throws IOException {
		scheduleLog.println(order);
	}
	
	@Override
	public void writeSchedule(List<ScheduledPhaseDTO> schedule) throws IOException {
		scheduleLog.println("PLANIFICACIÓN DEL TIEMPO");
		for (ScheduledPhaseDTO scheduled : schedule) writeScheduledPhase(scheduled);
		scheduleLog.println(); scheduleLog.println();
	}

	@Override
	public void writeScheduledPhase(ScheduledPhaseDTO scheduledPhase) throws IOException {
		scheduleLog.println(scheduledPhase);
	}
	
	@Override
	public void writeResults(List<Result> results) throws IOException {
		scheduleLog.println("RESULTADOS");
		for (Result result : results) writeResult(result);
	}

	@Override
	public void writeResult(Result result) throws IOException {
		scheduleLog.println(result);
	}
	
	@Override
	public void clear() throws IOException {
		close();
		Files.deleteIfExists(new File(path).toPath());
		scheduleLog = new PrintWriter(path);
	}
	
	@Override
	public void close() throws IOException {
		scheduleLog.close();
	}

}
