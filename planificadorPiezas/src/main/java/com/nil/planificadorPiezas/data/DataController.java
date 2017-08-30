package com.nil.planificadorPiezas.data;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.google.common.collect.Lists;
import com.nil.planificadorPiezas.domain.PieceDTO;
import com.nil.planificadorPiezas.domain.Piece;
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
	
	public void save(Piece piece) throws ClassNotFoundException, SQLException {
		// TODO
		//He cambiado el parametro a Piece pk sino desdel PieceProcesor no se puede obtener el DTO xD Seguramente este mal. grasia
		//String id = piece.getId();
		int id = 5;
		Map<Integer, Double> phases = piece.getPhases();
		for (int i = 0; i < phases.size(); i++) {
			Double horas = phases.get(i);
			database.update("INSERT INTO piezas (id_pedido,id_fase,horas,fecha_inicio) VALUES ("+id+",'"+i+"','"+horas+"','23/23/55')");
		}
	}
	
	/** @return all non-finished pieces */
	public List<PieceDTO> getAll() {
		// TODO
		return Lists.newArrayList();
	}
	
}