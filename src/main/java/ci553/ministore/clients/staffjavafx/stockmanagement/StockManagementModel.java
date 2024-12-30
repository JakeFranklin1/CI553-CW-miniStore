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

public class StockManagementModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final MiddleFactory middleFactory;
    private final StockReadWriter stockReader;
    private int currentQuantity = 1;
    private Image productImage;

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

    // Add getter for MiddleFactory
    public MiddleFactory getMiddleFactory() {
        return middleFactory;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty replyProperty() {
        return reply;
    }

    public Image getProductImage() {
        return productImage;
    }

    public void setCurrentQuantity(int quantity) {
        this.currentQuantity = quantity;
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

    // public void doUpdateImage(String productNum, String imagePath) {
    // try {
    // Product product = stockReader.getDetails(productNum);
    // product.setImagePath(imagePath);
    // stockReader.modifyStock(product);

    // String formattedText = String.format("""
    // Updated image for product: %s
    // New image path: %s""",
    // product.getDescription(),
    // imagePath);

    // reply.set(formattedText);

    // // Update image display
    // byte[] imgBytes = stockReader.getImage(productNum);
    // if (imgBytes != null) {
    // productImage = new Image(new ByteArrayInputStream(imgBytes));
    // }
    // } catch (StockException e) {
    // DEBUG.error("StockManagementModel::doUpdateImage\n%s", e.getMessage());
    // reply.set("System Error: " + e.getMessage());
    // productImage = null;
    // }
    // }

    public void updateProductImage(String productNum, String imagePath) {
        try {
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

    public void doFinish() {
        // Implement the logic for finishing the stock management
        reply.set("Finished stock management.");
    }

    public StockReadWriter getStockReader() {
        return stockReader;
    }

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
