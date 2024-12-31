package ci553.ministore.clients;

import javafx.application.Application;
import javafx.stage.Stage;
import ci553.ministore.debug.DEBUG;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            DEBUG.set(true);
            DEBUG.trace("ministore starting");
            DEBUG.trace("App::start");

            AutoSetup.initializeIfNeeded();

            View view = new View();
            view.start(primaryStage, "ministore_start.fxml");

            DEBUG.trace("ministore running");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
