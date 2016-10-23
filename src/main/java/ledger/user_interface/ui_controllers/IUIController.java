package ledger.user_interface.ui_controllers;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * To assist in unifying all UI controllers common code
 */
public interface IUIController {

    /**
     * Generating code for the error popup
     *
     * @param s a string containing the error message
     */
    default void setupErrorPopup(String s){
        try {
            ErrorPopupController errCon = new ErrorPopupController(s);
            Scene scene = new Scene(errCon);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Ledger");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.show();
        } catch (Exception e) {
            System.out.println("Error on triggering add error screen: " + e);
        }
    }
}
