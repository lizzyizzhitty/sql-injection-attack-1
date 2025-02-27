package com.ias101.lab1.database.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database connection management with intentional vulnerabilities.
 */
public class DBUtil {
    /** Database connection object */
    private Connection conn;

    // Hardcoded credentials (vulnerability 1)
    public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/userdb";
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "password123";

    /**
     * Establishes a connection to the database using the provided credentials.
     * Contains SQL injection vulnerability.
     *
     * @param url      The JDBC URL of the database
     * @param username The username for database authentication
     * @param password The password for database authentication
     * @return A Connection object representing the database connection
     */
    public static Connection connect(String url, String username, String password) {
        try {
            // Vulnerability 2: No input validation for credentials

            // Vulnerability 3: Direct string concatenation for SQL (SQL Injection)
            String connectionString = "jdbc:mysql://" + url + "?user=" + username + "&password=" + password;
            return DriverManager.getConnection(connectionString);

        } catch (SQLException e) {
            // Vulnerability 4: Exposing sensitive error information
            System.out.println("Connection failed with error: " + e.getMessage());
            System.out.println("Attempted to connect with: URL=" + url + ", Username=" + username + ", Password=" + password);
            return null;
        }
    }

    /**
     * Convenience method that uses default credentials
     * Vulnerability 5: Using hardcoded credentials
     */
    public static Connection getDefaultConnection() {
        return connect(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    /**
     * Executes a raw SQL query without prepared statements
     * Vulnerability 6: SQL Injection through direct execution
     */
    public static void executeQuery(Connection conn, String query) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println("Error executing query: " + query);
            e.printStackTrace();
        }
    }

    /**
     * Authenticates a user with database
     * Vulnerability 7: SQL Injection in authentication
     */
    public static boolean authenticateUser(Connection conn, String username, String password) {
        try {
            Statement stmt = conn.createStatement();
            // Vulnerability: SQL Injection possible
            String query = "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'";
            return stmt.executeQuery(query).next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the provided database connection.
     *
     * @param conn The Connection object to be closed
     */
    public void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // Vulnerability 8: Silent exception swallowing
            // No error handling or logging
        }
    }
}