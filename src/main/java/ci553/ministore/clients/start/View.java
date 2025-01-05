package ci553.ministore.clients.start;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ci553.ministore.middle.LocalMiddleFactory;
import ci553.ministore.middle.MiddleFactory;

/**
 * View class for initializing and displaying the main application window.
 * Loads the specified FXML file and sets up the primary stage.
 */
public class View {

    /**
     * Starts the application by loading the specified FXML file and setting up the
     * primary stage.
     * 
     * @param primaryStage The primary stage for this application.
     * @param fxmlFile     The name of the FXML file to load.
     */
    public void start(Stage primaryStage, String fxmlFile) {
        try {
            // Create the MiddleFactory instance
            MiddleFactory mlf = new LocalMiddleFactory();

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ci553/ministore/fxml/" + fxmlFile));
            Parent root = loader.load();

            // Get the controller and pass the MiddleFactory instance
            MinistoreStartController controller = loader.getController();
            controller.setMiddleFactory(mlf);

            // Set the scene with the loaded FXML root
            Scene scene = new Scene(root);
            primaryStage.setTitle("MiniStore"); // Set the title of the primary stage
            primaryStage.setScene(scene); // Set the scene on the primary stage
            primaryStage.centerOnScreen(); // Center the stage on the screen
            primaryStage.show(); // Display the primary stage
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }
}
