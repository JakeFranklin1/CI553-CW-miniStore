package ci553.ministore.clients;

import ci553.ministore.clients.start.App;
import ci553.ministore.clients.start.AutoSetup;

/**
 * Main class for launching the MiniStore application.
 * Sets up the application configuration and starts the main application.
 */
public class Main {
    /**
     * Main method that serves as the entry point for the application.
     * Configures the automatic setup and launches the main application.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        // Disable automatic setup
        AutoSetup.setRunAuto(false);

        // Launch the main application
        App.main(args);
    }
}
