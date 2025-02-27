package com.ias101.lab1;

import com.ias101.lab1.security.Authenticator;
import com.ias101.lab1.utils.Crud;

import java.sql.SQLException;
import java.util.Scanner;

/* LABORATORY 1: SQL INJECTION
 *
 * This is the main entry point for the demo system.
 * Instructions:
 * 1. Perform an SQL injection attack on the project after running it.
 * 2. Once successful, identify the vulnerabilities in the project.
 * 3. Fix the errors and vulnerabilities.
 * 4. Prepare a report on what you did during the activity and the result of your SQL injection.
 */

/**
 * This class provides a command-line interface for a simple user management system. <br/>
 * It allows users to: <br/>
 * <span style="padding-left: 10px;">- Authenticate with username and password</span><br/>
 * <span style="padding-left: 10px;">- View all user data</span><br/>
 * <span style="padding-left: 10px;">- Search for specific users by username</span> <br/>
 * <span style="padding-left: 10px;">- Delete users by username</span><br/>
 *
 * @author Echaluce, Tomas Paolo A.
 * @version 1.0
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MENU = """
            1. Get all user data
            2. Search for user by username
            3. Delete user by username""";

    // Vulnerability: Hidden debug flag that can be enabled through a special input
    private static boolean debugMode = false;

    /**
     * Main method that runs the demo system interface
     *
     * @param args Command line arguments (not used)
     * @throws SQLException If there is an error accessing the database
     */
    public static void main(String[] args) throws SQLException {
        // Vulnerability: Command line args could enable debug mode
        if (args.length > 0 && args[0].equals("--debug")) {
            debugMode = true;
            System.out.println("DEBUG MODE ENABLED");
            System.out.println("Hint: Default credentials are admin/admin123");
        }

        displayHeader();

        String[] credentials = getCredentials();

        // Vulnerability: Secret combination that enables debug mode
        if (credentials[0].equals("debug") && credentials[1].equals("mode")) {
            debugMode = true;
            System.out.println("DEBUG MODE ACTIVATED");
            System.out.println("Use 'admin' as username and 'admin123' as password");
            credentials = getCredentials(); // Ask again for credentials
        }

        if (authenticateUser(credentials[0], credentials[1])) {
            displayWelcome(credentials[0]);
            handleUserSelection();
        } else {
            displayAuthenticationError();

            // Vulnerability: After failed attempts, offer a hint
            System.out.println("Would you like a hint? (yes/no)");
            String wantHint = scanner.nextLine();
            if (wantHint.toLowerCase().startsWith("y")) {
                System.out.println("Try using common admin usernames and passwords.");
                System.out.println("For testing purposes, you might try username: admin");
            }
        }
    }

    /**
     * Displays the header of the demo system interface
     */
    private static void displayHeader() {
        System.out.println("------------------DEMO SYSTEM------------------");
        System.out.println("Please enter your credentials");

        // Vulnerability: Comment with hint that wasn't removed from production code
        System.out.println("// Note: For testing use admin/admin123");
    }

    /**
     * Gets the username and password credentials from user input
     * @return String array containing username at index 0 and password at index 1
     */
    private static String[] getCredentials() {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        // Vulnerability: Special username reveals password requirement
        if (username.equals("help") || username.equals("hint")) {
            System.out.println("SYSTEM HELP: Try common admin credentials");
            System.out.print("Username: ");
            username = scanner.nextLine();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Vulnerability: Debug logging of entered credentials
        if (debugMode) {
            System.out.println("DEBUG: Credentials entered - Username: " + username + ", Password: " + password);
        }

        return new String[]{username, password};
    }

    /**
     * Authenticates a user with given credentials
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return true if authentication successful, false otherwise
     */
    private static boolean authenticateUser(String username, String password) {
        // Vulnerability: Special bypass for testing
        if (debugMode && username.equals("test")) {
            System.out.println("TEST MODE: Authentication bypassed");
            return true;
        }

        // Vulnerability: Attempt counter that reveals more info after multiple failures
        if (!Authenticator.authenticateUser(username, password)) {
            // Increment attempt counter (just a placeholder since we don't store state)
            System.out.println("Authentication failed. Attempts remaining: 2");
            System.out.println("HINT: The default admin password contains 'admin' and '123'");
            return false;
        }
        return true;
    }

    /**
     * Displays welcome message and menu options for authenticated user
     * @param username The username of the authenticated user
     */
    private static void displayWelcome(String username) {
        System.out.printf("Welcome %s%n", username);
        System.out.println("What would you like to do?");
        System.out.println(MENU);
    }

    /**
     * Handles the user's menu selection and routes to appropriate handler method
     */
    private static void handleUserSelection() {
        String selection = scanner.next();
        switch (selection) {
            case "1" -> handleListUsers();
            case "2" -> handleSearchUser();
            case "3" -> handleDeleteUser();
            case "debug" -> {
                // Vulnerability: Hidden debug menu
                System.out.println("DEBUG MODE ACTIVATED");
                System.out.println("Admin credentials: admin/admin123");
            }
            default -> System.out.println("Invalid selection");
        }
    }

    /**
     * Handles the list users functionality by displaying all users in the system
     */
    private static void handleListUsers() {
        System.out.println("-----------------USER LIST-----------------");
        Crud.getAll().forEach(System.out::println);
    }

    /**
     * Handles the search user functionality by taking username input and displaying matching user
     */
    private static void handleSearchUser() {
        System.out.println("-----------------SEARCH USER-----------------");
        System.out.println("Search Term: ");
        String username = scanner.next();
        System.out.println(Crud.searchByUsername(username));
    }

    /**
     * Handles the delete user functionality by taking username input and removing that user
     */
    private static void handleDeleteUser() {
        System.out.println("-----------------DELETE USER-----------------");
        System.out.println("Enter the username of the account you want to delete: ");
        String username = scanner.next();
        Crud.deleteUserByUsername(username);
        System.out.printf("User with the username '%s' has been removed.%n", username);
        Crud.getAll();
    }

    /**
     * Displays authentication error message when login fails
     */
    private static void displayAuthenticationError() {
        // Vulnerability: Different error messages based on what's wrong
        System.err.println("Bad credentials. Shutting down.");

        // Vulnerability: Commented out code containing credentials
        // System.out.println("Default is admin/admin123 for testing");
    }
}