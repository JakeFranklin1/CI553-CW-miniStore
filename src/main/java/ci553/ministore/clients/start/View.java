package ci553.ministore.clients.start;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ci553.ministore.middle.LocalMiddleFactory;
import ci553.ministore.middle.MiddleFactory;

public class View {
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

            // Set the scene
            Scene scene = new Scene(root);
            primaryStage.setTitle("MiniStore");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
