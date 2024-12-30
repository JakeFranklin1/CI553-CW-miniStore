package ci553.ministore.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageHandler {
    public static File showImageChooser(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");

        File imagesDir = new File("images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        fileChooser.setInitialDirectory(imagesDir);

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        return fileChooser.showOpenDialog(window);
    }

    public static void copyImageFile(File source, String productNum) throws IOException {
        String destFileName = "Pic" + productNum + ".png";
        File destFile = new File("images", destFileName);
        Files.copy(source.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
