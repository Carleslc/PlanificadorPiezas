package com.nil.planificadorPiezas.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.google.common.collect.Lists;
import com.nil.planificadorPiezas.domain.PieceDTO;

public class DataController {

	private Config config;
	private Database database;
	private String piecesTable;
	
	public DataController() throws IOException, InvalidConfigurationException {
		config = new Config("config.yml");
		database = new AccessDatabase(config.getDatabaseLocation());
		piecesTable = config.getPiecesTable();
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		database.connect();
	}
	
	public void save(PieceDTO dto) {
		// TODO
	}
	
	/** @return all non-finished pieces */
	public List<PieceDTO> getAll() {
		// TODO
		return Lists.newArrayList();
	}
	
}