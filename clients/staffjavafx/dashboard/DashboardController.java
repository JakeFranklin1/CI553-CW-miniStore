package clients.staffjavafx.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import middle.MiddleFactory;
import clients.start.MinistoreStartController;
import clients.staffjavafx.packing.PackingController;
import clients.staffjavafx.stockmanagement.StockManagementController;

public class DashboardController {
    @FXML
    private Button check_stock_btn;

    @FXML
    private Button start_order_btn;

    @FXML
    private Text staff_go_back_btn;

    private MiddleFactory mlf;
    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
    }
    @FXML
    public void initialize() {
        check_stock_btn.setOnAction(event -> navigateToStockManagement());
        start_order_btn.setOnAction(event -> navigateToPacking());
        staff_go_back_btn.setOnMouseClicked(event -> navigateToStart());
    }

    private void navigateToStockManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_staff_stock_management.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            StockManagementController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) check_stock_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the stock management screen.");
        }
    }

    private void navigateToPacking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_staff_pack_orders.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            PackingController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) start_order_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the packing screen.");
        }
    }

    private void navigateToStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf);  // Pass the MiddleFactory instance

            Stage stage = (Stage) staff_go_back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the start screen.");
        }
    }

    private void showAlert(String title, String message) {
        // Implement alert dialog
    }
}
