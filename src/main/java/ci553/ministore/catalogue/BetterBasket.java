package ci553.ministore.catalogue;

import java.util.Currency;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

/**
 * Enhanced version of Basket with additional functionalities.
 */
public class BetterBasket extends Basket {
    private static int nextOrderNumber = 1; // Static counter for order numbers - this is for getDetails

    /**
     * Constructor initializes with next order number
     */
    public BetterBasket() {
        super();
        setOrderNum(nextOrderNumber++); // Automatically set next order number
    }

    /**
     * Removes the last product added to the Basket
     */
    public void removeLastItem() {
        if (!isEmpty()) {
            remove(size() - 1); // Remove the last product
        }
    }

    /**
     * Removes a specific product from the Basket by product number
     */
    public void removeByProductNum(String productNum) {
        removeIf(product -> product.getProductNum().equals(productNum));
    }

    /**
     * Merges products with the same product number
     */
    private ArrayList<Product> mergeProducts() {
        Map<String, Product> productMap = new HashMap<>();
        // Iterate through the products
        for (Product pr : this) {
            // If the product number is already in the map
            if (productMap.containsKey(pr.getProductNum())) {
                // Get the existing product and add the quantity
                Product existingProduct = productMap.get(pr.getProductNum());
                existingProduct.setQuantity(existingProduct.getQuantity() + pr.getQuantity());
            } else {
                // Add the product to the map
                productMap.put(pr.getProductNum(),
                        new Product(pr.getProductNum(), pr.getDescription(), pr.getPrice(), pr.getQuantity()));
            }
        }
        return new ArrayList<>(productMap.values());
    }

    /**
     * Returns a description of the products, sorted by product number
     */
    @Override
    public String getDetails() {
        Locale uk = Locale.UK;
        StringBuilder sb = new StringBuilder(256);
        Formatter fr = new Formatter(sb, uk);
        String csign = (Currency.getInstance(uk)).getSymbol();
        double total = 0.00;

        // Always show order number
        fr.format("Order number: %03d\n", getOrderNum());

        // Merge duplicate products and combine their quantities
        // This creates a new list where products with the same number are combined
        ArrayList<Product> mergedProducts = mergeProducts();

        // Sort products by their product number for consistent display
        // Uses a lambda comparison function:
        // - p1, p2: two products being compared
        // - getProductNum(): gets the product number string
        // - compareTo(): compares the strings
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
     * Gets quantity of a specific product
     */
    public int getProductQuantity(String productNum) {
        return stream()
                .filter(p -> p.getProductNum().equals(productNum))
                .mapToInt(Product::getQuantity)
                .sum();
    }

    /**
     * Removes specific quantity of a product
     */
    public void removeQuantityByProductNum(String productNum, int quantityToRemove) {
        int remainingToRemove = quantityToRemove;

        // Iterate backwards to remove from the end
        for (int i = size() - 1; i >= 0 && remainingToRemove > 0; i--) {
            Product p = get(i);
            // If the product number matches
            if (p.getProductNum().equals(productNum)) {
                if (p.getQuantity() <= remainingToRemove) {
                    remainingToRemove -= p.getQuantity();
                    // Remove the product from the basket
                    remove(i);
                } else {
                    // Reduce the quantity of the product
                    p.setQuantity(p.getQuantity() - remainingToRemove);
                    remainingToRemove = 0;
                }
            }
        }
    }
}
