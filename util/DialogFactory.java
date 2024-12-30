package util;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import catalogue.Product;
import clients.cashierjavafx.CashierModelJavaFX;

import java.util.Optional;

public final class DialogFactory {

    private DialogFactory() {
        // Prevent instantiation
    }

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

    public static Optional<Pair<String, Integer>> showRemoveItemDialog(CashierModelJavaFX model) {
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

        productNum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int inBasket = model.getProductQuantityInBasket(newValue);
                currentQuantityLabel.setText(String.format("Current quantity in order: %d", inBasket));
            } else {
                currentQuantityLabel.setText("");
            }
        });

        dialog.getDialogPane().setContent(grid);

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

}
