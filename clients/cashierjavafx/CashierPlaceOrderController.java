package clients.cashierjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import middle.MiddleFactory;
import debug.DEBUG;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;

import clients.start.MinistoreStartController;

public class CashierPlaceOrderController {
    @FXML
    private TextField message;
    @FXML
    private TextArea reply;
    @FXML
    private Button place_order_check_stock_btn;
    @FXML
    private Button place_order_add_to_order_btn;
    @FXML
    private Button place_order_purchase_btn;
    @FXML
    private Button place_order_clear_last_btn;
    @FXML
    private Button place_order_clear_order_btn;
    @FXML
    private Button place_order_remove_item_btn;
    @FXML
    private Button place_order_enter_btn;
    @FXML
    private Button place_order_clear_btn;
    @FXML
    private Button place_order_cancel_btn;
    @FXML
    private Button place_order_menu_btn;

    private CashierModelJavaFX model;
    private OrderState state;
    private MiddleFactory mlf;

    public void setMiddleFactory(MiddleFactory mf) {
        this.mlf = mf;
        try {
            // Initialize model with provided MiddleFactory
            model = new CashierModelJavaFX(mf);

            // Bind text properties
            message.textProperty().bindBidirectional(model.messageProperty());
            reply.textProperty().bind(model.replyProperty());

            // Initialize state
            state = OrderState.ENTERING_PRODUCT;

            // Add event handler for Enter key press
            message.setOnKeyPressed(this::handleKeyPress);

            // Add listener for message text changes
            message.textProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != null && !oldValue.equals(newValue)) {
                    state = OrderState.ENTERING_PRODUCT;
                }
            });

            // Add shutdown hook to handle closing the screen without pressing "purchase"
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (state != OrderState.PURCHASING) {
                    model.clearBasket(true);
                }
            }));

        } catch (Exception e) {
            DEBUG.error("CashierPlaceOrderController::setMiddleFactory\n%s", e.getMessage());
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

    private void process(String action) {
        DEBUG.trace("CashierPlaceOrderController::process: action = " + action);

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
            case "CLEAR ORDER":
                processClearOrder();
                break;
            case "MENU":
                processMenu();
                break;
            case "CANCEL":
                processCancel();
                break;
            case "CHECK STOCK":
                processCheck();
                break;
            case "ADD TO ORDER":
                processAddToOrder();
                break;
            case "PURCHASE":
                processPurchase();
                break;
            case "CLEAR LAST":
                processClearLast();
                break;
            case "REMOVE ITEM":
                processRemoveItem();
                break;
            default:
                DEBUG.trace("Unknown button: " + action);
                break;
        }
    }

    private void processEnter() {
        switch (state) {
            case ENTERING_PRODUCT:
                processCheck();
                break;
            case ADDING_TO_ORDER:
                processAddToOrder();
                break;
            default:
                break;
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
        model.clearBasket(true);
        model.messageProperty().set("");
        model.replyProperty().set("");
        state = OrderState.ENTERING_PRODUCT;
    }

    private void processCancel() {
        processClearOrder();
    }

    private void processCheck() {
        model.doCheck(message.getText());
        state = OrderState.ADDING_TO_ORDER;
    }

    private void processAddToOrder() {
        if (state != OrderState.ADDING_TO_ORDER) {
            model.replyProperty().set("Cannot add to order without checking stock first.");
            return;
        }

        // Prompt the user for the quantity
        Optional<Integer> quantity = promptForQuantity();
        if (quantity.isPresent()) {
            model.setCurrentQuantity(quantity.get());
            // Add the current product to the order
            model.addToOrder(message.getText());
            // The reply will be updated by the model's addToOrder method
            state = OrderState.ENTERING_PRODUCT;
        }
    }

    private Optional<Integer> promptForQuantity() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Quantity");
        dialog.setHeaderText("Enter the quantity of the product to add to the order:");
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Optional.of(Integer.parseInt(result.get()));
            } catch (NumberFormatException e) {
                DEBUG.error("Invalid quantity entered: %s", e.getMessage());
            }
        }
        return Optional.empty();
    }

    private void processPurchase() {
        // Complete the purchase
        model.purchase();
        model.messageProperty().set("");
        // model.replyProperty().set("");
        state = OrderState.ENTERING_PRODUCT;
    }

    private void processClearLast() {
        model.removeLastItem();
    }

    private void processRemoveItem() {
        // Create a custom dialog
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Remove Item");
        dialog.setHeaderText("Remove items from the order:");

        // Set the button types
        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        // Create the product number and quantity fields
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

        // Update current quantity label when product number changes
        productNum.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int inBasket = model.getProductQuantityInBasket(newValue);
                currentQuantityLabel.setText(String.format("Current quantity in order: %d", inBasket));
            } else {
                currentQuantityLabel.setText("");
            }
        });

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a pair when the remove button is clicked
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

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String pNum = pair.getKey();
            int qty = pair.getValue();
            if (qty > 0 && qty <= model.getProductQuantityInBasket(pNum)) {
                model.removeQuantityFromBasket(pNum, qty);
            }
        });
    }

    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("CashierPlaceOrderController::processMenu\n%s", e.getMessage());
        }
    }

    public void addProductToOrder(String productNum) {
        model.setCurrentQuantity(1); // Default quantity to 1
        model.addToOrder(productNum);
        state = OrderState.ENTERING_PRODUCT;
    }
}
