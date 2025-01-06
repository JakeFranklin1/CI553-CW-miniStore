package ci553.ministore.dbAccess;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.middle.StockException;
import ci553.ministore.middle.StockReader;

import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the StockReader interface for database operations.
 * Provides methods to read product information and images from the database.
 * Uses JDBC for database connectivity and handles connection lifecycle.
 */
public class StockR implements StockReader {
    private Connection theCon = null;  // Database connection instance
    private Statement theStmt = null;  // SQL statement instance

    /**
     * Constructor that initializes the database connection.
     * Creates a new connection using DBAccess factory and configures auto-commit.
     *
     * @throws StockException If database connection or driver loading fails
     */
    public StockR() throws StockException {
        try {
            // Create database access instance
            DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();

            // Establish database connection with credentials
            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password());

            // Create statement object and enable auto-commit
            theStmt = theCon.createStatement();
            theCon.setAutoCommit(true);
        } catch (SQLException e) {
            throw new StockException("SQL problem:" + e.getMessage());
        } catch (Exception e) {
            throw new StockException("Can not load database driver.");
        }
    }

    /**
     * Gets the Statement object for executing SQL queries.
     * @return The Statement object
     */
    protected Statement getStatementObject() {
        return theStmt;
    }

    /**
     * Gets the Connection object for database operations.
     * @return The Connection object
     */
    protected Connection getConnectionObject() {
        return theCon;
    }

    /**
     * Checks if a product exists in the database.
     * @param pNum Product number to check
     * @return true if product exists, false otherwise
     * @throws StockException If SQL query fails
     */
    public synchronized boolean exists(String pNum) throws StockException {
        try {
            // Query to check product existence by product number
            ResultSet rs = getStatementObject().executeQuery(
                    "select price from ProductTable " +
                            "  where  ProductTable.productNo = '" + pNum + "'"
            );
            boolean res = rs.next();
            DEBUG.trace("DB StockR: exists(%s) -> %s", pNum, (res ? "T" : "F"));
            return res;
        } catch (SQLException e) {
            throw new StockException("SQL exists: " + e.getMessage());
        }
    }

    /**
     * Retrieves detailed product information from the database.
     * @param pNum Product number to retrieve details for
     * @return Product object containing product details
     * @throws StockException If SQL query fails
     */
    public synchronized Product getDetails(String pNum) throws StockException {
        try {
            // Create default product object
            Product dt = new Product("0", "", 0.00, 0);

            // Query to get product details from both ProductTable and StockTable
            ResultSet rs = getStatementObject().executeQuery(
                    "select description, price, stockLevel " +
                            "  from ProductTable, StockTable " +
                            "  where  ProductTable.productNo = '" + pNum + "' " +
                            "  and    StockTable.productNo   = '" + pNum + "'"
            );

            // Populate product object if result exists
            if (rs.next()) {
                dt.setProductNum(pNum);
                dt.setDescription(rs.getString("description"));
                dt.setPrice(rs.getDouble("price"));
                dt.setQuantity(rs.getInt("stockLevel"));
            }
            rs.close();
            return dt;
        } catch (SQLException e) {
            throw new StockException("SQL getDetails: " + e.getMessage());
        }
    }

    /**
     * Retrieves product image as byte array from the database.
     * Falls back to default image if specified image not found.
     * @param pNum Product number to retrieve image for
     * @return byte array containing the image data
     * @throws StockException If image retrieval fails
     */
    public synchronized byte[] getImage(String pNum) throws StockException {
        String imagePath = "default.png";  // Default image path
        try {
            // Query to get image path from ProductTable
            ResultSet rs = getStatementObject().executeQuery(
                    "select picture from ProductTable " +
                            "  where  ProductTable.productNo = '" + pNum + "'");

            if (rs.next()) {
                imagePath = rs.getString("picture");
            }
            rs.close();
        } catch (SQLException e) {
            DEBUG.error("getImage()\n%s\n", e.getMessage());
            throw new StockException("SQL getImage: " + e.getMessage());
        }

        DEBUG.trace("DB StockR: getImage -> %s", imagePath);

        // Read image file from resources and convert to byte array
        try (InputStream is = getClass().getResourceAsStream("/" + imagePath);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            if (is == null) {
                throw new IOException("Resource not found: " + imagePath);
            }

            // Read image data in chunks
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new StockException("Error reading image file: " + imagePath + " (" + e.getMessage() + ")");
        }
    }
}
