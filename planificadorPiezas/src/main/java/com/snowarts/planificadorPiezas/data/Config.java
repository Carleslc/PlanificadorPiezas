package com.snowarts.planificadorPiezas.data;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

class Config {

	private YamlFile config;
	private int phases, workers;
	private LocalTime dayOpeningTime, dayClosingTime;

	Config(String path) throws IOException, InvalidConfigurationException {
		config = new YamlFile(path);
		if (config.exists()) config.load();
		else config.createNewFile(true);
		load();
	}

	void load() {
		phases = config.getInt("fases");
		workers = config.getInt("trabajadores");
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
		dayOpeningTime = LocalTime.parse(config.getString("hora_apertura"), timeFormat);
		dayClosingTime = LocalTime.parse(config.getString("hora_cierre"), timeFormat);
	}

	int getPhases() {
		return phases;
	}

	int getWorkers() {
		return workers;
	}

	LocalTime getDayOpeningTime() {
		return dayOpeningTime;
	}

	LocalTime getDayClosingTime() {
		return dayClosingTime;
	}
}