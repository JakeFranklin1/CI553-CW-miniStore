package clients.cashierjavafx;

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
import middle.MiddleFactory;
import debug.DEBUG;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CashierPlaceOrderController {
    @FXML private TextField message;
    @FXML private TextArea reply;
    @FXML private ImageView check_stock_image;
    @FXML private Button place_order_check_stock_btn;
    @FXML private Button place_order_add_to_order_btn;
    @FXML private Button place_order_purchase_btn;
    @FXML private Button place_order_clear_last_btn;
    @FXML private Button place_order_clear_order_btn;
    @FXML private Button place_order_remove_item_btn;
    @FXML private Button place_order_enter_btn;
    @FXML private Button place_order_clear_btn;
    @FXML private Button place_order_cancel_btn;
    @FXML private Button place_order_menu_btn;

    private CashierModelJavaFX model;

    public void setMiddleFactory(MiddleFactory mf) {
        try {
            // Initialize model with provided MiddleFactory
            model = new CashierModelJavaFX(mf);

            // Bind text properties
            message.textProperty().bindBidirectional(model.messageProperty());
            reply.textProperty().bind(model.replyProperty());

            // Add event handler for Enter key press
            message.setOnKeyPressed(this::handleKeyPress);

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
            case "1": case "2": case "3":
            case "4": case "5": case "6":
            case "7": case "8": case "9":
            case "0": case "00":
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
            case "CHECK":
                processCheck();
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
        model.doCheck(message.getText());
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
        // model.doCheck(message.getText());
    }

    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("CashierPlaceOrderController::processMenu\n%s", e.getMessage());
        }
    }
}
