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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

/**
 * Controller class for the cashier interface.
 * Handles user interactions and updates the model accordingly.
 */
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
    @FXML
    private ImageView stock_image;

    private CashierModel model;
    private OrderState state;
    private MiddleFactory mlf;

    /**
     * Sets the MiddleFactory instance and initializes the model.
     *
     * @param mf The MiddleFactory instance.
     */
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

    /**
     * Handles button actions and delegates to the appropriate method.
     *
     * @param event The ActionEvent triggered by the button press.
     */
    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();
        process(buttonText);
    }

    /**
     * Handles key press events for the message TextField.
     *
     * @param event The KeyEvent triggered by the key press.
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            processEnter();
        }
    }

    /**
     * Processes the action based on the button text.
     *
     * @param action The action to be processed.
     */
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

    /**
     * Processes the Enter key press based on the current state.
     */
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

    /**
     * Processes number input and appends it to the message.
     *
     * @param number The number to be appended.
     */
    private void processNumber(String number) {
        String currentMessage = model.messageProperty().get();
        if (currentMessage == null) {
            currentMessage = "";
        }
        model.messageProperty().set(currentMessage + number);
    }

    /**
     * Clears the last character from the message.
     */
    private void processClear() {
        String currentMessage = model.messageProperty().get();
        if (currentMessage != null && currentMessage.length() > 0) {
            model.messageProperty().set(currentMessage.substring(0, currentMessage.length() - 1));
        }
    }

    /**
     * Clears the entire order and resets the state.
     */
    private void processClearOrder() {
        model.clearBasket(true);
        model.messageProperty().set("");
        model.replyProperty().set("");
        state = OrderState.ENTERING_PRODUCT;
    }

    /**
     * Cancels the current order by clearing it.
     */
    private void processCancel() {
        processClearOrder();
    }

    /**
     * Checks the stock for the product in the message.
     */
    private void processCheck() {
        model.doCheck(message.getText());
        updateImage();
        state = OrderState.ADDING_TO_ORDER;
    }

    /**
     * Adds the product to the order after checking stock.
     */
    private void processAddToOrder() {
        if (state != OrderState.ADDING_TO_ORDER) {
            model.replyProperty().set("Cannot add to order without checking stock first.");
            return;
        }

        Optional<Integer> quantity = DialogFactory.showQuantityPromptDialog();
        if (quantity.isPresent()) {
            model.setCurrentQuantity(quantity.get());
            model.addToOrder(message.getText());
            clearImage();
            state = OrderState.ENTERING_PRODUCT;
        }
    }

    /**
     * Completes the purchase and resets the state.
     * Will not proceed if basket is empty.
     */
    private void processPurchase() {
        // Check if basket is empty
        if (model.getBasket().isEmpty()) {
            model.replyProperty().set("Cannot complete purchase: Basket is empty");
            return;
        }

        // Complete the purchase
        model.purchase();
        model.messageProperty().set("");
        state = OrderState.ENTERING_PRODUCT;
    }

    /**
     * Clears the last item from the order.
     */
    private void processClearLast() {
        model.removeLastItem();
    }

    /**
     * Removes a specified quantity of a product from the order.
     */
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

    /**
     * Navigates back to the main menu.
     */
    private void processMenu() {
        try {
            // Clear basket before loading new scene
            model.clearBasket(true);

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

    /**
     * Adds a product to the order with a default quantity of 1.
     *
     * @param productNum The product number to be added.
     */
    public void addProductToOrder(String productNum) {
        model.setCurrentQuantity(1); // Default quantity to 1
        model.addToOrder(productNum);
        state = OrderState.ENTERING_PRODUCT;
    }

    /**
     * Updates the image view with the product image.
     */
    private void updateImage() {
        Image image = model.getProductImage();
        if (image != null) {
            stock_image.setImage(image);
        } else {
            stock_image.setImage(null);
        }
    }

    /**
     * Clears the image view.
     */
    private void clearImage() {
        stock_image.setImage(null);
    }
}
