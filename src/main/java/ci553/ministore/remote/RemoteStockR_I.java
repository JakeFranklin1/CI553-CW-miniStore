package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.StockException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the RMI interface for read access to the stock object.
 * @version 2.0
 */
public interface RemoteStockR_I extends Remote {
    boolean exists(String number) throws RemoteException, StockException;
    Product getDetails(String number) throws RemoteException, StockException;
    byte[] getImage(String number) throws RemoteException, StockException; // Change return type to byte[]
}
