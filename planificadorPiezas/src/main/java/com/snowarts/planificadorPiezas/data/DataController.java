package com.snowarts.planificadorPiezas.data;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.presentation.PlanificadorPiezas;

public class DataController {

	public static final String MAIN_FOLDER = getMainFolder();
	private final String DATABASE_PATH = System.getProperty("user.dir") + MAIN_FOLDER + "/pedidos.accdb";
	
	private Config config;
	private Database database;
	
	public DataController() throws IOException, InvalidConfigurationException, ClassNotFoundException, SQLException {
		checkMainFolder();
		config = new Config();
		boolean exists = checkDatabase();
		database = new AccessDatabase(DATABASE_PATH);
		if (!exists) database.update("CREATE TABLE pedidos (id_pedido TEXT NOT NULL, id_fase INTEGER NOT NULL, horas DOUBLE NOT NULL, fecha_inicio DATE NOT NULL, PRIMARY KEY (id_pedido, id_fase))");
	}
	
	private static String getMainFolder() {
		String name = PlanificadorPiezas.PROGRAM_NAME;
		String dir = System.getProperty("user.dir");
		String[] dirFolders = dir.split(Pattern.quote(File.separator));
		dir = dirFolders[dirFolders.length - 1];
		return name.equalsIgnoreCase(dir) ? "" : File.separator + name;
	}
	
	private void checkMainFolder() {
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
	
	public void save(OrderDTO order) throws ClassNotFoundException, SQLException {
		if (exists(order.getId())) delete(order.getId());
		insert(order);
	}
	
	private void insert(OrderDTO order) throws ClassNotFoundException, SQLException {
		PreparedStatement insert = database.preparedStatement("INSERT INTO pedidos (id_pedido, id_fase, horas, fecha_inicio) VALUES (?, ?, ?, ?)");
		insert.setString(1, order.getId());
		insert.setDate(4, new Date(DateUtils.getEpochMillis(order.getStartDate())));
		Map<Integer, Double> phases = order.getPhases();
		for (Entry<Integer, Double> phase : phases.entrySet()) {
			int phaseId = phase.getKey();
			double hoursRaw = phase.getValue();
			insert.setInt(2, phaseId);
			insert.setDouble(3, hoursRaw);
			insert.executeUpdate();
		}
	}
	
	public List<OrderDTO> getAll() throws ClassNotFoundException, SQLException {
		List<OrderDTO> all = new ArrayList<>();
		ResultSet orders = database.query("SELECT id_pedido, id_fase, horas, fecha_inicio FROM pedidos ORDER BY fecha_inicio, id_pedido, id_fase");
		String lastOrderId = null;
		LocalDate lastOrderStartDate = null;
		Map<Integer, Double> phases = new HashMap<>();
		while (orders.next()) {
			String orderId = orders.getString(1);
			if (!orderId.equals(lastOrderId)) {
				if (lastOrderId != null) {
					all.add(new OrderDTO(lastOrderId, phases, lastOrderStartDate));
					phases = new HashMap<>();
				}
				lastOrderId = orders.getString(1);
				lastOrderStartDate = DateUtils.getLocalDate(orders.getDate(4).getTime());
			}
			phases.put(orders.getInt(2), orders.getDouble(3));
		}
		if (lastOrderId != null) all.add(new OrderDTO(lastOrderId, phases, lastOrderStartDate));
		return all;
	}
	
	public OrderDTO get(String orderId) throws ClassNotFoundException, SQLException {
		ResultSet order = database.query("SELECT id_fase, horas, fecha_inicio FROM pedidos WHERE id_pedido = '" + orderId + "'");
		order.next(); // Throws exception if not exists
		Map<Integer, Double> phases = new HashMap<>();
		LocalDate startDate = DateUtils.getLocalDate(order.getDate(3).getTime());
		do { phases.put(order.getInt(1), order.getDouble(2)); } while (order.next());
		return new OrderDTO(orderId, phases, startDate);
	}
	
	public boolean exists(String orderId) throws ClassNotFoundException, SQLException {
		return database.query("SELECT * FROM pedidos WHERE id_pedido = '" + orderId + "'").next();
	}
	
	public void deleteAll() throws ClassNotFoundException, SQLException {
		delete(null);
	}
	
	public void delete(String orderId) throws ClassNotFoundException, SQLException {
		database.update("DELETE FROM pedidos" + (orderId != null ? " WHERE id_pedido = '" + orderId + "'" : ""));
	}
	
	public int getPhases() {
		return config.getPhases();
	}
	
	public LocalTime getOpenTime() {
		return config.getDayOpeningTime();
	}
	
	public LocalTime getCloseTime() {
		return config.getDayClosingTime();
	}
}