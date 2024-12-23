package clients.staffjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

public class StaffLoginController {
    @FXML
    private TextField staff_account_field;

    @FXML
    private PasswordField staff_password_field;

    @FXML
    private Button staff_login_button;

    @FXML
    private Text staff_go_back_btn;

    @FXML
    public void initialize() {
        staff_login_button.setOnAction(event -> handleLoginAction());
        staff_go_back_btn.setOnMouseClicked(event -> handleBackAction());
    }

    @FXML
    private void handleLoginAction() {
        // Implement login logic here
        String username = staff_account_field.getText();
        String password = staff_password_field.getText();
        // Validate credentials and proceed
    }

    @FXML
    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) staff_go_back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
