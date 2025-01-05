package ci553.ministore.clients.customerjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.debug.DEBUG;
import java.io.IOException;

import ci553.ministore.clients.start.MinistoreStartController;
import ci553.ministore.clients.cashierjavafx.CashierController;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller class for the customer interface.
 * Handles user interactions and updates the model accordingly.
 */
public class CustomerController {
    @FXML
    private TextField check_stock_message;
    @FXML
    private TextArea check_stock_reply;
    @FXML
    private ImageView check_stock_image;
    @FXML
    private Button check_stock_check_btn;
    @FXML
    private Button check_stock_clear_btn;

    private CustomerModel model;

    /**
     * Sets the MiddleFactory instance and initializes the model.
     * 
     * @param mf The MiddleFactory instance.
     */
    public void setMiddleFactory(MiddleFactory mf) {
        try {
            // Initialize model with provided MiddleFactory
            model = new CustomerModel(mf);

            // Bind text properties
            check_stock_message.textProperty().bindBidirectional(model.messageProperty());
            check_stock_reply.textProperty().bind(model.replyProperty());

            // Add event handler for Enter key press
            check_stock_message.setOnKeyPressed(this::handleKeyPress);

            // Add keyboard event handler to the scene
            // Platform.runLater is used to ensure the scene is fully loaded
            // before adding the event handler. This is necessary because
            // I want the scene to handle key presses, not the text field.
            Platform.runLater(() -> {
                check_stock_message.getScene().setOnKeyPressed(event -> {
                    String key = event.getText();
                    if (key.matches("[0-9]")) {
                        processNumber(key);
                        check_stock_message.requestFocus();
                    }
                });
            });

        } catch (Exception e) {
            DEBUG.error("CustomerCheckStockController::setMiddleFactory\n%s", e.getMessage());
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
        DEBUG.trace("CustomerCheckStockController::process: action = " + action);

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
            case "START ORDER":
                processDetails();
                break;
            default:
                DEBUG.trace("Unknown button: " + action);
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
     * Processes the Enter key press.
     * Checks the stock for the product and updates the image.
     */
    private void processEnter() {
        model.doCheck(check_stock_message.getText());
        updateImage();
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
        model.messageProperty().set("");
        model.replyProperty().set("");
        check_stock_image.setImage(null);
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
        model.doCheck(check_stock_message.getText());
        updateImage();
    }

    /**
     * Updates the image view with the product image.
     */
    private void updateImage() {
        Image image = model.getProductImage();
        if (image != null) {
            check_stock_image.setImage(image);
        } else {
            check_stock_image.setImage(null);
        }
    }

    /**
     * Processes the details of the product and switches to the place order scene.
     */
    private void processDetails() {
        String productNum = check_stock_message.getText();
        if (productNum != null && !productNum.isEmpty()) {
            switchToPlaceOrder(productNum);
        }
    }

    /**
     * Switches to the place order scene and adds the product to the order.
     * 
     * @param productNum The product number to add to the order.
     */
    private void switchToPlaceOrder(String productNum) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ci553/ministore/fxml/ministore_place_order.fxml"));
            Parent root = loader.load();

            CashierController controller = loader.getController();
            controller.setMiddleFactory(model.getMiddleFactory());
            controller.addProductToOrder(productNum);

            Stage stage = (Stage) check_stock_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("CustomerCheckStockController::switchToPlaceOrder\n%s", e.getMessage());
        }
    }

    /**
     * Navigates back to the main menu.
     */
    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(model.getMiddleFactory()); // Use model's MiddleFactory instance

            Stage stage = (Stage) check_stock_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("CustomerCheckStockController::processMenu\n%s", e.getMessage());
        }
    }
}
