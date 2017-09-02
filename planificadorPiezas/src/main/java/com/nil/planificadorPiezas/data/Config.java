package com.nil.planificadorPiezas.data;

import java.io.IOException;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

class Config {

	private YamlFile config;
	private int phases, workers, dailyHours;
	
	Config(String path) throws IOException, InvalidConfigurationException {
		config = new YamlFile(path);
		if (config.exists()) config.load();
		else config.createNewFile(true);
		load();
	}
	
	void load() {
		phases = config.getInt("fases");
		workers = config.getInt("trabajadores");
		dailyHours = config.getInt("horas_diarias");
	}
	
	int getPhases() {
		return phases;
	}

	int getWorkers() {
		return workers;
	}
	
	int getDailyHours() {
		return dailyHours;
	}
}