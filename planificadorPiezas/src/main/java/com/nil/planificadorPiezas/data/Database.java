package com.nil.planificadorPiezas.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * General database template.
 */
abstract class Database {
	
	private static final int PRIMARY_KEY_ERROR_CODE = 1062;
	
	private Connection connection;
	
	/**
	 * Establish a connection with this database.
	 * @return the connection established.
	 * @throws SQLException if the connection fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 */
	abstract Connection getConnection() throws SQLException, ClassNotFoundException;
	
	/**
	 * Establishes a connection with this database and gets this database operable.
	 * @throws SQLException if the connection fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 */
	public void connect() throws SQLException, ClassNotFoundException {
		connection = getConnection();
	}

	/**
	 * Disconnects this database closing the current connection if needed.
	 * @throws SQLException if the process fails
	 */
	public void disconnect() throws SQLException {
		if (connection != null)
			connection.close();
	}

	/**
	 * Performs a disconnection followed by a connection to refresh this database connection.
	 * @throws SQLException if the process fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 * @see #disconnect()
	 * @see #connect()
	 */
	public void reconnect() throws SQLException, ClassNotFoundException {
		disconnect();
		connect();
	}

	/**
	 * Precompile a {@link PreparedStatement} so in the future you only need to
	 * change values and execute it without compile the statement again.
	 * @param statement the SQL statement to compile with <code>?</code> as the runtime modifiable values
	 * @return the compiled {@link PreparedStatement}
	 * @throws SQLException if the connection fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 */
	public PreparedStatement preparedStatement(String statement) throws SQLException, ClassNotFoundException {
		checkConnection();
		return connection.prepareStatement(statement);
	}
	
	/**
	 * Compiles and performs a SELECT operation.
	 * @param statement the SELECT SQL statement to execute
	 * @return the {@link ResultSet result} of the query; never <code>null</code>
	 * @throws SQLException if the connection fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 */
	public ResultSet query(String statement) throws SQLException, ClassNotFoundException {
		checkConnection();
		return connection.createStatement().executeQuery(statement);
	}

	/**
	 * Compiles and performs an UPDATE, DELETE, INSERT, CREATE, DLL
	 * or other statement operation that modifies the state of this database.
	 * @param statement the SQL statement to execute
	 * @return the number of rows affected with this call or 0 if the operation returns nothing
	 * @throws SQLException if the connection fails
	 * @throws ClassNotFoundException if the JDBC driver cannot be found
	 */
	public int update(String statement) throws SQLException, ClassNotFoundException {
		checkConnection();
		return connection.createStatement().executeUpdate(statement);
	}

	/**
	 * Checks if this database has an opened connection.
	 * @return if this database has an opened connection.
	 */
	public boolean isConnected() {
		boolean connected = false;
		try {
			connected = connection != null && !connection.isClosed();
		} catch (SQLException ignore) {
			// Exceptions are treated as not connected
		}
		return connected;
	}
	
	private void checkConnection() throws SQLException, ClassNotFoundException {
		if (!isConnected()) {
			connect();
		}
	}
	
	public static boolean isDuplication(Exception e) {
		return e instanceof SQLException && ((SQLException) e).getErrorCode() == PRIMARY_KEY_ERROR_CODE;
	}
	
	/**
	 * Gets a String representation of a {@link ResultSet}.
	 * @param rs the {@link ResultSet} to convert to text
	 * @return the String representation of <b>rs</b>
	 */
	@SuppressWarnings("nls")
	public static String toString(ResultSet rs) {
		StringBuilder sb = new StringBuilder();

		try {
			ResultSetMetaData metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();    

			for (int i = 1; i <= columnCount; i++)
				sb.append(metadata.getColumnName(i)).append(i < columnCount ? ", " : "");
			sb.append("\n");

			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++)
					sb.append(rs.getString(i)).append(i < columnCount ? ",\t" : "");          
				sb.append("\n");
			}
		} catch (Exception e) {
			sb.append("toString ERROR: ").append(e.getMessage());
		}
		return sb.toString();
	}

}