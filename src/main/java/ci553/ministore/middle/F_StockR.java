package ci553.ministore.middle;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.remote.RemoteStockR_I;

import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Facade for remote read access to the stock list.
 * Implements the StockReader interface and provides RMI connectivity to the remote stock system.
 * Handles connection management, remote method invocation, and error recovery.
 *
 * @version 2.0
 */
public class F_StockR implements StockReader {
    private RemoteStockR_I aR_StockR = null;  // Remote object reference
    private String theStockURL = null;        // RMI URL for the remote service

    /**
     * Constructs a new F_StockR instance with specified RMI URL.
     * @param url The RMI URL for the remote stock service
     */
    public F_StockR(String url) {
        DEBUG.trace("F_StockR: %s", url);
        theStockURL = url;
    }

    /**
     * Establishes connection to the remote stock service.
     * Attempts to lookup the remote object using RMI.
     * @throws StockException if connection fails
     */
    private void connect() throws StockException {
        try {
            // Lookup remote object in RMI registry
            aR_StockR = (RemoteStockR_I) Naming.lookup(theStockURL);
        } catch (Exception e) {
            aR_StockR = null;  // Reset connection on failure
            throw new StockException("Com: " + e.getMessage());
        }
    }

    /**
     * Checks if a product exists in the remote stock system.
     * Automatically reconnects if connection is lost.
     * @param number Product number to check
     * @return true if product exists, false otherwise
     * @throws StockException if remote operation fails
     */
    public synchronized boolean exists(String number) throws StockException {
        DEBUG.trace("F_StockR:exists()");
        try {
            if (aR_StockR == null) connect();  // Reconnect if needed
            return aR_StockR.exists(number);   // Forward to remote object
        } catch (RemoteException e) {
            aR_StockR = null;  // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Retrieves product details from the remote stock system.
     * Automatically reconnects if connection is lost.
     * @param number Product number to retrieve details for
     * @return Product object containing product details
     * @throws StockException if remote operation fails
     */
    public synchronized Product getDetails(String number) throws StockException {
        DEBUG.trace("F_StockR:getDetails()");
        try {
            if (aR_StockR == null) connect();  // Reconnect if needed
            return aR_StockR.getDetails(number);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockR = null;  // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Retrieves product image from the remote stock system.
     * Automatically reconnects if connection is lost.
     * @param number Product number to retrieve image for
     * @return byte array containing the image data
     * @throws StockException if remote operation fails
     */
    public synchronized byte[] getImage(String number) throws StockException {
        DEBUG.trace("F_StockR:getImage()");
        try {
            if (aR_StockR == null) connect();  // Reconnect if needed
            return aR_StockR.getImage(number);  // Forward to remote object
        } catch (RemoteException e) {
            aR_StockR = null;  // Reset connection on failure
            throw new StockException("Net: " + e.getMessage());
        }
    }
}
