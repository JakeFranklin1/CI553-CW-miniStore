package clients.staffjavafx.packing;

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

import clients.staffjavafx.dashboard.DashboardController;
import middle.MiddleFactory;
import debug.DEBUG;

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

    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();
        process(buttonText);
    }

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

    private void processPacked() {
        model.doPack();
    }

    private void processMenu() {
        try {
            if (model != null) {
                model.cleanup();  // Clean up before switching scenes
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_staff_dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            DashboardController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            Stage stage = (Stage) pack_orders_message.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            DEBUG.error("PackingController::processMenu\n%s", e.getMessage());
        }
    }
}
