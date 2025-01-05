package ci553.ministore.clients.staffjavafx.packing;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import ci553.ministore.clients.staffjavafx.dashboard.DashboardController;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.debug.DEBUG;

/**
 * Controller class for the packing interface.
 * Handles user interactions and updates the model accordingly.
 */
public class PackingController {
    @FXML
    private Button pack_orders_packed_btn;

    @FXML
    private Button pack_orders_menu_btn;

    @FXML
    private TextField pack_orders_message;

    @FXML
    private TextArea check_stock_reply;

    private PackingModel model;

    private MiddleFactory mlf;

    /**
     * Sets the MiddleFactory instance and initializes the model.
     *
     * @param mlf The MiddleFactory instance.
     */
    public void setMiddleFactory(MiddleFactory mlf) {
        this.mlf = mlf;
        try {
            // Initialize model with provided MiddleFactory
            model = new PackingModel(mlf);

            // Bind text properties
            pack_orders_message.textProperty().bindBidirectional(model.messageProperty());
            check_stock_reply.textProperty().bind(model.replyProperty());

        } catch (Exception e) {
            DEBUG.error("PackingController::setMiddleFactory\n%s", e.getMessage());
        }
    }

    /**
     * Handles button actions and delegates to the appropriate method.
     *
     * @param event The ActionEvent triggered by the button press.
     */
    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();
        process(buttonText);
    }

    /**
     * Processes the action based on the button text.
     *
     * @param action The action to be processed.
     */
    private void process(String action) {
        DEBUG.trace("PackingController::process: action = " + action);

        switch (action) {
            case "PACKED":
                processPacked();
                break;
            case "MENU":
                processMenu();
                break;
            default:
                DEBUG.trace("Unknown button: " + action);
                break;
        }
    }

    /**
     * Processes the "PACKED" action.
     * Marks the order as packed.
     */
    private void processPacked() {
        model.doPack();
    }

    /**
     * Processes the "MENU" action.
     * Navigates back to the staff dashboard.
     */
    private void processMenu() {
        try {
            if (model != null) {
                model.cleanup(); // Clean up before switching scenes
            }
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ci553/ministore/fxml/ministore_staff_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            DashboardController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the new scene
            Stage stage = (Stage) pack_orders_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("PackingController::processMenu\n%s", e.getMessage());
        }
    }
}
