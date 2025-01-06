package ci553.ministore.dbAccess;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ci553.ministore.middle.StockException;
import ci553.ministore.util.PasswordUtil;

/**
 * Data Access Object for user authentication operations.
 * Handles database operations related to user management and authentication.
 * Uses prepared statements to prevent SQL injection and secure password hashing.
 */
public class UserDAO {
    // Database connection instance
    private Connection theCon = null;

    /**
     * Constructor that initializes the database connection.
     * Creates a new database connection using the DBAccess factory.
     *
     * @throws StockException If there's an error loading the driver or connecting to the database
     */
    public UserDAO() throws StockException {
        try {
            // Create new database access instance
            DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();

            // Establish database connection with credentials
            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password());

            // Enable auto-commit for immediate transaction processing
            theCon.setAutoCommit(true);
        } catch (SQLException e) {
            throw new StockException("SQL problem:" + e.getMessage());
        } catch (Exception e) {
            throw new StockException("Can not load database driver.");
        }
    }

    /**
     * Validates user credentials against the database.
     * Uses secure password hashing with salt for validation.
     *
     * @param username The username to validate
     * @param password The password to validate
     * @return true if credentials are valid, false otherwise
     * @throws SQLException If there's an error executing the SQL query
     * @throws NoSuchAlgorithmException If the hashing algorithm is not available
     * @throws InvalidKeySpecException If there's an error with the key specification
     */
    public boolean validateUser(String username, String password)
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // SQL query to fetch password hash and salt for the given username
        String sql = "SELECT password, salt FROM Users WHERE username = ?";

        try (PreparedStatement stmt = theCon.prepareStatement(sql)) {
            // Bind username parameter to prevent SQL injection
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract stored hash and salt from result set
                    String storedHash = rs.getString("password");
                    String salt = rs.getString("salt");

                    // Validate password using stored hash and salt
                    return PasswordUtil.validatePassword(password, salt, storedHash);
                }
            }
        }
        // Return false if username not found or invalid credentials
        return false;
    }
}
