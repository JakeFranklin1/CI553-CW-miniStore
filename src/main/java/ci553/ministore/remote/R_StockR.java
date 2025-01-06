package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.dbAccess.StockR;
import ci553.ministore.middle.StockException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Remote implementation of the StockReader interface.
 * Provides remote access to stock information using RMI.
 * Delegates actual data operations to the StockR class.
 *
 * This class handles the RMI setup and delegates the method calls to the
 * StockR instance which interacts with the database.
 * 
 */
public class R_StockR extends UnicastRemoteObject implements RemoteStockR_I {
    private static final long serialVersionUID = 1;
    private StockR aStockR = null;  // Local StockR instance for database operations

    /**
     * Constructs a new R_StockR instance.
     * Initializes the local StockR instance.
     *
     * @param url The database URL (not used in this implementation)
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error initializing the StockR instance
     */
    public R_StockR(String url) throws RemoteException, StockException {
        aStockR = new StockR();  // Initialize local StockR instance
    }

    /**
     * Checks if a product exists in the database.
     * Delegates the call to the local StockR instance.
     *
     * @param pNum The product number to check
     * @return true if the product exists, false otherwise
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized boolean exists(String pNum) throws RemoteException, StockException {
        return aStockR.exists(pNum);  // Delegate to local StockR instance
    }

    /**
     * Retrieves detailed product information from the database.
     * Delegates the call to the local StockR instance.
     *
     * @param pNum The product number to retrieve details for
     * @return A Product object containing the product details
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized Product getDetails(String pNum) throws RemoteException, StockException {
        return aStockR.getDetails(pNum);  // Delegate to local StockR instance
    }

    /**
     * Retrieves the product image as a byte array from the database.
     * Delegates the call to the local StockR instance.
     *
     * @param pNum The product number to retrieve the image for
     * @return byte array containing the image data
     * @throws RemoteException If there is an RMI error
     * @throws StockException If there is an error accessing the database
     */
    public synchronized byte[] getImage(String pNum) throws RemoteException, StockException {
        return aStockR.getImage(pNum);  // Delegate to local StockR instance
    }
}
