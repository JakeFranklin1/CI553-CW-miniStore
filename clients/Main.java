package clients;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;
import clients.start.MinistoreStartController;
import debug.DEBUG;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DEBUG.set(true);
        try {
            // Create the MiddleFactory instance
            MiddleFactory mlf = new LocalMiddleFactory();

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/layout/ministore_start.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the scene
            Scene scene = new Scene(root);
            primaryStage.setTitle("MiniStore");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen(); // Center the stage on the screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
