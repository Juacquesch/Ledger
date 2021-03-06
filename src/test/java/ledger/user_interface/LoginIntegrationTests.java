package ledger.user_interface;

import javafx.geometry.VerticalDirection;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.window.CreateDatabaseController;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotException;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Login User Interface using the TestFX framework.
 */
public class LoginIntegrationTests extends FxRobot {

    public void createDatabase() {
        try{
            clickOn(window("Hello!"), MouseButton.PRIMARY);
            type(KeyCode.ENTER);
        } catch (NoSuchElementException ignored){}

        clickOn("#newFileBtn");

        CreateDatabaseController controller = lookup(node ->  node instanceof CreateDatabaseController).query();
        interact(() -> controller.setSaveLocation(new File("Ledger.mv.db")));
        clickOn(node -> node instanceof PasswordField && ((Stage)node.getScene().getWindow()).getModality() == Modality.APPLICATION_MODAL && "password".equals(node.getId()));
        write("PasswordForUiTesting1234");
        type(KeyCode.TAB);
        write("PasswordForUiTesting1234");
        type(KeyCode.ENTER);

        try {
            clickOn(window("Welcome!"), MouseButton.PRIMARY);
            type(KeyCode.ENTER);
        } catch (NoSuchElementException ignored){}

        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.startTask();
        List<Account> accounts = task.waitForResult();

        assertEquals(0, accounts.size());
    }

    /**
     * Uses the TestFx framework to logout from the application, when the Miscellaneous VBox is currently closed
     *
     * @param vBoxClosed True if the Miscellaneous VBox is currently closed
     */
    public void logout(boolean vBoxClosed) {
        closeAccountOperations();
        closeTransactionOperations();

        if (vBoxClosed) clickOn("Miscellaneous");
        while(true) {
            try {
                scroll(20, VerticalDirection.DOWN);
                clickOn("Logout");
                break;
            } catch (FxRobotException ignored) { }
        }
    }

    private void closeAccountOperations() {
        TitledPane pane = lookup("Account Operations").query();
        interact(() -> pane.setExpanded(false));
    }

    private void closeTransactionOperations() {
        TitledPane pane = lookup("Transaction Operations").query();
        interact(() -> pane.setExpanded(false));
    }
}
