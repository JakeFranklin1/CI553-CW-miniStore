package ci553.ministore.clients.customerjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.middle.StockReader;
import ci553.ministore.middle.StockException;

import java.io.ByteArrayInputStream;

import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;

/**
 * Model class for the customer interface.
 * Handles business logic and updates the view through properties.
 */
public class CustomerModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private Image productImage;
    private final StockReader stockReader;
    private final MiddleFactory middleFactory;

    /**
     * Constructor for CustomerModel.
     * Initializes the model with the provided MiddleFactory.
     * 
     * @param mf The MiddleFactory instance.
     */
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
     * Gets the message value.
     * 
     * @return The message value.
     */
    public String getMessage() {
        return message.get();
    }

    /**
     * Sets the message value.
     * 
     * @param value The message value to set.
     */
    public void setMessage(String value) {
        message.set(value);
    }

    /**
     * Gets the reply value.
     * 
     * @return The reply value.
     */
    public String getReply() {
        return reply.get();
    }

    /**
     * Sets the reply value.
     * 
     * @param value The reply value to set.
     */
    public void setReply(String value) {
        reply.set(value);
    }

    /**
     * Gets the product image.
     * 
     * @return The product image.
     */
    public Image getProductImage() {
        return productImage;
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
                    // Add a warning if stock is low
                    String stockWarning = product.getQuantity() < 5 ? "\nWarning: Low Stock!" : "";

                    // Format product details
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

                    // Load product image
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
