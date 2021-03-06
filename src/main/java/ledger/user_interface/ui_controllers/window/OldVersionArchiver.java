package ledger.user_interface.ui_controllers.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.user_interface.ui_controllers.IUIController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Allows the user to chose to either delete or archive old versions of Ledger
 */
public class OldVersionArchiver extends GridPane implements IUIController, Initializable {

    private final File jarFile;

    @FXML
    private Button deleteButton;

    @FXML
    private Button archiveButton;

    public OldVersionArchiver(File jarFile) {
        this.jarFile = jarFile;
        String pageLoc = "/fxml_files/DeleteArchivePrompt.fxml";
        this.initController(pageLoc, this, "Error loading Old Version Archiver");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteButton.setOnAction(this::deleteFile);
        archiveButton.setOnAction(this::archiveFile);
    }

    private void archiveFile(ActionEvent actionEvent) {
        Path newPath = Paths.get(jarFile.getParent(), "Archive", jarFile.getName());

        try {
            new File(newPath.getParent().toString()).mkdirs();
            Files.move(jarFile.toPath(), newPath);
        } catch (IOException e) {
            this.setupErrorPopup("Unable to move file to archive.", e);
        }
        ((Stage) this.getScene().getWindow()).close();
    }

    private void deleteFile(ActionEvent actionEvent) {
        jarFile.delete();
        ((Stage) this.getScene().getWindow()).close();
    }
}
