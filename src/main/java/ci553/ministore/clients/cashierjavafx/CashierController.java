package ci553.ministore.clients.cashierjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.util.DialogFactory;
import ci553.ministore.debug.DEBUG;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import ci553.ministore.clients.start.MinistoreStartController;

public class CashierController {
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

    private CashierModel model;
    private OrderState state;
    private MiddleFactory mlf;

    public void setMiddleFactory(MiddleFactory mf) {
        this.mlf = mf;
        try {
            // Initialize model with provided MiddleFactory
            model = new CashierModel(mf);

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

            // Add keyboard event handler to the scene
            // Platform.runLater is used to ensure the scene is fully loaded
            // before adding the event handler. This is necessary because
            // I want the scene to handle key presses, not the text field.
            Platform.runLater(() -> {
                message.getScene().setOnKeyPressed(event -> {
                    String key = event.getText();
                    if (key.matches("[0-9]")) {
                        processNumber(key);
                        message.requestFocus();
                    }
                });
            });

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

        Optional<Integer> quantity = DialogFactory.showQuantityPromptDialog();
        if (quantity.isPresent()) {
            model.setCurrentQuantity(quantity.get());
            model.addToOrder(message.getText());
            state = OrderState.ENTERING_PRODUCT;
        }
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
        Optional<Pair<String, Integer>> result = DialogFactory.showRemoveItemDialog(model);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_start.fxml"));
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
