package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
public class TypeTable {

    private static final String tableType = "CREATE TABLE IF NOT EXISTS TYPE " +
            "(TYPE_ID INTEGER PRIMARY KEY    AUTOINCREMENT, " +
            "TYPE_NAME TEXT              NOT NULL, " +
            "TYPE_DESC TEXT              NOT NULL" +
            ")";

    public static String TABLE_NAME = "TYPE";
    public static String TYPE_ID = "TYPE_ID";
    public static String TYPE_NAME = "TYPE_NAME";
    public static String TYPE_DESC = "TYPE_DESC";

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, TYPE_ID, TYPE_NAME, TYPE_DESC);
    }
}
