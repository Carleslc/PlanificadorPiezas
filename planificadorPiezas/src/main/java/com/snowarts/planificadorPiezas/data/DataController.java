package com.snowarts.planificadorPiezas.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.presentation.PlanificadorPiezas;

public class DataController {

	public static final String MAIN_FOLDER = getMainFolder();
	
	private static final String DATABASE_PATH = System.getProperty("user.dir") + MAIN_FOLDER + "/pedidos.accdb";
	private static final String SCHEDULER_PATH = System.getProperty("user.dir") + DataController.MAIN_FOLDER + "/informe.txt";
	
	static { checkMainFolder(); }
	
	private Config config;
	private Database database;
	private PreparedStatement insertPstmnt, updatePstmnt;
	
	public DataController() throws IOException, InvalidConfigurationException, ClassNotFoundException, SQLException {
		config = new Config();
		boolean exists = checkDatabase();
		database = new AccessDatabase(DATABASE_PATH);
		if (!exists) database.update("CREATE TABLE pedidos (id_pedido TEXT NOT NULL, id_fase INTEGER NOT NULL, horas DOUBLE NOT NULL, fecha_inicio DATE NOT NULL, fecha_final DATE, PRIMARY KEY (id_pedido, id_fase))");
	}
	
	private static String getMainFolder() {
		String name = PlanificadorPiezas.PROGRAM_NAME;
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
		if (insertPstmnt == null || insertPstmnt.isClosed()) {
			insertPstmnt = database.preparedStatement("INSERT INTO pedidos (id_pedido, id_fase, horas, fecha_inicio) VALUES (?, ?, ?, ?)");
		}
		if (updatePstmnt == null || updatePstmnt.isClosed()) {
			updatePstmnt = database.preparedStatement("UPDATE pedidos SET fecha_final = ? WHERE id_pedido = ?");
		}
	}
	
	public PrintWriter getScheduleWriter() throws IOException {
		return new PrintWriter(SCHEDULER_PATH);
	}
	
	public void save(OrderDTO order) throws ClassNotFoundException, SQLException {
		Validate.notNull(order);
		if (exists(order.getId())) delete(order.getId());
		insert(order);
	}
	
	private void insert(OrderDTO order) throws ClassNotFoundException, SQLException {
		Validate.notNull(order);
		checkStatements();
		insertPstmnt.setString(1, order.getId());
		insertPstmnt.setDate(4, new Date(DateUtils.getEpochMillis(order.getStartDate())));
		Map<Integer, Double> phases = order.getPhases();
		for (Entry<Integer, Double> phase : phases.entrySet()) {
			int phaseId = phase.getKey();
			double hoursRaw = phase.getValue();
			insertPstmnt.setInt(2, phaseId);
			insertPstmnt.setDouble(3, hoursRaw);
			insertPstmnt.executeUpdate();
		}
	}
	
	public void setFinishDate(String orderId, LocalDate date) throws ClassNotFoundException, SQLException {
		Validate.notNull(orderId);
		checkStatements();
		if (date == null) updatePstmnt.setNull(1, Types.DATE);
		else updatePstmnt.setDate(1, new Date(DateUtils.getEpochMillis(date)));
		updatePstmnt.setString(2, orderId);
		updatePstmnt.executeUpdate();
	}
	
	public List<OrderDTO> getAll() throws ClassNotFoundException, SQLException {
		List<OrderDTO> all = new ArrayList<>();
		ResultSet orders = database.query("SELECT id_pedido, id_fase, horas, fecha_inicio, fecha_final FROM pedidos ORDER BY fecha_inicio, id_pedido, id_fase");
		String lastOrderId = null;
		LocalDate lastOrderStartDate = null;
		LocalDate lastOrderEndDate = null;
		Map<Integer, Double> phases = new HashMap<>();
		while (orders.next()) {
			String orderId = orders.getString(1);
			if (!orderId.equals(lastOrderId)) {
				if (lastOrderId != null) {
					OrderDTO dto = new OrderDTO(lastOrderId, phases, lastOrderStartDate);
					if (lastOrderEndDate != null) dto.setFinishDate(lastOrderEndDate);
					all.add(dto);
					phases = new HashMap<>();
				}
				lastOrderId = orders.getString(1);
				lastOrderStartDate = DateUtils.getLocalDate(orders.getDate(4));
				lastOrderEndDate = DateUtils.getLocalDate(orders.getDate(5));
			}
			phases.put(orders.getInt(2), orders.getDouble(3));
		}
		if (lastOrderId != null) all.add(new OrderDTO(lastOrderId, phases, lastOrderStartDate));
		return all;
	}
	
	public OrderDTO get(String orderId) throws ClassNotFoundException, SQLException {
		Validate.notNull(orderId);
		ResultSet order = database.query("SELECT id_fase, horas, fecha_inicio, fecha_final FROM pedidos WHERE id_pedido = '" + orderId + "'");
		order.next(); // Throws exception if not exists
		Map<Integer, Double> phases = new HashMap<>();
		LocalDate startDate = DateUtils.getLocalDate(order.getDate(3));
		LocalDate finishDate = DateUtils.getLocalDate(order.getDate(4));
		do { phases.put(order.getInt(1), order.getDouble(2)); } while (order.next());
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
		Validate.notNull(orderId);
		database.update("DELETE FROM pedidos" + (orderId != null ? " WHERE id_pedido = '" + orderId + "'" : ""));
	}
	
	public int getPhases() {
		return config.getPhases();
	}
	
	public Map<Integer, String> getPhaseTags() {
		return config.getTags();
	}
	
	public LocalTime getOpenTime() {
		return config.getDayOpeningTime();
	}
	
	public LocalTime getCloseTime() {
		return config.getDayClosingTime();
	}
}