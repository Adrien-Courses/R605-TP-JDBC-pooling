package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDatabase {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3314/jdbc-copooling";
        String user = "root";
        String password = "password";
		
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, user, password);
			createTable(connection);
			initialize(connection);
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}

	private static void initialize(Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO test_data (data) VALUES(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            for(int i = 0; i < 1000; i++) {
            	pstmt.setString(1, "Sample Data " + i);
                pstmt.executeUpdate();
            }
        }
	}

	private static void createTable(Connection connection) throws SQLException {
        String createTableQuery = """
					CREATE TABLE IF NOT EXISTS test_data (
					    id INT AUTO_INCREMENT PRIMARY KEY,
					    data VARCHAR(100)
					);
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        }
    }
}
