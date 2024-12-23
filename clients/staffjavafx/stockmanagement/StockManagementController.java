package clients.staffjavafx.stockmanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import middle.MiddleFactory;

import java.io.IOException;

import clients.staffjavafx.dashboard.DashboardController;
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
    private Button stock_management_finished_btn;

    @FXML
    private TextField stock_management_message;

    @FXML
    private TextArea stock_management_reply;

    private StockManagementModel model;

    private MiddleFactory mlf;

    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
        try {
            // Initialize model with provided MiddleFactory
            model = new StockManagementModel(mlf);

            // Bind text properties
            stock_management_message.textProperty().bindBidirectional(model.messageProperty());
            stock_management_reply.textProperty().bind(model.replyProperty());

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

    private void process(String action) {
        DEBUG.trace("StockManagementController::process: action = " + action);

        switch (action) {
            case "CHECK STOCK":
                handleCheckStock();
                break;
            case "ADD STOCK":
                handleAddStock();
                break;
            case "CORRECT STOCK":
                handleCorrectStock();
                break;
            case "NEW PRODUCT":
                handleNewProduct();
                break;
            case "ADD IMAGE":
                handleAddImage();
                break;
            case "FINISHED":
                handleFinished();
                break;
            case "MENU":
                processMenu();
                break;
            default:
                DEBUG.trace("Unknown button: " + action);
                break;
        }
    }

    private void handleCheckStock() {
        model.doCheck(stock_management_message.getText());
    }

    private void handleAddStock() {
        model.doAdd(stock_management_message.getText());
    }

    private void handleCorrectStock() {
        model.doCorrect(stock_management_message.getText());
    }

    private void handleNewProduct() {
        model.doNewProduct(stock_management_message.getText());
    }

    private void handleAddImage() {
        model.doAddImage(stock_management_message.getText());
    }

    private void handleFinished() {
        model.doFinish();
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
