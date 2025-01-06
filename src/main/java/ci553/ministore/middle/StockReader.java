package ci553.ministore.middle;

import ci553.ministore.catalogue.Product;

/**
 * Interface for reading product information from the stock database.
 * Provides methods to check product existence, retrieve product details,
 * and fetch product images. This interface is part of the middleware layer
 * that separates the UI from the database implementation.
 */
public interface StockReader {

    /**
     * Checks if a product exists in the database.
     *
     * @param pNum The product number to check
     * @return true if the product exists, false otherwise
     * @throws StockException if there is an error accessing the database
     */
    boolean exists(String pNum) throws StockException;

    /**
     * Retrieves detailed product information from the database.
     *
     * @param pNum The product number to retrieve details for
     * @return A Product object containing the product details
     * @throws StockException if the product doesn't exist or there is a database error
     */
    Product getDetails(String pNum) throws StockException;

    /**
     * Retrieves the product image as a byte array.
     * Implementation should handle image loading from the database or file system.
     *
     * @param pNum The product number to retrieve the image for
     * @return byte array containing the image data
     * @throws StockException if the image cannot be found or loaded
     */
    byte[] getImage(String pNum) throws StockException;
}
