package clients.utils;

import javafx.scene.control.TextInputDialog;
import catalogue.Product;
import java.util.Optional;

public class DialogFactory {
    public static Optional<String> showDescriptionDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Product");
        dialog.setHeaderText("Enter the product description:");
        dialog.setContentText("Description:");
        return dialog.showAndWait();
    }

    public static Optional<String> showPriceDialog() {
        TextInputDialog dialog = new TextInputDialog("0.00");
        dialog.setTitle("New Product");
        dialog.setHeaderText("Enter the product price:");
        dialog.setContentText("Price:");
        return dialog.showAndWait();
    }

    public static Optional<String> showQuantityDialog(double price) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("New Product");
        dialog.setHeaderText(String.format("Enter quantity to add for £%.2f product:", price));
        dialog.setContentText("Quantity:");
        return dialog.showAndWait();
    }

    public static Optional<String> showCorrectStockDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getQuantity()));
        dialog.setTitle("Correct Stock");
        dialog.setHeaderText(String.format("Correct stock for product: %s\nCurrent stock level: %d",
                product.getDescription(), product.getQuantity()));
        dialog.setContentText("New stock quantity:");
        return dialog.showAndWait();
    }
}
