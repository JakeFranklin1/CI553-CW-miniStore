package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.StockException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Defines the RMI interface for read/write access to the stock object.
 * Extends RemoteStockR_I to include write operations.
 * Provides methods for managing stock levels, adding products, and updating product details.
 *
 * This interface is used to define the methods that can be called remotely via RMI.
 * Each method throws RemoteException to handle network-related issues and StockException for stock-related errors.
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
@SuppressWarnings("unused")
public interface RemoteStockRW_I extends RemoteStockR_I, Remote {

    /**
     * Buys stock by decrementing available quantity.
     *
     * @param number The product number to purchase
     * @param amount The quantity to buy
     * @return true if the purchase is successful, false if insufficient stock
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    boolean buyStock(String number, int amount) throws RemoteException, StockException;

    /**
     * Adds stock to an existing product.
     *
     * @param number The product number to add stock to
     * @param amount The quantity to add
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    void addStock(String number, int amount) throws RemoteException, StockException;

    /**
     * Modifies stock details for a given product number.
     * Information modified: Description, Price.
     *
     * @param detail The product details to be modified
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    void modifyStock(Product detail) throws RemoteException, StockException;

    /**
     * Sets the stock level for a specific product.
     *
     * @param productNum The product number to update
     * @param quantity The new stock level
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    void setStock(String productNum, int quantity) throws RemoteException, StockException;

    /**
     * Adds a new product to the database.
     *
     * @param product The product to add
     * @throws RemoteException If there is an RMI error
     * @throws StockException If the product already exists or there is an error accessing the stock
     */
    void addProduct(Product product) throws RemoteException, StockException;

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all products
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    List<Product> getProducts() throws RemoteException, StockException;

    /**
     * Updates the image path for a product.
     *
     * @param productNum The product number to update
     * @param imagePath The new image path
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    void updateProductImage(String productNum, String imagePath) throws RemoteException, StockException;

    /**
     * Deletes a product from the database.
     *
     * @param productNum The product number to delete
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    void deleteProduct(String productNum) throws RemoteException, StockException;
}
