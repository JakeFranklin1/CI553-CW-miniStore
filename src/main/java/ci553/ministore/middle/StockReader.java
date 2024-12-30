package middle;

import catalogue.Product;

public interface StockReader {
    boolean exists(String pNum) throws StockException;
    Product getDetails(String pNum) throws StockException;
    byte[] getImage(String pNum) throws StockException; // Change return type to byte[]
}
