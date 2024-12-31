package ci553.ministore.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageHandler {
    public static File showImageChooser(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");

        // Use resources directory for images
        String resourcePath = "src/main/resources/ci553/ministore/images";
        File imagesDir = new File(resourcePath);
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
        Path destPath = Paths.get("src/main/resources/ci553/ministore/images", destFileName);
        Files.copy(source.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
