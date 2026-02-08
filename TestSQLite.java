import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestSQLite {
    public static void main(String[] args) {
        System.out.println("Testing SQLite JDBC Driver...");
        try {
            // 1. Load the driver (optional in newer JDBC, but good for verification)
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver Loaded Successfully!");

            // 2. Establish connection
            String url = "jdbc:sqlite:test_db.db";
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("Connection to SQLite has been established.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("FAILED: SQLite JDBC Driver not found in classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("FAILED: SQL Exception.");
            e.printStackTrace();
        }
    }
}
