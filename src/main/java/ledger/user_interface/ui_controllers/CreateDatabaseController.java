package ledger.user_interface.ui_controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateDatabaseController extends Pane implements IUIController, Initializable {

    @FXML
    private TextField fileName;
    @FXML
    private Button submitButton;

    public final static String pageLoc = "/fxml_files/CreateDatabasePage.fxml";
    private LoginPageController parentController;

    CreateDatabaseController(LoginPageController parentControl) {
        this.parentController = parentControl;
        this.initController(pageLoc, this, "Error on new database startup: ");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.submitButton.setOnAction((event) -> {
            String fileName = this.fileName.getText();
            if (fileName == null) {
                setupErrorPopup("Please provide a file name", null);
                return;
            }

            this.parentController.setFileBtnText(this.fileName.getText());
            this.parentController.setFilePath("~/" + this.fileName.getText());

            Startup.INSTANCE.runLater(() -> {
                ((Stage) this.getScene().getWindow()).close();
            });
        });
    }
}
