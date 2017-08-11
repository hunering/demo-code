package com.xmo.demo.java7.jdbc;

import java.sql.*;
import org.postgresql.*;

public class PgSqlInsertTest {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://192.168.1.171:5432/orcmdb";

	// Database credentials
	static final String USER = "orcmuser";
	static final String PASS = "orcmuser";

	public static void main(String[] args) {
		Connection conn = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			String query = "INSERT INTO data_sample_raw (hostname, data_item, time_stamp, "
					+ "value_int, value_real, value_str, units, data_type_id, "
					+ "app_value_type_id, event_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement ps = conn.prepareStatement(query);
			
			for (int iter = 0; iter < 10; iter++) {
				int i = 0;
				int rowsPerIter = 10000;
				java.util.Date start = new java.util.Date();
				while (i < rowsPerIter) {
					ps.setString(1, "sensysnode-3999");
					ps.setString(2, "sigar_mem_actual_free");
					ps.setTimestamp(3, getCurrentTimeStamp());
					//ps.setTimestamp(3, null);
					ps.setInt(4, 1234);
					ps.setFloat(5, 0.0f);
					ps.setString(6, "");
					ps.setString(7, "Bytes");
					ps.setInt(8, 81234);
					ps.setInt(9, 91234);
					ps.setInt(10, 101234);
					//ps.execute();
					ps.addBatch();
					i++;
				}
				ps.executeBatch();
				
				java.util.Date end = new java.util.Date();
				long timeSpan = end.getTime() - start.getTime();
				System.out.println("iteration used: " + timeSpan);
			}
			// STEP 6: Clean-up environment
			ps.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");

	}

	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
}
