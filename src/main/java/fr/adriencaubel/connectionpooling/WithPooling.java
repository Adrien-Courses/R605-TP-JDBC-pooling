package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp2.BasicDataSource;

public class WithPooling {

	public static void main(String[] args) throws SQLException, InterruptedException {
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
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//createTable(dataSource.getConnection());
			dataSource.getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        TransfertService transferService = new TransfertService();

        long startNanos = System.nanoTime();
        int threadCount = 1000;
         
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
         
        for (int i = 0; i < threadCount; i++) {
        	Thread t = new Thread() {
        	    public void run() {
        	    	try {
                        startLatch.await();
                        transferService.transfer(dataSource.getConnection(), "Alice-123", "Bob-456", "5");
                    } catch (Exception e) {
                    	System.out.println("Transfer failed");
                    } finally {
                        endLatch.countDown();
                    }
        	    }  
        	};
        	t.start();
        }
         
        System.out.println("Starting threads");
        startLatch.countDown();
        endLatch.await();
         
        System.out.printf(
            "The %s transfers were executed on %s database connections in %s ms",
            threadCount,
            dataSource.getInitialSize(),
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos)
        );
	}
	
    
	private static void createTable(Connection connection) throws SQLException {
        String createTableQuery = """
					CREATE TABLE IF NOT EXISTS bank_transert (
					    id INT AUTO_INCREMENT PRIMARY KEY,
					    source VARCHAR(100),
					    destination VARCHAR(100),
					    amount VARCHAR(10)
					);
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        }
    }
}