package passoff.server.server;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306";
        String user = "root";
        String password = "273Rv932$";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connection successful!");
        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
