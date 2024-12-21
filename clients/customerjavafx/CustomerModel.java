package clients.customerjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import middle.MiddleFactory;
import middle.StockReader;
import middle.StockException;
import catalogue.Product;
import debug.DEBUG;

public class CustomerModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private Image productImage;
    private final StockReader stockReader;

    public CustomerModel(MiddleFactory mf) {
        try {
            this.stockReader = mf.makeStockReader();
            DEBUG.trace("CustomerModel: StockReader created successfully");
        } catch (Exception e) {
            DEBUG.error("CustomerModel: Failed to create stock reader: %s", e.getMessage());
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

    // String getters/setters
    public String getMessage() {
        return message.get();
    }

    public void setMessage(String value) {
        message.set(value);
    }

    public String getReply() {
        return reply.get();
    }

    public void setReply(String value) {
        reply.set(value);
    }

    public Image getProductImage() {
        return productImage;
    }

    public void doCheck(String productNum) {
        try {
            if (stockReader.exists(productNum)) {
                Product product = stockReader.getDetails(productNum);
                if (product.getQuantity() > 0) {
                    reply.set(String.format("%s : %7.2f (%2d)", product.getDescription(), product.getPrice(), product.getQuantity()));
                } else {
                    reply.set(product.getDescription() + " not in stock");
                    productImage = null;
                }
            } else {
                reply.set("Unknown product number " + productNum);
                productImage = null;
            }
        } catch (StockException e) {
            reply.set("Error: " + e.getMessage());
        }
    }
}
