package ci553.ministore.clients.staffjavafx.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.clients.start.MinistoreStartController;
import ci553.ministore.clients.staffjavafx.packing.PackingController;
import ci553.ministore.clients.staffjavafx.stockmanagement.StockManagementController;

/**
 * Controller class for the staff dashboard interface.
 * Handles user interactions and navigates to different sections of the
 * application.
 */
public class DashboardController {
    @FXML
    private Button check_stock_btn;

    @FXML
    private Button start_order_btn;

    @FXML
    private Text staff_go_back_btn;

    private MiddleFactory mlf;

    /**
     * Sets the MiddleFactory instance.
     * 
     * @param mlf The MiddleFactory instance.
     */
    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
    }

    /**
     * Initializes the controller.
     * Sets up event handlers for the buttons.
     */
    @FXML
    public void initialize() {
        // Set up event handler for the "Check Stock" button
        check_stock_btn.setOnAction(event -> navigateToStockManagement());

        // Set up event handler for the "Start Order" button
        start_order_btn.setOnAction(event -> navigateToPacking());

        // Set up event handler for the "Go Back" text
        staff_go_back_btn.setOnMouseClicked(event -> navigateToStart());
    }

    /**
     * Navigates to the stock management screen.
     */
    private void navigateToStockManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ci553/ministore/fxml/ministore_staff_stock_management.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            StockManagementController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the new scene
            Stage stage = (Stage) check_stock_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the stock management screen.");
        }
    }

    /**
     * Navigates to the packing screen.
     */
    private void navigateToPacking() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ci553/ministore/fxml/ministore_staff_pack_orders.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            PackingController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the new scene
            Stage stage = (Stage) start_order_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the packing screen.");
        }
    }

    /**
     * Navigates back to the start screen.
     */
    private void navigateToStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf); // Pass the MiddleFactory instance

            // Set the new scene
            Stage stage = (Stage) staff_go_back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Loading Error", "An error occurred while loading the start screen.");
        }
    }

    /**
     * Shows an alert dialog with the specified title and message.
     * 
     * @param title   The title of the alert.
     * @param message The message of the alert.
     */
    private void showAlert(String title, String message) {
        // Implement alert dialog
    }
}
