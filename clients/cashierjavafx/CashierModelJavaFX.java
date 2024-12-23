package clients.cashierjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReadWriter;
import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;

public class CashierModelJavaFX {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final StockReadWriter stockReader;
    private Basket basket;
    private int nextOrderNumber = 1; // Start with order number 1
    private int currentQuantity = 1; // Default quantity to add to the order

    public CashierModelJavaFX(MiddleFactory mf) {
        try {
            this.stockReader = mf.makeStockReadWriter(); // Change to makeStockReadWriter
            DEBUG.trace("CashierModelJavaFX: StockReader created successfully");
            this.basket = new Basket();
        } catch (Exception e) {
            DEBUG.error("CashierModelJavaFX: Failed to create stock reader: %s", e.getMessage());
            throw new RuntimeException("Failed to create stock reader", e);
        }
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
                    // Try to buy the stock first
                    boolean stockBought = stockReader.buyStock(productNum, currentQuantity);

                    if (stockBought) {
                        product.setQuantity(currentQuantity);
                        basket.add(product);
                        basket.setOrderNum(nextOrderNumber);
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
        // Implement purchase logic here
        reply.set("Purchase completed. Order Number: " + basket.getOrderNum());
        nextOrderNumber++;
        clearBasket();
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

    public void clearBasket() {
        addStockBackToDatabase();
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
