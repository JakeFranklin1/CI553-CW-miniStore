package clients.customerjavafx;

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
import middle.RemoteMiddleFactory;
import debug.DEBUG;
import java.io.IOException;

public class CustomerCheckStockController {
    @FXML private TextField check_stock_message;
    @FXML private TextArea check_stock_reply;
    @FXML private ImageView check_stock_image;
    @FXML private Button check_stock_check_btn;
    @FXML private Button check_stock_clear_btn;

    private CustomerModel model;

    @FXML
    public void initialize() {
        try {
            // Initialize model with RemoteMiddleFactory
            MiddleFactory mf = new RemoteMiddleFactory();
            model = new CustomerModel(mf);

            // Bind text properties
            check_stock_message.textProperty().bindBidirectional(model.messageProperty());
            check_stock_reply.textProperty().bind(model.replyProperty());

        } catch (Exception e) {
            DEBUG.error("CustomerCheckStockController::initialize\n%s", e.getMessage());
        }
    }

    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();
        process(buttonText);
    }

    private void process(String action) {
        DEBUG.trace("CustomerCheckStockController::process: action = " + action);

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
        model.messageProperty().set(model.messageProperty().get() + number);
    }

    private void processEnter() {
        model.doCheck(check_stock_message.getText());
    }

    private void processClear() {
        model.messageProperty().set("");
        model.replyProperty().set("");
        check_stock_image.setImage(null);
    }

    private void processCancel() {
        processClear();
    }

    private void processCheck() {
        model.doCheck(check_stock_message.getText());
    }

    private void processMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) check_stock_message.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            DEBUG.error("CustomerCheckStockController::processMenu\n%s", e.getMessage());
        }
    }
}
