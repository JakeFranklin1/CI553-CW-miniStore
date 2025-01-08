package ci553.ministore.catalogue;

import java.io.Serializable;

/**
 * Used to hold the following information about
 * a product: Product number, Description, Price, Stock level.
 *
 * @version 2.0
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 20092506;
    private String theProductNum; // Product number
    private String theDescription; // Description of product
    private double thePrice; // Price of product
    private int theQuantity; // Quantity involved

    /**
     * Construct a product details
     *
     * @param aProductNum  Product number
     * @param aDescription Description of product
     * @param aPrice       The price of the product
     * @param aQuantity    The Quantity of the product involved
     */
    public Product(String aProductNum, String aDescription,
            double aPrice, int aQuantity) {
        theProductNum = aProductNum; // Product number
        theDescription = aDescription; // Description of product
        thePrice = aPrice; // Price of product
        theQuantity = aQuantity; // Quantity involved
    }

    /**
     * Returns the product number.
     *
     * @return the product number
     */
    public String getProductNum() {
        return theProductNum;
    }

    /**
     * Returns the description of the product.
     *
     * @return the description of the product
     */
    public String getDescription() {
        return theDescription;
    }

    /**
     * Returns the price of the product.
     *
     * @return the price of the product
     */
    public double getPrice() {
        return thePrice;
    }

    /**
     * Returns the quantity of the product.
     *
     * @return the quantity of the product
     */
    public int getQuantity() {
        return theQuantity;
    }

    /**
     * Sets the product number.
     *
     * @param aProductNum the new product number
     */
    public void setProductNum(String aProductNum) {
        theProductNum = aProductNum;
    }

    /**
     * Sets the description of the product.
     *
     * @param aDescription the new description of the product
     */
    public void setDescription(String aDescription) {
        theDescription = aDescription;
    }

    /**
     * Sets the price of the product.
     *
     * @param aPrice the new price of the product
     */
    public void setPrice(double aPrice) {
        thePrice = aPrice;
    }

    /**
     * Sets the quantity of the product.
     *
     * @param aQuantity the new quantity of the product
     */
    public void setQuantity(int aQuantity) {
        theQuantity = aQuantity;
    }
}
