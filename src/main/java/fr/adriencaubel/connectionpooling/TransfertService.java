package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TransfertService {
	String url = "jdbc:mysql://localhost:3314/jdbc-copooling";
    String user = "root";
    String password = "password";
    
	public void transfer(
			Connection connection,
            String sourceAccount,
            String destinationAccount,
            String amount) {
        try {			
	        String insertQuery = "INSERT INTO bank_transert (source, destination, amount) VALUES(?, ?, ?)";
	        PreparedStatement pstmt = connection.prepareStatement(insertQuery);
	        pstmt.setString(1, sourceAccount);
	        pstmt.setString(2, destinationAccount);
	        pstmt.setString(3, amount);
            pstmt.executeUpdate();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
