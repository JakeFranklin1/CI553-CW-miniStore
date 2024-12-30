package clients.cashierjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReadWriter;
import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.OrderProcessing;

public class CashierModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final StockReadWriter stockReader;
    private final MiddleFactory middleFactory;
    private Basket basket;
    private int currentQuantity = 1;

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

    public MiddleFactory getMiddleFactory() {
        return middleFactory;
    }

    // Properties for binding
    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty replyProperty() {
        return reply;
    }

    // Method to set the current quantity
    public void setCurrentQuantity(int quantity) {
        this.currentQuantity = quantity;
    }

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

    // Method to add stock back to the database
    private void addStockBackToDatabase() {
        for (Product product : basket) {
            try {
                stockReader.addStock(product.getProductNum(), product.getQuantity());
            } catch (StockException e) {
                DEBUG.error("Failed to add stock back to database: %s", e.getMessage());
            }
        }
    }

    public void clearBasket(boolean addStockBack) {
        if (addStockBack) {
            addStockBackToDatabase();
        }
        basket.clear();
    }

    // Method to remove the last item added to the basket
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

    public int getProductQuantityInBasket(String productNum) {
        return basket.getProductQuantity(productNum);
    }

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
