package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.StockException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the RMI interface for read access to the stock object.
 * Provides methods for checking product existence, retrieving product details,
 * and fetching product images. This interface is used to define the methods
 * that can be called remotely via RMI.
 *
 * Each method throws RemoteException to handle network-related issues and
 * StockException for stock-related errors.
 *
 * @version 2.0
 */
public interface RemoteStockR_I extends Remote {

    /**
     * Checks if a product exists in the database.
     *
     * @param number The product number to check
     * @return true if the product exists, false otherwise
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    boolean exists(String number) throws RemoteException, StockException;

    /**
     * Retrieves detailed product information from the database.
     *
     * @param number The product number to retrieve details for
     * @return A Product object containing the product details
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    Product getDetails(String number) throws RemoteException, StockException;

    /**
     * Retrieves the product image as a byte array from the database.
     *
     * @param number The product number to retrieve the image for
     * @return byte array containing the image data
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the stock
     */
    byte[] getImage(String number) throws RemoteException, StockException;
}
