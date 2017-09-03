package com.snowarts.planificadorPiezas.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class AccessDatabase extends Database {

	private String databaseUrl;
	
	AccessDatabase(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
	
	@Override
	Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		String additionalSettings = "memory=false";
		return DriverManager.getConnection("jdbc:ucanaccess://" + databaseUrl + ";" + additionalSettings);
	}

}
