package com.ias101.lab1.security;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ias101.lab1.database.util.DBUtil;

/**
 * Class responsible for authenticating users
 * Contains multiple security vulnerabilities
 */
public class Authenticator {

    // Vulnerability 1: Hardcoded credentials as fallback
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    /**
     * Authenticates a user with given credentials
     * Contains SQL Injection vulnerability
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return true if authentication successful, false otherwise
     */
    public static boolean authenticateUser(String username, String password) {
        // Vulnerability 2: Backdoor authentication
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("Admin bypass login successful");
            return true;
        }

        // Vulnerability 3: No input validation

        try {
            // Vulnerability 4: Using default connection
            Connection conn = DBUtil.getDefaultConnection();

            // Vulnerability 5: SQL Injection in query
            String query = "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            boolean result = rs.next();

            // Vulnerability 6: Not closing resources properly
            return result;

        } catch (SQLException e) {
            // Vulnerability 7: Exposing error details
            System.out.println("Authentication error: " + e.getMessage());
            System.out.println("Attempted credentials - Username: " + username + ", Password: " + password);

            // Vulnerability 8: Returning false on exception instead of proper error handling
            return false;
        }
    }

    /**
     * Stores password in plain text
     * Vulnerability 9: No password hashing
     */
    public static void registerUser(String username, String password) {
        try {
            Connection conn = DBUtil.getDefaultConnection();

            // Vulnerability 10: SQL Injection in registration
            String query = "INSERT INTO users (username, password) VALUES ('" +
                    username + "', '" + password + "')";

            DBUtil.executeQuery(conn, query);

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}