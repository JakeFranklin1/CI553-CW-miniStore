package clients.staffjavafx.stockmanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import middle.MiddleFactory;
import middle.StockException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import catalogue.Product;
import clients.staffjavafx.dashboard.DashboardController;
import clients.utils.DialogFactory;
import clients.utils.ImageHandler;
import debug.DEBUG;

public class StockManagementController {
    @FXML
    private Button stock_management_check_stock_btn;

    @FXML
    private Button stock_management_add_stock_btn;

    @FXML
    private Button stock_management_correct_stock_btn;

    @FXML
    private Button stock_management_new_product_btn;

    @FXML
    private Button stock_management_add_image_btn;

    @FXML
    private Button stock_management_delete_btn;

    @FXML
    private TextField stock_management_message;

    @FXML
    private TextArea stock_management_reply;

    @FXML
    private ImageView stock_image;

    private StockManagementModel model;
    private OrderState state;
    private MiddleFactory mlf;

    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
        try {
            // Initialize model with provided MiddleFactory
            model = new StockManagementModel(mlf);

            // Bind text properties
            stock_management_message.textProperty().bindBidirectional(model.messageProperty());
            stock_management_reply.textProperty().bind(model.replyProperty());

            // Initialize state
            state = OrderState.ENTERING_PRODUCT;

            // Add event handler for Enter key press
            stock_management_message.setOnKeyPressed(this::handleKeyPress);

            // Add listener for message text changes
            // stock_management_message.textProperty().addListener((observable, oldValue,
            // newValue) -> {
            // if (oldValue != null && !oldValue.equals(newValue)) {
            // if (state != OrderState.ENTERING_PRODUCT) {
            // state = OrderState.ENTERING_PRODUCT;
            // String currentReply = model.replyProperty().get();
            // model.replyProperty().set(currentReply + "\nProduct Number changed, please
            // check stock again.");
            // }
            // }
            // });

        } catch (Exception e) {
            DEBUG.error("StockManagementController::setMiddleFactory\n%s", e.getMessage());
        }
    }

    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();
        process(buttonText);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processEnter();
        }
    }

    private void processEnter() {
        switch (state) {
            case ENTERING_PRODUCT:
                processCheck();
                break;
            case MANAGING_STOCK:
                processAddStock();
                break;
            default:
                break;
        }
    }

    private void process(String action) {
        DEBUG.trace("StockManagementController::process: action = " + action);

        switch (action) {

            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "0":
            case "00":
                processNumber(action);
                break;
            case "ENTER":
                processEnter();
                break;
            case "CLEAR":
                processClear();
                break;
            case "CANCEL":
                processCancel();
                break;
            case "CHECK STOCK":
                processCheck();
                break;
            case "ADD STOCK":
                processAddStock();
                break;
            case "CORRECT":
                processCorrectStock();
                break;
            case "NEW PRODUCT":
                processNewProduct();
                break;
            case "ADD IMAGE":
                processAddImage();
                break;
            case "DELETE":
                processClearOrder();
                break;
            case "MENU":
                processMenu();
                break;
            default:
                DEBUG.trace("Unknown button: " + action);
                break;
        }
    }

    private void updateImage() {
        Image image = model.getProductImage();
        if (image != null) {
            stock_image.setImage(image);
        } else {
            stock_image.setImage(null);
        }
    }

    private void processNumber(String number) {
        String currentMessage = model.messageProperty().get();
        if (currentMessage == null) {
            currentMessage = "";
        }
        model.messageProperty().set(currentMessage + number);
    }

    private void processClear() {
        String currentMessage = model.messageProperty().get();
        if (currentMessage != null && currentMessage.length() > 0) {
            model.messageProperty().set(currentMessage.substring(0, currentMessage.length() - 1));
        }
    }

    private void processClearOrder() {
        // model.clearBasket(true);
        model.messageProperty().set("");
        model.replyProperty().set("");
        // state = OrderState.ENTERING_PRODUCT;
    }

    private void processCancel() {
        processClearOrder();
    }

    private void processCheck() {
        model.doCheck(stock_management_message.getText());
        updateImage();
        state = OrderState.MANAGING_STOCK;
    }

    private void processAddStock() {
        String productNum = stock_management_message.getText().trim();

        if (productNum.isEmpty()) {
            model.replyProperty().set("Please enter a product number first");
            return;
        }

        if (!model.validateProduct(productNum)) {
            return;
        }

        model.doCheck(productNum);
        state = OrderState.MANAGING_STOCK;
        Optional<Integer> quantity = promptForQuantity();

        if (quantity.isPresent()) {
            model.setCurrentQuantity(quantity.get());
            model.doAdd(productNum);
            updateImage();
            state = OrderState.ENTERING_PRODUCT;
        }
    }

    private Optional<Integer> promptForQuantity() {
        String productNum = stock_management_message.getText().trim();

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add Stock");

        try {
            // Get product details from stock reader
            Product product = model.getStockReader().getDetails(productNum);
            dialog.setHeaderText(String.format("""
                    New stock arrived for product: %s?
                    Current stock level: %d
                    Enter quantity to add:""",
                    product.getDescription(),
                    product.getQuantity()));

        } catch (StockException e) {
            dialog.setHeaderText("Enter quantity to add:");
        }

        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int quantity = Integer.parseInt(result.get());
                if (quantity > 0) {
                    return Optional.of(quantity);
                } else {
                    model.replyProperty().set("Quantity must be positive");
                }
            } catch (NumberFormatException e) {
                model.replyProperty().set("Invalid quantity entered");
            }
        }
        return Optional.empty();
    }

    private void processCorrectStock() {
        String productNum = stock_management_message.getText().trim();

        if (productNum.isEmpty()) {
            model.replyProperty().set("Please enter a product number first");
            return;
        }

        if (!model.validateProduct(productNum)) {
            return;
        }

        try {
            Product product = model.getStockReader().getDetails(productNum);
            int currentStock = product.getQuantity();

            // Create dialog to prompt for new stock quantity
            TextInputDialog dialog = new TextInputDialog(String.valueOf(currentStock));
            dialog.setTitle("Correct Stock");
            dialog.setHeaderText(String.format("Correct stock for product: %s\nCurrent stock level: %d",
                    product.getDescription(), currentStock));
            dialog.setContentText("New stock quantity:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int newStock = Integer.parseInt(result.get());
                    if (newStock >= 0) {
                        model.doCorrectStock(productNum, newStock);
                    } else {
                        model.replyProperty().set("Quantity must be non-negative");
                    }
                } catch (NumberFormatException e) {
                    model.replyProperty().set("Invalid quantity entered");
                }
            }
        } catch (StockException e) {
            model.replyProperty().set("System Error: " + e.getMessage());
        }
    }

    private void processNewProduct() {
        Optional<String> descriptionResult = DialogFactory.showDescriptionDialog();
        if (descriptionResult.isPresent() && !descriptionResult.get().trim().isEmpty()) {
            String description = descriptionResult.get().trim();

            Optional<String> priceResult = DialogFactory.showPriceDialog();
            if (priceResult.isPresent()) {
                try {
                    double price = Double.parseDouble(priceResult.get().trim());
                    price = Math.round(price * 100.0) / 100.0;

                    Optional<String> quantityResult = DialogFactory.showQuantityDialog(price);
                    if (quantityResult.isPresent()) {
                        try {
                            int quantity = Integer.parseInt(quantityResult.get().trim());
                            if (quantity > 0) {
                                model.doNewProduct(description, price, quantity);
                            } else {
                                model.replyProperty().set("Quantity must be positive");
                            }
                        } catch (NumberFormatException e) {
                            model.replyProperty().set("Invalid quantity entered");
                        }
                    }
                } catch (NumberFormatException e) {
                    model.replyProperty().set("Invalid price entered");
                }
            }
        } else {
            model.replyProperty().set("Description cannot be empty");
        }
    }

    private void processAddImage() {
        String productNum = stock_management_message.getText().trim();
        if (productNum.isEmpty() || !model.validateProduct(productNum)) {
            return;
        }

        File selectedFile = ImageHandler.showImageChooser(stock_management_message.getScene().getWindow());
        if (selectedFile != null) {
            try {
                ImageHandler.copyImageFile(selectedFile, productNum);
                model.updateProductImage(productNum, "images/Pic" + productNum + ".png");
                updateImage();
            } catch (IOException e) {
                model.replyProperty().set("Error copying image file: " + e.getMessage());
            }
        }
    }

    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_staff_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            DashboardController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) stock_management_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("StockManagementController::processMenu\n%s", e.getMessage());
        }
    }
}
