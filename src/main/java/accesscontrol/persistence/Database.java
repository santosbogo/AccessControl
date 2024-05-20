package accesscontrol.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String dbUrl = "jdbc:mysql://172.31.22.162:3306/accessControlDB";
    private final String dbUser = "Application";
    private final String dbPassword = "AccessControl2024!";
    private Connection connection;

    public void startDBServer() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to the MySQL database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the MySQL database.");
        }
    }

    public void stopDBServer() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from the MySQL database successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to disconnect from the MySQL database.");
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}


