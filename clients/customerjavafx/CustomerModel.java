package clients.customerjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import middle.MiddleFactory;

public class CustomerModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private Image productImage;
    private final MiddleFactory middleFactory;

    public CustomerModel(MiddleFactory mf) {
        this.middleFactory = mf;
    }

    // Properties for binding
    public StringProperty messageProperty() { return message; }
    public StringProperty replyProperty() { return reply; }

    public void doCheck(String productNum) {
        // Existing check logic from original CustomerModel
        try {
            if (middleFactory.makeStockReader().exists(productNum)) {
                // Update properties instead of using Observer
                reply.set("Product found: " + productNum);
            } else {
                reply.set("Unknown product");
            }
        } catch (Exception e) {
            reply.set("Error: " + e.getMessage());
        }
    }
}
