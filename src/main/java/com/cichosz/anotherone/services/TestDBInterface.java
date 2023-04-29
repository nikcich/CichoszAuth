package com.cichosz.anotherone.services;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.logging.Logger;


@Singleton
@LocalBean
public class TestDBInterface {
	private Connection conn = null;
	public boolean connectionsuccess = false;
	public static int counter = 0;
    private final Logger LOGGER = Logger.getLogger(TestDBInterface.class.getName());

	@PostConstruct
	public void initialize() {
		System.out.println("EJB initialized.");
	}

	public TestDBInterface() {
		counter++;
		System.out.println("TestDBInterface Initialized");
		this.connectionsuccess = true;
	}
	
	protected void finalize() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getData(){
		List<Integer> results = new ArrayList<>();
		try {
			// Register the JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// Open a connection to the database
			conn = DriverManager.getConnection("jdbc:mariadb://10.201.1.232:3306/test", "roaming", "password");

			// Create a statement
			Statement stmt = conn.createStatement();

			// Execute a select query
			ResultSet rs = stmt.executeQuery("SELECT * FROM test_column;");

			// Process the result set
			while (rs.next()) {
				int id = rs.getInt("id");
				// do something with the data
				results.add(id);
			}

			// Close the result set, statement, and connection
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e) {

		}finally {

		}

		return results;
	}
	
	public void insertData() {
		try {
			// Register the JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// Open a connection to the database
			Connection conn = DriverManager.getConnection("jdbc:mariadb://10.201.1.232:3306/test", "roaming", "password");

			HashMap<String, String> map = new HashMap<>();
			map.put("key1", "value1");
			map.put("key2", "value2");
			
			ObjectMapper om = new ObjectMapper();
			String json = "";
			try {
				json = om.writeValueAsString(map);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			// Create a prepared statement
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO test_column (json_column) VALUES (?)");
			pstmt.setString(1, json);

			// Execute the insert command
			int rowsInserted = pstmt.executeUpdate();

			// Close the prepared statement and connection
			pstmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String execute(String query) {
		StringBuilder result = new StringBuilder();
		
		try {
			// Register the JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// Open a connection to the database
			Connection conn = DriverManager.getConnection("jdbc:mariadb://10.201.1.232:3306/test", "roaming", "password");

			Statement stmt = conn.createStatement();

			// Execute the SQL command and get the result set
			boolean hasResultSet = stmt.execute(query);
			if (hasResultSet) {
				ResultSet rs = stmt.getResultSet();
				ResultSetMetaData meta = rs.getMetaData();
				int numColumns = meta.getColumnCount();

				// Build the result string with the column headers
				for (int i = 1; i <= numColumns; i++) {
					result.append(meta.getColumnName(i)).append("\t");
				}
				result.append("\n");

				// Append the result rows
				while (rs.next()) {
					for (int i = 1; i <= numColumns; i++) {
						result.append(rs.getString(i)).append("\t");
					}
					result.append("\n");
				}

				rs.close();
			} else {
				// Get the number of rows affected
				int rowsAffected = stmt.getUpdateCount();
				result.append(rowsAffected).append(" rows affected\n");
			}
			
			LOGGER.info("Nik was here");

			// Close the statement and connection
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public HashMap<String,Object> getUserByUsername(String usr){
		HashMap<String, Object> results = new HashMap<>();
		try {
			// Register the JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// Open a connection to the database
			conn = DriverManager.getConnection("jdbc:mariadb://10.201.1.232:3306/test", "roaming", "password");

			// Create a statement
			Statement stmt = conn.createStatement();

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM user WHERE username='").append(usr).append("';");
			// Execute a select query
			ResultSet rs = stmt.executeQuery(sb.toString());

			// Process the result set
			while (rs.next()) {
				int id = rs.getInt("id");
				long uid = rs.getLong("uid");
				String jsonString = rs.getString("data");
				String username = rs.getString("username");
				String password = rs.getString("password");
				
				results.put("username", username);
				results.put("password", password);
				results.put("id", id);
				results.put("data", jsonString);
				results.put("uid", uid);
				
				break;
			}

			// Close the result set, statement, and connection
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e) {

		}finally {

		}
		
		return results;
	}
	
	public boolean createUser(String usr, String pass) {
	    boolean success = true;
	    try {
	        // Register the JDBC driver
	        Class.forName("org.mariadb.jdbc.Driver");

	        // Open a connection to the database
	        conn = DriverManager.getConnection("jdbc:mariadb://10.201.1.232:3306/test", "roaming", "password");

	        // Create a prepared statement
	        String sql = "INSERT INTO user(username, password) VALUES(?, ?)";
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, usr);
	        pstmt.setString(2, pass);

	        LOGGER.info(pstmt.toString());

	        // Execute the update query
	        int rows = pstmt.executeUpdate();

	        pstmt.close();
	        conn.close();

	        // Check if any rows were affected
	        if (rows == 0) {
	            success = false;
	        }
	    } catch (Exception e) {
	        success = false;
	    }

	    return success;
	}

}
