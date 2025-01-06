package ci553.ministore.dbAccess;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.middle.StockException;
import ci553.ministore.middle.StockReadWriter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 * Stock Read-Write implementation for database operations.
 * Extends StockR to provide write capabilities in addition to read operations.
 * Implements thread-safe database operations for stock management.
 * All public methods are synchronized to ensure thread safety.
 */
public class StockRW extends StockR implements StockReadWriter {

    /**
     * Constructs a new StockRW instance.
     * Initializes database connection through parent class.
     * @throws StockException If database initialization fails
     */
    public StockRW() throws StockException {
        super();
    }

    /**
     * Attempts to purchase stock by reducing the stock level.
     * Only succeeds if sufficient stock is available.
     * @param pNum Product number to buy
     * @param amount Quantity to buy
     * @return true if purchase successful, false if insufficient stock
     * @throws StockException If database operation fails
     */
    public synchronized boolean buyStock(String pNum, int amount) throws StockException {
        DEBUG.trace("DB StockRW: buyStock(%s,%d)", pNum, amount);
        int updates = 0;
        try {
            // Update stock level only if sufficient stock exists
            getStatementObject().executeUpdate(
                    "update StockTable set stockLevel = stockLevel-" + amount +
                            "       where productNo = '" + pNum + "' and " +
                            "             stockLevel >= " + amount + "");
            updates = 1;
        } catch (SQLException e) {
            throw new StockException("SQL buyStock: " + e.getMessage());
        }
        DEBUG.trace("buyStock() updates -> %n", updates);
        return updates > 0;
    }

    /**
     * Adds stock to an existing product.
     * @param pNum Product number to add stock to
     * @param amount Quantity to add
     * @throws StockException If database operation fails
     */
    public synchronized void addStock(String pNum, int amount) throws StockException {
        try {
            // Increment stock level by specified amount
            getStatementObject().executeUpdate(
                    "update StockTable set stockLevel = stockLevel + " + amount +
                            "         where productNo = '" + pNum + "'");
            DEBUG.trace("DB StockRW: addStock(%s,%d)", pNum, amount);
        } catch (SQLException e) {
            throw new StockException("SQL addStock: " + e.getMessage());
        }
    }

    /**
     * Updates or creates a product in the database.
     * If product exists, updates details; if not, creates new entry.
     * @param detail Product details to modify/create
     * @throws StockException If database operation fails
     */
    public synchronized void modifyStock(Product detail) throws StockException {
        DEBUG.trace("DB StockRW: modifyStock(%s)", detail.getProductNum());
        try {
            if (!exists(detail.getProductNum())) {
                // Insert new product into ProductTable with placeholder image
                getStatementObject().executeUpdate(
                        "insert into ProductTable values ('" +
                                detail.getProductNum() + "', " +
                                "'" + detail.getDescription() + "', " +
                                "'ci553/ministore/images/placeholder.png', " +
                                detail.getPrice() + ")");
                // Insert initial stock level
                getStatementObject().executeUpdate(
                        "insert into StockTable values ('" +
                                detail.getProductNum() + "', " +
                                detail.getQuantity() + ")");
            } else {
                // Update existing product details
                getStatementObject().executeUpdate(
                        "update ProductTable " +
                                "  set description = '" + detail.getDescription() + "' , " +
                                "      price       = " + detail.getPrice() +
                                "  where productNo = '" + detail.getProductNum() + "' ");
                // Update stock level
                getStatementObject().executeUpdate(
                        "update StockTable set stockLevel = " + detail.getQuantity() +
                                "  where productNo = '" + detail.getProductNum() + "'");
            }
        } catch (SQLException e) {
            throw new StockException("SQL modifyStock: " + e.getMessage());
        }
    }

    /**
     * Sets the stock level for a specific product.
     * @param productNum Product number to update
     * @param quantity New stock level
     * @throws StockException If database operation fails
     */
    @Override
    public synchronized void setStock(String productNum, int quantity) throws StockException {
        try {
            // Get current product details
            Product product = getDetails(productNum);
            // Update quantity and modify stock
            product.setQuantity(quantity);
            modifyStock(product);
        } catch (StockException e) {
            throw new StockException("Error setting stock: " + e.getMessage());
        }
    }

    /**
     * Adds a new product to the database.
     * @param product Product to add
     * @throws StockException If product already exists or database operation fails
     */
    @Override
    public synchronized void addProduct(Product product) throws StockException {
        DEBUG.trace("DB StockRW: addProduct(%s)", product.getProductNum());
        try {
            if (!exists(product.getProductNum())) {
                // Insert new product with default image path
                getStatementObject().executeUpdate(
                        "insert into ProductTable values ('" +
                                product.getProductNum() + "', " +
                                "'" + product.getDescription() + "', " +
                                "'ci553/ministore/images/Pic" + product.getProductNum() + ".png', " +
                                "'" + product.getPrice() + "' " + ")");
                // Insert initial stock level
                getStatementObject().executeUpdate(
                        "insert into StockTable values ('" +
                                product.getProductNum() + "', " +
                                "'" + product.getQuantity() + "' " + ")");
            } else {
                throw new StockException("Product already exists: " + product.getProductNum());
            }
        } catch (SQLException e) {
            throw new StockException("SQL addProduct: " + e.getMessage());
        }
    }

    /**
     * Retrieves all products from the database.
     * @return List of all products
     * @throws StockException If database query fails
     */
    @Override
    public synchronized List<Product> getProducts() throws StockException {
        List<Product> products = new ArrayList<>();
        try {
            // Join ProductTable and StockTable to get complete product information
            ResultSet rs = getStatementObject().executeQuery(
                    "SELECT ProductTable.productNo, ProductTable.description, ProductTable.price, StockTable.stockLevel " +
                            "FROM ProductTable " +
                            "JOIN StockTable ON ProductTable.productNo = StockTable.productNo");
            // Create Product objects from result set
            while (rs.next()) {
                String productNum = rs.getString("productNo");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("stockLevel");
                products.add(new Product(productNum, description, price, quantity));
            }
            rs.close();
        } catch (SQLException e) {
            throw new StockException("SQL getProducts: " + e.getMessage());
        }
        return products;
    }

    /**
     * Updates the image path for a product.
     * @param productNum Product number to update
     * @param imagePath New image path
     * @throws StockException If database operation fails
     */
    public synchronized void updateProductImage(String productNum, String imagePath) throws StockException {
        DEBUG.trace("DB StockRW: updateProductImage(%s, %s)", productNum, imagePath);
        try {
            // Update image path in ProductTable
            getStatementObject().executeUpdate(
                    "UPDATE ProductTable SET picture = '" + imagePath +
                            "' WHERE productNo = '" + productNum + "'");
        } catch (SQLException e) {
            throw new StockException("SQL updateProductImage: " + e.getMessage());
        }
    }

    /**
     * Deletes a product from the database.
     * Removes entries from both StockTable and ProductTable.
     * @param productNum Product number to delete
     * @throws StockException If database operation fails
     */
    @Override
    public synchronized void deleteProduct(String productNum) throws StockException {
        DEBUG.trace("DB StockRW: deleteProduct(%s)", productNum);
        try {
            // Delete from StockTable first due to foreign key constraint
            getStatementObject().executeUpdate(
                    "DELETE FROM StockTable WHERE productNo = '" + productNum + "'");
            // Then delete from ProductTable
            getStatementObject().executeUpdate(
                    "DELETE FROM ProductTable WHERE productNo = '" + productNum + "'");
        } catch (SQLException e) {
            throw new StockException("SQL deleteProduct: " + e.getMessage());
        }
    }
}
