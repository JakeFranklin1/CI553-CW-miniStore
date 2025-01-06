package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.dbAccess.StockRW;
import ci553.ministore.middle.StockException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Remote implementation of the StockReadWriter interface.
 * Provides remote access to stock read/write operations using RMI.
 * Delegates actual data operations to the StockRW class.
 *
 * This class handles the RMI setup and delegates the method calls to the
 * StockRW instance which interacts with the database.
 *
 */
public class R_StockRW extends UnicastRemoteObject implements RemoteStockRW_I {
    private static final long serialVersionUID = 1;
    private StockRW aStockRW = null;  // Local StockRW instance for database operations

    /**
     * Constructs a new R_StockRW instance.
     * Initializes the local StockRW instance.
     *
     * @param url The database URL (not used in this implementation)
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error initializing the StockRW instance
     */
    public R_StockRW(String url) throws RemoteException, StockException {
        aStockRW = new StockRW();  // Initialize local StockRW instance
    }

    /**
     * Checks if a product exists in the database.
     * Delegates the call to the local StockRW instance.
     *
     * @param pNum The product number to check
     * @return true if the product exists, false otherwise
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized boolean exists(String pNum) throws RemoteException, StockException {
        return aStockRW.exists(pNum);  // Delegate to local StockRW instance
    }

    /**
     * Retrieves detailed product information from the database.
     * Delegates the call to the local StockRW instance.
     *
     * @param pNum The product number to retrieve details for
     * @return A Product object containing the product details
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized Product getDetails(String pNum) throws RemoteException, StockException {
        return aStockRW.getDetails(pNum);  // Delegate to local StockRW instance
    }

    /**
     * Retrieves the product image as a byte array from the database.
     * Delegates the call to the local StockRW instance.
     *
     * @param pNum The product number to retrieve the image for
     * @return byte array containing the image data
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized byte[] getImage(String pNum) throws RemoteException, StockException {
        return aStockRW.getImage(pNum);  // Delegate to local StockRW instance
    }

    /**
     * Buys stock by decrementing available quantity.
     * Delegates the call to the local StockRW instance.
     *
     * @param pNum The product number to purchase
     * @param amount The quantity to buy
     * @return true if the purchase is successful, false if insufficient stock
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized boolean buyStock(String pNum, int amount) throws RemoteException, StockException {
        return aStockRW.buyStock(pNum, amount);  // Delegate to local StockRW instance
    }

    /**
     * Adds stock to an existing product.
     * Delegates the call to the local StockRW instance.
     *
     * @param pNum The product number to add stock to
     * @param amount The quantity to add
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized void addStock(String pNum, int amount) throws RemoteException, StockException {
        aStockRW.addStock(pNum, amount);  // Delegate to local StockRW instance
    }

    /**
     * Modifies stock details for a given product number.
     * Delegates the call to the local StockRW instance.
     *
     * @param product The product details to be modified
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized void modifyStock(Product product) throws RemoteException, StockException {
        aStockRW.modifyStock(product);  // Delegate to local StockRW instance
    }

    /**
     * Sets the stock level for a specific product.
     * Delegates the call to the local StockRW instance.
     *
     * @param productNum The product number to update
     * @param quantity The new stock level
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    @Override
    public synchronized void setStock(String productNum, int quantity) throws RemoteException, StockException {
        aStockRW.setStock(productNum, quantity);  // Delegate to local StockRW instance
    }

    /**
     * Adds a new product to the database.
     * Delegates the call to the local StockRW instance.
     *
     * @param product The product to add
     * @throws RemoteException If there is an RMI error
     * @throws StockException If the product already exists or there is an error accessing the database
     */
    @Override
    public synchronized void addProduct(Product product) throws RemoteException, StockException {
        aStockRW.addProduct(product);  // Delegate to local StockRW instance
    }

    /**
     * Retrieves all products from the database.
     * Delegates the call to the local StockRW instance.
     *
     * @return A list of all products
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    @Override
    public synchronized List<Product> getProducts() throws RemoteException, StockException {
        return aStockRW.getProducts();  // Delegate to local StockRW instance
    }

    /**
     * Updates the image path for a product.
     * Delegates the call to the local StockRW instance.
     *
     * @param productNum The product number to update
     * @param imagePath The new image path
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    @Override
    public synchronized void updateProductImage(String productNum, String imagePath) throws RemoteException, StockException {
        aStockRW.updateProductImage(productNum, imagePath);  // Delegate to local StockRW instance
    }

    /**
     * Deletes a product from the database.
     * Delegates the call to the local StockRW instance.
     *
     * @param productNum The product number to delete
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    @Override
    public synchronized void deleteProduct(String productNum) throws RemoteException, StockException {
        aStockRW.deleteProduct(productNum);  // Delegate to local StockRW instance
    }
}
