package clients.customerjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import middle.MiddleFactory;
import middle.StockReader;
import middle.StockException;

import java.io.ByteArrayInputStream;

import catalogue.Product;
import debug.DEBUG;

public class CustomerModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private Image productImage;
    private final StockReader stockReader;
    private final MiddleFactory middleFactory;

    public CustomerModel(MiddleFactory mf) {
        try {
            this.middleFactory = mf; // Store middleware factory
            this.stockReader = mf.makeStockReader();
            DEBUG.trace("CustomerModel: StockReader created successfully");
        } catch (Exception e) {
            DEBUG.error("CustomerModel: Failed to create stock reader: %s", e.getMessage());
            throw new RuntimeException("Failed to create stock reader", e);
        }
    }

    // Getter for MiddleFactory
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
                    String stockWarning = product.getQuantity() < 5 ? "\nWarning: Low Stock!" : "";

                    String formattedText = String.format("""
                            Product Number: %s
                            Description: %s
                            Price: £%.2f
                            Quantity in Stock: %d%s""",
                            productNum,
                            product.getDescription(),
                            product.getPrice(),
                            product.getQuantity(),
                            stockWarning);

                    reply.set(formattedText);

                    byte[] imgBytes = stockReader.getImage(productNum);
                    if (imgBytes != null) {
                        productImage = new Image(new ByteArrayInputStream(imgBytes));
                    } else {
                        productImage = null;
                    }
                } else {
                    reply.set("Product: " + product.getDescription() + " is currently out of stock");
                    productImage = null;
                }
            } else {
                reply.set("Error: Unknown product number " + productNum);
                productImage = null;
            }
        } catch (StockException e) {
            reply.set("System Error: " + e.getMessage());
            productImage = null;
        }
    }
}
