package ci553.ministore.middle;

import java.util.List;

import ci553.ministore.catalogue.Product;

/**
 * Interface for read/write access to the stock list.
 *
 * @author Mike Smith University of Brighton
 * @version 2.0
 */

public interface StockReadWriter extends StockReader {
    /**
     * Customer buys stock,
     * stock level is thus decremented by amount bought.
     *
     * @param pNum   Product number
     * @param amount Quantity of product
     * @return StockNumber, Description, Price, Quantity
     * @throws middle.StockException if issue
     */
    boolean buyStock(String pNum, int amount) throws StockException;

    /**
     * Adds stock (Restocks) to store.
     *
     * @param pNum   Product number
     * @param amount Quantity of product
     * @throws middle.StockException if issue
     */
    void addStock(String pNum, int amount) throws StockException;

    /**
     * Modifies Stock details for a given product number.
     * Information modified: Description, Price
     *
     * @param detail Replace with this version of product
     * @throws middle.StockException if issue
     */
    void modifyStock(Product detail) throws StockException;

    /**
     * Sets the stock quantity for a product.
     *
     * @param productNum The product number
     * @param quantity   The new stock quantity
     * @throws StockException if an error occurs while setting the stock
     */
    void setStock(String productNum, int quantity) throws StockException;

    /**
     * Retrieves the list of all products.
     *
     * @return List of products
     * @throws StockException if an error occurs while retrieving the products
     */
    List<Product> getProducts() throws StockException;

    /**
     * Adds a new product to the stock.
     *
     * @param product The product to add
     * @throws StockException if an error occurs while adding the product
     */
    void addProduct(Product product) throws StockException;

    /**
     * Updates the image path for a product
     *
     * @param productNum The product number
     * @param imagePath  The new image path
     * @throws StockException if an error occurs
     */
    void updateProductImage(String productNum, String imagePath) throws StockException;

    /**
     * Deletes a product from the stock.
     *
     * @param productNum The product number
     * @throws StockException if an error occurs while deleting the product
     */
    void deleteProduct(String productNum) throws StockException;
}
