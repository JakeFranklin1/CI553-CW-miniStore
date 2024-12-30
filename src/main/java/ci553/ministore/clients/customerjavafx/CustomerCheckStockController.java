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
import ci553.ministore.clients.cashierjavafx.CashierPlaceOrderController;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomerCheckStockController {
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

    private void processNumber(String number) {
        String currentMessage = model.messageProperty().get();
        if (currentMessage == null) {
            currentMessage = "";
        }
        model.messageProperty().set(currentMessage + number);
    }

    private void processEnter() {
        model.doCheck(check_stock_message.getText());
        updateImage();
    }

    private void processClear() {
        String currentMessage = model.messageProperty().get();
        if (currentMessage != null && currentMessage.length() > 0) {
            model.messageProperty().set(currentMessage.substring(0, currentMessage.length() - 1));
        }
    }

    private void processClearOrder() {
        model.messageProperty().set("");
        model.replyProperty().set("");
        check_stock_image.setImage(null);
    }

    private void processCancel() {
        processClearOrder();
    }

    private void processCheck() {
        model.doCheck(check_stock_message.getText());
        updateImage();
    }

    private void updateImage() {
        Image image = model.getProductImage();
        if (image != null) {
            check_stock_image.setImage(image);
        } else {
            check_stock_image.setImage(null);
        }
    }

    private void processDetails() {
        String productNum = check_stock_message.getText();
        if (productNum != null && !productNum.isEmpty()) {
            switchToPlaceOrder(productNum);
        }
    }

    private void switchToPlaceOrder(String productNum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_place_order.fxml"));
            Parent root = loader.load();

            CashierPlaceOrderController controller = loader.getController();
            controller.setMiddleFactory(model.getMiddleFactory());
            controller.addProductToOrder(productNum);

            Stage stage = (Stage) check_stock_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("CustomerCheckStockController::switchToPlaceOrder\n%s", e.getMessage());
        }
    }

    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
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
