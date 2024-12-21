package clients.start;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MinistoreStartController {

    @FXML
    private Button check_stock_btn;

    @FXML
    private Button start_order_btn;

    @FXML
    public void initialize() {
        check_stock_btn.setOnAction(event -> loadCheckStock());
        start_order_btn.setOnAction(event -> loadPlaceOrder());
    }

    private void loadCheckStock() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_check_stock.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) check_stock_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaceOrder() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/res/layout/ministore_place_order.fxml"));
            Stage stage = (Stage) start_order_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
