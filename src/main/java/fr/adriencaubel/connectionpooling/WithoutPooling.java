package fr.adriencaubel.connectionpooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WithoutPooling {

    public static void main(String[] args) throws Exception {
		String url = "jdbc:mysql://localhost:3314/jdbc-copooling";
        String user = "root";
        String password = "password";
        
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, user, password);
			//createTable(connection);
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        TransfertService transferService = new TransfertService();

        long startNanos = System.nanoTime();
        int threadCount = 100;
         
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
         
        for (int i = 0; i < threadCount; i++) {
        	Thread t = new Thread() {
        	    public void run() {
        	    	try {
                        startLatch.await();
                        Connection connection = DriverManager.getConnection(url, user, password);
                        transferService.transfer(connection, "Alice-123", "Bob-456", "5");
            			connection.close();
                    } catch (Exception e) {
                    	System.out.println("Transfer failed " + e);
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
            threadCount,
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
