package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class WithoutPooling {

    public static void main(String[] args) throws Exception {
		String url = "jdbc:mysql://localhost:3314/jdbc-copooling";
        String user = "root";
        String password = "password";

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM test_data")) {
                while (resultSet.next()) {
                    resultSet.getInt("id"); // Simulate processing rows
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time without pooling: " + (endTime - startTime) + " ms");
    }

}
