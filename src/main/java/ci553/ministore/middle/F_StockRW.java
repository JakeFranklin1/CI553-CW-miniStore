package middle;

/**
 * Facade for read/write access to the stock list.
 * The actual implementation of this is held on the middle tier.
 * The actual stock list is held in a relational DataBase on the
 * third tier.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */

import catalogue.Product;
import debug.DEBUG;
import remote.RemoteStockRW_I;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

/**
 * Setup connection to the middle tier
 */

public class F_StockRW extends F_StockR
        implements StockReadWriter {
    private RemoteStockRW_I aR_StockRW = null;
    private String theStockURL = null;

    public F_StockRW(String url) {
        super(url); // Not used
        theStockURL = url;
    }

    private void connect() throws StockException {
        try // Setup
        { // connection
            aR_StockRW = // Connect to
                    (RemoteStockRW_I) Naming.lookup(theStockURL);// Stub returned
        } catch (Exception e) // Failure to
        { // attach to the
            aR_StockRW = null;
            throw new StockException("Com: " +
                    e.getMessage()); // object

        }
    }

    /**
     * Buys stock and hence decrements number in stock list
     *
     * @return StockNumber, Description, Price, Quantity
     * @throws StockException if remote exception
     */

    public boolean buyStock(String number, int amount)
            throws StockException {
        DEBUG.trace("F_StockRW:buyStock()");
        try {
            if (aR_StockRW == null)
                connect();
            return aR_StockRW.buyStock(number, amount);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Adds (Restocks) stock to the product list
     *
     * @param number Stock number
     * @param amount of stock
     * @throws StockException if remote exception
     */

    public void addStock(String number, int amount)
            throws StockException {
        DEBUG.trace("F_StockRW:addStock()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.addStock(number, amount);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    /**
     * Modifies Stock details for a given product number.
     * Information modified: Description, Price
     *
     * @param detail Stock details to be modified
     * @throws StockException if remote exception
     */

    public void modifyStock(Product detail)
            throws StockException {
        DEBUG.trace("F_StockRW:modifyStock()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.modifyStock(detail);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    @Override
    public void setStock(String productNum, int quantity) throws StockException {
        DEBUG.trace("F_StockRW:setStock()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.setStock(productNum, quantity);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    @Override
    public void addProduct(Product product) throws StockException {
        DEBUG.trace("F_StockRW:addProduct()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.addProduct(product);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    @Override
    public List<Product> getProducts() throws StockException {
        DEBUG.trace("F_StockRW:getProducts()");
        try {
            if (aR_StockRW == null)
                connect();
            return aR_StockRW.getProducts();
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    @Override
    public void updateProductImage(String productNum, String imagePath) throws StockException {
        DEBUG.trace("F_StockRW:updateProductImage()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.updateProductImage(productNum, imagePath);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }

    @Override
    public void deleteProduct(String productNum) throws StockException {
        DEBUG.trace("F_StockRW:deleteProduct()");
        try {
            if (aR_StockRW == null)
                connect();
            aR_StockRW.deleteProduct(productNum);
        } catch (RemoteException e) {
            aR_StockRW = null;
            throw new StockException("Net: " + e.getMessage());
        }
    }
}
