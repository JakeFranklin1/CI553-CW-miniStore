package ci553.ministore.clients.start;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.clients.customerjavafx.CustomerController;
import ci553.ministore.clients.cashierjavafx.CashierController;
import ci553.ministore.clients.staffjavafx.StaffLoginController;

import java.io.IOException;

public class MinistoreStartController {

    @FXML
    private Button check_stock_btn;

    @FXML
    private Button start_order_btn;
    @FXML
    private Text staff_log_in_btn;

    private MiddleFactory mlf;

    @FXML
    public void initialize() {
        check_stock_btn.setOnAction(event -> loadCheckStock());
        start_order_btn.setOnAction(event -> loadPlaceOrder());
        // setOnMouseClicked is used instead of setOnAction because this button is actually a Text
        staff_log_in_btn.setOnMouseClicked(event -> loadStaffLogin());
    }

    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
    }

    private void loadCheckStock() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_check_stock.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            CustomerController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_place_order.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            CashierController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) start_order_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Center the stage on the screen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStaffLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_staff_login.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            StaffLoginController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) staff_log_in_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
