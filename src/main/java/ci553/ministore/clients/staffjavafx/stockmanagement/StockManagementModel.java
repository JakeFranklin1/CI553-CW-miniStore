package ci553.ministore.clients.staffjavafx.stockmanagement;

import java.io.ByteArrayInputStream;
import ci553.ministore.catalogue.Product;
import ci553.ministore.debug.DEBUG;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.middle.StockException;
import ci553.ministore.middle.StockReadWriter;

/**
 * Model class for the stock management interface.
 * Handles business logic and updates the view through properties.
 */
public class StockManagementModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final MiddleFactory middleFactory;
    private final StockReadWriter stockReader;
    private int currentQuantity = 1;
    private Image productImage;

    /**
     * Constructor for StockManagementModel.
     * Initializes the model with the provided MiddleFactory.
     *
     * @param mlf The MiddleFactory instance.
     */
    public StockManagementModel(MiddleFactory mlf) {
        try {
            this.middleFactory = mlf; // Store middleware factory
            this.stockReader = mlf.makeStockReadWriter();
            DEBUG.trace("StockManagementModel: StockReader created successfully");
        } catch (Exception e) {
            DEBUG.error("StockManagementModel: Failed to create stock reader: %s", e.getMessage());
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
     * Gets the product image.
     *
     * @return The product image.
     */
    public Image getProductImage() {
        return productImage;
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

    /**
     * Adds stock to the given product number.
     * Updates the reply property with the updated product details or an error
     * message.
     *
     * @param productNum The product number to add stock to.
     */
    public void doAdd(String productNum) {
        try {
            productNum = productNum.trim();
            if (stockReader.exists(productNum)) {
                // Get current product details
                Product product = stockReader.getDetails(productNum);

                // Add stock
                stockReader.addStock(productNum, currentQuantity);

                // Get updated details
                Product updatedProduct = stockReader.getDetails(productNum);

                String formattedText = String.format("""
                        Added %d units to stock
                        Product: %s
                        Previous quantity: %d
                        New quantity: %d""",
                        currentQuantity,
                        product.getDescription(),
                        product.getQuantity(),
                        updatedProduct.getQuantity());

                reply.set(formattedText);

                // Update image display
                byte[] imgBytes = stockReader.getImage(productNum);
                if (imgBytes != null) {
                    productImage = new Image(new ByteArrayInputStream(imgBytes));
                }
            } else {
                reply.set("Error: Unknown product number " + productNum);
                productImage = null;
            }
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::doAdd\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
            productImage = null;
        }
    }

    /**
     * Corrects the stock for the given product number.
     * Updates the reply property with the updated product details or an error
     * message.
     *
     * @param productNum The product number to correct stock for.
     * @param newStock   The new stock quantity.
     */
    public void doCorrectStock(String productNum, int newStock) {
        try {
            stockReader.setStock(productNum, newStock);

            Product updatedProduct = stockReader.getDetails(productNum);
            String formattedText = String.format("""
                    Corrected stock for product: %s
                    New quantity: %d""",
                    updatedProduct.getDescription(),
                    updatedProduct.getQuantity());

            reply.set(formattedText);

            // Update image display
            byte[] imgBytes = stockReader.getImage(productNum);
            if (imgBytes != null) {
                productImage = new Image(new ByteArrayInputStream(imgBytes));
            }
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::doCorrectStock\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
            productImage = null;
        }
    }

    /**
     * Creates a new product with the given description, price, and quantity.
     * Updates the reply property with the new product details or an error message.
     *
     * @param description The description of the new product.
     * @param price       The price of the new product.
     * @param quantity    The quantity of the new product.
     */
    public void doNewProduct(String description, double price, int quantity) {
        try {
            // Generate a new product number
            String productNum = generateNewProductNumber();

            // Create a new product with placeholder image
            Product newProduct = new Product(productNum, description, price, quantity);

            // Add the new product to the database
            stockReader.modifyStock(newProduct);

            String formattedText = String.format("""
                    Added new product:
                    Product Number: %s
                    Description: %s
                    Price: %.2f
                    Quantity: %d""",
                    newProduct.getProductNum(),
                    newProduct.getDescription(),
                    newProduct.getPrice(),
                    newProduct.getQuantity());

            reply.set(formattedText);

            // Update image display (if applicable)
            productImage = null;
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::doNewProduct\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
            productImage = null;
        }
    }

    /**
     * Generates a new product number based on existing products.
     *
     * @return The new product number.
     * @throws StockException If an error occurs while generating the product
     *                        number.
     */
    private String generateNewProductNumber() throws StockException {
        // Generate a new product number based on existing products
        int maxProductNum = 0;
        for (Product product : stockReader.getProducts()) {
            int productNum = Integer.parseInt(product.getProductNum());
            if (productNum > maxProductNum) {
                maxProductNum = productNum;
            }
        }
        return String.format("%04d", maxProductNum + 1);
    }

    /**
     * Updates the product image for the given product number.
     * Updates the reply property with the result or an error message.
     *
     * @param productNum The product number to update the image for.
     * @param imagePath  The path to the new image.
     */
    public void updateProductImage(String productNum, String imagePath) {
        try {
            // Update image path in database
            stockReader.updateProductImage(productNum, imagePath);

            String formattedText = String.format("Updated image for product %s", productNum);
            reply.set(formattedText);

            // Update image display
            byte[] imgBytes = stockReader.getImage(productNum);
            if (imgBytes != null) {
                productImage = new Image(new ByteArrayInputStream(imgBytes));
            }
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::updateProductImage\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
            productImage = null;
        }
    }

    /**
     * Finishes the stock management process.
     * Updates the reply property with a completion message.
     */
    public void doFinish() {
        // Implement the logic for finishing the stock management
        reply.set("Finished stock management.");
    }

    /**
     * Gets the StockReadWriter instance.
     *
     * @return The StockReadWriter instance.
     */
    public StockReadWriter getStockReader() {
        return stockReader;
    }

    /**
     * Validates the given product number.
     * Updates the reply property with an error message if the product number is
     * invalid.
     *
     * @param productNum The product number to validate.
     * @return True if the product number is valid, false otherwise.
     */
    public boolean validateProduct(String productNum) {
        try {
            if (stockReader.exists(productNum)) {
                return true;
            } else {
                reply.set("Error: Unknown product number " + productNum + "\nPlease enter a valid product number.");
                return false;
            }
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::validateProduct\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the product with the given product number.
     * Updates the reply property with the result or an error message.
     *
     * @param productNum The product number to delete.
     */
    public void doDeleteProduct(String productNum) {
        try {
            stockReader.deleteProduct(productNum);
            reply.set("Deleted product: " + productNum);
            productImage = null;
        } catch (StockException e) {
            DEBUG.error("StockManagementModel::doDeleteProduct\n%s", e.getMessage());
            reply.set("System Error: " + e.getMessage());
        }
    }
}
