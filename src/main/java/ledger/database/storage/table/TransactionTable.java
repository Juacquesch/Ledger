package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
public class TransactionTable {

    private static final String tableTransaction = "CREATE TABLE IF NOT EXISTS TRANSACT " +
            "(TRANS_ID INTEGER PRIMARY KEY  AUTOINCREMENT, " +
            "TRANS_DATETIME REAL        NOT NULL, " +
            "TRANS_AMOUNT INT           NOT NULL," +
            "TRANS_PENDING BOOLEAN      NOT NULL, " +
            "TRANS_ACCOUNT_ID INT       NOT NULL, " +
            "TRANS_PAYEE_ID INT         NOT NULL, " +
            "TRANS_TYPE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TRANS_ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID), " +
            "FOREIGN KEY(TRANS_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)," +
            "FOREIGN KEY(TRANS_TYPE_ID) REFERENCES TYPE(TYPE_ID)" +
            ")";

    public static final String TABLE_NAME = "TRANSACT";
    public static final String TRANS_ID = "TRANS_ID";
    public static final String TRANS_DATETIME = "TRANS_DATETIME";
    public static final String TRANS_AMOUNT = "TRANS_AMOUNT";
    public static final String TRANS_PENDING = "TRANS_PENDING";
    public static final String TRANS_ACCOUNT_ID = "TRANS_ACCOUNT_ID";
    public static final String TRANS_PAYEE_ID = "TRANS_PAYEE_ID";
    public static final String TRANS_TYPE_ID= "TRANS_TYPE_ID";

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s REAL NOT NULL, " +
                "%s INT NOT NULL, " +
                "%s BOOLEAN NOT NULL, " +
                "%s INT NOT NULL, " +
                "%s INT NOT NULL, " +
                "%s INT NOT NULL, " +
                "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                ")", TABLE_NAME,
                TRANS_ID, TRANS_DATETIME, TRANS_AMOUNT, TRANS_PENDING, TRANS_ACCOUNT_ID, TRANS_PAYEE_ID, TRANS_TYPE_ID,
                TRANS_ACCOUNT_ID, AccountTable.TABLE_NAME ,AccountTable.ACCOUNT_ID,
                TRANS_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID,
                TRANS_TYPE_ID, TypeTable.TABLE_NAME, TypeTable.TYPE_ID
                );
    }

}
