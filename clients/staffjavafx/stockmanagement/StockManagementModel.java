package clients.staffjavafx.stockmanagement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import middle.MiddleFactory;

public class StockManagementModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final MiddleFactory middleFactory; 

    public StockManagementModel(MiddleFactory mlf) {
        this.middleFactory = mlf;
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

    public void doCheck(String message) {
        // Implement the logic for checking stock
        reply.set("Checking stock: " + message);
    }

    public void doAdd(String message) {
        // Implement the logic for adding stock
        reply.set("Adding stock: " + message);
    }

    public void doCorrect(String message) {
        // Implement the logic for correcting stock
        reply.set("Correcting stock: " + message);
    }

    public void doNewProduct(String message) {
        // Implement the logic for adding a new product
        reply.set("Adding new product: " + message);
    }

    public void doAddImage(String message) {
        // Implement the logic for adding an image
        reply.set("Adding image: " + message);
    }

    public void doFinish() {
        // Implement the logic for finishing the stock management
        reply.set("Finished stock management.");
    }
}
