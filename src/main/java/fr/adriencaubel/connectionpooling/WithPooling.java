package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

public class WithPooling {

	public static void main(String[] args) throws SQLException {
		BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3314/jdbc-copooling");
        dataSource.setUsername("root");
        dataSource.setPassword("password");

        // Pool configuration
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5); // Minimum number of idle connections
        dataSource.setMaxIdle(10); // Maximum number of idle connections
        dataSource.setMaxOpenPreparedStatements(200); // Maximum prepared statements

        // Measure performance
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM test_data")) {

                while (resultSet.next()) {
                    resultSet.getInt("id"); // Simulate processing rows
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time with connection pooling: " + (endTime - startTime) + " ms");
	}
}