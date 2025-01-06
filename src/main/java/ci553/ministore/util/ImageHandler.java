package ci553.ministore.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for handling image file operations.
 * Provides methods to show a file chooser dialog and copy image files.
 */
public class ImageHandler {

    /**
     * Shows a file chooser dialog to select an image file.
     *
     * @param window The parent window for the file chooser dialog
     * @return The selected image file, or null if no file was selected
     */
    public static File showImageChooser(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");

        // Use resources directory for images
        String resourcePath = "src/main/resources/ci553/ministore/images";
        File imagesDir = new File(resourcePath);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();  // Create directory if it doesn't exist
        }
        fileChooser.setInitialDirectory(imagesDir);

        // Set file extension filters for image files
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Show the file chooser dialog and return the selected file
        return fileChooser.showOpenDialog(window);
    }

    /**
     * Copies the selected image file to the resources directory with a new name.
     *
     * @param source The source image file to copy
     * @param productNum The product number to use in the destination file name
     * @throws IOException If an I/O error occurs during the file copy
     */
    public static void copyImageFile(File source, String productNum) throws IOException {
        // Construct the destination file name and path
        String destFileName = "Pic" + productNum + ".png";
        Path destPath = Paths.get("src/main/resources/ci553/ministore/images", destFileName);

        // Copy the source file to the destination path, replacing any existing file
        Files.copy(source.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
