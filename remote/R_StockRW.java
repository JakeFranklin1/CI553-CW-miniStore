package remote;

import catalogue.Product;
import dbAccess.StockRW;
import middle.StockException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implements Read/Write access to the stock list,
 * the stock list is held in a relational DataBase.
 * @version 2.1
 */
public class R_StockRW extends UnicastRemoteObject implements RemoteStockRW_I {
    private static final long serialVersionUID = 1;
    private StockRW aStockRW = null;

    public R_StockRW(String url) throws RemoteException, StockException {
        aStockRW = new StockRW();
    }

    public synchronized boolean exists(String pNum) throws RemoteException, StockException {
        return aStockRW.exists(pNum);
    }

    public synchronized Product getDetails(String pNum) throws RemoteException, StockException {
        return aStockRW.getDetails(pNum);
    }

    public synchronized byte[] getImage(String pNum) throws RemoteException, StockException {
        return aStockRW.getImage(pNum);
    }

    public synchronized boolean buyStock(String pNum, int amount) throws RemoteException, StockException {
        return aStockRW.buyStock(pNum, amount);
    }

    public synchronized void addStock(String pNum, int amount) throws RemoteException, StockException {
        aStockRW.addStock(pNum, amount);
    }

    public synchronized void modifyStock(Product product) throws RemoteException, StockException {
        aStockRW.modifyStock(product);
    }
}
