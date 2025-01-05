package ci553.ministore.clients.start;

import javafx.application.Application;
import javafx.stage.Stage;
import ci553.ministore.debug.DEBUG;

/**
 * Main application class for the MiniStore application.
 * Extends the JavaFX Application class and serves as the entry point for the
 * application.
 */
public class App extends Application {

    /**
     * Main method that launches the JavaFX application.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

    /**
     * The start method is called after the JavaFX application is launched.
     * Initializes the application, sets up debugging, and loads the initial view.
     * 
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Enable debugging
            DEBUG.set(true);
            DEBUG.trace("ministore starting");
            DEBUG.trace("App::start");

            // Initialize the application setup if needed
            AutoSetup.initializeIfNeeded();

            // Create and start the initial view
            View view = new View();
            view.start(primaryStage, "ministore_start.fxml");

            DEBUG.trace("ministore running");
        } catch (Exception e) {
            // Print stack trace and exit if an exception occurs
            e.printStackTrace();
            System.exit(1);
        }
    }
}
