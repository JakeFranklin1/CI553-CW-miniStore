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

/**
 * Controller class for the staff login interface.
 * Handles user interactions and updates the model accordingly.
 */
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

    /**
     * Initializes the controller.
     * Sets up event handlers and initializes the UserDAO.
     */
    @FXML
    public void initialize() {
        try {
            // Initialize UserDAO for database access
            userDAO = new UserDAO();
        } catch (StockException e) {
            // Show alert if database initialization fails
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "An error occurred while initializing the database connection.");
            e.printStackTrace();
        }

        // Set up event handler for the login button
        staff_login_button.setOnAction(event -> handleLoginAction());

        // Set up event handler for the "Go Back" text
        staff_go_back_btn.setOnMouseClicked(event -> handleBackAction());
    }

    /**
     * Sets the MiddleFactory instance.
     * 
     * @param mlf The MiddleFactory instance.
     */
    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
    }

    /**
     * Handles the login action.
     * Validates the user credentials and loads the staff dashboard if successful.
     */
    @FXML
    private void handleLoginAction() {
        String username = staff_account_field.getText();
        String password = staff_password_field.getText();

        try {
            // Validate user credentials
            boolean isValidUser = userDAO.validateUser(username, password);
            if (isValidUser) {
                // Login successful, load the staff dashboard
                loadStaffDashboard();
            } else {
                // Login failed, show alert
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Show alert if an error occurs during database access
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
        }
    }

    /**
     * Loads the staff dashboard.
     * Navigates to the staff dashboard scene.
     */
    private void loadStaffDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ci553/ministore/fxml/ministore_staff_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            DashboardController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the new scene
            Stage stage = (Stage) staff_login_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            // Show alert if an error occurs while loading the staff dashboard
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "An error occurred while loading the staff dashboard.");
        }
    }

    /**
     * Handles the "Go Back" action.
     * Navigates back to the start screen.
     */
    @FXML
    private void handleBackAction() {
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
            // Show alert if an error occurs while loading the start screen
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert dialog with the specified title and message.
     * 
     * @param alertType The type of alert.
     * @param title     The title of the alert.
     * @param message   The message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
