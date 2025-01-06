package ci553.ministore.util;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ci553.ministore.catalogue.Product;
import ci553.ministore.clients.cashierjavafx.CashierModel;

import java.util.Optional;

/**
 * Utility class for creating various dialogs used in the MiniStore application.
 * Provides static methods to show different types of dialogs for user input and confirmation.
 */
public final class DialogFactory {

    // Private constructor to prevent instantiation
    private DialogFactory() {
        // Prevent instantiation
    }

    /**
     * Shows a dialog to input a product description.
     *
     * @return An Optional containing the entered description, or empty if cancelled
     */
    public static Optional<String> showDescriptionDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Product");
        dialog.setHeaderText("Enter the product description:");
        dialog.setContentText("Description:");
        return dialog.showAndWait();
    }

    /**
     * Shows a dialog to input a product price.
     *
     * @return An Optional containing the entered price, or empty if cancelled
     */
    public static Optional<String> showPriceDialog() {
        TextInputDialog dialog = new TextInputDialog("0.00");
        dialog.setTitle("New Product");
        dialog.setHeaderText("Enter the product price:");
        dialog.setContentText("Price:");
        return dialog.showAndWait();
    }

    /**
     * Shows a dialog to input a product quantity.
     *
     * @param price The price of the product
     * @return An Optional containing the entered quantity, or empty if cancelled
     */
    public static Optional<String> showQuantityDialog(double price) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("New Product");
        dialog.setHeaderText(String.format("Enter quantity to add for £%.2f product:", price));
        dialog.setContentText("Quantity:");
        return dialog.showAndWait();
    }

    /**
     * Shows a dialog to correct the stock level of a product.
     *
     * @param product The product to correct the stock for
     * @return An Optional containing the new stock quantity, or empty if cancelled
     */
    public static Optional<String> showCorrectStockDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getQuantity()));
        dialog.setTitle("Correct Stock");
        dialog.setHeaderText(String.format("Correct stock for product: %s\nCurrent stock level: %d",
                product.getDescription(), product.getQuantity()));
        dialog.setContentText("New stock quantity:");
        return dialog.showAndWait();
    }

    /**
     * Shows a dialog to prompt the user for a quantity.
     *
     * @return An Optional containing the entered quantity, or empty if cancelled or invalid
     */
    public static Optional<Integer> showQuantityPromptDialog() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Quantity");
        dialog.setHeaderText("Enter the quantity of the product to add to the order:");
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Optional.of(Integer.parseInt(result.get()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Shows a dialog to remove an item from the order.
     *
     * @param model The CashierModel to interact with
     * @return An Optional containing a Pair of product number and quantity to remove, or empty if cancelled or invalid
     */
    public static Optional<Pair<String, Integer>> showRemoveItemDialog(CashierModel model) {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Remove Item");
        dialog.setHeaderText("Remove items from the order:");

        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField productNum = new TextField();
        TextField quantity = new TextField();
        Label currentQuantityLabel = new Label("");

        grid.add(new Label("Product Number:"), 0, 0);
        grid.add(productNum, 1, 0);
        grid.add(new Label("Quantity to Remove:"), 0, 1);
        grid.add(quantity, 1, 1);
        grid.add(currentQuantityLabel, 1, 2);

        // Update current quantity label based on product number input
        productNum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int inBasket = model.getProductQuantityInBasket(newValue);
                currentQuantityLabel.setText(String.format("Current quantity in order: %d", inBasket));
            } else {
                currentQuantityLabel.setText("");
            }
        });

        dialog.getDialogPane().setContent(grid);

        // Convert dialog result to Pair of product number and quantity
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
                try {
                    String pNum = productNum.getText();
                    int qty = Integer.parseInt(quantity.getText());
                    return new Pair<>(pNum, qty);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Shows a dialog to add stock to a product.
     *
     * @param product The product to add stock to
     * @return An Optional containing the quantity to add, or empty if cancelled or invalid
     */
    public static Optional<Integer> showAddStockDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add Stock");
        dialog.setHeaderText(String.format("""
                New stock arrived for product: %s?
                Current stock level: %d
                Enter quantity to add:""",
                product.getDescription(),
                product.getQuantity()));
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int quantity = Integer.parseInt(result.get());
                return quantity > 0 ? Optional.of(quantity) : Optional.empty();
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Shows a dialog to correct the stock level of a product.
     *
     * @param product The product to correct the stock for
     * @return An Optional containing the new stock quantity, or empty if cancelled or invalid
     */
    public static Optional<Integer> showStockCorrectionDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getQuantity()));
        dialog.setTitle("Correct Stock");
        dialog.setHeaderText(String.format("Correct stock for product: %s\nCurrent stock level: %d",
                product.getDescription(), product.getQuantity()));
        dialog.setContentText("New stock quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newStock = Integer.parseInt(result.get());
                return newStock >= 0 ? Optional.of(newStock) : Optional.empty();
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Shows a confirmation dialog to delete a product.
     *
     * @param productNum The product number to delete
     * @return true if the user confirmed the deletion, false otherwise
     */
    public static boolean showDeleteConfirmationDialog(String productNum) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText("Product Number: " + productNum);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
