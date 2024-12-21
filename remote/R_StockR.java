// filepath: /c:/Users/jakef/OneDrive/Desktop/project/CI553-CW-miniStore/remote/R_StockR.java
package remote;

import catalogue.Product;
import dbAccess.StockR;
import middle.StockException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class R_StockR extends UnicastRemoteObject implements RemoteStockR_I {
    private static final long serialVersionUID = 1;
    private StockR aStockR = null;

    public R_StockR(String url) throws RemoteException, StockException {
        aStockR = new StockR();
    }

    public synchronized boolean exists(String pNum) throws RemoteException, StockException {
        return aStockR.exists(pNum);
    }

    public synchronized Product getDetails(String pNum) throws RemoteException, StockException {
        return aStockR.getDetails(pNum);
    }

    public synchronized byte[] getImage(String pNum) throws RemoteException, StockException { // Change return type to byte[]
        return aStockR.getImage(pNum);
    }
}
