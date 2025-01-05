package ci553.ministore.clients.cashierjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.middle.StockException;
import ci553.ministore.middle.StockReadWriter;
import ci553.ministore.catalogue.Basket;
import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import ci553.ministore.middle.OrderProcessing;

/**
 * Model class for the cashier interface.
 * Handles business logic and updates the view through properties.
 */
public class CashierModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final StockReadWriter stockReader;
    private final MiddleFactory middleFactory;
    private Basket basket;
    private int currentQuantity = 1;

    /**
     * Constructor for CashierModel.
     * Initializes the model with the provided MiddleFactory.
     *
     * @param mf The MiddleFactory instance.
     */
    public CashierModel(MiddleFactory mf) {
        try {
            this.middleFactory = mf; // Store middleware factory
            this.stockReader = mf.makeStockReadWriter();
            DEBUG.trace("CashierModelJavaFX: StockReader created successfully");
            this.basket = new Basket();
        } catch (Exception e) {
            DEBUG.error("CashierModelJavaFX: Failed to create stock reader: %s", e.getMessage());
            throw new RuntimeException("Failed to create stock reader", e);
        }
    }

    /**
     * Gets the MiddleFactory instance.
     *
     * @return The MiddleFactory instance.
     */
    public MiddleFactory getMiddleFactory() {
        return middleFactory;
    }

    /**
     * Gets the message property for binding.
     *
     * @return The message property.
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Gets the reply property for binding.
     *
     * @return The reply property.
     */
    public StringProperty replyProperty() {
        return reply;
    }

    /**
     * Sets the current quantity for the product.
     *
     * @param quantity The quantity to set.
     */
    public void setCurrentQuantity(int quantity) {
        this.currentQuantity = quantity;
    }

    /**
     * Gets the current basket.
     * 
     * @return The current basket.
     */
    public Basket getBasket() {
        return basket;
    }

    /**
     * Checks the stock for the given product number.
     * Updates the reply property with the product details or an error message.
     *
     * @param productNum The product number to check.
     */
    public void doCheck(String productNum) {
        try {
            if (stockReader.exists(productNum)) {
                Product product = stockReader.getDetails(productNum);
                if (product.getQuantity() > 0) {
                    String formattedText = String.format("""
                            Product Number: %s
                            Description: %s
                            Price: £%.2f
                            Quantity in Stock: %d""",
                            productNum,
                            product.getDescription(),
                            product.getPrice(),
                            product.getQuantity());

                    reply.set(formattedText);
                } else {
                    reply.set("Product: " + product.getDescription() + " is currently out of stock");
                }
            } else {
                reply.set("Error: Unknown product number " + productNum);
            }
        } catch (StockException e) {
            reply.set("System Error: " + e.getMessage());
        }
    }

    /**
     * Adds the product to the order.
     * Updates the reply property with the basket details or an error message.
     *
     * @param productNum The product number to add.
     */
    public void addToOrder(String productNum) {
        try {
            if (stockReader.exists(productNum)) {
                Product product = stockReader.getDetails(productNum);
                if (product.getQuantity() >= currentQuantity) {
                    boolean stockBought = stockReader.buyStock(productNum, currentQuantity);

                    if (stockBought) {
                        product.setQuantity(currentQuantity);
                        basket.add(product);
                        reply.set(basket.getDetails());
                    } else {
                        reply.set("Failed to purchase " + product.getDescription());
                    }
                } else {
                    reply.set("Product: " + product.getDescription() + " does not have enough stock");
                }
            } else {
                reply.set("Error: Unknown product number " + productNum);
            }
        } catch (StockException e) {
            reply.set("System Error: " + e.getMessage());
        }
    }

    /**
     * Completes the purchase and resets the basket.
     * Updates the reply property with the order number or an error message.
     */
    public void purchase() {
        try {
            OrderProcessing orderProcessing = middleFactory.makeOrderProcessing();
            // Set order number before sending
            basket.setOrderNum(Basket.getNextOrderNumber());
            // Send basket to order processing
            orderProcessing.newOrder(basket);

            int completedOrderNum = basket.getOrderNum();
            reply.set(String.format("Purchase completed. Order Number: %03d",
                    completedOrderNum));

            // Create new basket for next order
            basket = new Basket();
            Basket.incrementOrderNumber();
        } catch (Exception e) {
            DEBUG.error("CashierModelJavaFX::purchase\n%s", e.getMessage());
            reply.set("Error processing order: " + e.getMessage());
        }
    }

    /**
     * Adds stock back to the database for all products in the basket.
     */
    private void addStockBackToDatabase() {
        for (Product product : basket) {
            try {
                stockReader.addStock(product.getProductNum(), product.getQuantity());
            } catch (StockException e) {
                DEBUG.error("Failed to add stock back to database: %s", e.getMessage());
            }
        }
    }

    /**
     * Clears the basket and optionally adds stock back to the database.
     *
     * @param addStockBack Whether to add stock back to the database.
     */
    public void clearBasket(boolean addStockBack) {
        if (addStockBack) {
            addStockBackToDatabase();
        }
        basket.clear();
    }

    /**
     * Removes the last item added to the basket.
     * Updates the reply property with the basket details.
     */
    public void removeLastItem() {
        if (!basket.isEmpty()) {
            Product lastProduct = basket.get(basket.size() - 1);
            try {
                stockReader.addStock(lastProduct.getProductNum(), lastProduct.getQuantity());
            } catch (StockException e) {
                DEBUG.error("Failed to add stock back to database: %s", e.getMessage());
            }
            basket.removeLastItem();
            reply.set(basket.getDetails());
        }
    }

    /**
     * Removes a product from the basket by its product number.
     * Updates the reply property with the basket details.
     *
     * @param productNum The product number to remove.
     */
    public void removeItemByProductNum(String productNum) {
        int quantity = basket.getProductQuantity(productNum);
        if (quantity > 0) {
            try {
                stockReader.addStock(productNum, quantity);
            } catch (StockException e) {
                DEBUG.error("Failed to add stock back to database: %s", e.getMessage());
            }
            basket.removeByProductNum(productNum);
            reply.set(basket.getDetails());
        }
    }

    /**
     * Gets the quantity of a product in the basket by its product number.
     *
     * @param productNum The product number to check.
     * @return The quantity of the product in the basket.
     */
    public int getProductQuantityInBasket(String productNum) {
        return basket.getProductQuantity(productNum);
    }

    /**
     * Removes a specified quantity of a product from the basket.
     * Updates the reply property with the basket details.
     *
     * @param productNum The product number to remove.
     * @param quantity   The quantity to remove.
     */
    public void removeQuantityFromBasket(String productNum, int quantity) {
        basket.removeQuantityByProductNum(productNum, quantity);
        try {
            stockReader.addStock(productNum, quantity);
        } catch (StockException e) {
            DEBUG.error("Failed to add stock back to database: %s", e.getMessage());
        }
        reply.set(basket.getDetails());
    }
}
