package ci553.ministore.remote;

import ci553.ministore.catalogue.Product;
import ci553.ministore.dbAccess.StockRW;
import ci553.ministore.middle.StockException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

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

    @Override
    public synchronized void setStock(String productNum, int quantity) throws RemoteException, StockException {
        aStockRW.setStock(productNum, quantity);
    }

    @Override
    public synchronized void addProduct(Product product) throws RemoteException, StockException {
        aStockRW.addProduct(product);
    }

    @Override
    public synchronized List<Product> getProducts() throws RemoteException, StockException {
        return aStockRW.getProducts();
    }

    @Override
    public synchronized void updateProductImage(String productNum, String imagePath)
            throws RemoteException, StockException {
        aStockRW.updateProductImage(productNum, imagePath);
    }

    @Override
    public synchronized void deleteProduct(String productNum) throws RemoteException, StockException {
        aStockRW.deleteProduct(productNum);
    }
}
