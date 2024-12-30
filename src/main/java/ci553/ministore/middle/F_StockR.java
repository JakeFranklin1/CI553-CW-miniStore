package ci553.ministore.middle;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.remote.RemoteStockR_I;
import javafx.scene.image.Image;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.io.ByteArrayInputStream;

/**
 * Facade for read access to the stock list.
 * @version 2.0
 */
public class F_StockR implements StockReader {
    private RemoteStockR_I aR_StockR = null;
    private String theStockURL = null;

    public F_StockR(String url) {
        DEBUG.trace("F_StockR: %s", url);
        theStockURL = url;
    }

    private void connect() throws StockException {
        try {
            aR_StockR = (RemoteStockR_I) Naming.lookup(theStockURL);
        } catch (Exception e) {
            aR_StockR = null;
            throw new StockException("Com: " + e.getMessage());
        }
    }

    public synchronized boolean exists(String number) throws StockException {
        DEBUG.trace("F_StockR:exists()");
        try {
            if (aR_StockR == null) connect();
            return aR_StockR.exists(number);
        } catch (RemoteException e) {
            aR_StockR = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    public synchronized Product getDetails(String number) throws StockException {
        DEBUG.trace("F_StockR:getDetails()");
        try {
            if (aR_StockR == null) connect();
            return aR_StockR.getDetails(number);
        } catch (RemoteException e) {
            aR_StockR = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    public synchronized byte[] getImage(String number) throws StockException {
        DEBUG.trace("F_StockR:getImage()");
        try {
            if (aR_StockR == null) connect();
            return aR_StockR.getImage(number);
        } catch (RemoteException e) {
            aR_StockR = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }
}
