/**
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */

package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.StockException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Defines the RMI interface for read/write access to the stock object.
 *
 * @author Mike Smith University of Brighton
 * @version 2.0
 */

public interface RemoteStockRW_I
        extends RemoteStockR_I, Remote {
    boolean buyStock(String number, int amount)
            throws RemoteException, StockException;

    void addStock(String number, int amount)
            throws RemoteException, StockException;

    void modifyStock(Product detail)
            throws RemoteException, StockException;

    void setStock(String productNum, int quantity) throws RemoteException, StockException;

    void addProduct(Product product) throws RemoteException, StockException;

    List<Product> getProducts() throws RemoteException, StockException;

    void updateProductImage(String productNum, String imagePath)
            throws RemoteException, StockException;

    void deleteProduct(String productNum) throws RemoteException, StockException;
}
