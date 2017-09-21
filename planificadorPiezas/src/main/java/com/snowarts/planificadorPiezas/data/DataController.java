package com.snowarts.planificadorPiezas.data;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.PhaseDTO;
import com.snowarts.planificadorPiezas.presentation.Rem;

public class DataController {

	public static final String MAIN_FOLDER = getMainFolder();
	
	private static final String DATABASE_PATH = System.getProperty("user.dir") + MAIN_FOLDER + "/pedidos.accdb";
	private static final String SCHEDULER_PATH = System.getProperty("user.dir") + DataController.MAIN_FOLDER + "/informe.txt";
	
	static { checkMainFolder(); }
	
	private Config config;
	private Database database;
	private PreparedStatement insertOrderPstmnt, insertPhasePstmnt, updateFinalDatePstmnt;
	
	public DataController() throws IOException, InvalidConfigurationException, ClassNotFoundException, SQLException {
		config = new Config();
		boolean exists = checkDatabase();
		database = new AccessDatabase(DATABASE_PATH);
		if (!exists) {
			database.update("CREATE TABLE pedidos (id_pedido TEXT PRIMARY KEY, fecha_inicio DATETIME NOT NULL, fecha_final DATETIME)");
			database.update("CREATE TABLE fases (id_pedido TEXT NOT NULL, id_fase INTEGER NOT NULL, horas DOUBLE NOT NULL, externa BOOLEAN NOT NULL, PRIMARY KEY (id_pedido, id_fase))");
			database.update("CREATE TABLE " + ScheduleDatabaseReporter.TABLE + " (id_pedido TEXT NOT NULL, id_fase INTEGER NOT NULL, fecha_inicio DATETIME NOT NULL, fecha_final DATETIME NOT NULL, PRIMARY KEY (id_pedido, id_fase, fecha_inicio, fecha_final))");
		}
	}
	
	private static String getMainFolder() {
		String name = Rem.PROGRAM_NAME;
		String dir = System.getProperty("user.dir");
		String[] dirFolders = dir.split(Pattern.quote(File.separator));
		dir = dirFolders[dirFolders.length - 1];
		return name.equalsIgnoreCase(dir) ? "" : File.separator + name;
	}
	
	private static void checkMainFolder() {
		if (MAIN_FOLDER.isEmpty()) return;
		new File(MAIN_FOLDER.substring(1)).mkdirs();
	}
	
	private boolean checkDatabase() throws IOException {
		File dbFile = new File(DATABASE_PATH);
		if (!dbFile.exists()) {
			DatabaseBuilder.create(FileFormat.V2010, dbFile);
			return false;
		}
		return true;
	}
	
	private void checkStatements() throws ClassNotFoundException, SQLException {
		if (insertOrderPstmnt == null || insertOrderPstmnt.isClosed()) {
			insertOrderPstmnt = database.preparedStatement("INSERT INTO pedidos (id_pedido, fecha_inicio) VALUES (?, ?)");
		}
		if (insertPhasePstmnt == null || insertPhasePstmnt.isClosed()) {
			insertPhasePstmnt = database.preparedStatement("INSERT INTO fases (id_pedido, id_fase, horas, externa) VALUES (?, ?, ?, ?)");
		}
		if (updateFinalDatePstmnt == null || updateFinalDatePstmnt.isClosed()) {
			updateFinalDatePstmnt = database.preparedStatement("UPDATE pedidos SET fecha_final = ? WHERE id_pedido = ?");
		}
	}
	
	public Reporter getScheduleReporter() throws IOException {
		MixedReporter mixed = new MixedReporter(new ScheduleDatabaseReporter(database));
		if (config.needsReport()) mixed.addReporter(new FileReporter(SCHEDULER_PATH));
		return mixed;
	}
	
	public void save(OrderDTO order) throws ClassNotFoundException, SQLException {
		Validate.notNull(order);
		if (exists(order.getId())) delete(order.getId());
		insert(order);
	}
	
	private void insert(OrderDTO order) throws ClassNotFoundException, SQLException {
		Validate.notNull(order);
		checkStatements();
		insertOrderPstmnt.setString(1, order.getId());
		insertOrderPstmnt.setTimestamp(2, new Timestamp(DateUtils.getEpochMillis(order.getStartDate())));
		insertOrderPstmnt.executeUpdate();
		insertPhasePstmnt.setString(1, order.getId());
		for (PhaseDTO phase : order.getPhases()) {
			insertPhasePstmnt.setInt(2, phase.getId());
			insertPhasePstmnt.setDouble(3, phase.getRawHours());
			insertPhasePstmnt.setBoolean(4, phase.isExternal());
			insertPhasePstmnt.executeUpdate();
		}
	}
	
