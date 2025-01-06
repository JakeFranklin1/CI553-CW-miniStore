package ci553.ministore.middle;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.remote.RemoteStockRW_I;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Facade for read/write access to the stock list.
 * The actual implementation of this is held on the middle tier.
 * The actual stock list is held in a relational DataBase on the
 * third tier.
 *
 * This class provides methods to interact with the remote stock system
 * using RMI (Remote Method Invocation). It handles connection management,
 * remote method invocation, and error recovery.
 *
 * @version 2.0
 */
public class F_StockRW extends F_StockR implements StockReadWriter {
    private RemoteStockRW_I aR_StockRW = null;  // Remote object reference for RW operations
    private String theStockURL = null;          // RMI URL for service lookup

    /**
     * Creates a new facade for stock read/write operations.
     * Initializes RMI connection details.
     *
     * @param url The RMI URL for the remote stock service
     */
    public F_StockRW(String url) {
        super(url);                    // Initialize parent read-only operations
        theStockURL = url;             // Store URL for write operations
    }

    /**
     * Establishes connection to remote service.
     * Performs RMI lookup and caches remote object reference.
     *
     * @throws StockException if RMI lookup fails
     */
    private void connect() throws StockException {
        try {
            // Lookup remote object in RMI registry
            aR_StockRW = (RemoteStockRW_I) Naming.lookup(theStockURL);
        } catch (Exception e) {
            aR_StockRW = null;         // Clear reference on failure
            throw new StockException("Com: " + e.getMessage());
        }
    }

    /**
     * Buys stock by decrementing available quantity.
     * Thread-safe and handles reconnection if needed.
     *
     * @param number Product number to purchase
     * @param amount Quantity to buy
     * @return true if purchase successful, false if insufficient stock
     * @throws StockException if remote operation fails
     */
    public synchronized boolean buyStock(String number, int amount) throws StockException {
        DEBUG.trace("F_StockRW:buyStock()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            return aR_StockRW.buyStock(number, amount);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Adds stock to an existing product.
     * Thread-safe and handles reconnection if needed.
     *
     * @param number Product number to add stock to
     * @param amount Quantity to add
     * @throws StockException if remote operation fails
     */
    public synchronized void addStock(String number, int amount) throws StockException {
        DEBUG.trace("F_StockRW:addStock()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.addStock(number, amount);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Modifies stock details for a given product number.
     * Information modified: Description, Price.
     * Thread-safe and handles reconnection if needed.
     *
     * @param detail Product details to be modified
     * @throws StockException if remote operation fails
     */
    public synchronized void modifyStock(Product detail) throws StockException {
        DEBUG.trace("F_StockRW:modifyStock()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.modifyStock(detail);       // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Sets the stock level for a specific product.
     * Thread-safe and handles reconnection if needed.
     *
     * @param productNum Product number to update
     * @param quantity New stock level
     * @throws StockException if remote operation fails
     */
    @Override
    public synchronized void setStock(String productNum, int quantity) throws StockException {
        DEBUG.trace("F_StockRW:setStock()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.setStock(productNum, quantity);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Adds a new product to the database.
     * Thread-safe and handles reconnection if needed.
     *
     * @param product Product to add
     * @throws StockException if product already exists or remote operation fails
     */
    @Override
    public synchronized void addProduct(Product product) throws StockException {
        DEBUG.trace("F_StockRW:addProduct()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.addProduct(product);       // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Retrieves all products from the database.
     * Thread-safe and handles reconnection if needed.
     *
     * @return List of all products
     * @throws StockException if remote operation fails
     */
    @Override
    public synchronized List<Product> getProducts() throws StockException {
        DEBUG.trace("F_StockRW:getProducts()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            return aR_StockRW.getProducts();      // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Updates the image path for a product.
     * Thread-safe and handles reconnection if needed.
     *
     * @param productNum Product number to update
     * @param imagePath New image path
     * @throws StockException if remote operation fails
     */
    @Override
    public synchronized void updateProductImage(String productNum, String imagePath) throws StockException {
        DEBUG.trace("F_StockRW:updateProductImage()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.updateProductImage(productNum, imagePath);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Deletes a product from the database.
     * Removes entries from both StockTable and ProductTable.
     * Thread-safe and handles reconnection if needed.
     *
     * @param productNum Product number to delete
     * @throws StockException if remote operation fails
     */
    @Override
    public synchronized void deleteProduct(String productNum) throws StockException {
        DEBUG.trace("F_StockRW:deleteProduct()");
        try {
            if (aR_StockRW == null) connect();    // Ensure connection exists
            aR_StockRW.deleteProduct(productNum); // Forward to remote object
        } catch (RemoteException e) {
            aR_StockRW = null;         // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }
}
