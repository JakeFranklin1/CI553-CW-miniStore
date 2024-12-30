package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.dbAccess.StockR;
import ci553.ministore.middle.StockException;

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
