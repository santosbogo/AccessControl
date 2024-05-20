package accesscontrol.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String dbUrl = "jdbc:mysql://172.31.22.162:3306/AccessControlDB";
    private final String dbUser = "Application";
    private final String dbPassword = "AccessControl2024!";
    private Connection connection;

    public void startDBServer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to the MySQL database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the MySQL database.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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


