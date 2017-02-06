package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;
import ledger.user_interface.utils.AmountStringConverter;

/**
 * TableColumn for amounts
 */
public class AmountColumn extends AAmountColumn {

    public AmountColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountStringConverter()));
        this.setOnEditCommit(this.amountEditHandler);
    }
}
