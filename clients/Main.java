package clients;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/res/layout/ministore_start.fxml"));
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
