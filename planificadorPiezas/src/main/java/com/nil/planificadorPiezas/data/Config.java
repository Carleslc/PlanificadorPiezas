package com.nil.planificadorPiezas.data;

import java.io.IOException;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

class Config {

	private YamlFile config;
	private String databaseLocation, piecesTable;
	
	Config(String path) throws IOException, InvalidConfigurationException {
		load(path);
	}
	
	void load(String path) throws IOException, InvalidConfigurationException {
		config = new YamlFile(path);
		if (config.exists()) config.load();
		else config.createNewFile(true);
		
		databaseLocation = config.getString("database");
		piecesTable = config.getString("table");
	}
	
	String getDatabaseLocation() {
		return databaseLocation;
	}
	
	String getPiecesTable() {
		return piecesTable;
	}
	
}
