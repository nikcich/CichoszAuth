package com.cichosz.auth.services;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.*;

import com.cichosz.auth.common.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.logging.Logger;


@Singleton
@LocalBean
public class AuthServiceDBInterface {
	private Connection conn = null;
	public boolean connectionsuccess = false;
	public static int counter = 0;
    private final Logger LOGGER = Logger.getLogger(AuthServiceDBInterface.class.getName());
    
    private static final String db = ConfigReader.getInstance().getProperty("database.url", "jdbc:mariadb://10.201.1.232:3306/test");
    private static final String dbUser = ConfigReader.getInstance().getProperty("database.username", "roaming");
    private static final String dbPw = ConfigReader.getInstance().getProperty("database.password", "password");


	@PostConstruct
	public void initialize() {
		System.out.println("EJB initialized.");
	}

	public AuthServiceDBInterface() {
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


//			HashMap<String, String> map = new HashMap<>();
//			map.put("key1", "value1");
//			map.put("key2", "value2");
//			
//			ObjectMapper om = new ObjectMapper();
//			String json = "";
//			try {
//				json = om.writeValueAsString(map);
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
	
	public String execute(String query) {
		StringBuilder result = new StringBuilder();
		
		try {
			// Register the JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// Open a connection to the database
			Connection conn = DriverManager.getConnection(db, dbUser, dbPw);

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
			conn = DriverManager.getConnection(db, dbUser, dbPw);

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
		}catch(Exception e) {}finally {}
		
		return results;
	}
	
	public HashMap<String, Object> createUser(String usr, String pass) {
		HashMap<String, Object> res = new HashMap<>();
	    boolean success = true;
	    try {
	        // Register the JDBC driver
	        Class.forName("org.mariadb.jdbc.Driver");

	        // Open a connection to the database
	        conn = DriverManager.getConnection(db, dbUser, dbPw);

	        // Create a prepared statement
	        String sql = "INSERT INTO user(username, password) VALUES(?, ?) RETURNING id;";
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, usr);
	        pstmt.setString(2, pass);

	        LOGGER.info(pstmt.toString());

	        // Execute the update query
	        int rows = pstmt.executeUpdate();
	        
	        // Get the returned value
	        ResultSet rs = pstmt.getResultSet();
	        if (rs != null && rs.next()) {
	            long id = rs.getLong(1);
	            res.put("id", id);
	        }

	        pstmt.close();
	        conn.close();
	        
	       
	        // Check if any rows were affected
	        if (rows == 0) {
	            success = false;
	        }
	    } catch (Exception e) {
	        success = false;
	    }
	    res.put("success", success);
	    return res;
	}
}
