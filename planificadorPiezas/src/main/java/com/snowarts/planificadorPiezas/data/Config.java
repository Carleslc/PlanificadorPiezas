package com.snowarts.planificadorPiezas.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

class Config {
	
	private static final String FILE_NAME = "/config.yml";
	private static final String PATH = System.getProperty("user.dir") + DataController.MAIN_FOLDER + FILE_NAME;

	private YamlFile config;
	private int phases;
	private Map<Integer, String> tags;
	private LocalTime dayOpeningTime, dayClosingTime;

	Config() throws IOException, InvalidConfigurationException {
		config = new YamlFile(PATH);
		if (!config.exists()) saveDefaultConfig();
		load();
	}

	void load() throws InvalidConfigurationException, IOException {
		config.load();
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
		dayOpeningTime = LocalTime.parse(config.getString("hora_apertura"), timeFormat);
		dayClosingTime = LocalTime.parse(config.getString("hora_cierre"), timeFormat);
		phases = config.getInt("fases.total");
		ConfigurationSection tagsSection = config.getConfigurationSection("fases.etiquetas");
		try {
			tags = tagsSection.getKeys(false).stream().collect(Collectors.toMap(key -> Integer.valueOf(key), key -> tagsSection.getString(key)));
		} catch (NumberFormatException ignore) {
			throw new InvalidConfigurationException("Los identificadores de etiqueta de fase deben ser números enteros (config.yml)");
		}
	}

	int getPhases() {
		return phases;
	}
	
	Map<Integer, String> getTags() {
		return tags;
	}

	LocalTime getDayOpeningTime() {
		return dayOpeningTime;
	}

	LocalTime getDayClosingTime() {
		return dayClosingTime;
	}
	
	private void saveDefaultConfig() throws IOException {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = Config.class.getResourceAsStream(FILE_NAME);
            if (stream == null) {
                throw new IOException("Cannot get resource " + FILE_NAME + " from jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(PATH);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } finally {
            if (stream != null) stream.close();
            if (resStreamOut != null) resStreamOut.close();
        }
	}
}