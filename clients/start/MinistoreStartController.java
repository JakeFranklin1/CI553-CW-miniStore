package clients.start;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;
import clients.customerjavafx.CustomerCheckStockController;
import clients.cashierjavafx.CashierPlaceOrderController;

import java.io.IOException;

public class MinistoreStartController {

    @FXML
    private Button check_stock_btn;

    @FXML
    private Button start_order_btn;

    private MiddleFactory mlf;

    @FXML
    public void initialize() {
        mlf = new LocalMiddleFactory();
        check_stock_btn.setOnAction(event -> loadCheckStock());
        start_order_btn.setOnAction(event -> loadPlaceOrder());
    }

    private void loadCheckStock() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_check_stock.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            CustomerCheckStockController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) check_stock_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Center the stage on the screen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaceOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_place_order.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            CashierPlaceOrderController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) start_order_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Center the stage on the screen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