	public void setFinishDate(String orderId, LocalDateTime date) throws ClassNotFoundException, SQLException {
		Validate.notNull(orderId);
		checkStatements();
		if (date == null) updateFinalDatePstmnt.setNull(1, Types.TIMESTAMP);
		else updateFinalDatePstmnt.setTimestamp(1, new Timestamp(DateUtils.getEpochMillis(date)));
		updateFinalDatePstmnt.setString(2, orderId);
		updateFinalDatePstmnt.executeUpdate();
	}
	
	public List<OrderDTO> getAll() throws ClassNotFoundException, SQLException {
		List<OrderDTO> all = new ArrayList<>();
		ResultSet orders = database.query(
				"SELECT id_pedido, id_fase, horas, fecha_inicio, fecha_final, externa "
				+ "FROM pedidos INNER JOIN fases ON pedidos.id_pedido = fases.id_pedido "
				+ "ORDER BY fecha_inicio, id_pedido, id_fase");
		String lastOrderId = null;
		LocalDateTime lastOrderStartDate = null;
		LocalDateTime lastOrderEndDate = null;
		List<PhaseDTO> phases = new LinkedList<>();
		while (orders.next()) {
			String orderId = orders.getString(1);
			if (!orderId.equals(lastOrderId)) {
				if (lastOrderId != null) {
					OrderDTO dto = new OrderDTO(lastOrderId, phases, lastOrderStartDate);
					if (lastOrderEndDate != null) dto.setFinishDate(lastOrderEndDate);
					all.add(dto);
					phases = new LinkedList<>();
				}
				lastOrderId = orders.getString(1);
				lastOrderStartDate = DateUtils.getLocalDateTime(orders.getTimestamp(4));
				lastOrderEndDate = DateUtils.getLocalDateTime(orders.getTimestamp(5));
			}
			phases.add(new PhaseDTO(orders.getInt(2), orders.getDouble(3), orders.getBoolean(6)));
		}
		if (lastOrderId != null) all.add(new OrderDTO(lastOrderId, phases, lastOrderStartDate));
		return all;
	}
	
	public OrderDTO get(String orderId) throws ClassNotFoundException, SQLException {
		Validate.notNull(orderId);
		ResultSet order = database.query(
				"SELECT id_fase, horas, fecha_inicio, fecha_final, externa "
				+ "FROM pedidos INNER JOIN fases ON pedidos.id_pedido = fases.id_pedido "
				+ "WHERE id_pedido = '" + orderId + "'");
		order.next(); // Throws exception if not exists
		List<PhaseDTO> phases = new LinkedList<>();
		LocalDateTime startDate = DateUtils.getLocalDateTime(order.getTimestamp(3));
		LocalDateTime finishDate = DateUtils.getLocalDateTime(order.getTimestamp(4));
		do { phases.add(new PhaseDTO(order.getInt(1), order.getDouble(2), order.getBoolean(5))); } while (order.next());
		OrderDTO dto = new OrderDTO(orderId, phases, startDate);
		if (finishDate != null) dto.setFinishDate(finishDate);
		return dto;
	}
	
	public boolean exists(String orderId) throws ClassNotFoundException, SQLException {
		Validate.notNull(orderId);
		return database.query("SELECT * FROM pedidos WHERE id_pedido = '" + orderId + "'").next();
	}
	
	public void deleteAll() throws ClassNotFoundException, SQLException {
		delete(null);
	}
	
	public void delete(String orderId) throws ClassNotFoundException, SQLException {
		String condition = "";
		if (orderId != null) condition = " WHERE id_pedido = '" + orderId + "'";
		database.update("DELETE FROM pedidos" + condition);
		database.update("DELETE FROM fases" + condition);
	}
	
	public int getPhases() {
		return config.getPhases();
	}
	
	public int getExternalPhases() {
		return config.getExternalPhases();
	}
	
	public Map<Integer, String> getPhaseTags() {
		return config.getTags();
	}
	
	public Map<Integer, String> getExternalPhaseTags() {
		return config.getExternalTags();
	}
	
	public LocalTime getOpenTime() {
		return config.getDayOpeningTime();
	}
	
	public LocalTime getCloseTime() {
		return config.getDayClosingTime();
	}
}