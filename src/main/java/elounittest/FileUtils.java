package elounittest;

import com.example.elounittest.EloUnittestApp;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    private static String sanitizeFilename(String fileName) {
        String regex = "[\\/\\?<>\\\\:\\*\\|\":]";
        return fileName.replaceAll(regex, " ");
    }

    static void SaveToFile(String dirName, String fileName, String fileText, String fileExt ) {
        fileName = sanitizeFilename(fileName);
        File dir = new File(dirName);
        File file = new File(dirName + "\\" + fileName + "." + fileExt);

        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(fileText);
                bw.flush();
            }
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage() + " dirName=" + dirName + " fileName=" + fileName);
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
            });
        }
    }
}
