package ci553.ministore.catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A collection of products, used to record the products that are to be wished
 * to be purchased.
 * This class extends ArrayList to manage a list of Product objects.
 *
 * @version 2.3
 */
@SuppressWarnings("unused")
public class Basket extends ArrayList<Product> implements Serializable {
    private static final long serialVersionUID = 1;
    private int theOrderNum; // Order number
    private static int nextOrderNumber = 1;

    /**
     * Constructor for a basket which is used to represent a customer order/wish
     * list.
     * Initializes the order number with the next available order number.
     */
    public Basket() {
        theOrderNum = nextOrderNumber; // Initialize with the next order number
    }

    /**
     * Sets the order number for this basket.
     *
     * @param anOrderNum The order number to set
     */
    public void setOrderNum(int anOrderNum) {
        theOrderNum = anOrderNum;
    }

    /**
     * Gets the current order number of this basket.
     *
     * @return The order number of this basket
     */
    public int getOrderNum() {
        return theOrderNum;
    }

    /**
     * Gets the next available order number without incrementing it.
     *
     * @return The next order number that will be assigned
     */
    public static int getNextOrderNumber() {
        return nextOrderNumber;
    }

    /**
     * Increments the next order number.
     * Called after an order is completed to prepare for the next order.
     */
    public static void incrementOrderNumber() {
        nextOrderNumber++;
    }

    /**
     * Adds a product to the Basket.
     * Product is appended to the end of the existing products in the basket.
     *
     * @param pr A product to be added to the basket
     * @return true if successfully adds the product
     */
    @Override
    public boolean add(Product pr) {
        return super.add(pr); // Call add in ArrayList
    }

    /**
     * Removes the last product added to the Basket.
     */
    public void removeLastItem() {
        if (!isEmpty()) {
            remove(size() - 1); // Remove the last item in the list
        }
    }

    /**
     * Removes a specific product from the Basket by product number.
     *
     * @param productNum The product number of the product to remove
     */
    public void removeByProductNum(String productNum) {
        removeIf(product -> product.getProductNum().equals(productNum)); // Remove product if product number matches
    }

    /**
     * Merges products with the same product number into a single product with the
     * combined quantity.
     *
     * @return a list of merged products
     */
    private ArrayList<Product> mergeProducts() {
        Map<String, Product> productMap = new HashMap<>();
        for (Product pr : this) {
            if (productMap.containsKey(pr.getProductNum())) {
                Product existingProduct = productMap.get(pr.getProductNum());
                existingProduct.setQuantity(existingProduct.getQuantity() + pr.getQuantity());
            } else {
                productMap.put(pr.getProductNum(),
                        new Product(pr.getProductNum(), pr.getDescription(), pr.getPrice(), pr.getQuantity()));
            }
        }
        return new ArrayList<>(productMap.values());
    }

    /**
     * Returns a description of the products in the basket suitable for printing.
     * Sorted by product number.
     *
     * @return a string description of the basket products
     */
    public String getDetails() {
        Locale uk = Locale.UK;
        StringBuilder sb = new StringBuilder(256);
        Formatter fr = new Formatter(sb, uk);
        String csign = (Currency.getInstance(uk)).getSymbol();
        double total = 0.00;
        if (theOrderNum != 0)
            fr.format("Order number: %03d\n", theOrderNum);

        // Merge products with the same product number
        ArrayList<Product> mergedProducts = mergeProducts();
        // Sort products by product number
        mergedProducts.sort((p1, p2) -> p1.getProductNum().compareTo(p2.getProductNum()));

        if (!mergedProducts.isEmpty()) {
            for (Product pr : mergedProducts) {
                int number = pr.getQuantity();
                fr.format("%-7s", pr.getProductNum());
                fr.format("%-14.14s ", pr.getDescription());
                fr.format("(%3d) ", number);
                fr.format("%s%7.2f", csign, pr.getPrice() * number);
                fr.format("\n");
                total += pr.getPrice() * number;
            }
            fr.format("----------------------------\n");
            fr.format("Total                       ");
            fr.format("%s%7.2f\n", csign, total);
            fr.close();
        }
        return sb.toString();
    }

    /**
     * Gets the quantity of a specific product in the basket by product number.
     *
     * @param productNum The product number to check
     * @return The quantity of the product in the basket
     */
    public int getProductQuantity(String productNum) {
        return stream()
                .filter(p -> p.getProductNum().equals(productNum))
                .mapToInt(Product::getQuantity)
                .sum();
    }

    /**
     * Removes a specific quantity of a product from the Basket.
     *
     * @param productNum       The product number to remove
     * @param quantityToRemove The quantity to remove
     */
    public void removeQuantityByProductNum(String productNum, int quantityToRemove) {
        int remainingToRemove = quantityToRemove;

        // Iterate through the basket in reverse order to remove the specified quantity
        for (int i = size() - 1; i >= 0 && remainingToRemove > 0; i--) {
            Product p = get(i);
            if (p.getProductNum().equals(productNum)) {
                if (p.getQuantity() <= remainingToRemove) {
                    remainingToRemove -= p.getQuantity();
                    remove(i); // Remove the product if its quantity is less than or equal to the remaining
                               // quantity to remove
                } else {
                    p.setQuantity(p.getQuantity() - remainingToRemove); // Reduce the product quantity
                    remainingToRemove = 0;
                }
            }
        }
    }
}
