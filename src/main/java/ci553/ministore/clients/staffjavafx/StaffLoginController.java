package ci553.ministore.clients.staffjavafx;

import ci553.ministore.dbAccess.UserDAO;
import ci553.ministore.middle.StockException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import ci553.ministore.middle.MiddleFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import ci553.ministore.clients.staffjavafx.dashboard.DashboardController;
import ci553.ministore.clients.start.MinistoreStartController;

public class StaffLoginController {
    @FXML
    private TextField staff_account_field;

    @FXML
    private PasswordField staff_password_field;

    @FXML
    private Button staff_login_button;

    @FXML
    private Text staff_go_back_btn;

    private UserDAO userDAO;
    private MiddleFactory mlf;

    @FXML
    public void initialize() {
        try {
            userDAO = new UserDAO();
        } catch (StockException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while initializing the database connection.");
            e.printStackTrace();
        }

        staff_login_button.setOnAction(event -> handleLoginAction());
        staff_go_back_btn.setOnMouseClicked(event -> handleBackAction());
    }

    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
    }

    @FXML
    private void handleLoginAction() {
        String username = staff_account_field.getText();
        String password = staff_password_field.getText();

        try {
            boolean isValidUser = userDAO.validateUser(username, password);
            if (isValidUser) {
                // Login successful, load the staff dashboard
                loadStaffDashboard();
            } else {
                // Login failed, show alert
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
        }
    }

    private void loadStaffDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_staff_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            DashboardController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) staff_login_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "An error occurred while loading the staff dashboard.");
        }
    }

    @FXML
    private void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf); // Pass the MiddleFactory instance

            Stage stage = (Stage) staff_go_back_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
