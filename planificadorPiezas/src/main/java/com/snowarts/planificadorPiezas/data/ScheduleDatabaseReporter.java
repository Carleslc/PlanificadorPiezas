package com.snowarts.planificadorPiezas.data;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.Result;
import com.snowarts.planificadorPiezas.domain.ScheduledPhaseDTO;

class ScheduleDatabaseReporter implements Reporter {

	public static final String TABLE = "planificacion";
	
	private Database database;
	private PreparedStatement insertPstmnt;
	
	public ScheduleDatabaseReporter(Database database) {
		this.database = database;
	}
	
	private void checkStatements() throws ClassNotFoundException, SQLException {
		if (insertPstmnt == null || insertPstmnt.isClosed()) {
			insertPstmnt = database.preparedStatement("INSERT INTO " + TABLE + " (id_pedido, id_fase, fecha_inicio, fecha_final) VALUES (?, ?, ?, ?)");
		}
	}
	
	@Override
	public void writeScheduledPhase(ScheduledPhaseDTO scheduledPhase) throws IOException {
		run(() -> {
			checkStatements();
			insertPstmnt.setString(1, scheduledPhase.getOrderId());
			insertPstmnt.setInt(2, scheduledPhase.getPhaseId());
			insertPstmnt.setTimestamp(3, new Timestamp(DateUtils.getEpochMillis(scheduledPhase.getStartDate())));
			insertPstmnt.setTimestamp(4, new Timestamp(DateUtils.getEpochMillis(scheduledPhase.getFinishDate())));
			insertPstmnt.executeUpdate();
		});
	}

	@Override
	public void writeOrder(OrderDTO order) throws IOException {
		// Not needed. It is already in orders table.
	}

	@Override
	public void writeResult(Result result) throws IOException {
		// Not needed. It is already in orders table.
	}
	
	@Override
	public void clear() throws IOException {
		run(() -> {
			database.update("DELETE FROM " + TABLE);
		});
	}
	
	private void run(DatabaseRunnable onDatabase) throws IOException {
		try {
			onDatabase.run();
		} catch (SQLException | ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
	
	private static interface DatabaseRunnable {
		void run() throws ClassNotFoundException, SQLException;
	}

}
